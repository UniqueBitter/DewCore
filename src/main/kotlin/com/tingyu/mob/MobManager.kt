package com.tingyu.mob

import com.tingyu.player.PlayerManager
import com.tingyu.player.stat.StatCalculator
import com.tingyu.player.stat.StatType
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.persistence.PersistentDataType
import taboolib.common.platform.event.SubscribeEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object MobManager {

    private val VANILLA_MAP = listOf(
        StatType.HP               to Attribute.GENERIC_MAX_HEALTH,
        StatType.ARMOR            to Attribute.GENERIC_ARMOR,
        StatType.ARMOR_TOUGHNESS  to Attribute.GENERIC_ARMOR_TOUGHNESS,
        StatType.MOVE_SPEED       to Attribute.GENERIC_MOVEMENT_SPEED,
        StatType.KNOCKBACK_RESIST to Attribute.GENERIC_KNOCKBACK_RESISTANCE,
    )

    private val NON_VANILLA = listOf(
        StatType.JOB_POWER,
        StatType.ATTACK,
        StatType.ATTACK_SPEED,
        StatType.ARROW_SPEED,
        StatType.SKILL_EFFECT,
        StatType.KNOCKBACK,
        StatType.EFFECT_HIT,
        StatType.EFFECT_RESIST,
        StatType.RECOVERY,
    )

    val MOB_ID_KEY = NamespacedKey("dew", "mob_id")

    private val cache = ConcurrentHashMap<UUID, Map<StatType, Double>>()

    // ======================== 公开 API ========================

    /**
     * 将 [profile] 的属性应用到 [entity]，并缓存非原版属性。
     * 自动生成监听会调用此方法；也可在自定义生成逻辑中手动调用。
     */
    fun apply(entity: LivingEntity, profile: MobProfile) {
        entity.persistentDataContainer.set(MOB_ID_KEY, PersistentDataType.STRING, profile.id)

        if (profile.displayName.isNotBlank()) {
            entity.customName(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacySection().deserialize(profile.displayName))
            entity.isCustomNameVisible = true
        }

        for ((statType, attribute) in VANILLA_MAP) {
            val instance = entity.getAttribute(attribute) ?: continue
            instance.baseValue = StatCalculator.compute(statType, profile.buildLayer(statType))
        }

        cache[entity.uniqueId] = NON_VANILLA.associateWith { type ->
            StatCalculator.compute(type, profile.buildLayer(type))
        }

        entity.health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
    }

    /** 获取怪物的某项非原版属性值（HP/护甲等原版属性直接用 entity.getAttribute） */
    fun getStat(entity: LivingEntity, type: StatType): Double =
        cache[entity.uniqueId]?.get(type) ?: 0.0

    fun isManaged(entity: LivingEntity): Boolean =
        entity.persistentDataContainer.has(MOB_ID_KEY, PersistentDataType.STRING)

    fun getMobId(entity: LivingEntity): String? =
        entity.persistentDataContainer.get(MOB_ID_KEY, PersistentDataType.STRING)

    // ======================== 事件 ========================

    @SubscribeEvent
    fun onSpawn(event: CreatureSpawnEvent) {
        val entity = event.entity as? LivingEntity ?: return
        // 已经手动应用过的（如 SPAWNER_EGG 触发但已有 mob_id）跳过
        if (isManaged(entity)) return
        val profile = MobRegistry.fromEntityType(event.entityType) ?: return
        apply(entity, profile)
    }

    @SubscribeEvent
    fun onDeath(event: EntityDeathEvent) {
        cache.remove(event.entity.uniqueId)

        val mobId = getMobId(event.entity) ?: return
        val profile = MobRegistry.fromId(mobId) ?: return

        // 清除原版掉落，改用自定义掉落表
        event.drops.clear()

        val loc = event.entity.location
        for (drop in profile.drops()) {
            if (Math.random() < drop.chance) {
                val item = drop.supplier() ?: continue
                item.amount = drop.amount.random()
                loc.world?.dropItemNaturally(loc, item)
            }
        }

        // 经验奖励（仅玩家击杀）
        val killer = event.entity.killer ?: return
        if (profile.xpReward > 0) {
            PlayerManager.gainExp(killer, profile.xpReward.toLong())
        }
    }

    @SubscribeEvent
    fun onChunkUnload(event: ChunkUnloadEvent) {
        event.chunk.entities.filterIsInstance<LivingEntity>().forEach {
            cache.remove(it.uniqueId)
        }
    }
}
