package com.tingyu.item.equip

import com.tingyu.player.job.Job
import org.bukkit.inventory.EquipmentSlot

/**
 * 装备元数据，嵌入 ItemData 后写入物品 PDC。
 *
 * @param job          职业限制，null = 全职业通用
 * @param tier         等阶 (1–6)，公式中 L = tier
 * @param slot         装备槽位
 * @param element      五行属性
 * @param expRequired    阅历需求（类等级门槛）
 * @param affixes        词条列表（护甲: 词条；武器: 魔咒）
 * @param description    装备/武器描述文字（可多行，换行用 \n）
 * @param tierDescription 等阶描述（武器专用，显示在等阶旁）
 * @param uniqueEffect   独立效果文字（每个元素为 lore 一行）
 * @param branchEffect   武器分支效果（武器专用）
 * @param passiveEffect  武器特效/被动效果（武器专用）
 * @param spiritSlots    武器载灵槽列表（武器专用）
 * @param crafter        锻造者名称
 */
data class EquipData(
    val job: Job?,
    val tier: Int,
    val slot: EquipmentSlot,
    val element: FiveElement,
    val expRequired: Int = 0,
    val affixes: List<Affix> = emptyList(),
    val description: String = "",
    val tierDescription: String = "",
    val uniqueEffect: List<String> = emptyList(),
    val branchEffect: List<String> = emptyList(),
    val passiveEffect: List<String> = emptyList(),
    val spiritSlots: List<SpiritSlot> = emptyList(),
    val crafter: String = ""
)
