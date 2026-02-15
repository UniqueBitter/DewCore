package com.tingyu.forge

import com.tingyu.item.Element
import com.tingyu.item.Pill

/**
 * 炼丹配方注册
 * 格式: AlchemyRecipe.register(成品, 药引, 元素1, 元素2, 元素3, 元素4, 元素5)
 *
 * 示例:
 * AlchemyRecipe.register(
 *     Pill.HEALING_DRINK_BASIC,        // 成品
 *     Element.ELEMENT_WOOD,            // 药引
 *     Element.ELEMENT_FIRE to 2,       // 元素1 (2个)
 *     Element.ELEMENT_WATER,           // 元素2
 *     Element.ELEMENT_EARTH,           // 元素3
 *     null,                            // 元素4 (空)
 *     null                             // 元素5 (空)
 * )
 */
fun registerAllAlchemy() {
    // 在这里注册炼丹配方 -药引-金木水火土
    AlchemyRecipe.register(
        Pill.WOOD_THROW_ADVANCED to 64,
        Element.ELEMENT_WOOD to 5,
    )
}