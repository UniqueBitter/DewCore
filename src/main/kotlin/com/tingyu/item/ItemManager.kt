package com.tingyu.item

import com.google.gson.Gson
import org.bukkit.NamespacedKey
import java.util.UUID
import ink.ptms.um.Mythic
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ItemManager {

    /**
     * 玩家数据Map，键为UUID，值为任意可嵌套的数据结构
     */
    private val playerData = mutableMapOf<UUID, MutableMap<String, Any?>>()
    private val gson = Gson()
    private val dataKey = NamespacedKey("dew", "player_data")
    fun open(player: Player){

    }


}