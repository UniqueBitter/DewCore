package com.tingyu.item

import com.tingyu.item.equip.EquipData
import org.bukkit.Material

/**
 * 物品配置数据类
 */
data class ItemData(
    val material: Material,
    val displayName: String,
    val id: String,
    val lore: List<String> = emptyList(),
    val equipData: EquipData? = null
)