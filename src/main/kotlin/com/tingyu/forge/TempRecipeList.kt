package com.tingyu.forge

import com.tingyu.item.util.AddPDC
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

data class TempRecipeList(
    val slotItems: Array<String?> = arrayOfNulls(6),
    val slotAmounts: IntArray = IntArray(6)
) {
    companion object {
        // 确保这个 Key 与你 DewItem.itemStack 里的 addPDC("id") 使用的 Key 一致
        val ITEM_ID: NamespacedKey = AddPDC.DEW_ID_KEY

        /**
         * 获取物品的唯一标识
         */
        fun getItemId(item: ItemStack?): String? {
            if (item == null || item.type.isAir) return null
            val meta = item.itemMeta ?: return "minecraft:${item.type.name.lowercase()}"

            // 1. 尝试读取 PDC 自定义 ID
            val customId = meta.persistentDataContainer.get(ITEM_ID, PersistentDataType.STRING)
            if (customId != null) return customId

            // 2. 如果没有自定义 ID，则返回原版材质名
            return "minecraft:${item.type.name.lowercase()}"
        }

        /**
         * 将 ItemStack 数组转换为匹配 Key
         */
        fun fromItems(items: Array<ItemStack?>): TempRecipeList {
            val recipe = TempRecipeList()
            // 强制循环 6 次，对应 6 个槽位
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

    // 必须重写以实现 HashMap 的严格匹配
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TempRecipeList) return false
        return slotItems.contentEquals(other.slotItems) && slotAmounts.contentEquals(other.slotAmounts)
    }

    override fun hashCode(): Int {
        var result = slotItems.contentHashCode()
        result = 31 * result + slotAmounts.contentHashCode()
        return result
    }
}