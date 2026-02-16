package com.tingyu.item

import com.tingyu.command.DewItem
import com.tingyu.command.DewItem.Companion.R0
import com.tingyu.command.DewItem.Companion.R2
import com.tingyu.command.DewItem.Companion.R3
import com.tingyu.command.DewItem.Companion.R4
import com.tingyu.command.DewItem.Companion.R5
import org.bukkit.Material
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object Misc {

    // ===== 基础材料 =====
    lateinit var OLD_LEATHER: DewItem          // 破旧的皮革
    lateinit var SPIDER_EYE: DewItem           // 蜘蛛眼
    lateinit var COAL: DewItem                 // 煤炭

    // ===== 药材 =====
    lateinit var THOUSAND_YEAR_POLYGONUM: DewItem  // 千年何首乌
    lateinit var RABBIT_HIDE: DewItem           // 兔子皮

    // ===== 神秘物品 =====
    lateinit var UNKNOWN_CRYSTAL: DewItem       // 未知的彩色晶块

    // ===== 鱼类 =====
    lateinit var RAW_FISH: DewItem               // 生鱼
    lateinit var RAW_SALMON: DewItem             // 生鲑鱼
    lateinit var TROPICAL_FISH: DewItem          // 奇特的鱼

    // ===== 杂物 =====
    lateinit var MEDICINAL_LIQUOR: DewItem       // 神秘的药酒
    lateinit var ROTTEN_BAMBOO: DewItem          // 腐竹
    lateinit var OLD_BOOT: DewItem                // 臭靴子

    // ===== 古代遗物 =====
    lateinit var ANCIENT_BAMBOO: DewItem          // 古老的竹简
    lateinit var ANCIENT_LOCK: DewItem            // 古老的长生锁
    lateinit var ANCIENT_KEY: DewItem             // 古老的钥匙

    // ===== 特殊工具 =====
    lateinit var COMPASS_NORTH: DewItem           // 八方归北仪
    lateinit var TIME_COMPASS: DewItem            // 时序罗盘

    // ===== 钥匙系列 =====
    lateinit var KEY_COPPER: DewItem              // 铜钥匙
    lateinit var KEY_SILVER: DewItem              // 银钥匙
    lateinit var KEY_GOLD: DewItem                // 金钥匙

    // ===== 符咒 =====
    lateinit var EXTINGUISH_CHARM: DewItem        // 退火符

    @Awake(LifeCycle.ENABLE)
    fun init() {
        // 基础材料
        OLD_LEATHER = DewItem.reg(Material.LEATHER, "§7破旧的皮革", "old_leather", R0).maxStack(99)
        SPIDER_EYE = DewItem.reg(Material.SPIDER_EYE, "§7蜘蛛眼", "spider_eye", R0).maxStack(99)
        COAL = DewItem.reg(Material.COAL, "§7煤炭", "coal", R0).maxStack(99)

        // 药材
        THOUSAND_YEAR_POLYGONUM = DewItem.reg(Material.NETHER_WART, "§7千年何首乌", "thousand_year_polygonum", R3, "§7§o千年何首乌").maxStack(99)
        RABBIT_HIDE = DewItem.reg(Material.RABBIT_HIDE, "§7兔子皮", "rabbit_hide", R2).maxStack(99)

        // 神秘物品
        UNKNOWN_CRYSTAL = DewItem.reg(Material.AMETHYST_SHARD, "§e未知的彩色晶块", "unknown_crystal", R3, "§7§o不知为何的彩色晶体", "§7§o似乎有人在集市收购").maxStack(99)

        // 鱼类
        RAW_FISH = DewItem.reg(Material.COD, "§7生鱼", "raw_fish", R0).maxStack(99)
        RAW_SALMON = DewItem.reg(Material.SALMON, "§f生鲑鱼", "raw_salmon", R3).maxStack(99)
        TROPICAL_FISH = DewItem.reg(Material.TROPICAL_FISH, "§a奇特的鱼", "tropical_fish", R3).maxStack(99)

        // 杂物
        MEDICINAL_LIQUOR = DewItem.reg(Material.POTION, "§7神秘的药酒", "medicinal_liquor", R0, "§7§o还是不要喝的好").maxStack(99)
        ROTTEN_BAMBOO = DewItem.reg(Material.STICK, "§7腐竹", "rotten_bamboo", R0, "§7§o只是腐烂的竹子而已").maxStack(99)
        OLD_BOOT = DewItem.reg(Material.LEATHER_BOOTS, "§7臭靴子", "old_boot", R0, "§7§o好臭啊！谁丢的！").maxStack(99)

        // 古代遗物
        ANCIENT_BAMBOO = DewItem.reg(Material.ENCHANTED_BOOK, "§e古老的竹简", "ancient_bamboo", R3, "§7§o上面有一些看不懂的文字").maxStack(99)
        ANCIENT_LOCK = DewItem.reg(Material.TRIPWIRE_HOOK, "§e古老的长生锁", "ancient_lock", R3, "§7§o有些年头的东西,或许可以找到配对的钥匙").maxStack(99)
        ANCIENT_KEY = DewItem.reg(Material.IRON_NUGGET, "§e古老的钥匙", "ancient_key", R3, "§7§o有些年头的东西,或许可以找到配对的锁").maxStack(99)

        // 特殊工具
        COMPASS_NORTH = DewItem.reg(Material.COMPASS, "§f八方归北仪", "compass_north", R2).maxStack(99)
        TIME_COMPASS = DewItem.reg(Material.CLOCK, "§f时序罗盘", "time_compass", R2).maxStack(99)

        // 钥匙系列
        KEY_COPPER = DewItem.reg(Material.GOLD_NUGGET, "§9铜钥匙", "key_copper", R3).maxStack(99)
        KEY_SILVER = DewItem.reg(Material.IRON_INGOT, "§d银钥匙", "key_silver", R4).maxStack(99)
        KEY_GOLD = DewItem.reg(Material.GOLD_INGOT, "§e金钥匙", "key_gold", R5).maxStack(99)

        EXTINGUISH_CHARM = DewItem.reg(Material.PAPER, "§b退火符", "extinguish_charm", R0, "§7§o针对火焰魔制作的符咒", "§7§o对一般怪物只有击退作用").maxStack(99)
    }
}