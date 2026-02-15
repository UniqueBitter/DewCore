package com.tingyu.ui

import com.tingyu.forge.ForgeRecipe
import com.tingyu.forge.TempRecipeList
import com.tingyu.item.util.ForgeRecipe.hideAllCompat
import com.tingyu.ui.type.ShulkerBox
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.platform.util.buildItem

object ForgeUI {

    private val materialSlots = intArrayOf(10, 11, 12, 14, 15, 16)
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
                name = "§7"
                hideAllCompat()
            }) { isCancelled = true }

            set('B', buildItem(Material.ANVIL) {
                name = "§c点击锻造"
                hideAllCompat()
            }) {
                isCancelled = true

                // 1. 检查成品槽
                if (inventory.getItem(resultSlot)?.type?.isAir == false) {
                    clicker.sendMessage("§c请先取走成品槽中的物品！")
                    return@set
                }

                // 2. 获取当前槽位快照
                val currentItems = arrayOfNulls<ItemStack>(6)
                materialSlots.forEachIndexed { index, slot ->
                    currentItems[index] = inventory.getItem(slot)
                }

                // 3. 生成 Key 并进行严格匹配
                val currentKey = TempRecipeList.fromItems(currentItems)
                val resultItem = ForgeRecipe.getRecipes()[currentKey]

                if (resultItem != null) {
                    // 4. 成功匹配：扣除全部材料
                    // 因为是“严格匹配”，玩家放的数量肯定等于配方要求的数量
                    materialSlots.forEach { slot ->
                        inventory.setItem(slot, null)
                    }

                    // 5. 产出成品
                    inventory.setItem(resultSlot, resultItem.clone())
                    clicker.sendMessage("§a锻造成功！")
                    clicker.playSound(clicker.location, Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f)
                } else {
                    clicker.sendMessage("§c材料不足、数量不精确或配方错误！")
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

            onClick {
                val allowed = materialSlots.toList() + resultSlot
                if (it.rawSlot in 0..26 && it.rawSlot !in allowed) {
                    it.isCancelled = true
                }
            }
        }
    }
}