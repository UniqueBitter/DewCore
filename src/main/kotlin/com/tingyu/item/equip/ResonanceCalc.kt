package com.tingyu.item.equip

import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType

/**
 * 套装共鸣与五行相生相克修正计算。
 *
 * 调用方传入当前装备的元素列表（4 个护甲槽 + 武器槽，可为 null），
 * 返回额外叠加的 StatLayer 修正。
 */
object ResonanceCalc {

    /** 全属性类型列表（用于"全数值"/"生存数值"等群体加成） */
    private val ALL_STATS   = StatType.entries
    private val SURVIVAL    = listOf(StatType.HP, StatType.ARMOR, StatType.ARMOR_TOUGHNESS, StatType.KNOCKBACK_RESIST)
    private val DAMAGE      = listOf(StatType.ATTACK, StatType.JOB_POWER, StatType.SKILL_EFFECT)

    /**
     * 计算所有共鸣与相生相克加成。
     *
     * @param equipped    装备槽上的 (元素, 阶数) 对，null 代表该槽空
     * @return 额外 StatLayer 修正（每种 StatType 一个）
     */
    fun compute(
        equipped: List<Pair<FiveElement, Int>?>
    ): Map<StatType, StatLayer> {
        val result = mutableMapOf<StatType, StatLayer>()

        val valid = equipped.filterNotNull()
        val elements = valid.map { it.first }

        // 1. 同元素共鸣（同元素件数 → 共鸣级别）
        val elementCount = elements.groupingBy { it }.eachCount()
        for ((element, count) in elementCount) {
            if (element == FiveElement.QIANLIAN) {
                qianlianResonance(count, valid, result)
                continue
            }
            // 取该元素最高阶数（用于有 x 的公式）
            val maxTier = valid.filter { it.first == element }.maxOf { it.second }.toDouble()
            resonanceBonus(element, count, maxTier, result)
        }

        // 2. 五行相生相克修正（针对非千炼元素）
        val fiveElements = elements.filter { it != FiveElement.QIANLIAN }.toSet()
        for (elem in fiveElements) {
            val gen  = elem.generatedBy()   // 生我的元素
            val over = elem.overcomeBy()    // 克我的元素

            if (gen != null && gen in fiveElements) {
                // 相邻的装备被生 → 最终基础属性+8%
                for (type in ALL_STATS) {
                    result[type] = (result[type] ?: StatLayer()) + StatLayer(finalPercent = 0.08)
                }
            }
            if (over != null && over in fiveElements) {
                // 相邻的装备被克 → 各元素有不同惩罚
                overcomeDebuff(elem, result)
            }
        }

        return result
    }

    // ======================== 五行共鸣 ========================

    private fun resonanceBonus(
        element: FiveElement,
        count: Int,
        x: Double,
        out: MutableMap<StatType, StatLayer>
    ) {
        when (element) {
            FiveElement.METAL -> when {
                count >= 4 -> // 完整共鸣：最终伤害+8+x%
                    addAll(DAMAGE, StatLayer(finalPercent = (8.0 + x) / 100.0), out)
                // 双生/三生：触发型（cd16s），静态部分无加成，留给技能系统
                count >= 2 -> {
                    // 相邻套装效果：伤害数值+24%，被动cd+8%
                    addAll(DAMAGE, StatLayer(percent = 0.24), out)
                }
            }
            FiveElement.WOOD -> when {
                count >= 4 -> // 完整共鸣：每秒自然恢复，留给技能系统，此处暂空
                    Unit
                count >= 2 -> // 相邻套装效果：全数值+8%
                    addAll(ALL_STATS, StatLayer(percent = 0.08), out)
            }
            FiveElement.WATER -> when {
                count >= 4 -> // 完整共鸣：触发型，留给技能系统
                    Unit
                count >= 2 -> // 相邻套装效果：被动cd-12%（用 COOLDOWN_REDUCTION 表达）
                    add(StatType.COOLDOWN_REDUCTION, StatLayer(percent = 0.12), out)
            }
            FiveElement.FIRE -> when {
                count >= 4 -> // 完整共鸣：触发型，留给技能系统
                    Unit
                count >= 3 -> // 三生：进攻属性+12+x%
                    add(StatType.ATTACK, StatLayer(percent = (12.0 + x) / 100.0), out)
                count >= 2 -> // 双生：进攻属性+8%
                    add(StatType.ATTACK, StatLayer(percent = 0.08), out)
                count >= 1 -> // 独立：进攻属性+4%
                    add(StatType.ATTACK, StatLayer(percent = 0.04), out)
            }
            FiveElement.EARTH -> when {
                count >= 4 -> {
                    // 完整共鸣：抗性等级+1，最终减伤+x%（此处用 KNOCKBACK_RESIST 近似）
                    add(StatType.KNOCKBACK_RESIST, StatLayer(finalPercent = x / 100.0), out)
                    addAll(SURVIVAL, StatLayer(finalPercent = 0.01), out)
                }
                count >= 3 -> // 三生：减伤12%（HP/ARMOR 近似）
                    addAll(SURVIVAL, StatLayer(percent = 0.12), out)
                count >= 2 -> // 双生：减伤8%
                    addAll(SURVIVAL, StatLayer(percent = 0.08), out)
                count >= 1 -> // 独立：减伤4%
                    addAll(SURVIVAL, StatLayer(percent = 0.04), out)
            }
            else -> {}
        }
    }

    // ======================== 千炼共鸣 ========================

    private fun qianlianResonance(
        count: Int,
        valid: List<Pair<FiveElement, Int>>,
        out: MutableMap<StatType, StatLayer>
    ) {
        if (count >= 2) {
            // 相邻套装效果：全数值+16%
            addAll(ALL_STATS, StatLayer(percent = 0.16), out)
        }
    }

    // ======================== 被克惩罚 ========================

    private fun overcomeDebuff(elem: FiveElement, out: MutableMap<StatType, StatLayer>) {
        when (elem) {
            FiveElement.METAL -> { // 被火克：最终基础属性-24%
                addAll(ALL_STATS, StatLayer(finalPercent = -0.24), out)
            }
            FiveElement.WOOD  -> { // 被金克：最终基础属性-16%，生存数值-8%
                addAll(ALL_STATS, StatLayer(finalPercent = -0.16), out)
                addAll(SURVIVAL, StatLayer(percent = -0.08), out)
            }
            FiveElement.WATER -> { // 被土克：最终基础属性-32%，被动cd减少8%
                addAll(ALL_STATS, StatLayer(finalPercent = -0.32), out)
                add(StatType.COOLDOWN_REDUCTION, StatLayer(percent = -0.08), out)
            }
            FiveElement.FIRE  -> { // 被水克：最终基础属性-24%
                addAll(ALL_STATS, StatLayer(finalPercent = -0.24), out)
            }
            FiveElement.EARTH -> { // 被木克：最终基础属性-16%，盔甲属性-12%
                addAll(ALL_STATS, StatLayer(finalPercent = -0.16), out)
                add(StatType.ARMOR, StatLayer(percent = -0.12), out)
            }
            else -> {}
        }
    }

    // ======================== 工具 ========================

    private fun add(type: StatType, layer: StatLayer, out: MutableMap<StatType, StatLayer>) {
        out[type] = (out[type] ?: StatLayer()) + layer
    }

    private fun addAll(types: Iterable<StatType>, layer: StatLayer, out: MutableMap<StatType, StatLayer>) {
        types.forEach { add(it, layer, out) }
    }
}
