package com.tingyu.player

import com.tingyu.player.stat.StatCalculator
import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

object StatApplier {

    // StatType → 对应的原版 Attribute（仅可直接映射的属性）
    private val VANILLA_MAP = listOf(
        StatType.HP               to Attribute.GENERIC_MAX_HEALTH,
        StatType.ARMOR            to Attribute.GENERIC_ARMOR,
        StatType.ARMOR_TOUGHNESS  to Attribute.GENERIC_ARMOR_TOUGHNESS,
        StatType.MOVE_SPEED       to Attribute.GENERIC_MOVEMENT_SPEED,
        StatType.KNOCKBACK_RESIST to Attribute.GENERIC_KNOCKBACK_RESISTANCE,
    )

    /**
     * 将玩家当前档案的属性写入 Minecraft 原版 Attribute。
     *
     * @param equipLayers 装备提供的额外属性层（装备系统完成后传入）
     */
    fun apply(player: Player, equipLayers: Map<StatType, StatLayer> = emptyMap()) {
        val profile = PlayerManager.get(player)

        for ((statType, attribute) in VANILLA_MAP) {
            val instance = player.getAttribute(attribute) ?: continue
            val layer = profile.buildLayer(statType) + (equipLayers[statType] ?: StatLayer())
            instance.baseValue = StatCalculator.compute(statType, layer)
        }

        // 当前血量不能超过新上限
        val maxHp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
        if (player.health > maxHp) player.health = maxHp
    }
}
