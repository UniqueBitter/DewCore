package com.tingyu.alchemy

import com.tingyu.command.DewItem
import com.tingyu.forge.TempAlchemyList
import com.tingyu.forge.registerAllAlchemy
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info

object AlchemyRecipe {
    private val recipes = HashMap<TempAlchemyList, ItemStack>()
    fun getRecipes(): Map<TempAlchemyList, ItemStack> = recipes

    /**
     * 注册炼丹配方
     * @param result   成品
     * @param catalyst 药引
     * @param elements 五种元素 (不足的传 null)
     */
    fun register(result: Any, catalyst: Any?, vararg elements: Any?) {
        val resultStack = toItemStack(result) ?: return

        // 6 个槽位：[0]=药引, [1..5]=元素
        val fixedMaterials = arrayOfNulls<ItemStack>(6)
        fixedMaterials[0] = toItemStack(catalyst)

        elements.forEachIndexed { index, e ->
            if (index < 5) {
                fixedMaterials[index + 1] = toItemStack(e)
            }
        }

        val key = TempAlchemyList.Companion.fromItems(fixedMaterials)
        recipes[key] = resultStack
    }

    private fun toItemStack(obj: Any?): ItemStack? {
        val item = when (obj) {
            is Pair<*, *> -> {
                val base = obj.first
                val amount = (obj.second as? Number)?.toInt() ?: 1
                when (base) {
                    is DewItem -> base.getItem(amount)
                    is Material -> ItemStack(base, amount)
                    is ItemStack -> base.clone().apply { this.amount = amount }
                    else -> null
                }
            }
            is DewItem -> obj.getItem()
            is Material -> ItemStack(obj)
            is ItemStack -> obj
            null -> null
            else -> null
        }
        // 1.21+ : 如果数量超过默认堆叠上限，强制设为64
        if (item != null && item.amount > item.maxStackSize) {
            val meta = item.itemMeta
            if (meta != null) {
                try {
                    val method = meta.javaClass.getMethod("setMaxStackSize", Integer::class.java)
                    method.invoke(meta, 64)
                    item.itemMeta = meta
                } catch (_: Exception) {}
            }
        }
        return item
    }

    @Awake(LifeCycle.ACTIVE)
    fun init() {
        recipes.clear()
        registerAllAlchemy()
        info("[炼丹系统] 已成功加载 ${recipes.size} 个炼丹配方")
    }
}