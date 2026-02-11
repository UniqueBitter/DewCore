package com.tingyu.player

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tingyu.common.pdc.PlayerDataManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import java.util.*

object PlayerManager {

    private val players = mutableMapOf<UUID, DewPlayer>()
    private val gson: Gson = GsonBuilder()
        .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT) // 跳过 @Transient 字段
        .create()

    private const val DATA_KEY = "dew_player"

    // ======================== 事件监听 ========================

    @SubscribeEvent
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        players[player.uniqueId] = load(player)
    }

    @SubscribeEvent
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        save(player)
        players.remove(player.uniqueId)
    }
    private fun load(player: Player): DewPlayer {
        val json = PlayerDataManager.dataOf(player)[DATA_KEY] as? String
        return if (json != null) {
            try {
                gson.fromJson(json, DewPlayer::class.java)
            } catch (e: Exception) {
                // JSON损坏时兜底，创建新数据
                DewPlayer(uuid = player.uniqueId.toString(), name = player.name)
            }
        } else {
            DewPlayer(uuid = player.uniqueId.toString(), name = player.name)
        }
    }
    private fun save(player: Player) {
        val dew = players[player.uniqueId] ?: return
        PlayerDataManager.dataOf(player)[DATA_KEY] = gson.toJson(dew)
        PlayerDataManager.saveNow(player)
    }
    fun saveAll() {
        Bukkit.getOnlinePlayers().forEach { save(it) }
    }
    fun savePlayer(player: Player) = save(player)
    fun reloadPlayer(player: Player) {
        players[player.uniqueId] = load(player)
    }
    // ======================== 公开 API ========================
    fun get(player: Player): DewPlayer {
        return players[player.uniqueId] ?: load(player).also { players[player.uniqueId] = it }
    }
    fun get(uuid: UUID): DewPlayer? = players[uuid]
    fun getAll(): Map<UUID, DewPlayer> = players
    // ======================== 扩展属性 ========================

    val Player.dew: DewPlayer
        get() = PlayerManager.get(this)
}