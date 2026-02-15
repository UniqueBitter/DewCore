package com.tingyu.item

import com.tingyu.item.DewItem.Companion.R3
import com.tingyu.item.DewItem.Companion.R4
import com.tingyu.item.DewItem.Companion.R5
import org.bukkit.Material
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object Forge {

    // ===== 武器核心材料 =====
    lateinit var WEAPON_CORE_3: DewItem  // 赤铜锭 - 三阶
    lateinit var WEAPON_CORE_4: DewItem  // 玄铁锭 - 四阶
    lateinit var WEAPON_CORE_5: DewItem  // 灵玉简 - 五阶

    // ===== 装备原核 =====
    lateinit var ARMOR_CORE_3: DewItem   // 三阶装备原核
    lateinit var ARMOR_CORE_4: DewItem   // 四阶装备原核
    lateinit var ARMOR_CORE_5: DewItem   // 五阶装备原核

    // ===== 千炼玉系列 =====
    lateinit var QIANLIAN_JADE: DewItem  // 千炼玉

    // ===== 千炼核心（锻造后）=====
    lateinit var QIANLIAN_CORE_HELMET: DewItem  // 千炼核心-帽 -> 三绫冠
    lateinit var QIANLIAN_CORE_CHESTPLATE: DewItem // 千炼核心-衣 -> 乐游甲（战士）
    lateinit var QIANLIAN_CORE_LEGGINGS: DewItem // 千炼核心-裤 -> 司正裤（弓箭手）
    lateinit var QIANLIAN_CORE_BOOTS: DewItem    // 千炼核心-鞋 -> 听音羽（炼丹师）

    @Awake(LifeCycle.ENABLE)
    fun init() {
        // 武器核心材料
        WEAPON_CORE_3 = DewItem.reg(Material.CLAY_BALL, "§b赤铜锭", "weapon_core_3", R3, "§7§o用来制作三阶武器核心的材料", "§7§o非常珍贵,可以到铁匠铺进行锻造") { shiny() }
        WEAPON_CORE_4 = DewItem.reg(Material.QUARTZ, "§b玄铁锭", "weapon_core_4", R4, "§7§o用来制作四阶武器核心的材料", "§7§o非常珍贵,可以到铁匠铺进行锻造") { shiny() }
        WEAPON_CORE_5 = DewItem.reg(Material.DIAMOND, "§b灵玉简", "weapon_core_5", R5, "§7§o用来制作五阶武器核心的材料", "§7§o非常珍贵,可以到铁匠铺进行锻造") { shiny() }

        // 装备原核
        ARMOR_CORE_3 = DewItem.reg(Material.IRON_NUGGET, "§b三阶装备原核", "armor_core_3", R3, "§7§o用来制作三阶装备核心的材料", "§7§o可以锻造成指定的三阶套装核心") { shiny() }
        ARMOR_CORE_4 = DewItem.reg(Material.IRON_NUGGET, "§b四阶装备原核", "armor_core_4", R4, "§7§o用来制作四阶装备核心的材料", "§7§o可以锻造成指定的四阶套装核心") { shiny() }
        ARMOR_CORE_5 = DewItem.reg(Material.IRON_NUGGET, "§b五阶装备原核", "armor_core_5", R5, "§7§o用来制作五阶装备核心的材料", "§7§o可以锻造成指定的五阶套装核心") { shiny() }





        // 千炼玉
        QIANLIAN_JADE = DewItem.reg(Material.DIAMOND, "§b千炼玉", "qianlian_jade", R5, "§7§o散落在大陆的珍奇宝玉","§7§o可以锻造成指定的绫音防具部位的核心", "§7§o注意,绫音系列部分装备有职业限制"
        ) { shiny() }

        // 千炼核心（锻造后）
        QIANLIAN_CORE_HELMET = DewItem.reg(Material.NETHER_QUARTZ_ORE, "§b千炼核心-帽", "qianlian_core_helmet", R5,"§7§o稀有的精锻玉核", "§7§o将该核心和四十个精炼土元素丢进锻造台后","§7§o可以锻造出通用装备-三绫冠"
        ) { shiny() }

        QIANLIAN_CORE_CHESTPLATE = DewItem.reg(Material.NETHER_QUARTZ_ORE, "§b千炼核心-衣", "qianlian_core_chestplate", R5, "§7§o稀有的精锻玉核", "§7§o将该核心和四十个精炼土元素丢进锻造台后", "§7§o可以锻造出战士专属装备-乐游甲"
        ) { shiny() }

        QIANLIAN_CORE_LEGGINGS = DewItem.reg(Material.NETHER_QUARTZ_ORE, "§b千炼核心-裤", "qianlian_core_leggings", R5,
            "§7§o稀有的精锻玉核",
            "§7§o将该核心和四十个精炼土元素丢进锻造台后",
            "§7§o可以锻造出弓箭手专属装备-司正裤"
        ) { shiny() }

        QIANLIAN_CORE_BOOTS = DewItem.reg(Material.NETHER_QUARTZ_ORE, "§b千炼核心-鞋", "qianlian_core_boots", R5,
            "§7§o稀有的精锻玉核",
            "§7§o将该核心和四十个精炼土元素丢进锻造台后",
            "§7§o可以锻造出炼丹师专属装备-听音羽"
        ) { shiny() }
    }
}