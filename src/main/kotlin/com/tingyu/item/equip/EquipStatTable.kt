package com.tingyu.item.equip

import com.tingyu.player.job.Job
import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.inventory.EquipmentSlot

/**
 * 装备属性整合入口。
 *
 * 单件最终属性 = 职业底层公式 + 元素单件加成（千炼直接覆盖）
 * 千炼的"移除"逻辑：如果某 StatType 的 base < -9000，视为移除，对应值置 0。
 */
object EquipStatTable {

    /**
     * 计算单件装备提供的完整 StatLayer Map。
     * 不含套装共鸣，套装共鸣由 EquipManager 汇总后调用 ResonanceCalc 计算。
     */
    fun compute(
        job: Job?,
        tier: Int,
        slot: EquipmentSlot,
        element: FiveElement
    ): Map<StatType, StatLayer> {

        val result = mutableMapOf<StatType, StatLayer>()

        // 1. 职业底层公式（千炼跳过，千炼有自己的全量属性）
        if (job != null && element != FiveElement.QIANLIAN) {
            EquipFormula.compute(job, tier, slot).forEach { (type, layer) ->
                result[type] = (result[type] ?: StatLayer()) + layer
            }
        }

        // 2. 元素单件加成
        ElementBonus.singlePiece(element, tier, slot).forEach { (type, layer) ->
            result[type] = (result[type] ?: StatLayer()) + layer
        }

        // 3. 千炼被动（静态部分）
        if (element == FiveElement.QIANLIAN) {
            ElementBonus.qianlianPassive(tier, slot).forEach { (type, layer) ->
                result[type] = (result[type] ?: StatLayer()) + layer
            }
        }

        // 4. 处理"移除"标记（base <= -9000 视为该属性被清除）
        result.entries.removeIf { (_, v) -> v.base <= -9000.0 }

        return result
    }
}
