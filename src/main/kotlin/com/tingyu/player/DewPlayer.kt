package com.tingyu.player

import com.tingyu.player.job.Job
import com.tingyu.player.race.Race

/**
 * 玩家数据类
 */
data class DewPlayer(
    val uuid: String,       // 玩家UUID，不可变
    var name: String,       // 玩家名字
    var race: Race = Race.NONE,
    var job: Job = Job.NONE,
    var copper: Long = 0,    // 铜币
    var element: Long = 0     // 元素
)