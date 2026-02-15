package com.tingyu.item

import com.tingyu.item.DewItem.Companion.R1
import com.tingyu.item.DewItem.Companion.R3
import com.tingyu.item.DewItem.Companion.R5
import org.bukkit.Material
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object Element {

    // --- 基础元素 (R1) ---
    lateinit var ELEMENT_METAL: DewItem
    lateinit var ELEMENT_WOOD: DewItem
    lateinit var ELEMENT_WATER: DewItem
    lateinit var ELEMENT_FIRE: DewItem
    lateinit var ELEMENT_EARTH: DewItem

    // --- 精炼元素 (R3) ---
    lateinit var ELEMENT_METAL_REFINED: DewItem
    lateinit var ELEMENT_WOOD_REFINED: DewItem
    lateinit var ELEMENT_WATER_REFINED: DewItem
    lateinit var ELEMENT_FIRE_REFINED: DewItem
    lateinit var ELEMENT_EARTH_REFINED: DewItem

    // --- 再次精炼元素 / 浓缩元素 (R5) ---
    lateinit var ELEMENT_METAL_AGAIN_REFINED: DewItem   // 对应原 AGAIN_REFINED_METAL
    lateinit var ELEMENT_WOOD_AGAIN_REFINED: DewItem    // 对应原 AGAIN_REFINED_WOOD
    lateinit var ELEMENT_WATER_AGAIN_REFINED: DewItem   // 对应原 AGAIN_REFINED_WATER
    lateinit var ELEMENT_FIRE_AGAIN_REFINED: DewItem    // 对应原 AGAIN_REFINED_FIRE
    lateinit var ELEMENT_EARTH_AGAIN_REFINED: DewItem   // 对应原 AGAIN_REFINED_EARTH

    @Awake(LifeCycle.ENABLE)
    fun init() {
        val elementLore = "§7§o构成世界的五种基础元素之一"

        // ================== [ 1. 基础元素 (R1) ] ==================
        ELEMENT_METAL = DewItem.reg(Material.EMERALD, "§f金元素", "element_metal", R1, elementLore)
        ELEMENT_WOOD = DewItem.reg(Material.BONE, "§f木元素", "element_wood", R1, elementLore)
        ELEMENT_WATER = DewItem.reg(Material.STRING, "§f水元素", "element_water", R1, elementLore)
        ELEMENT_FIRE = DewItem.reg(Material.BLAZE_ROD, "§f火元素", "element_fire", R1, elementLore)
        ELEMENT_EARTH = DewItem.reg(Material.MAGMA_CREAM, "§f土元素", "element_earth", R1, elementLore)

        // ================== [ 2. 精炼元素 (R3) ] ==================
        ELEMENT_METAL_REFINED = DewItem.reg(Material.EMERALD, "§b精炼金元素", "element_metal_refined", R3, "§7§o经过简单提纯后的金之元素"){ shiny() }
        ELEMENT_WOOD_REFINED = DewItem.reg(Material.BONE, "§b精炼木元素", "element_wood_refined", R3, "§7§o经过简单提纯后的木之元素"){ shiny() }
        ELEMENT_WATER_REFINED = DewItem.reg(Material.STRING, "§b精炼水元素", "element_water_refined", R3, "§7§o经过简单提纯后的水之元素"){ shiny() }
        ELEMENT_FIRE_REFINED = DewItem.reg(Material.BLAZE_ROD, "§b精炼火元素", "element_fire_refined", R3, "§7§o经过简单提纯后的火之元素"){ shiny() }
        ELEMENT_EARTH_REFINED = DewItem.reg(Material.MAGMA_CREAM, "§b精炼土元素", "element_earth_refined", R3, "§7§o经过简单提纯后的土之元素"){ shiny() }

        // ================== [ 3. 元素结晶 (R5) ] ==================
        val commonLoreA = "§7§o太上老君所发明的超浓缩元素结晶"
        val commonLoreB = "§7§o可以利用炼丹炉作为媒介,发动大范围的阵法"

        ELEMENT_METAL_AGAIN_REFINED = DewItem.reg(Material.FLINT, "§6浓缩金元素", "element_metal_again_refined", R5,
            commonLoreA, commonLoreB,
            "§7§o炼化金元素晶体可以造成大面积的腐蚀伤害",
            "§7§o并赋予范围内怪物减速效果",
            "§7§o就算是死气生物也难逃此劫"
        ) { shiny() }

        ELEMENT_WOOD_AGAIN_REFINED = DewItem.reg(Material.MELON_SEEDS, "§6浓缩木元素", "element_wood_again_refined", R5,
            commonLoreA, commonLoreB,
            "§7§o炼化木元素晶体可以将生机灌注到周围的队友身上",
            "§7§o可以减轻他们的饥饿并提高体力上限",
            "§7§o同时可以为他们治疗:祛除毒素并回复血量"
        ) { shiny() }

        ELEMENT_WATER_AGAIN_REFINED = DewItem.reg(Material.WHEAT_SEEDS, "§6浓缩水元素", "element_water_again_refined", R5,
            commonLoreA, commonLoreB,
            "§7§o炼化水元素晶体可以散发出剧毒烟雾",
            "§7§o可以为队友提供抗毒效果",
            "§7§o同时可以使周围敌人受到持续的中毒效果"
        ) { shiny() }

        ELEMENT_FIRE_AGAIN_REFINED = DewItem.reg(Material.CHARCOAL, "§6浓缩火元素", "element_fire_again_refined", R5,
            commonLoreA, commonLoreB,
            "§7§o炼化火元素晶体可以激活战力",
            "§7§o可以为队友提供火焰防护",
            "§7§o同时可以为队友提供进攻战斗力"
        ) { shiny() }

        ELEMENT_EARTH_AGAIN_REFINED = DewItem.reg(Material.PUMPKIN_SEEDS, "§6浓缩土元素", "element_earth_again_refined", R5,
            commonLoreA, commonLoreB,
            "§7§o炼化土元素晶体可以暂时让周围土壤变软",
            "§7§o可以保护周围的队友,为他们提供护甲",
            "§7§o同时可以长时间束缚范围内的所有怪物"
        ) { shiny() }
    }
}