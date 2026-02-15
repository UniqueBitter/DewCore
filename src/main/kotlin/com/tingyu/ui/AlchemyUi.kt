package com.tingyu.ui

import com.tingyu.forge.AlchemyRecipe
import com.tingyu.forge.TempAlchemyList
import com.tingyu.item.util.ForgeRecipe.hideAllCompat
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem

object AlchemyUI {

    //  1行9格布局:
    //  药引 元素1 元素2 元素3 元素4 元素5 | # | 成品 | 炼丹
    //   0    1    2    3    4    5     6    7     8

    private const val catalystSlot = 0
    private val elementSlots = intArrayOf(1, 2, 3, 4, 5)
    private const val resultSlot = 7
    private val allInputSlots = intArrayOf(catalystSlot) + elementSlots

    fun open(player: Player) {
        player.openMenu<Basic>("§f炼丹炉-§f药引-金木水火土") {
            rows(1)
            map("YEEEEE#RB")

            set('#', buildItem(Material.BONE_MEAL) {
                name = "§7"
                hideAllCompat()
            }) { isCancelled = true }

            set('B', buildItem(Material.CAULDRON) {
                name = "§c点击炼丹"
                hideAllCompat()
            }) {
                isCancelled = true

                // 检查成品槽
                if (inventory.getItem(resultSlot)?.type?.isAir == false
                    && inventory.getItem(resultSlot)?.type != Material.BARRIER) {
                    clicker.sendMessage("§c请先取走成品槽中的物品！")
                    return@set
                }

                // 获取快照: [0]=药引, [1..5]=元素
                val currentItems = arrayOfNulls<ItemStack>(6)
                currentItems[0] = inventory.getItem(catalystSlot)
                elementSlots.forEachIndexed { index, slot ->
                    currentItems[index + 1] = inventory.getItem(slot)
                }

                // 严格匹配
                val currentKey = TempAlchemyList.fromItems(currentItems)
                val resultItem = AlchemyRecipe.getRecipes()[currentKey]

                if (resultItem != null) {
                    inventory.setItem(catalystSlot, null)
                    elementSlots.forEach { slot -> inventory.setItem(slot, null) }

                    val clone = resultItem.clone()
                    val total = clone.amount
                    val maxStack = clone.maxStackSize

                    // 成品槽放一组
                    val slotItem = clone.clone().apply { amount = minOf(total, maxStack) }
                    inventory.setItem(resultSlot, slotItem)

                    // 超出部分直接给玩家背包
                    var remaining = total - slotItem.amount
                    while (remaining > 0) {
                        val give = clone.clone().apply { amount = minOf(remaining, maxStack) }
                        val leftover = clicker.inventory.addItem(give)
                        leftover.values.forEach { drop -> clicker.world.dropItem(clicker.location, drop) }
                        remaining -= give.amount
                    }

                    clicker.sendMessage("§a炼丹成功！共获得 ${total} 个")
                    clicker.playSound(clicker.location, Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.0f)
                } else {
                    clicker.sendMessage("§c药引或元素不正确，炼丹失败！")
                    clicker.playSound(clicker.location, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.0f)
                }
            }

            onClose {
                val inv = it.inventory
                val p = it.player as Player
                allInputSlots.forEach { slot ->
                    val item = inv.getItem(slot)
                    if (item != null && !item.type.isAir) {
                        val leftover = p.inventory.addItem(item)
                        leftover.values.forEach { drop -> p.world.dropItem(p.location, drop) }
                    }
                }
                // 成品也归还
                val result = inv.getItem(resultSlot)
                if (result != null && result.type != Material.BARRIER && !result.type.isAir) {
                    val leftover = p.inventory.addItem(result)
                    leftover.values.forEach { drop -> p.world.dropItem(p.location, drop) }
                }
            }
        }
    }
}