package com.tingyu.item.util

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import taboolib.platform.util.ItemBuilder

object AddPDC {
    // 定义全局统一的 Key
    val DEW_ID_KEY = NamespacedKey("dew", "item_id")

    /**
     * 极简 PDC 注册方法
     * 存储结果：键 "dew:item_id" -> 值 "传入的id"
     */
    fun ItemBuilder.addPDC(id: String) {
        if (originMeta == null) {
            originMeta = Bukkit.getItemFactory().getItemMeta(material)
        }
        originMeta?.persistentDataContainer?.set(DEW_ID_KEY, PersistentDataType.STRING, id)
    }
}