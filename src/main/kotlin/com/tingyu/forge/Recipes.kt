package com.tingyu.forge

import com.tingyu.forge.ForgeRecipe.register
import com.tingyu.item.DewItem
import com.tingyu.item.Element
import com.tingyu.item.util.ForgeRecipe

/**
 * 专门存放配方注册逻辑的文件
 */
fun registerAll() {

    // 示例：黄金苹果配方
    // 参数顺序对应你的：成品, 元素, 材料1, 材料2, 部位材料, 精炼材料, 核心
    register(
        DewItem.GOLD_APPLE to 64,
        DewItem.APPLE to 64, null, null, null, null, null
    )

    register(
        DewItem.GOLD_APPLE,
        DewItem.APPLE to 1, null, null, null, null, null
    )

    register(
        Element.ELEMENT_WOOD_AGAIN_REFINED,
        Element.ELEMENT_WOOD to 64, null, null, null, null, null
    )


}