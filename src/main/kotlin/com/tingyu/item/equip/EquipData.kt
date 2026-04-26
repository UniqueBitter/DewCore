package com.tingyu.item.equip

import com.tingyu.player.job.Job
import org.bukkit.inventory.EquipmentSlot

/**
 * 装备元数据，嵌入 ItemData 后会在 itemStack 构建时写入 PDC。
 *
 * @param job  职业限制，null 表示全职业通用
 * @param tier 等阶 (1-6)
 * @param slot 装备槽位
 */
data class EquipData(
    val job: Job?,
    val tier: Int,
    val slot: EquipmentSlot
)
