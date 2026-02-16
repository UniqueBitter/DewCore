package com.tingyu.forge

import com.tingyu.item.util.AddPDC
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * 炼丹配方匹配 Key
 * 槽位 0 = 药引，槽位 1-5 = 五种元素
 */
data class TempAlchemyList(
    val slotItems: Array<String?> = arrayOfNulls(6),
    val slotAmounts: IntArray = IntArray(6)
) {
    companion object {
        val ITEM_ID: NamespacedKey = AddPDC.DEW_ID_KEY

        fun getItemId(item: ItemStack?): String? {
            if (item == null || item.type.isAir) return null
            val meta = item.itemMeta ?: return "minecraft:${item.type.name.lowercase()}"
            val customId = meta.persistentDataContainer.get(ITEM_ID, PersistentDataType.STRING)
            if (customId != null) return customId
            return "minecraft:${item.type.name.lowercase()}"
        }

        fun fromItems(items: Array<ItemStack?>): TempAlchemyList {
            val recipe = TempAlchemyList()
            for (i in 0 until 6) {
                val item = if (i < items.size) items[i] else null
                if (item != null && !item.type.isAir) {
                    recipe.slotItems[i] = getItemId(item)
                    recipe.slotAmounts[i] = item.amount
                } else {
                    recipe.slotItems[i] = null
                    recipe.slotAmounts[i] = 0
                }
            }
            return recipe
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TempAlchemyList) return false
        return slotItems.contentEquals(other.slotItems) && slotAmounts.contentEquals(other.slotAmounts)
    }

    override fun hashCode(): Int {
        var result = slotItems.contentHashCode()
        result = 31 * result + slotAmounts.contentHashCode()
        return result
    }
}