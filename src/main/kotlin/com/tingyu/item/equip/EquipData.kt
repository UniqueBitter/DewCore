package com.tingyu.item.equip

import com.tingyu.player.job.Job
import org.bukkit.inventory.EquipmentSlot

/**
 * 装备元数据，嵌入 ItemData 后写入物品 PDC。
 *
 * @param job     职业限制，null = 全职业通用
 * @param tier    等阶 (1-6)
 * @param slot    装备槽位
 * @param element 五行属性
 */
data class EquipData(
    val job: Job?,
    val tier: Int,
    val slot: EquipmentSlot,
    val element: FiveElement
)
