package com.tingyu.item.equip

import com.tingyu.player.job.Job
import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.inventory.EquipmentSlot

/**
 * 按职业×阶数×部位计算装备基础属性（公式化，不依赖静态表）。
 * 独立效果（单件 finalPercent 加成）也在此合并计算。
 */
object EquipFormula {

    fun compute(job: Job, tier: Int, slot: EquipmentSlot): Map<StatType, StatLayer> {
        val x = tier.toDouble()
        return when (job) {
            Job.WARRIOR   -> if (slot == EquipmentSlot.HAND) warriorWeapon(x) else warrior(x, slot)
            Job.RANGER    -> if (slot == EquipmentSlot.HAND) rangerWeapon(x)  else ranger(x, slot)
            Job.ALCHEMIST -> if (slot == EquipmentSlot.HAND) alchemistWeapon(x) else alchemist(x, slot)
            else          -> emptyMap()
        }
    }

    // ======================== 战士 ========================

    private fun warrior(x: Double, slot: EquipmentSlot): Map<StatType, StatLayer> {
        val map = mutableMapOf(
            StatType.HP               to StatLayer(base = 2*x + 0.1*x*x),
            StatType.ARMOR            to StatLayer(base = 1.2 + 0.8*x + 0.02*x*x),
            StatType.ARMOR_TOUGHNESS  to StatLayer(base = 1.0 + 0.2*x + 0.01*x*x),
            StatType.ATTACK           to StatLayer(base = 0.4*x + 0.01*x*x),
            StatType.ENCHANT_POWER    to StatLayer(base = 4.0 + x + x*x),
            StatType.MOVE_SPEED       to StatLayer(base = warriorSpeed(x, slot)),
        )
        if (slot == EquipmentSlot.CHEST)
            map[StatType.KNOCKBACK_RESIST] = StatLayer(base = 2.0 + 0.16*x)

        // 独立效果
        when (slot) {
            EquipmentSlot.HEAD  -> map[StatType.HP] =
                map[StatType.HP]!!.copy(finalPercent = (24.0 + 2*x) / 100.0)
            EquipmentSlot.CHEST -> {
                map[StatType.ARMOR]           = map[StatType.ARMOR]!!.copy(finalPercent = (16.0 + x) / 100.0)
                map[StatType.ARMOR_TOUGHNESS] = map[StatType.ARMOR_TOUGHNESS]!!.copy(finalPercent = (8.0 + 0.2*x) / 100.0)
            }
            EquipmentSlot.LEGS  -> map[StatType.KNOCKBACK_RESIST] =
                StatLayer(finalPercent = (32.0 + 2*x) / 100.0)
            EquipmentSlot.FEET  -> map[StatType.MOVE_SPEED] =
                map[StatType.MOVE_SPEED]!!.copy(finalPercent = (8.0 + 0.4*x) / 100.0)
            else -> {}
        }
        return map
    }

    private fun warriorSpeed(x: Double, slot: EquipmentSlot) = when (slot) {
        EquipmentSlot.HEAD  -> -0.0125
        EquipmentSlot.CHEST -> -0.02
        EquipmentSlot.LEGS  -> -0.0175
        EquipmentSlot.FEET  -> 0.01 + 0.002*x
        else -> 0.0
    }

    private fun warriorWeapon(x: Double) = mapOf(
        StatType.ATTACK        to StatLayer(base = 2.0 + 2.8*x + 0.2*x*x),
        StatType.ENCHANT_POWER to StatLayer(base = 4.0 + 2*x + x*x),
        StatType.KNOCKBACK_RESIST to StatLayer(base = 1.0 + 0.1*x),
    )

    // ======================== 游侠 ========================

