package ltd.mc233.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag

object ItemGet {

    /**
     * 检测物品是否具有指定的ID
     * @param item 要检查的物品堆栈
     * @param itemid 要匹配的NBT ID值，例如"panling:vantone_tick"
     * @return 如果物品不为空且NBT ID匹配则返回true，否则返回false
     */
    fun checkItem(item: ItemStack, itemid: String): Boolean {
        if (item.type == Material.AIR) {
            return false
        }
        return try {
            item.getItemTag().getDeep("id")?.asString() == itemid
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 检测玩家背包中指定NBT ID的物品数量
     * @param player 要检查的玩家
     * @param itemId 要匹配的NBT ID值，例如"panling:vantone_tick"
     * @return 匹配物品的总数量
     */
    fun getItemCount(player: Player, itemId: String): Int {
        return player.inventory.contents
            .filterNotNull()
            .filter { checkItem(it, itemId) }
            .sumOf { it.amount }
    }


    /**
     * 根据NBT ID判断要不要取走物品
     * @param player 要检查的玩家
     * @param itemId 要匹配的NBT ID值
     * @param takeCount 要取走的数量
     * @return 如果数量足够并成功扣除则返回true，否则返回false
    **/
    fun takeItem(player: Player, itemId: String, takeCount: Int): Boolean {
        return if (getItemCount(player, itemId) >= takeCount) {
            removeItems(player, itemId, takeCount)
            true
        } else {
            false
        }
    }

    /**
     * 根据NBT ID判断要不要取走物品
     * @param player 要检查的玩家
     * @param itemId 要匹配的NBT ID值
     * @param count 要取走的数量
     * @return 如果数量足够并成功扣除则返回true，否则返回false
     **/
    fun removeItems(player: Player, itemId: String, count: Int) {
        var remaining = count
        for (item in player.inventory.contents) {
            if (item != null && checkItem(item, itemId) && remaining > 0) {
                val removeAmount = minOf(item.amount, remaining)
                remaining -= removeAmount

                if (removeAmount >= item.amount) {
                    player.inventory.removeItem(item)
                } else {
                    item.amount -= removeAmount
                }
            }
        }
    }
}