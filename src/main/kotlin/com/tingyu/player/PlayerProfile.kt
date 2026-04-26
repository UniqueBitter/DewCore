package com.tingyu.player

import com.tingyu.player.job.Job
import com.tingyu.player.race.Race
import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import java.util.UUID

/**
 * 玩家档案，存储职业/种族/等级以及各属性的基础值。
 *
 * 基础值来源于升级、职业、种族加成，装备加成在计算时临时叠加，不写入此处。
 */
data class PlayerProfile(
    val uuid: UUID,
    var job: Job = Job.NONE,
    var race: Race = Race.NONE,
    var level: Int = 1,
    var jobPromotion: Int = 0,
    val baseStats: MutableMap<StatType, Double> = mutableMapOf()
) {
    fun getBase(type: StatType): Double = baseStats.getOrDefault(type, 0.0)

    fun setBase(type: StatType, value: Double) {
        baseStats[type] = value
    }

    /**
     * 构建该属性的基础 [StatLayer]。
     * finalPercent 固定为 1.0（100% 基准），装备在此之上叠加 delta。
     */
    fun buildLayer(type: StatType): StatLayer = StatLayer(
        base = getBase(type),
        finalPercent = 1.0
    )
}
