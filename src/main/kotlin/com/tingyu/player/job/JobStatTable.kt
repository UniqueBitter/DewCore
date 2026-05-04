package com.tingyu.player.job

import com.tingyu.player.stat.StatType

object JobStatTable {

    private data class Growth(val base: Double, val perLevel: Double)

    private val table: Map<Job, Map<StatType, Growth>> = mapOf(
        Job.WARRIOR to mapOf(
            StatType.HP              to Growth(80.0,  20.0),
            StatType.ATTACK          to Growth(10.0,   5.0),
            StatType.ARMOR           to Growth(5.0,    2.0),
            StatType.ARMOR_TOUGHNESS to Growth(2.0,    1.0),
            StatType.KNOCKBACK       to Growth(0.5,    0.1),
        ),
        Job.RANGER to mapOf(
            StatType.HP              to Growth(60.0,  15.0),
            StatType.ATTACK          to Growth(8.0,    4.0),
            StatType.ARROW_SPEED     to Growth(0.5,    0.5),
            StatType.ATTACK_SPEED    to Growth(0.2,    0.1),
            StatType.MOVE_SPEED      to Growth(0.02,   0.005),
        ),
        Job.ALCHEMIST to mapOf(
            StatType.HP              to Growth(50.0,  12.0),
            StatType.ATTACK          to Growth(5.0,    2.0),
            StatType.SKILL_EFFECT    to Growth(5.0,    5.0),
            StatType.RECOVERY        to Growth(2.0,    2.0),
        ),
        Job.NONE to mapOf(
            StatType.HP              to Growth(20.0,   5.0),
            StatType.ATTACK          to Growth(3.0,    1.0),
        )
    )

    /** 返回 (job, level) 应写入 baseStats 的数值，等级从 1 起算 */
    fun compute(job: Job, level: Int): Map<StatType, Double> =
        table[job]?.mapValues { (_, g) -> g.base + (level - 1) * g.perLevel }
            ?: emptyMap()
}
