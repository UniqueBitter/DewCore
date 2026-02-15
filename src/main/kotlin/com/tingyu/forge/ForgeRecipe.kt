package com.tingyu.forge

import com.tingyu.item.DewItem
import com.tingyu.item.util.AddPDC
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sun.awt.geom.AreaOp
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info


data class TempRecipeList(
    val slotItems: Array<String?> = arrayOfNulls(6),
    val slotAmounts: IntArray = IntArray(6)
) {
    companion object {
        val ITEM_ID = AddPDC.DEW_ID_KEY
        // 统一获取物品标识的方法：优先取 PDC ID，没有则取材质名
        fun getItemId(item: ItemStack?): String? {
            if (item == null || item.type.isAir) return null
            return item.itemMeta?.persistentDataContainer?.get(ITEM_ID, PersistentDataType.STRING)
                ?: "minecraft:${item.type.name.lowercase()}"
        }

        // 快速从物品数组生成匹配 Key
        fun fromItems(items: Array<ItemStack?>): TempRecipeList {
            val recipe = TempRecipeList()
            items.forEachIndexed { index, item ->
                if (index < 6) {
                    recipe.slotItems[index] = getItemId(item)
                    recipe.slotAmounts[index] = item?.amount ?: 0
                }
            }
            return recipe
        }
    }

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

object ForgeRecipe {
    private val recipes = HashMap<TempRecipeList, ItemStack>()
    fun getRecipes(): Map<TempRecipeList, ItemStack> = recipes

    // 你最想要的写法：直接传入 ItemStack 列表
    fun register(result: ItemStack, vararg materials: ItemStack?) {
        // 将传入的 materials (vararg 自动转为数组) 转换为 TempRecipeList
        val key = TempRecipeList.fromItems(materials.toList().toTypedArray())
        recipes[key] = result
    }

    // 专门为 DewItem 准备的重载
    fun register(result: DewItem, vararg materials: Any?) {
        val itemStacks = materials.map {
            when (it) {
                is DewItem -> it.getItem()
                is ItemStack -> it
                is Material -> ItemStack(it)
                else -> null
            }
        }.toTypedArray()

        register(result.getItem(), *itemStacks)
    }

    @Awake(LifeCycle.ENABLE)
    fun init() {
        recipes.clear()

        // 调用刚才在另一个文件里写的扩展函数
        registerAll()

        info("[锻造系统] 已成功加载 ${recipes.size} 个自定义配方")
    }
}