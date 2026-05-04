package com.tingyu.item

import com.tingyu.command.DewItem

/**
 * 装备快捷别名——所有定义已移至 DewItem companion，此处仅保留兼容访问。
 * 建议新代码直接使用 DewItem.WARRIOR_CHEST_METAL_1 等。
 */
object Armor {
    val WarriorChestMetal1:    DewItem get() = DewItem.WARRIOR_CHEST_METAL_1
    val RangerLegsWood1:       DewItem get() = DewItem.RANGER_LEGS_WOOD_1
    val AlchemistHelmWater1:   DewItem get() = DewItem.ALCHEMIST_HELM_WATER_1
    val UniversalBootsEarth1:  DewItem get() = DewItem.UNIVERSAL_BOOTS_EARTH_1
    val WarriorSwordFire1:     DewItem get() = DewItem.WARRIOR_SWORD_FIRE_1
}
