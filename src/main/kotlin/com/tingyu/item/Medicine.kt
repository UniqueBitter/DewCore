package com.tingyu.item

import com.tingyu.item.DewItem.Companion.R0
import com.tingyu.item.DewItem.Companion.R2
import com.tingyu.item.DewItem.Companion.R4
import com.tingyu.item.DewItem.Companion.R6
import org.bukkit.Material
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object Medicine {

    lateinit var YAO_YIN_1: DewItem
    lateinit var YAO_YIN_2: DewItem
    lateinit var YAO_YIN_3: DewItem
    lateinit var YAO_YIN_41: DewItem
    lateinit var YAO_YIN_42: DewItem
    lateinit var REN_SHEN: DewItem
    lateinit var LING_ZHI: DewItem

    @Awake(LifeCycle.ENABLE)
    fun init() {
        YAO_YIN_1 = DewItem.reg(Material.SUGAR, "§b初级药引", "yao_yin_1", R0, "§7§o炼丹师用来炼制丹药的必备材料")
        YAO_YIN_2 = DewItem.reg(Material.GHAST_TEAR, "§b中级药引", "yao_yin_2", R2, "§7§o炼丹师用来炼制丹药的必备材料") { shiny() }
        YAO_YIN_3 = DewItem.reg(Material.BLAZE_POWDER, "§b高级药引", "yao_yin_3", R4, "§7§o炼丹师用来炼制丹药的必备材料")

        YAO_YIN_41 = DewItem.reg(Material.NETHER_STAR, "§b究极药引-法", "yao_yin_41", R6, "§7§o由太上老君调制的究极药引,用以炼制究极法丹")
        YAO_YIN_42 = DewItem.reg(Material.NETHER_STAR, "§b究极药引-术", "yao_yin_42", R6, "§7§o由太上老君调制的究极药引,用以炼制究极术丹")
        REN_SHEN = DewItem.reg(Material.CARROT, "§f人参", "ren_shen", R0, "§7§o滋补养生的名贵药材")
        LING_ZHI = DewItem.reg(Material.RED_MUSHROOM, "§f灵芝", "ling_zhi", R0, "§7§o具有神奇药效的灵芝")



    }
}