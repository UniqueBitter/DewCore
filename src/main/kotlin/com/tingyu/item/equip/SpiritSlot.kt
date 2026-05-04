package com.tingyu.item.equip

enum class SpiritSlotType(val displayName: String, val color: String) {
    ATTACK ("攻灵", "§c"),
    DEFENSE("防灵", "§a"),
    SPEED  ("速灵", "§e"),
    SPECIAL("奇灵", "§d"),
}

/**
 * 武器载灵槽（技能宝石）。
 * gemName 为空表示未嵌入，非空时显示宝石名称。
 */
data class SpiritSlot(
    val type: SpiritSlotType,
    val gemName: String = ""
) {
    fun display(): String {
        val slotLabel = "§8[${type.color}${type.displayName}§8]"
        return if (gemName.isBlank()) "$slotLabel §8(空)"
        else "$slotLabel §f$gemName"
    }
}
