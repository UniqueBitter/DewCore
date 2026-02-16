package com.tingyu.player.race

import com.tingyu.common.interfaces.Displayable
import com.tingyu.common.interfaces.Validatable

/**
 * 玩家种族类型枚举
 *
 * @property displayName 游戏内显示名称
 * @property displayColor 游戏内显示颜色
 */
enum class Race(
    override val displayName: String,
    override val displayColor: String
) : Displayable, Validatable {
    CELESTIAL("神族", "§b"),

    HUMAN("人族", "§6"),

    IMMORTAL("仙族", "§3"),

    YAOGUAI("妖族", "§a"),

    WARGOD("战神族", "§c"),

    NONE("无种族", "§7");

    /**
     * 是否为有效种族（排除 [NONE]）
     *
     * @return
     * - `true` 为有效种族（[CELESTIAL], [HUMAN], [IMMORTAL] 等）
     * - `false` 为无效种族（仅 [NONE]）
     */
    override fun isValid(): Boolean = this != NONE

    companion object {
        /** 通过显示名称查找种族 */
        fun fromDisplayName(name: String): Race? =
            entries.find { it.displayName == name }
    }
}