package com.tingyu.item.equip

import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.inventory.EquipmentSlot

/**
 * 每件装备的五行元素加成（单件基础属性）。
 * 千炼的独立被动效果也在此定义（per-slot）。
 */
object ElementBonus {

    /**
     * 单件元素基础属性（穿上任意一件该元素装备即生效）。
     * 返回的 StatLayer 叠加到 EquipFormula 结果上。
     */
    fun singlePiece(element: FiveElement, tier: Int, slot: EquipmentSlot): Map<StatType, StatLayer> {
        val x = tier.toDouble()
        return when (element) {
            FiveElement.METAL    -> metalSingle(x)
            FiveElement.WOOD     -> woodSingle(x)
            FiveElement.WATER    -> waterSingle(x)
            FiveElement.FIRE     -> fireSingle(x)
            FiveElement.EARTH    -> earthSingle(x)
            FiveElement.QIANLIAN -> qianlianSlot(x, slot)
        }
    }

    // ======================== 金 ========================
    // 进攻属性+1+0.2x（flat base）
    private fun metalSingle(x: Double) = mapOf(
        StatType.ATTACK to StatLayer(base = 1.0 + 0.2*x)
    )

    // ======================== 木 ========================
    // 生命上限+4+x%（percent）
    private fun woodSingle(x: Double) = mapOf(
        StatType.HP to StatLayer(percent = (4.0 + x) / 100.0)
    )

    // ======================== 水 ========================
    // 最终冷缩+(4+0.5x)%
    private fun waterSingle(x: Double) = mapOf(
        StatType.COOLDOWN_REDUCTION to StatLayer(finalPercent = (4.0 + 0.5*x) / 100.0)
    )

    // ======================== 火 ========================
    // 进攻属性+(8+2x)%（percent）
    private fun fireSingle(x: Double) = mapOf(
        StatType.ATTACK to StatLayer(percent = (8.0 + 2*x) / 100.0)
    )

    // ======================== 土 ========================
    // 盔甲值+(2+x)%，盔甲韧性+(1+0.5x)%
    private fun earthSingle(x: Double) = mapOf(
        StatType.ARMOR           to StatLayer(percent = (2.0 + x) / 100.0),
        StatType.ARMOR_TOUGHNESS to StatLayer(percent = (1.0 + 0.5*x) / 100.0),
    )

    // ======================== 千炼（按槽位）========================
    private fun qianlianSlot(x: Double, slot: EquipmentSlot): Map<StatType, StatLayer> = when (slot) {
        // 头：生命上限+16+2x%，移除进攻属性（attack base 归零）
        EquipmentSlot.HEAD  -> mapOf(
            StatType.HP     to StatLayer(finalPercent = (16.0 + 2*x) / 100.0),
            StatType.ATTACK to StatLayer(base = -(99999.0)),  // 标记移除，EquipManager 处理
        )
        // 胸：进攻属性+2+0.5x，移除生存属性
        EquipmentSlot.CHEST -> mapOf(
            StatType.ATTACK to StatLayer(base = 2.0 + 0.5*x),
        )
        // 腿：进攻属性+2+0.5x，移除生存属性
        EquipmentSlot.LEGS  -> mapOf(
            StatType.ATTACK to StatLayer(base = 2.0 + 0.5*x),
        )
        // 脚：进攻属性+2+0.5x
        EquipmentSlot.FEET  -> mapOf(
            StatType.ATTACK to StatLayer(base = 2.0 + 0.5*x),
        )
        else -> emptyMap()
    }

    // ======================== 千炼独立被动（触发型，供技能系统使用）========================
    // 以下为触发型被动，不作为静态 StatLayer，留给技能/被动系统实现：
    // 千炼头被动：获得12+1.2x%减伤，职业属性-12-x%
    // 千炼胸被动：职业属性+16+2x%，击退抗性+24+2x%，移动速度-12-x%
    // 千炼腿被动：职业属性+8+x%，移动速度+16+4x%，击退抗性-16-x%
    // 千炼脚被动：职业属性+8+x%，冷却缩减+8+0.8x%
    fun qianlianPassive(tier: Int, slot: EquipmentSlot): Map<StatType, StatLayer> {
        val x = tier.toDouble()
        return when (slot) {
            EquipmentSlot.HEAD  -> mapOf(
                StatType.JOB_POWER to StatLayer(percent = -(12.0 + x) / 100.0),
            )
            EquipmentSlot.CHEST -> mapOf(
                StatType.JOB_POWER        to StatLayer(finalPercent = (16.0 + 2*x) / 100.0),
                StatType.KNOCKBACK_RESIST to StatLayer(finalPercent = (24.0 + 2*x) / 100.0),
                StatType.MOVE_SPEED       to StatLayer(percent = -(12.0 + x) / 100.0),
            )
            EquipmentSlot.LEGS  -> mapOf(
                StatType.JOB_POWER        to StatLayer(finalPercent = (8.0 + x) / 100.0),
                StatType.MOVE_SPEED       to StatLayer(finalPercent = (16.0 + 4*x) / 100.0),
                StatType.KNOCKBACK_RESIST to StatLayer(percent = -(16.0 + x) / 100.0),
            )
            EquipmentSlot.FEET  -> mapOf(
                StatType.JOB_POWER          to StatLayer(finalPercent = (8.0 + x) / 100.0),
                StatType.COOLDOWN_REDUCTION to StatLayer(finalPercent = (8.0 + 0.8*x) / 100.0),
            )
            else -> emptyMap()
        }
    }
}
