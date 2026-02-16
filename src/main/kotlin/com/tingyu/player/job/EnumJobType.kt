package com.tingyu.player.job

import com.tingyu.common.interfaces.Displayable
import com.tingyu.common.interfaces.Validatable

/**
 * 玩家职业类型枚举
 *
 * @property displayName 游戏内显示名称
 * @property displayColor 游戏内显示颜色
 */
enum class Job(
    override val displayName: String,
    override val displayColor: String
) : Displayable, Validatable {
    WARRIOR("战士", "§c"),

    RANGER("游侠", "§a"),

    ALCHEMIST("炼丹", "§6"),

    NONE("无职业", "§7");

    /**
     * 是否为有效职业（排除 [NONE]）
     */
    override fun isValid(): Boolean = this != NONE

    companion object {
        /** 通过显示名称查找职业 */
        fun fromDisplayName(name: String): Job? =
            entries.find { it.displayName == name }
    }
}