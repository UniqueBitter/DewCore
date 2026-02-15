package com.tingyu.item

import com.tingyu.item.DewItem.Companion.R0
import com.tingyu.item.DewItem.Companion.R2
import com.tingyu.item.DewItem.Companion.R4
import com.tingyu.item.DewItem.Companion.R6
import org.bukkit.Material
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object Pill {

    // ===== [药] 喝的类型 =====

    // 疗愈丹系列 
    lateinit var HEALING_DRINK_BASIC: DewItem      // 新手疗愈丹
    lateinit var HEALING_DRINK_NORMAL: DewItem     // 一般疗伤丹
    lateinit var HEALING_DRINK_MEDIUM: DewItem     // 中级疗愈丹
    lateinit var HEALING_DRINK_ADVANCED: DewItem   // 高级痊愈丹

    // 解毒丹系列 
    lateinit var ANTIDOTE_DRINK_BASIC: DewItem     // 解毒丹
    lateinit var ANTIDOTE_DRINK_STRONG: DewItem    // 辛氏抗毒丹

    // 万灵丹系列 
    lateinit var QUICK_DRINK_BASIC: DewItem        // 万灵丹-速
    lateinit var JUMP_DRINK_BASIC: DewItem         // 万灵丹-跃
    lateinit var LUOSHEN_DRINK_BASIC: DewItem      // 洛神丹

    // 混元一气丹系列 
    lateinit var HUNYUAN_DRINK_BASIC: DewItem      // 初级
    lateinit var HUNYUAN_DRINK_MEDIUM: DewItem     // 中级
    lateinit var HUNYUAN_DRINK_ADVANCED: DewItem   // 高级

    // 辟谷丹系列 
    lateinit var BIGU_DRINK_BASIC: DewItem         // 初级
    lateinit var BIGU_DRINK_MEDIUM: DewItem        // 中级
    lateinit var BIGU_DRINK_ADVANCED: DewItem      // 高级

    // 巨力丸系列 
    lateinit var STRENGTH_DRINK_BASIC: DewItem     // 初级
    lateinit var STRENGTH_DRINK_MEDIUM: DewItem    // 中级
    lateinit var STRENGTH_DRINK_ADVANCED: DewItem  // 高级

    // 玄甲丹系列 
    lateinit var ARMOR_DRINK_BASIC: DewItem        // 初级
    lateinit var ARMOR_DRINK_MEDIUM: DewItem       // 中级
    lateinit var ARMOR_DRINK_ADVANCED: DewItem     // 高级


    // ===== [法] 丢的类型 =====

    // 逢木回春露系列 
    lateinit var WOOD_THROW_BASIC: DewItem         // 初级
    lateinit var WOOD_THROW_MEDIUM: DewItem        // 中级
    lateinit var WOOD_THROW_ADVANCED: DewItem      // 高级

    // 天神护体系列 
    lateinit var DIVINE_THROW_BASIC: DewItem       // 初级
    lateinit var DIVINE_THROW_MEDIUM: DewItem      // 中级
    lateinit var DIVINE_THROW_ADVANCED: DewItem    // 高级

    // 九转还魂香系列 
    lateinit var REVIVE_THROW_BASIC: DewItem       // 初级
    lateinit var REVIVE_THROW_MEDIUM: DewItem      // 中级
    lateinit var REVIVE_THROW_ADVANCED: DewItem    // 高级

    // 起死回生泉 
    lateinit var RESURRECTION_THROW: DewItem       // 起死回生泉


    // ===== [术] 丢的类型 =====

    // 封喉系列 
    lateinit var POISON_THROW_BASIC: DewItem       // 初级
    lateinit var POISON_THROW_MEDIUM: DewItem      // 中级
    lateinit var POISON_THROW_ADVANCED: DewItem    // 高级

    @Awake(LifeCycle.ENABLE)
    fun init() {
        // ===== [药] =====
        HEALING_DRINK_BASIC = DewItem.reg(Material.POTION, "§b新手疗愈丹([药])", "healing_drink_basic", R0, "§7§o新手专用治愈丹药", "§2[治疗] 回复4点生命值")
        HEALING_DRINK_NORMAL = DewItem.reg(Material.POTION, "§2一般疗伤丹([药])", "healing_drink_normal", R2, "§7§o一般的回复丹药", "§7§o服下可以适当回复状态", "§2[治疗] 回复8点生命值")
        HEALING_DRINK_MEDIUM = DewItem.reg(Material.POTION, "§2中级疗愈丹([药])", "healing_drink_medium", R4, "§7§o较好的回复丹药", "§7§o服下可以回复状态", "§2[治疗] 回复16生命值", "§2[丹药] 提供伤害吸收护盾")
        HEALING_DRINK_ADVANCED = DewItem.reg(Material.POTION, "§2高级痊愈丹([药])", "healing_drink_advanced", R6, "§7§o高品的回复丹药", "§7§o服下可以大量回复状态", "§2[治疗] 回复32生命值", "§2[丹药] 提供伤害吸收护盾")

        ANTIDOTE_DRINK_BASIC = DewItem.reg(Material.POTION, "§2解毒丹([药])", "antidote_drink_basic", R2, "§7§o普通的解毒丹", "§7§o服下可以消除中毒效果", "§2[祛毒] 消除中毒效果")
        ANTIDOTE_DRINK_STRONG = DewItem.reg(Material.POTION, "§2辛氏抗毒丹([药])", "antidote_drink_strong", R4, "§7§o用麦克蜂毒液炼制的丹药", "§7§o能化解几乎全部的毒素", "§2[抗毒Ⅱ (10:00)] 抵抗部分负面效果")

        QUICK_DRINK_BASIC = DewItem.reg(Material.POTION, "§2万灵丹-速(初级[药])", "quick_drink_basic", R2, "§7§o万灵丹-速")
        JUMP_DRINK_BASIC = DewItem.reg(Material.POTION, "§2万灵丹-跃(初级[药])", "jump_drink_basic", R2, "§7§o万灵丹-跃")
        LUOSHEN_DRINK_BASIC = DewItem.reg(Material.POTION, "§2洛神丹(初级[药])", "luoshen_drink_basic", R2, "§7§o洛神丹")

        HUNYUAN_DRINK_BASIC = DewItem.reg(Material.POTION, "§2混元一气丹(初级[药])", "hunyuan_drink_basic", R2, "§7§o混元一气丹")
        HUNYUAN_DRINK_MEDIUM = DewItem.reg(Material.POTION, "§2混元一气丹(中级[药])", "hunyuan_drink_medium", R4, "§7§o混元一气丹")
        HUNYUAN_DRINK_ADVANCED = DewItem.reg(Material.POTION, "§2混元一气丹(高级[药])", "hunyuan_drink_advanced", R6, "§7§o混元一气丹")

        BIGU_DRINK_BASIC = DewItem.reg(Material.POTION, "§2辟谷丹(初级[药])", "bigu_drink_basic", R2, "§7§o辟谷丹")
        BIGU_DRINK_MEDIUM = DewItem.reg(Material.POTION, "§2辟谷丹(中级[药])", "bigu_drink_medium", R4, "§7§o辟谷丹")
        BIGU_DRINK_ADVANCED = DewItem.reg(Material.POTION, "§2辟谷丹(高级[药])", "bigu_drink_advanced", R6, "§7§o辟谷丹")

        STRENGTH_DRINK_BASIC = DewItem.reg(Material.POTION, "§2巨力丸(初级[药])", "strength_drink_basic", R2, "§9力量（05:00）", "", "§9+20%近战攻击&箭矢强度")
        STRENGTH_DRINK_MEDIUM = DewItem.reg(Material.POTION, "§2巨力丸(中级[药])", "strength_drink_medium", R4, "§9力量 II（05:00）", "", "§9+40%近战攻击&箭矢强度")
        STRENGTH_DRINK_ADVANCED = DewItem.reg(Material.POTION, "§2巨力丸(高级[药])", "strength_drink_advanced", R6, "§9力量 III（05:00）", "", "§9+60%近战攻击&箭矢强度")

        ARMOR_DRINK_BASIC = DewItem.reg(Material.POTION, "§2玄甲丹(初级[药])", "armor_drink_basic", R2, "§7§o玄甲丹")
        ARMOR_DRINK_MEDIUM = DewItem.reg(Material.POTION, "§2玄甲丹(中级[药])", "armor_drink_medium", R4, "§7§o玄甲丹")
        ARMOR_DRINK_ADVANCED = DewItem.reg(Material.POTION, "§2玄甲丹(高级[药])", "armor_drink_advanced", R6, "§7§o玄甲丹")


        // ===== [法]  =====
        WOOD_THROW_BASIC = DewItem.reg(Material.SPLASH_POTION, "§a逢木回春露(初级[法])", "wood_throw_basic", R2, "§7§o逢木回春露")
        WOOD_THROW_MEDIUM = DewItem.reg(Material.SPLASH_POTION, "§a逢木回春露(中级[法])", "wood_throw_medium", R4, "§7§o逢木回春露")
        WOOD_THROW_ADVANCED = DewItem.reg(Material.SPLASH_POTION, "§a逢木回春露(高级[法])", "wood_throw_advanced", R6, "§7§o逢木回春露")

        DIVINE_THROW_BASIC = DewItem.reg(Material.SPLASH_POTION, "§a天神护体(初级[法])", "divine_throw_basic", R2, "§7§o天神护体")
        DIVINE_THROW_MEDIUM = DewItem.reg(Material.SPLASH_POTION, "§a天神护体(中级[法])", "divine_throw_medium", R4, "§7§o天神护体")
        DIVINE_THROW_ADVANCED = DewItem.reg(Material.SPLASH_POTION, "§a天神护体(高级[法])", "divine_throw_advanced", R6, "§7§o天神护体")

        REVIVE_THROW_BASIC = DewItem.reg(Material.SPLASH_POTION, "§a九转还魂香(初级[法])", "revive_throw_basic", R2, "§7§o九转还魂香")
        REVIVE_THROW_MEDIUM = DewItem.reg(Material.SPLASH_POTION, "§a九转还魂香(中级[法])", "revive_throw_medium", R4, "§7§o九转还魂香")
        REVIVE_THROW_ADVANCED = DewItem.reg(Material.SPLASH_POTION, "§a九转还魂香(高级[法])", "revive_throw_advanced", R6, "§7§o九转还魂香")

        RESURRECTION_THROW = DewItem.reg(Material.SPLASH_POTION, "§a起死回生泉(究极[法])", "resurrection_throw", R6, "§7§o起死回生泉")


        // ===== [术]  =====
        POISON_THROW_BASIC = DewItem.reg(Material.SPLASH_POTION, "§c封喉(初级[术])", "poison_throw_basic", R2, "§7§o封喉")
        POISON_THROW_MEDIUM = DewItem.reg(Material.SPLASH_POTION, "§c封喉(中级[术])", "poison_throw_medium", R4, "§7§o封喉")
        POISON_THROW_ADVANCED = DewItem.reg(Material.SPLASH_POTION, "§c封喉(高级[术])", "poison_throw_advanced", R6, "§7§o封喉")
    }
}