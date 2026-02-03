package com.tingyu.common.database

import java.util.UUID

/**
 * 玩家数据封装 - 6个核心属性
 */
data class PlayerData(
    val uuid: UUID,
    val name: String,
    var health: Double = 100.0,        // 生命值 - 能挨几下
    var attack: Double = 10.0,         // 攻击力 - 能打多疼
    var defense: Double = 5.0,         // 防御力 - 少受多少伤
    var attackSpeed: Double = 1.0,     // 攻击速度 - 出手多快
    var moveSpeed: Double = 1.0,       // 移动速度 - 跑多快
    var mana: Double = 100.0           // 灵气/法力 - 放技能用
)
