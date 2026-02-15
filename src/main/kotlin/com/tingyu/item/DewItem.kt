package com.tingyu.item

import com.tingyu.item.util.AddPDC.addPDC
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.buildItem

enum class DewItem(val data: ItemData) {

    // --- 注册区域：现在看起来非常整洁 ---
    
    APPLE(ItemData(
        material = org.bukkit.Material.APPLE,
        displayName = "§f苹果",
        id = "apple",
        lore = listOf("§7§o这是一个普通的苹果")
    )),

    GOLD_APPLE(ItemData(
        material = org.bukkit.Material.GOLDEN_APPLE,
        displayName = "§a金苹果",
        id = "gold_apple",
        lore = listOf("§7§o据要有很好牙口才能吃下去")
    ));


    /**
     * 核心 ItemStack 对象
     * 所有的属性都从 this.data 中获取
     */
    val itemStack: ItemStack by lazy {
        buildItem(data.material) {
            name = data.displayName
            this.lore.addAll(data.lore)
            addPDC(data.id)
        }
    }

    /**
     * 获取克隆物品
     */
    fun getItem(amount: Int = 1): ItemStack {
        return itemStack.clone().apply { this.amount = amount }
    }

    companion object {
        /** 仿照 Job 类，增加按 ID 查找的功能 */
        fun fromId(id: String): DewItem? =
            entries.find { it.data.id == id }
    }
}