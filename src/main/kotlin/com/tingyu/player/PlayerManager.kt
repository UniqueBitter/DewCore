package com.tingyu.player

import com.tingyu.common.database.DatabaseManager
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap

object PlayerManager {

    private val playerMap = ConcurrentHashMap<String, DewPlayer>()

    fun get(player: Player): DewPlayer? {
        return playerMap[player.uniqueId.toString()]
    }

    fun get(uuid: String): DewPlayer? {
        return playerMap[uuid]
    }

    @SubscribeEvent
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = player.uniqueId.toString()
        val name = player.name

        // 加载或创建新玩家
        val dewPlayer = DatabaseManager.loadPlayer(uuid)
            ?: DewPlayer(uuid = uuid, name = name)

        playerMap[uuid] = dewPlayer
    }

    @SubscribeEvent
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId.toString()

        val dewPlayer = playerMap[uuid] ?: return

        DatabaseManager.savePlayer(dewPlayer)

        playerMap.remove(uuid)
    }
}