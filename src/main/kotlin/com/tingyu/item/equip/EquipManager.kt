package com.tingyu.item.equip

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.tingyu.player.PlayerManager
import com.tingyu.player.StatApplier
import com.tingyu.player.job.Job
import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import taboolib.common.platform.event.SubscribeEvent

object EquipManager {

    val EQUIP_JOB_KEY  = NamespacedKey("dew", "equip_job")
    val EQUIP_TIER_KEY = NamespacedKey("dew", "equip_tier")

    private val ARMOR_SLOTS = listOf(
        EquipmentSlot.HEAD,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET
    )

    // ======================== 公开 API ========================

    /**
     * 扫描玩家全身装备，汇总所有有效属性层，重新应用到 Minecraft Attribute。
     * 在职业改变、装备变动、玩家加入时调用。
     */
    fun recalculate(player: Player) {
        val playerJob = PlayerManager.get(player).job
        val combined  = mutableMapOf<StatType, StatLayer>()

        for (slot in ARMOR_SLOTS) {
            val item = player.equipment?.getItem(slot) ?: continue
            if (item.type.isAir) continue
            val layers = getLayersFromItem(item, slot, playerJob) ?: continue
            for ((type, layer) in layers) {
                combined[type] = (combined[type] ?: StatLayer()) + layer
            }
        }

        StatApplier.apply(player, combined)
    }

    /** 判断某物品是否为 DewCore 装备 */
    fun isEquip(item: ItemStack): Boolean =
        item.itemMeta?.persistentDataContainer?.has(EQUIP_TIER_KEY, PersistentDataType.INTEGER) == true

    // ======================== 内部逻辑 ========================

    private fun getLayersFromItem(
        item: ItemStack,
        slot: EquipmentSlot,
        playerJob: Job
    ): Map<StatType, StatLayer>? {
        val pdc  = item.itemMeta?.persistentDataContainer ?: return null
        val tier = pdc.get(EQUIP_TIER_KEY, PersistentDataType.INTEGER) ?: return null
        val jobName = pdc.get(EQUIP_JOB_KEY, PersistentDataType.STRING) ?: return null

        val itemJob = if (jobName == "ALL") null
                      else runCatching { Job.valueOf(jobName) }.getOrNull()

        // 职业不匹配则不给属性
        if (itemJob != null && itemJob != playerJob) return null

        return EquipStatTable.getStats(itemJob, tier, slot)
    }

    // ======================== 事件 ========================

    @SubscribeEvent
    fun onArmorChange(event: PlayerArmorChangeEvent) {
        recalculate(event.player)
    }
}
