package com.tingyu.player

import com.tingyu.player.job.Job
import com.tingyu.player.race.Race

/**
 * 玩家数据类
 *
 * 作用：存放单个玩家的所有游戏数据
 *
 * 为什么需要这个类？
 * - Bukkit 的 Player 对象只有原版数据（血量、位置等）
 * - 我们需要存储自定义数据（种族、等级、金币等）
 * - DewPlayer 就是用来装这些自定义数据的容器
 *
 * 生命周期：
 * - 玩家进服时创建
 * - 游戏过程中一直在内存里
 * - 玩家退服时保存到数据库，然后销毁
 */
class DewPlayer(
    val uuid: String,
    var name: String,
    var race: Race = Race.NONE,
    var job: Job = Job.NONE,
    var copper: Long = 0
) {
}