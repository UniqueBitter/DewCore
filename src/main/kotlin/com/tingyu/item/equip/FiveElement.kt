package com.tingyu.item.equip

/**
 * 五行枚举，包含相生相克关系。
 *
 * 相生：木→火→土→金→水→木
 * 相克：木克土，土克水，水克火，火克金，金克木
 */
enum class FiveElement(val displayName: String, val color: String) {
    METAL ("金", "§e"),
    WOOD  ("木", "§a"),
    WATER ("水", "§b"),
    FIRE  ("火", "§c"),
    EARTH ("土", "§6"),
    QIANLIAN("千炼", "§d");

    /** 我所生的元素 */
    fun generates(): FiveElement? = when (this) {
        METAL    -> WATER
        WOOD     -> FIRE
        WATER    -> WOOD
        FIRE     -> EARTH
        EARTH    -> METAL
        QIANLIAN -> null
    }

    /** 我所克的元素 */
    fun overcomes(): FiveElement? = when (this) {
        METAL    -> WOOD
        WOOD     -> EARTH
        WATER    -> FIRE
        FIRE     -> METAL
        EARTH    -> WATER
        QIANLIAN -> null
    }

    /** 生我的元素 */
    fun generatedBy(): FiveElement? = entries.find { it.generates() == this }

    /** 克我的元素 */
    fun overcomeBy(): FiveElement? = entries.find { it.overcomes() == this }
}
