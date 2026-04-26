package com.tingyu.map.spawn

/**
 * 区域内的一个怪物刷新点。
 *
 * @param zoneId       所属区域 id
 * @param mobId        MobRegistry 中的怪物 id
 * @param x/y/z        生成坐标
 * @param world        世界名
 * @param maxCount     该点最多同时存在的怪物数
 * @param respawnTicks 上一只死亡后多少 tick 再刷新（200 = 10s）
 */
data class ZoneSpawn(
    val id: String,
    val zoneId: String,
    val mobId: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val world: String,
    val maxCount: Int = 1,
    val respawnTicks: Long = 200L
)
