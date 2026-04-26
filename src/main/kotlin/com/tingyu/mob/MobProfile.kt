package com.tingyu.mob

import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.entity.EntityType

/**
 * 定义一种怪物的属性。
 * 与 PlayerProfile 不同，怪物属性在注册时写死，不需要存档。
 *
 * @param entityType 绑定的原版实体类型，非 null 时会在自然生成时自动应用；
 *                   为 null 则只能通过 MobManager.apply() 手动应用。
 */
class MobProfile(val id: String, val entityType: EntityType? = null) {

    private val stats: MutableMap<StatType, StatLayer> = mutableMapOf()

    fun buildLayer(type: StatType): StatLayer =
        stats.getOrDefault(type, StatLayer(finalPercent = 1.0))

    /** 设置某属性的完整四层 */
    fun set(
        type: StatType,
        base: Double = 0.0,
        percent: Double = 0.0,
        finalPercent: Double = 1.0,
        finalFlat: Double = 0.0
    ): MobProfile {
        stats[type] = StatLayer(base, percent, finalPercent, finalFlat)
        return this
    }

    /** 只设置基础值，其余保持默认 */
    fun base(type: StatType, value: Double): MobProfile =
        set(type, base = value)
}
