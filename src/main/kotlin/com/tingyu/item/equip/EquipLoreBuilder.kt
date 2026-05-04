package com.tingyu.item.equip

import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.inventory.EquipmentSlot

object EquipLoreBuilder {

    private const val SEP = "§8§m──────────────────"
    private val IS_WEAPON = setOf(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND)

    fun build(data: EquipData): List<String> {
        val lines = mutableListOf<String>()

        // ── 头部：槽位 · 职业 · 元素 · 等阶 ─────────────────────────
        val jobStr = if (data.job == null) "§7全职业" else "${data.job.displayColor}${data.job.displayName}"
        val slotLabel = if (data.slot in IS_WEAPON) "武器" else slotName(data.slot)
        lines += "§8[$slotLabel] $jobStr  ${data.element.color}${data.element.displayName}§7元素  §7第§f${toRoman(data.tier)}§7阶"
        lines += SEP

        // ── 描述（可选）────────────────────────────────────────────
        if (data.description.isNotBlank()) {
            data.description.split("\n").forEach { lines += "§7§o$it" }
            lines += SEP
        }

        // ── 等阶 / 职业 / 阅历 / 词条 / 稀有度 ──────────────────────
        val tierLabel = if (data.slot in IS_WEAPON) "武器" else "装备"
        val tierLine = buildString {
            append("§7${tierLabel}等阶: §f${toRoman(data.tier)}")
            if (data.tierDescription.isNotBlank()) append("  §8${data.tierDescription}")
        }
        lines += tierLine

        val expStr = if (data.expRequired > 0) "  §7阅历需求: §f${data.expRequired}" else ""
        if (data.job != null) {
            lines += "§7限制职业: ${data.job.displayColor}[${data.job.displayName}]$expStr"
        } else if (expStr.isNotBlank()) {
            lines += "§7全职业$expStr"
        }

        if (data.affixes.isNotEmpty()) {
            val label = if (data.slot in IS_WEAPON) "§8武器魔咒  " else ""
            lines += "$label${data.affixes.joinToString("  ") { it.display() }}"
        }

        lines += rarityFromTier(data.tier)
        lines += SEP

        // ── 武器分支效果（武器专用）──────────────────────────────────
        if (data.slot in IS_WEAPON && data.branchEffect.isNotEmpty()) {
            lines += "§e武器分支效果"
            data.branchEffect.forEach { lines += "  §7$it" }
            lines += SEP
        }

        // ── 装备基础属性 ──────────────────────────────────────────────
        lines += if (data.slot in IS_WEAPON) "§e武器基础属性" else "§e装备基础属性"
        val baseStats = if (data.job != null) EquipFormula.compute(data.job, data.tier, data.slot) else emptyMap()
        if (baseStats.isEmpty()) {
            lines += "  §8(无基础属性)"
        } else {
            baseStats.entries.sortedBy { it.key.ordinal }.forEach { (type, layer) ->
                renderLayer(type, layer, lines)
            }
        }
        lines += SEP

        // ── 装备元素独立效果 ──────────────────────────────────────────
        lines += "§e装备元素独立效果"
        lines += "  ${ElementLore.singleLine(data.element, data.tier)}"
        data.uniqueEffect.forEach { lines += "  §8$it" }
        lines += SEP

        // ── 装备元素特殊效果 ──────────────────────────────────────────
        val specialLines = ElementLore.specialLines(data.element, data.tier)
        val passiveLines = if (data.slot in IS_WEAPON) data.passiveEffect else emptyList()
        if (specialLines.isNotEmpty() || passiveLines.isNotEmpty()) {
            lines += "§e装备元素特殊效果"
            specialLines.forEach { lines += "  $it" }
            passiveLines.forEach { lines += "  §7$it" }
            lines += SEP
        }

        // ── 装备元素套装效果 ──────────────────────────────────────────
        lines += "§e装备元素套装效果"
        ElementLore.resonanceLines(data.element, data.tier).forEach { lines += "  $it" }
        lines += SEP

        // ── 武器载灵（可选）──────────────────────────────────────────
        if (data.slot in IS_WEAPON && data.spiritSlots.isNotEmpty()) {
            lines += "§e武器载灵"
            data.spiritSlots.forEach { lines += "  ${it.display()}" }
            lines += SEP
        }

        // ── 锻造者（可选）────────────────────────────────────────────
        if (data.crafter.isNotBlank()) {
            lines += "§8锻造者: §7${data.crafter}"
        }

        return lines
    }

    // ======================== 稀有度 ========================

    private fun rarityFromTier(tier: Int): String = when (tier) {
        1    -> "§f稀有度:★"
        2    -> "§a稀有度:★★"
        3    -> "§9稀有度:★★★"
        4    -> "§5稀有度:★★★★"
        5    -> "§e稀有度:★★★★★"
        6    -> "§4稀有度:★★★★★★"
        else -> "§7稀有度:${tier}阶"
    }

    // ======================== 属性渲染 ========================

    private fun renderLayer(type: StatType, layer: StatLayer, out: MutableList<String>) {
        val name = "  §7${type.displayName.padEnd(6)}"
        if (layer.base != 0.0)         out += "$name  ${numStr(layer.base)}"
        if (layer.percent != 0.0)      out += "$name  ${pctStr(layer.percent)} §8百分"
        if (layer.finalPercent != 0.0) out += "$name  ${pctStr(layer.finalPercent)} §8最终"
        if (layer.finalFlat != 0.0)    out += "$name  ${numStr(layer.finalFlat)} §8额外"
    }

    private fun numStr(v: Double): String {
        val color = if (v >= 0) "§a" else "§c"
        val sign  = if (v >= 0) "+" else ""
        return "$color$sign${fmt(v)}"
    }

    private fun pctStr(v: Double): String {
        val color = if (v >= 0) "§e" else "§c"
        val sign  = if (v >= 0) "+" else ""
        return "$color$sign${pct(v)}"
    }

    // ======================== 工具 ========================

    private fun slotName(slot: EquipmentSlot) = when (slot) {
        EquipmentSlot.HEAD  -> "头盔"
        EquipmentSlot.CHEST -> "胸甲"
        EquipmentSlot.LEGS  -> "护腿"
        EquipmentSlot.FEET  -> "靴子"
        else                -> "装备"
    }

    private fun toRoman(n: Int) = when (n) {
        1 -> "I"; 2 -> "II"; 3 -> "III"; 4 -> "IV"; 5 -> "V"; 6 -> "VI"
        else -> n.toString()
    }

    private fun fmt(v: Double): String =
        if (v % 1.0 == 0.0) v.toLong().toString() else "%.2f".format(v)

    private fun pct(v: Double): String = "%.1f%%".format(v * 100.0)
}
