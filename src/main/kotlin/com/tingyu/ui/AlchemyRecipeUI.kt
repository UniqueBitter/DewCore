package com.tingyu.ui

import com.tingyu.alchemy.AlchemyRecipe
import com.tingyu.command.DewItem
import com.tingyu.forge.TempAlchemyList
import com.tingyu.item.util.ForgeRecipe.hideAllCompat
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest
import taboolib.platform.util.buildItem

object AlchemyRecipeUI {

    fun open(player: Player, page: Int = 0) {
        val allRecipes = AlchemyRecipe.getRecipes().entries.toList()
        val maxPage = maxOf(0, (allRecipes.size - 1) / 45)
        val currentPage = page.coerceIn(0, maxPage)
        val startIndex = currentPage * 45
        val pageRecipes = allRecipes.subList(
            startIndex,
            minOf(startIndex + 45, allRecipes.size)
        )

        player.openMenu<Chest>("§8炼丹配方 §7(${currentPage + 1}/${maxPage + 1})") {
            map(
                "#########",
                "#########",
                "#########",
                "#########",
                "#########",
                "PBBBIBBBN"
            )

            // 设置配方格子（默认空）
            set('#', buildItem(Material.AIR)){
                isCancelled = true
            }

            // 设置边框
            set('B', buildItem(Material.GRAY_STAINED_GLASS_PANE) {
                name = "§7"
                hideAllCompat()
            }){
                isCancelled = true
            }

            // 设置上一页
            if (currentPage > 0) {
                set('P', buildItem(Material.ARROW) {
                    name = "§e上一页"
                    lore += listOf("§7点击翻到第 §f${currentPage} §7页")
                    hideAllCompat()
                }) {
                    isCancelled = true
                    open(clicker as Player, currentPage - 1)
                }
            } else {
                set('P', buildItem(Material.GRAY_STAINED_GLASS_PANE) {
                    name = "§7已是第一页"
                    hideAllCompat()
                }){
                    isCancelled
                }
            }

            // 设置信息
            set('I', buildItem(Material.BOOK) {
                name = "§e配方图鉴"
                lore += listOf(
                    "§7共 §f${allRecipes.size} §7个配方",
                    "§7当前第 §f${currentPage + 1}§7/§f${maxPage + 1} §7页"
                )
                hideAllCompat()
            }){
                isCancelled = true
            }

            // 设置下一页
            if (currentPage < maxPage) {
                set('N', buildItem(Material.ARROW) {
                    name = "§e下一页"
                    lore += listOf("§7点击翻到第 §f${currentPage + 2} §7页")
                    hideAllCompat()
                }) {
                    isCancelled = true
                    open(clicker as Player, currentPage + 1)
                }
            } else {
                set('N', buildItem(Material.GRAY_STAINED_GLASS_PANE) {
                    name = "§7已是最后一页"
                    hideAllCompat()
                }){
                    isCancelled = true
                }
            }

            // 设置配方项
            pageRecipes.forEachIndexed { index, (key, result) ->
                val previewItem = buildRecipePreview(key, result)
                set(index, previewItem) {
                    isCancelled = true
                }
            }
        }
    }

    private fun buildRecipePreview(key: TempAlchemyList, result: ItemStack): ItemStack {
        val preview = result.clone()
        val meta = preview.itemMeta ?: return preview

        val lore = meta.lore?.toMutableList() ?: mutableListOf()
        lore += ""
        lore += "§e── 所需材料 ──"

        val catalystId = key.slotItems[0]
        if (catalystId != null) {
            val catalystName = getItemDisplayName(catalystId)
            lore += "§7药引: §f$catalystName §7x${key.slotAmounts[0]}"
        } else {
            lore += "§7药引: §8无"
        }

        for (i in 0 until 5) {
            val elemId = key.slotItems[i + 1]
            if (elemId != null) {
                val elemName = getItemDisplayName(elemId)
                lore += "§7元素${i + 1}: §f$elemName §7x${key.slotAmounts[i + 1]}"
            }
        }

        lore += ""
        lore += "§a产出: §fx${result.amount}"

        meta.lore = lore
        preview.itemMeta = meta
        return preview
    }

    private fun getItemDisplayName(itemId: String): String {
        val item = getOriginalItemStack(itemId)
        if (item != null) {
            val displayName = item.itemMeta?.displayName
            if (displayName != null && displayName.isNotEmpty()) {
                return displayName
            }
        }
        return formatId(itemId)
    }

    private fun getOriginalItemStack(itemId: String): ItemStack? {
        return DewItem.fromId(itemId)?.itemStack
    }

    private fun formatId(itemId: String): String {
        return if (itemId.startsWith("minecraft:")) {
            itemId.removePrefix("minecraft:").replace('_', ' ')
        } else {
            itemId
        }
    }
}