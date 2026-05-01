package com.tingyu.item

import com.tingyu.command.DewItem
import com.tingyu.item.equip.EquipData
import com.tingyu.item.equip.EquipLoreBuilder
import com.tingyu.item.equip.FiveElement
import com.tingyu.item.util.ForgeRecipe.hideAllCompat
import com.tingyu.player.job.Job
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object Armor {

    lateinit var WarriorChestMetal1: DewItem
    lateinit var RangerLegsWood1: DewItem
    lateinit var AlchemistHelmWater1: DewItem
    lateinit var UniversalBootsEarth1: DewItem
    lateinit var WarriorSwordFire1: DewItem

    @Awake(LifeCycle.ENABLE)
    fun init() {

        // 战士·金·1阶·胸甲
        val wc = EquipData(Job.WARRIOR, 1, EquipmentSlot.CHEST, FiveElement.METAL)
        WarriorChestMetal1 = DewItem.register(
            ItemData(
                material    = Material.IRON_CHESTPLATE,
                displayName = "${FiveElement.METAL.color}金·战铠 §81阶",
                id          = "warrior_chest_metal_1",
                lore        = listOf(DewItem.LIMIT_WARRIOR, DewItem.R2) + EquipLoreBuilder.build(wc),
                equipData   = wc
            )
        ) { hideAllCompat() }

        // 游侠·木·1阶·护腿
        val rl = EquipData(Job.RANGER, 1, EquipmentSlot.LEGS, FiveElement.WOOD)
        RangerLegsWood1 = DewItem.register(
            ItemData(
                material    = Material.LEATHER_LEGGINGS,
                displayName = "${FiveElement.WOOD.color}木·疾风腿 §81阶",
                id          = "ranger_legs_wood_1",
                lore        = listOf(DewItem.LIMIT_ARCHER, DewItem.R2) + EquipLoreBuilder.build(rl),
                equipData   = rl
            )
        ) { hideAllCompat() }

        // 炼丹师·水·1阶·头盔
        val ah = EquipData(Job.ALCHEMIST, 1, EquipmentSlot.HEAD, FiveElement.WATER)
        AlchemistHelmWater1 = DewItem.register(
            ItemData(
                material    = Material.LEATHER_HELMET,
                displayName = "${FiveElement.WATER.color}水·丹冠 §81阶",
                id          = "alchemist_helm_water_1",
                lore        = listOf(DewItem.LIMIT_ALCHEMIST, DewItem.R2) + EquipLoreBuilder.build(ah),
                equipData   = ah
            )
        ) { hideAllCompat() }

        // 通用·土·1阶·靴子
        val ub = EquipData(null, 1, EquipmentSlot.FEET, FiveElement.EARTH)
        UniversalBootsEarth1 = DewItem.register(
            ItemData(
                material    = Material.GOLDEN_BOOTS,
                displayName = "${FiveElement.EARTH.color}土·稳步靴 §81阶",
                id          = "universal_boots_earth_1",
                lore        = listOf(DewItem.RECOMMEND_ALL, DewItem.R1) + EquipLoreBuilder.build(ub),
                equipData   = ub
            )
        ) { hideAllCompat() }

        // 战士·火·1阶·武器
        val ws = EquipData(Job.WARRIOR, 1, EquipmentSlot.HAND, FiveElement.FIRE)
        WarriorSwordFire1 = DewItem.register(
            ItemData(
                material    = Material.IRON_SWORD,
                displayName = "${FiveElement.FIRE.color}火·战刃 §81阶",
                id          = "warrior_sword_fire_1",
                lore        = listOf(DewItem.LIMIT_WARRIOR, DewItem.R2) + EquipLoreBuilder.build(ws),
                equipData   = ws
            )
        ) { hideAllCompat() }
    }
}
