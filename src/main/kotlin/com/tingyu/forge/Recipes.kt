package com.tingyu.forge

import com.tingyu.item.DewItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * 专门存放配方注册逻辑的文件
 */
fun ForgeRecipe.registerAll() {

    // 示例：黄金苹果配方
    // 参数顺序对应你的：成品, 元素, 材料1, 材料2, 部位材料, 精炼材料, 核心
    register(
        DewItem.GOLD_APPLE,
        DewItem.APPLE, null, null, null, null, DewItem.APPLE
    )

}