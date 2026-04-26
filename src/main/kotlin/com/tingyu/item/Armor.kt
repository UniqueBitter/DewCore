package com.tingyu.item

import com.tingyu.command.DewItem
import com.tingyu.command.DewItem.Companion.R2
import com.tingyu.command.DewItem.Companion.R6
import com.tingyu.command.DewItem.Companion.register
import org.bukkit.Material
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.util.buildItem

object Armor {
    lateinit var TestArmor: DewItem
    @Awake(LifeCycle.ENABLE)
    fun init() {
        val TestArmor = DewItem.register(ItemData(
            material = Material.DIAMOND_CHESTPLATE,
            displayName = "§a测试胸甲",
            id = "test_armor",
            lore = listOf(" ", R6,"§7§o测试用胸甲","§7§o进攻属性:+10%","§7§o生命:+10","§7§o最终生命+5%")
        ))
    }
}