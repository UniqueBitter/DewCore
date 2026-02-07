package com.tingyu.player

import com.tingyu.common.pdc.PlayerDataManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import com.tingyu.player.job.Job
import com.tingyu.player.race.Race
import taboolib.common.platform.event.SubscribeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object PlayerManager {

    /**
     * 在线玩家的 DewPlayer 缓存
     */
    private val players = mutableMapOf<UUID, DewPlayer>()

    // ======================== PDC 键名常量 ========================

    private const val KEY_NAME = "name"
    private const val KEY_RACE = "race"
    private const val KEY_JOB = "job"
    private const val KEY_COPPER = "copper"

    // ======================== 事件监听 ========================

    @SubscribeEvent
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val dewPlayer = load(player)
        players[player.uniqueId] = dewPlayer
    }

    @SubscribeEvent
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        save(player)
        players.remove(player.uniqueId)
    }

    // ======================== 加载/保存 ========================

    /**
     * 从 PDC 加载 DewPlayer
     */
    private fun load(player: Player): DewPlayer {
        val data = PlayerDataManager.dataOf(player)
        return DewPlayer(
            uuid = player.uniqueId.toString(),
            name = data["name"] as? String ?: player.name,
            race = (data["race"] as? String)?.let { runCatching { Race.valueOf(it) }.getOrNull() } ?: Race.NONE,
            job = (data["job"] as? String)?.let { runCatching { Job.valueOf(it) }.getOrNull() } ?: Job.NONE,
            copper = (data["copper"] as? Number)?.toLong() ?: 0L
        )
    }

    /**
     * 将 DewPlayer 保存到 PDC
     */
    private fun save(player: Player) {
        val dewPlayer = players[player.uniqueId] ?: return
        val data = PlayerDataManager.dataOf(player)
        data[KEY_NAME] = dewPlayer.name
        data[KEY_RACE] = dewPlayer.race.name
        data[KEY_JOB] = dewPlayer.job.name
        data[KEY_COPPER] = dewPlayer.copper
        PlayerDataManager.saveNow(player)
    }

    /**
     * 保存所有在线玩家
     */
    fun saveAll() {
        Bukkit.getOnlinePlayers().forEach { save(it) }
    }

    // ======================== 公开 API ========================

    /**
     * 获取 DewPlayer，玩家必须在线
     */
    fun get(player: Player): DewPlayer {
        return players[player.uniqueId] ?: load(player).also { players[player.uniqueId] = it }
    }

    fun get(uuid: UUID): DewPlayer? = players[uuid]

    /**
     * 获取所有在线 DewPlayer
     */
    fun getAll(): Map<UUID, DewPlayer> = players

    // ======================== 便捷操作 ========================

    /**
     * 修改铜币
     */
    fun addCopper(player: Player, amount: Long): Long {
        val dew = get(player)
        dew.copper += amount
        return dew.copper
    }

    fun removeCopper(player: Player, amount: Long): Boolean {
        val dew = get(player)
        if (dew.copper < amount) return false
        dew.copper -= amount
        return true
    }

    fun setCopper(player: Player, amount: Long): Boolean {
        val dew = get(player)
        dew.copper == amount
        return true
    }

    fun hasCopper(player: Player, amount: Long): Boolean {
        return get(player).copper >= amount
    }

    /**
     * 设置种族
     */
    fun setRace(player: Player, race: Race) {
        get(player).race = race
    }

    /**
     * 设置职业
     */
    fun setJob(player: Player, job: Job) {
        get(player).job = job
    }
}