package com.tingyu.item.equip

/**
 * 装备 lore 中元素描述文字生成（L = tier）。
 *
 * singleLine  → 独立效果段（单件属性）
 * specialLines → 特殊效果段（元素被动关键字）
 * resonanceLines → 套装效果段（双生/众生/全效）
 */
object ElementLore {

    // ======================== 独立效果（单件属性） ========================

    fun singleLine(element: FiveElement, tier: Int): String {
        val x = tier.toDouble()
        return when (element) {
            FiveElement.METAL    -> "§7进攻属性  §a+${fmt(1.0 + 0.2*x)}"
            FiveElement.WOOD     -> "§7生命上限  §e+${fmt(4.0 + x)}%"
            FiveElement.WATER    -> "§7冷却缩减  §e+${fmt(4.0 + 0.5*x)}%"
            FiveElement.FIRE     -> "§7进攻属性  §e+${fmt(8.0 + 2*x)}%"
            FiveElement.EARTH    -> "§7盔甲值 §e+${fmt(2.0 + x)}%  §7护甲韧性 §e+${fmt(1.0 + 0.5*x)}%"
            FiveElement.QIANLIAN -> "§7全属性强化  §d千炼"
        }
    }

    // ======================== 特殊效果（元素被动关键字） ========================

    fun specialLines(element: FiveElement, tier: Int): List<String> {
        val x = tier.toDouble()
        return when (element) {
            FiveElement.METAL -> listOf(
                "§7伤害数值 §a+24%  被动cd §a+8%",
                "§8被动: §7属性无波动",
            )
            FiveElement.WOOD -> listOf(
                "§7全数值 §a+8%",
                "§8被动: §7持续回复生命",
            )
            FiveElement.WATER -> listOf(
                "§7被动cd §a-12%",
                "§8被动: §7cd溢出转化为冷却缩减",
            )
            FiveElement.FIRE -> listOf(
                "§7进攻属性 §a+4%",
                "§8被动: §7叠层提升伤害",
            )
            FiveElement.EARTH -> listOf(
                "§7生存数值 §a+4%",
                "§8被动: §7承伤后触发护盾",
            )
            FiveElement.QIANLIAN -> listOf(
                "§7全数值 §a+16%",
                "§8被动: §7千炼强化所有属性",
            )
        }
    }

    // ======================== 套装效果（双生/众生/全效） ========================

    fun resonanceLines(element: FiveElement, tier: Int): List<String> {
        val x = tier.toDouble()
        return when (element) {
            FiveElement.METAL -> listOf(
                "§8双生共鸣  §8cd16s: §7释放技能后 进攻属性 §e+32%§7，持续4s",
                "§8众生共鸣  §8cd16s: §7释放技能后 进攻属性 §e+${fmt(48.0+4*x)}%§7，持续4s",
                "§8全效共鸣  §7最终伤害 §e+${fmt(8.0+x)}%",
            )
            FiveElement.WOOD -> listOf(
                "§8双生共鸣  §7全数值 §a+8%",
                "§8众生共鸣  §7全数值 §a+16%  每秒回复 §a0.5% §7最大生命",
                "§8全效共鸣  §7每秒回复 §a${fmt(1.0+0.1*x)}% §7最大生命",
            )
            FiveElement.WATER -> listOf(
                "§8双生共鸣  §7被动cd §a-12%",
                "§8众生共鸣  §7被动cd §a-24%  技能效果 §a+${fmt(8.0+x)}%",
                "§8全效共鸣  §7技能效果 §a+${fmt(16.0+2*x)}%",
            )
            FiveElement.FIRE -> listOf(
                "§8双生共鸣  §7进攻属性 §e+8%",
                "§8众生共鸣  §8cd12s: §7命中后 进攻属性叠层 §e+${fmt(4.0+0.5*x)}%",
                "§8全效共鸣  §7最终伤害 §e+${fmt(12.0+x)}%",
            )
            FiveElement.EARTH -> listOf(
                "§8双生共鸣  §7生存数值 §a+8%",
                "§8众生共鸣  §7生存数值 §a+16%  承伤减伤 §a+${fmt(4.0+0.5*x)}%",
                "§8全效共鸣  §7减伤数值 §a+${fmt(8.0+x)}%",
            )
            FiveElement.QIANLIAN -> listOf(
                "§8双生共鸣  §7全数值 §a+16%",
            )
        }
    }

    private fun fmt(v: Double): String =
        if (v % 1.0 == 0.0) v.toLong().toString() else "%.2f".format(v)
}
