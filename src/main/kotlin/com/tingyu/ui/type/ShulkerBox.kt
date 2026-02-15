package com.tingyu.ui.type

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.ui.Menu
import taboolib.module.ui.type.Chest
import taboolib.module.ui.type.impl.ChestImpl
import taboolib.module.ui.virtual.virtualize

/**
 * 潜影盒容器接口
 */
interface ShulkerBox : Chest

/**
 * 潜影盒容器实现（固定 3 行 27 格）
 */
open class ShulkerBoxImpl(title: String) : ChestImpl(title), ShulkerBox {

    override fun build(): Inventory {
        var inventory = Bukkit.createInventory(holderCallback(this), InventoryType.SHULKER_BOX, title)
        if (virtualized) {
            inventory = inventory.virtualize(virtualizedStorageContents)
        }
        lastInventory = inventory
        var row = 0
        while (row < slots.size && row < 3) {
            val line = slots[row]
            var cel = 0
            while (cel < line.size && cel < 9) {
                inventory.setItem(row * 9 + cel, items[line[cel]] ?: ItemStack(Material.AIR))
                cel++
            }
            row++
        }
        return inventory
    }
}

/**
 * 自动注册 —— 插件启用时自动执行，无需手动调用
 */
object ShulkerBoxRegistry {

    @Awake(LifeCycle.ENABLE)
    fun register() {
        Menu.registerImplementation(ShulkerBox::class.java, ShulkerBoxImpl::class.java)
    }
}