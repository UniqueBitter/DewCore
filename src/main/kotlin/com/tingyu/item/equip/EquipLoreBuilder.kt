package com.tingyu.item.equip

import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.inventory.EquipmentSlot

/**
 * 根据 EquipData 计算并生成装备 lore。
 * 所有数值来自 EquipStatTable，与实际生效属性保持一致。
 */
object EquipLoreBuilder {

    fun build(data: EquipData): List<String> {
        val lines = mutableListOf<String>()

        val jobStr = if (data.job == null) "§7全职业" else "${data.job.displayColor}${data.job.displayName}"
        lines += "§8[${slotName(data.slot)}] $jobStr  ${data.element.color}${data.element.displayName}§8元素  §7第§f${data.tier}§7阶"
        lines += "§8──────────────────"

        val stats = EquipStatTable.compute(data.job, data.tier, data.slot, data.element)
        if (stats.isEmpty()) {
            lines += "§8(无属性)"
        } else {
            for ((type, layer) in stats.entries.sortedBy { it.key.ordinal }) {
                val line = formatStat(type, layer) ?: continue
                lines += line
            }
        }

        lines += "§8──────────────────"
        return lines
    }

    private fun formatStat(type: StatType, layer: StatLayer): String? {
        val parts = mutableListOf<String>()
        if (layer.base != 0.0)         parts += numStr(layer.base, "基础")
        if (layer.percent != 0.0)      parts += pctStr(layer.percent, "百分")
        if (layer.finalPercent != 0.0) parts += pctStr(layer.finalPercent, "最终")
        if (layer.finalFlat != 0.0)    parts += numStr(layer.finalFlat, "额外")
        if (parts.isEmpty()) return null
        return "§7${type.displayName}  ${parts.joinToString("  ")}"
    }

    private fun numStr(v: Double, label: String): String {
        val color = if (v >= 0) "§a" else "§c"
        val sign  = if (v >= 0) "+" else ""
        return "$color$sign${fmt(v)} §8$label"
    }

    private fun pctStr(v: Double, label: String): String {
        val color = if (v >= 0) "§e" else "§c"
        val sign  = if (v >= 0) "+" else ""
        return "$color$sign${pct(v)} §8$label"
    }

    private fun slotName(slot: EquipmentSlot) = when (slot) {
        EquipmentSlot.HEAD  -> "头盔"
        EquipmentSlot.CHEST -> "胸甲"
        EquipmentSlot.LEGS  -> "护腿"
        EquipmentSlot.FEET  -> "靴子"
        EquipmentSlot.HAND  -> "武器"
        else                -> "装备"
    }

    private fun fmt(v: Double): String =
        if (v % 1.0 == 0.0) v.toLong().toString()
        else String.format("%.2f", v)

    private fun pct(v: Double): String = String.format("%.1f%%", v * 100.0)
}
