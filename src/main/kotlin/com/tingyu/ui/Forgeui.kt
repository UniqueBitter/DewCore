package com.tingyu.ui

import com.tingyu.forge.ForgeRecipe
import com.tingyu.forge.TempRecipeList
import com.tingyu.item.util.hideAllCompat
import com.tingyu.ui.type.ShulkerBox
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.platform.util.buildItem

object ForgeUI {

    private val materialSlots = intArrayOf(10, 11, 12, 14, 15, 16) // 对应你图片里的 6 个红框
    private const val resultSlot = 22


    fun openUI(player: Player) {
        player.openMenu<ShulkerBox>("§6              ===锻造台===") {
            rows(3)
            map(
                "#########",
                "#   #   #",
                "####R##B#"
            )

            set('#', buildItem(Material.BONE_MEAL) {
                name = "§7§o请按配方放入物品"
                hideAllCompat()
            }) { isCancelled = true }

            set('B', buildItem(Material.ANVIL) {
                name = "§c点击锻造"
                hideAllCompat()
            }) {
                isCancelled = true

                if (inventory.getItem(resultSlot)?.type?.isAir == false) {
                    clicker.sendMessage("§c请先取走成品槽中的物品！")
                    return@set
                }

                // --- 修复点：正确读取当前 UI 上的 6 个物品 ---
                val currentItems = arrayOfNulls<ItemStack>(6)
                var hasAnyItem = false
                materialSlots.forEachIndexed { index, slot ->
                    val item = inventory.getItem(slot)
                    if (item != null && !item.type.isAir) {
                        currentItems[index] = item
                        hasAnyItem = true
                    }
                }

                if (!hasAnyItem) {
                    clicker.sendMessage("§7请放入材料...")
                    return@set
                }

                // 使用统一的 fromItems 生成匹配用的 Key
                val temp = TempRecipeList.fromItems(currentItems)
                val resultItem = ForgeRecipe.getRecipes()[temp]

                if (resultItem != null) {
                    // 扣除材料
                    materialSlots.forEach { slot ->
                        val item = inventory.getItem(slot) ?: return@forEach
                        if (item.amount > 1) item.amount -= 1 else inventory.setItem(slot, null)
                    }

                    inventory.setItem(resultSlot, resultItem.clone())
                    clicker.sendMessage("§a锻造成功！")
                    clicker.playSound(clicker.location, Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f)
                } else {
                    clicker.sendMessage("§c配方错误，无法锻造！")
                    clicker.playSound(clicker.location, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.0f)
                }
            }

            onClose {
                val inv = it.inventory
                val p = it.player as Player
                (materialSlots.toList() + resultSlot).forEach { slot ->
                    val item = inv.getItem(slot)
                    if (item != null && !item.type.isAir) {
                        val leftover = p.inventory.addItem(item)
                        leftover.values.forEach { drop -> p.world.dropItem(p.location, drop) }
                    }
                }
            }

            // 补充：防止玩家点击边框等位置
            onClick { it.isCancelled = it.rawSlot !in (materialSlots.toList() + resultSlot) && it.rawSlot in 0..26 }
        }
    }
}