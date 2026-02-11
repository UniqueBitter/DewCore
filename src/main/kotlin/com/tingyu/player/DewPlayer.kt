package com.tingyu.player

import com.tingyu.player.job.Job
import com.tingyu.player.race.Race

/**
 * 玩家数据类
 *
 * 规则：
 * - 普通字段 → Gson 自动存盘/读盘（加新字段只改这里，给默认值即可）
 * - @Transient 字段 → 不存盘，每次 update 重算
 */
data class DewPlayer(
    val uuid: String,
    val name: String,
    var race: Race = Race.NONE,
    var job: Job = Job.NONE,
    var copper: Long = 0,
    var element: Long = 0,
    var level: Int = 1,
    var exp: Long = 0,

    // ===== 基础属性（存盘） =====
    var baseMaxHealth: Double = 20.0,
    var baseAtk: Double = 5.0,
    var baseArmor: Double = 0.0,
    var baseCrit: Double = 5.0,
    var baseCritDamage: Double = 150.0,
    var baseSpeed: Double = 0.0,
    var baseAtkSpeed: Double = 4.0,

    // 以后要加新属性？直接在这里加一行就完事，别的文件不用动
    // var baseXXX: Double = 0.0,
) {
    // ===== 最终计算值（不存盘，update 时覆盖） =====
    @Transient var maxHealth: Double = baseMaxHealth
    @Transient var atk: Double = baseAtk
    @Transient var armor: Double = baseArmor
    @Transient var crit: Double = baseCrit
    @Transient var critDamage: Double = baseCritDamage
    @Transient var speed: Double = baseSpeed
    @Transient var atkSpeed: Double = baseAtkSpeed
}