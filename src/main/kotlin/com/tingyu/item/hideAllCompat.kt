@file:Suppress("DEPRECATION", "removal")

package com.tingyu.item

import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
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
    // 添加所有安全的 ItemFlag（过滤掉运行时不存在的）
    ItemFlag.entries.forEach { flag ->
        try {
            flags += flag
        } catch (_: Exception) {
        }
    }
    // 1.20.5+ HIDE_ATTRIBUTES 需要物品本身有 attribute 数据才能生效
    // 通过 finishing 回调在 build 完成后补一个空属性修饰符
    val originalFinishing = finishing
    finishing = { itemStack ->
        try {
            val meta = itemStack.itemMeta
            if (meta != null && !meta.hasAttributeModifiers()) {
                meta.addAttributeModifier(
                    Attribute.GENERIC_LUCK,
                    AttributeModifier(
                        UUID.randomUUID(),
                        "taboolib_hide",
                        0.0,
                        AttributeModifier.Operation.ADD_NUMBER
                    )
                )
                itemStack.itemMeta = meta
            }
        } catch (_: Exception) {
            // 低版本不支持则跳过
        }
        originalFinishing(itemStack)
    }
}

/**
 * 直接对 ItemStack 隐藏所有信息（已构建好的物品也能用）
 * 用法：
 *   val item = someItemStack.hideAllCompat()
 */
fun ItemStack.hideAllCompat(): ItemStack {
    val meta = itemMeta ?: return this
    // 添加所有 flag
    ItemFlag.entries.forEach { flag ->
        try {
            meta.addItemFlags(flag)
        } catch (_: Exception) {
        }
    }
    // 补空属性修饰符让 HIDE_ATTRIBUTES 生效
    try {
        if (!meta.hasAttributeModifiers()) {
            meta.addAttributeModifier(
                Attribute.GENERIC_LUCK,
                AttributeModifier(
                    UUID.randomUUID(),
                    "taboolib_hide",
                    0.0,
                    AttributeModifier.Operation.ADD_NUMBER
                )
            )
        }
    } catch (_: Exception) {
    }
    itemMeta = meta
    return this
}