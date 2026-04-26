package com.tingyu.item.equip

import com.tingyu.player.job.Job
import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.inventory.EquipmentSlot

object EquipStatTable {

    private val table = HashMap<Triple<Job?, Int, EquipmentSlot>, Map<StatType, StatLayer>>()

    /**
     * 注册某职业、某等阶、某槽位的属性。
     *
     * @param job  null = 全职业通用
     * @param tier 等阶 (1-6)
     * @param slot 装备槽位
     *
     * 用法：
     * ```
     * EquipStatTable.register(Job.WARRIOR, 3, EquipmentSlot.CHEST) {
     *     base(StatType.HP, 30.0)
     *     base(StatType.ARMOR, 6.0)
     *     set(StatType.JOB_POWER, base = 10.0, percent = 0.05)
     * }
     * ```
     */
    fun register(job: Job?, tier: Int, slot: EquipmentSlot, builder: EquipStatBuilder.() -> Unit) {
        table[Triple(job, tier, slot)] = EquipStatBuilder().apply(builder).build()
    }

    /**
     * 查询属性表。
     * 先查职业专属，找不到再查通用（job = null）。
     */
    fun getStats(job: Job?, tier: Int, slot: EquipmentSlot): Map<StatType, StatLayer>? =
        table[Triple(job, tier, slot)] ?: table[Triple(null, tier, slot)]

    fun clear() = table.clear()
}

/** 属性构建器，提供 DSL 语法 */
class EquipStatBuilder {

    private val stats = mutableMapOf<StatType, StatLayer>()

    /** 只设置基础值 */
    fun base(type: StatType, value: Double) {
        stats[type] = StatLayer(base = value, finalPercent = 1.0)
    }

    /** 设置完整四层 */
    fun set(
        type: StatType,
        base: Double = 0.0,
        percent: Double = 0.0,
        finalPercent: Double = 1.0,
        finalFlat: Double = 0.0
    ) {
        stats[type] = StatLayer(base, percent, finalPercent, finalFlat)
    }

    internal fun build(): Map<StatType, StatLayer> = stats.toMap()
}
