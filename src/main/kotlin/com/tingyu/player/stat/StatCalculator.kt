package com.tingyu.player.stat

import kotlin.math.sqrt

object StatCalculator {

    /**
     * 计算属性最终值。
     *
     * [arrowType] 仅对 [StatType.ARROW_SPEED] 有效：
     *   1 → √((3+base)×(1+%)) × 最终% + 最终
     *   2 → (1.5+base)×√(1+%) × 最终% + 最终
     */
    fun compute(type: StatType, layer: StatLayer, arrowType: Int = 1): Double = when (type) {

        StatType.HP ->
            (20.0 + layer.base) * (1.0 + layer.percent) * layer.finalPercent + layer.finalFlat

        StatType.ARMOR_TOUGHNESS ->
            layer.base * (1.0 + layer.percent) * layer.finalPercent + layer.finalFlat + 4.0

        StatType.RECOVERY ->
            sqrt(layer.base * (1.0 + layer.percent) * layer.finalPercent + layer.finalFlat) + 100.0

        StatType.MOVE_SPEED ->
            (layer.base + 0.1) * (1.0 + layer.percent) * layer.finalPercent + layer.finalFlat

        // √(((1+base)×(1+%))×最终%+最终+1) - 1，1 代表 1s/次
        StatType.ATTACK_SPEED ->
            sqrt((1.0 + layer.base) * (1.0 + layer.percent) * layer.finalPercent + layer.finalFlat + 1.0) - 1.0

        StatType.ARROW_SPEED -> if (arrowType == 1)
            sqrt((3.0 + layer.base) * (1.0 + layer.percent)) * layer.finalPercent + layer.finalFlat
        else
            (1.5 + layer.base) * sqrt(1.0 + layer.percent) * layer.finalPercent + layer.finalFlat

        // 标准公式：(base×(1+%))×最终%+最终
        else ->
            layer.base * (1.0 + layer.percent) * layer.finalPercent + layer.finalFlat
    }
}