    private fun ranger(x: Double, slot: EquipmentSlot): Map<StatType, StatLayer> {
        val map = mutableMapOf(
            StatType.HP               to StatLayer(base = 1.2*x + 0.04*x*x),
            StatType.ARMOR            to StatLayer(base = 1.0 + 0.5*x),
            StatType.ARMOR_TOUGHNESS  to StatLayer(base = 0.2 + 0.16*x),
            StatType.ATTACK           to StatLayer(base = 0.5*x + 0.02*x*x),
            StatType.ENCHANT_POWER    to StatLayer(base = 6.0 + 2*x + x*x),
            StatType.MOVE_SPEED       to StatLayer(base = rangerSpeed(x, slot)),
        )
        if (slot == EquipmentSlot.CHEST)
            map[StatType.KNOCKBACK_RESIST] = StatLayer(base = 1.0 + 0.04*x)

        when (slot) {
            EquipmentSlot.HEAD  -> map[StatType.HP] =
                map[StatType.HP]!!.copy(finalPercent = (12.0 + x) / 100.0)
            EquipmentSlot.CHEST -> map[StatType.ARMOR] =
                map[StatType.ARMOR]!!.copy(finalPercent = (4.0 + 0.2*x) / 100.0)
            EquipmentSlot.LEGS  -> map[StatType.KNOCKBACK_RESIST] =
                StatLayer(finalPercent = (12.0 + 0.2*x) / 100.0)
            EquipmentSlot.FEET  -> map[StatType.MOVE_SPEED] =
                map[StatType.MOVE_SPEED]!!.copy(finalPercent = (32.0 + 4*x) / 100.0)
            else -> {}
        }
        return map
    }

    private fun rangerSpeed(x: Double, slot: EquipmentSlot) = when (slot) {
        EquipmentSlot.HEAD  -> -0.0125
        EquipmentSlot.CHEST -> -0.02
        EquipmentSlot.LEGS  -> -0.0175
        EquipmentSlot.FEET  -> 0.04 + 0.005*x
        else -> 0.0
    }

    private fun rangerWeapon(x: Double) = mapOf(
        StatType.ATTACK           to StatLayer(base = 2.0 + 3.2*x + 0.2*x*x),
        StatType.ENCHANT_POWER    to StatLayer(base = 8.0 + 4*x + x*x),
        StatType.MOVE_SPEED       to StatLayer(base = 0.01 + 0.001*x),
        StatType.KNOCKBACK_RESIST to StatLayer(base = -0.5 - 0.1*x),
    )

    // ======================== 炼丹师 ========================

    private fun alchemist(x: Double, slot: EquipmentSlot): Map<StatType, StatLayer> {
        val map = mutableMapOf(
            StatType.HP               to StatLayer(base = 1.5*x + 0.06*x*x),
            StatType.ARMOR            to StatLayer(base = 0.8 + 0.6*x + 0.01*x*x),
            StatType.ARMOR_TOUGHNESS  to StatLayer(base = 0.2 + 0.1*x),
            StatType.ATTACK           to StatLayer(base = 0.4*x + 0.02*x*x),
            StatType.ENCHANT_POWER    to StatLayer(base = 8.0 + 2*x + x*x),
            StatType.MOVE_SPEED       to StatLayer(base = alchemistSpeed(x, slot)),
        )
        if (slot == EquipmentSlot.CHEST)
            map[StatType.KNOCKBACK_RESIST] = StatLayer(base = 1.2 + 0.1*x)

        when (slot) {
            EquipmentSlot.HEAD  -> map[StatType.HP] =
                map[StatType.HP]!!.copy(finalPercent = (16.0 + 1.2*x) / 100.0)
            EquipmentSlot.CHEST -> map[StatType.ARMOR] =
                map[StatType.ARMOR]!!.copy(finalPercent = (8.0 + 0.2*x) / 100.0)
            EquipmentSlot.LEGS  -> map[StatType.KNOCKBACK_RESIST] =
                StatLayer(finalPercent = (16.0 + x) / 100.0)
            EquipmentSlot.FEET  -> map[StatType.MOVE_SPEED] =
                map[StatType.MOVE_SPEED]!!.copy(finalPercent = (24.0 + 2*x) / 100.0)
            else -> {}
        }
        return map
    }

    private fun alchemistSpeed(x: Double, slot: EquipmentSlot) = when (slot) {
        EquipmentSlot.HEAD  -> -0.0125
        EquipmentSlot.CHEST -> -0.02
        EquipmentSlot.LEGS  -> -0.0175
        EquipmentSlot.FEET  -> 0.02 + 0.002*x
        else -> 0.0
    }

    private fun alchemistWeapon(x: Double) = mapOf(
        StatType.ATTACK           to StatLayer(base = 2.0 + 3.0*x + 0.2*x*x),
        StatType.ENCHANT_POWER    to StatLayer(base = 12.0 + 4*x + x*x),
        StatType.MOVE_SPEED       to StatLayer(base = -0.02 - 0.002*x),
        StatType.KNOCKBACK_RESIST to StatLayer(base = 1.0 + 0.1*x),
    )
}
