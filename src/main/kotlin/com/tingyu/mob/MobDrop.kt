package com.tingyu.mob

import org.bukkit.inventory.ItemStack

/**
 * @param supplier 掉落物提供者（懒加载，怪死亡时才调用，避免初始化顺序问题）
 * @param amount   掉落数量范围
 * @param chance   掉落概率，0.0–1.0
 */
class MobDrop(
    val supplier: () -> ItemStack?,
    val amount: IntRange = 1..1,
    val chance: Double = 1.0
)
