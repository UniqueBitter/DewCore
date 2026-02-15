package com.tingyu.ui

import com.tingyu.ui.type.ShulkerBox
import com.tingyu.item.hideAllCompat
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.ui.buildMenu
import taboolib.platform.util.buildItem

class forgeui {
    fun openUI(player: Player) {
        val ui = buildMenu<ShulkerBox>("§c              ===锻造台===") {
            map(
                "####B####",
                "#   #   #",
                "#### ####"
            )
            set('#', buildItem(Material.BONE_MEAL){ name = "§7§o请按配方放入物品" }){
                isCancelled = true
            }
            set('B',buildItem(Material.NETHERITE_AXE){
                name = "§c锻造"
                lore.add("§7将材料正确放入对应的位置")
                lore.add("§7点击即可锻造")
                customModelData = 2
                hideAllCompat()
            }){
                isCancelled = true
            }
        }
        player.openInventory(ui)
    }
}