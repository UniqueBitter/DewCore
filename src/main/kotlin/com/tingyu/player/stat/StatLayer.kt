package com.tingyu.player.stat

/**
 * 属性四层结构，对应公式: (基础 × (1+百分比%)) × 最终百分比% + 最终
 *
 * @param base        基础加成（加在公式的基础值上）
 * @param percent     百分比加成（如 0.10 代表 +10%）
 * @param finalPercent 最终百分比乘数（基础层为 1.0，装备贡献 delta，如 +5% → 0.05）
 * @param finalFlat   最终固定加成
 */
data class StatLayer(
    val base: Double = 0.0,
    val percent: Double = 0.0,
    val finalPercent: Double = 0.0,
    val finalFlat: Double = 0.0
) {
    operator fun plus(other: StatLayer) = StatLayer(
        base + other.base,
        percent + other.percent,
        finalPercent + other.finalPercent,
        finalFlat + other.finalFlat
    )
}
