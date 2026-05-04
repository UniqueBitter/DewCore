package com.tingyu.item.equip

enum class AffixCategory(val displayName: String, val color: String) {
    SHEN_YUN ("神韵", "§d"),
    ZHUANG_JIA("装甲", "§a"),
    LIU_GUANG ("流光", "§b"),
    HUN_LIAN  ("魂炼", "§c"),
    LING_QI   ("灵器", "§e"),
}

/** 装备词条（显示用；实际属性效果待技能系统完成后接入） */
data class Affix(
    val category: AffixCategory,
    val name: String,
    val level: Int   // 1–6 → I–VI
) {
    fun display(): String = "${category.color}[${category.displayName}] §f$name${toRoman(level)}"

    companion object {
        fun toRoman(n: Int) = when (n) {
            1 -> "I"; 2 -> "II"; 3 -> "III"; 4 -> "IV"; 5 -> "V"; 6 -> "VI"
            else -> n.toString()
        }
    }
}
