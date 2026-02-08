package com.tingyu.player

import com.tingyu.player.job.Job
import com.tingyu.player.race.Race

/**
 * 玩家数据类
 */
data class DewPlayer(
    val uuid: String,                   // 玩家UUID，不可变
    val name: String,                   // 玩家名字
    var race: Race = Race.NONE,         // 种族
    var job: Job = Job.NONE,            // 职业
    var copper: Long = 0,               // 铜币
    var element: Long = 0,              // 元素
    var maxHelth: Double = 20.00,       // 最大生命
    var nowHealth: Double = 20.00,      // 当前生命
    var level: Int = 1,                 // 等级
    var exp: Long = 0,                  // 经验值
    var atk: Double = 0.00,             // 攻击力
    var armor: Double = 0.00,            // 护甲
    )