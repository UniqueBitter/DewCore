package com.tingyu.forge

import com.tingyu.command.DewItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info

object ForgeRecipe {
    private val recipes = HashMap<TempRecipeList, ItemStack>()
    fun getRecipes(): Map<TempRecipeList, ItemStack> = recipes

    /**
     * 最通用的注册方法
     * @param result 成品 (DewItem, ItemStack 或 Material)
     * @param materials 原料列表 (支持 DewItem, Material, ItemStack 或 Pair)
     */
    fun register(result: Any, vararg materials: Any?) {
        // --- 修复：让成品也支持 Pair (例如 DewItem.GOLD_APPLE to 64) ---
        val resultStack = when (result) {
            is Pair<*, *> -> {
                val base = result.first
                val amount = (result.second as? Number)?.toInt() ?: 1
                when (base) {
                    is DewItem -> base.getItem(amount)
                    is Material -> ItemStack(base, amount)
                    is ItemStack -> base.clone().apply { this.amount = amount }
                    else -> return
                }
            }
            is DewItem -> result.getItem()
            is Material -> ItemStack(result)
            is ItemStack -> result
            else -> return
        }

        // --- 解析原料 (保持固定长度 6) ---
        val fixedMaterials = arrayOfNulls<ItemStack>(6)
        materials.forEachIndexed { index, m ->
            if (index < 6) {
                fixedMaterials[index] = when (m) {
                    is DewItem -> m.getItem()
                    is Material -> ItemStack(m)
                    is ItemStack -> m
                    is Pair<*, *> -> {
                        val base = m.first
                        val amt = (m.second as? Number)?.toInt() ?: 1
                        when (base) {
                            is DewItem -> base.getItem(amt)
                            is Material -> ItemStack(base as Material, amt)
                            is ItemStack -> base.clone().apply { amount = amt }
                            else -> null
                        }
                    }
                    else -> null
                }
            }
        }

        val key = TempRecipeList.fromItems(fixedMaterials)
        recipes[key] = resultStack
    }

    @Awake(LifeCycle.ENABLE)
    fun init() {
        recipes.clear()
        registerAll()
        info("[锻造系统] 已成功加载 ${recipes.size} 个严格匹配配方")
    }
}