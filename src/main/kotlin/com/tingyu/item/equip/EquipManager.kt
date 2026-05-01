package com.tingyu.item.equip

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.tingyu.player.PlayerManager
import com.tingyu.player.StatApplier
import com.tingyu.player.job.Job
import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import taboolib.common.platform.event.SubscribeEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object EquipManager {

    val EQUIP_JOB_KEY     = NamespacedKey("dew", "equip_job")
    val EQUIP_TIER_KEY    = NamespacedKey("dew", "equip_tier")
    val EQUIP_ELEMENT_KEY = NamespacedKey("dew", "equip_element")

    private val equipStatCache = ConcurrentHashMap<UUID, Map<StatType, StatLayer>>()

    private val ARMOR_SLOTS = listOf(
        EquipmentSlot.HEAD,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET,
        EquipmentSlot.HAND,
    )

    // ======================== 公开 API ========================

    /**
     * 扫描全身装备，汇总属性（含套装共鸣），写入原版 Attribute。
     */
    fun recalculate(player: Player) {
        val playerJob = PlayerManager.get(player).job
        val perPiece  = mutableMapOf<StatType, StatLayer>()

        // 收集每件装备信息（用于共鸣计算）
        val equippedElements = mutableListOf<Pair<FiveElement, Int>?>()

        for (slot in ARMOR_SLOTS) {
            val item = player.equipment?.getItem(slot)
            if (item == null || item.type.isAir) {
                equippedElements.add(null)
                continue
            }
            val meta = readEquipMeta(item, playerJob)
            if (meta == null) {
                equippedElements.add(null)
                continue
            }

            val (job, tier, element) = meta
            equippedElements.add(element to tier)

            // 单件属性
            EquipStatTable.compute(job, tier, slot, element).forEach { (type, layer) ->
                perPiece[type] = (perPiece[type] ?: StatLayer()) + layer
            }
        }

        // 套装共鸣叠加
        val resonance = ResonanceCalc.compute(equippedElements)
        resonance.forEach { (type, layer) ->
            perPiece[type] = (perPiece[type] ?: StatLayer()) + layer
        }

        equipStatCache[player.uniqueId] = perPiece
        StatApplier.apply(player, perPiece)
    }

    /** 获取玩家当前装备贡献的属性层（用于战斗计算），每次 recalculate 后自动更新 */
    fun getEquipStats(player: Player): Map<StatType, StatLayer> =
        equipStatCache[player.uniqueId] ?: emptyMap()

    fun isEquip(item: ItemStack): Boolean =
        item.itemMeta?.persistentDataContainer?.has(EQUIP_TIER_KEY, PersistentDataType.INTEGER) == true

    // ======================== 事件 ========================

    @SubscribeEvent
    fun onArmorChange(event: PlayerArmorChangeEvent) {
        val newItem = event.newItem
        if (newItem != null && !newItem.type.isAir) {
            val pdc = newItem.itemMeta?.persistentDataContainer
            val jobName = pdc?.get(EQUIP_JOB_KEY, PersistentDataType.STRING)
            if (jobName != null && jobName != "ALL") {
                val requiredJob = runCatching { Job.valueOf(jobName) }.getOrNull()
                if (requiredJob != null && requiredJob != PlayerManager.get(event.player).job) {
                    event.player.sendMessage(
                        "§c⚠ 该装备需要 ${requiredJob.displayColor}${requiredJob.displayName}§c 职业，当前属性不生效。"
                    )
                }
            }
        }
        recalculate(event.player)
    }

    @SubscribeEvent
    fun onQuit(event: PlayerQuitEvent) {
        equipStatCache.remove(event.player.uniqueId)
    }

    // ======================== 内部 ========================

    /**
     * 读取物品装备元数据，返回 Triple(job, tier, element)。
     * 职业不匹配时返回 null（不给属性）。
     */
    private fun readEquipMeta(item: ItemStack, playerJob: Job): Triple<Job?, Int, FiveElement>? {
        val pdc = item.itemMeta?.persistentDataContainer ?: return null
        val tier = pdc.get(EQUIP_TIER_KEY, PersistentDataType.INTEGER) ?: return null

        val jobName = pdc.get(EQUIP_JOB_KEY, PersistentDataType.STRING) ?: return null
        val itemJob = if (jobName == "ALL") null
                      else runCatching { Job.valueOf(jobName) }.getOrNull()
        if (itemJob != null && itemJob != playerJob) return null

        val elementName = pdc.get(EQUIP_ELEMENT_KEY, PersistentDataType.STRING) ?: return null
        val element = runCatching { FiveElement.valueOf(elementName) }.getOrNull() ?: return null

        return Triple(itemJob, tier, element)
    }
}
