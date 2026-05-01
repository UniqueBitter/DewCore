package com.tingyu.combat

import com.tingyu.item.equip.EquipManager
import com.tingyu.mob.MobManager
import com.tingyu.player.PlayerManager
import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

/**
 * 战斗伤害管线：
 *   玩家/管理怪 → 覆盖 event.damage 为自定义 ATTACK 值
 *   原版怪 → 不干涉，保留 Minecraft 默认伤害
 *
 * 防御（ARMOR / ARMOR_TOUGHNESS / KNOCKBACK_RESIST）已由 StatApplier 写入
 * 原版 Attribute，Minecraft 引擎自行扣减，此处无需重复处理。
 */
object CombatManager {

    @SubscribeEvent(priority = EventPriority.HIGH)
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        event.entity as? LivingEntity ?: return

        val attacker: LivingEntity = when (val raw = event.damager) {
            is LivingEntity -> raw
            is Projectile   -> raw.shooter as? LivingEntity ?: return
            else            -> return
        }

        val damage = when (attacker) {
            is Player -> playerDamage(attacker, event.damager is Arrow)
            else      -> mobDamage(attacker)
        } ?: return

        event.damage = damage
    }

    // ======================== 玩家出伤 ========================

    private fun playerDamage(player: Player, isArrow: Boolean): Double {
        val equip = EquipManager.getEquipStats(player)

        val attack = PlayerManager.computeStat(
            player, StatType.ATTACK,
            listOf(equip[StatType.ATTACK] ?: StatLayer())
        )

        return if (isArrow) {
            // 弓箭伤害 = ATTACK × 箭速修正（箭速越高伤害越高，上限3倍、下限0.5倍）
            val arrowSpeed = PlayerManager.computeStat(
                player, StatType.ARROW_SPEED,
                listOf(equip[StatType.ARROW_SPEED] ?: StatLayer()),
                arrowType = 1
            )
            // 基准箭速 sqrt(3) ≈ 1.73，修正系数归一化到 1.0
            (attack * (arrowSpeed / 1.732)).coerceIn(attack * 0.5, attack * 3.0)
        } else {
            attack
        }
    }

    // ======================== 怪物出伤 ========================

    private fun mobDamage(mob: LivingEntity): Double? {
        if (!MobManager.isManaged(mob)) return null
        val v = MobManager.getStat(mob, StatType.ATTACK)
        return if (v > 0.0) v else null
    }
}
