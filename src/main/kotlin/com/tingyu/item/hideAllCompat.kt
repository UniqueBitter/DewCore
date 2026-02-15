@file:Suppress("DEPRECATION", "removal")

package com.tingyu.item

import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.ItemBuilder
import java.util.*

/**
 * 兼容 1.21.1 的隐藏工具
 * 用法：
 *   buildItem(XMaterial.NETHERITE_AXE) {
 *       name = "§c锻造"
 *       hideAllCompat()
 *   }
 */
fun ItemBuilder.hideAllCompat() {
    ItemFlag.entries.forEach { flag ->
        try { flags += flag } catch (_: Exception) {}
    }
    val originalFinishing = finishing
    finishing = { itemStack ->
        try {
            val meta = itemStack.itemMeta
            if (meta != null) {
                // 先加一个再删掉 → 属性列表变成"非null的空"，覆盖默认属性
                val dummy = AttributeModifier(
                    UUID.randomUUID(), "dummy", 0.0, AttributeModifier.Operation.ADD_NUMBER
                )
                meta.addAttributeModifier(Attribute.GENERIC_LUCK, dummy)
                meta.removeAttributeModifier(Attribute.GENERIC_LUCK)
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                itemStack.itemMeta = meta
            }
        } catch (_: Exception) {}
        originalFinishing(itemStack)
    }
}

/**
 * 直接对 ItemStack 隐藏所有信息
 * 用法：
 *   val item = someItemStack.hideAllCompat()
 */
fun ItemStack.hideAllCompat(): ItemStack {
    val meta = itemMeta ?: return this
    ItemFlag.entries.forEach { flag ->
        try { meta.addItemFlags(flag) } catch (_: Exception) {}
    }
    try {
        val dummy = AttributeModifier(
            UUID.randomUUID(), "dummy", 0.0, AttributeModifier.Operation.ADD_NUMBER
        )
        meta.addAttributeModifier(Attribute.GENERIC_LUCK, dummy)
        meta.removeAttributeModifier(Attribute.GENERIC_LUCK)
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
    } catch (_: Exception) {}
    itemMeta = meta
    return this
}