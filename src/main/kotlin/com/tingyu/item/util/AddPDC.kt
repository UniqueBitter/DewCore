package com.tingyu.item.util

import com.tingyu.item.equip.EquipData
import com.tingyu.item.equip.EquipManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import taboolib.platform.util.ItemBuilder

object AddPDC {
    val DEW_ID_KEY = NamespacedKey("dew", "item_id")

    fun ItemBuilder.addPDC(id: String) {
        if (originMeta == null) {
            originMeta = Bukkit.getItemFactory().getItemMeta(material)
        }
        originMeta?.persistentDataContainer?.set(DEW_ID_KEY, PersistentDataType.STRING, id)
    }

    /** 写入装备的职业与等阶 PDC */
    fun ItemBuilder.addEquipPDC(equipData: EquipData) {
        if (originMeta == null) {
            originMeta = Bukkit.getItemFactory().getItemMeta(material)
        }
        val pdc = originMeta?.persistentDataContainer ?: return
        pdc.set(EquipManager.EQUIP_JOB_KEY,  PersistentDataType.STRING,  equipData.job?.name ?: "ALL")
        pdc.set(EquipManager.EQUIP_TIER_KEY, PersistentDataType.INTEGER, equipData.tier)
    }
}