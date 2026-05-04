package com.tingyu.player

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tingyu.player.job.Job
import com.tingyu.player.race.Race
import com.tingyu.player.stat.StatCalculator
import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.WorldSaveEvent
import org.bukkit.persistence.PersistentDataType
import taboolib.common.platform.event.SubscribeEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PlayerManager {

    private val profiles = ConcurrentHashMap<UUID, PlayerProfile>()
    private val gson = Gson()
    private val dataKey = NamespacedKey("dew", "player_profile")

    // ======================== 事件 ========================

    @SubscribeEvent
    fun onJoin(event: PlayerJoinEvent) {
        load(event.player)
        LevelManager.rebuildBaseStats(get(event.player))
        com.tingyu.item.equip.EquipManager.recalculate(event.player)
    }

    @SubscribeEvent
    fun onQuit(event: PlayerQuitEvent) {
        save(event.player)
        profiles.remove(event.player.uniqueId)
    }

    @SubscribeEvent
    fun onWorldSave(event: WorldSaveEvent) {
        event.world.players.forEach { save(it) }
    }

    // ======================== 公开 API ========================

    /** 获取玩家档案，若未加载则从 PDC 读取 */
    fun get(player: Player): PlayerProfile =
        profiles.getOrPut(player.uniqueId) { loadOrDefault(player) }

    fun get(uuid: UUID): PlayerProfile? = profiles[uuid]

    /** 立即保存指定玩家 */
    fun saveNow(player: Player) = save(player)

    /** 重新计算并应用玩家属性（含装备加成，修改 baseStats 或职业后调用） */
    fun refresh(player: Player) = com.tingyu.item.equip.EquipManager.recalculate(player)

    /** 给予经验，自动处理升级 */
    fun gainExp(player: Player, amount: Long) = LevelManager.gainExp(player, amount)

    /**
     * 计算玩家某属性的最终值。
     *
     * @param equipmentLayers 装备贡献的 StatLayer 列表（在此叠加到基础层上）
     * @param arrowType 箭矢速度公式类型（1 或 2）
     */
    fun computeStat(
        player: Player,
        type: StatType,
        equipmentLayers: List<StatLayer> = emptyList(),
        arrowType: Int = 1
    ): Double {
        val baseLayer = get(player).buildLayer(type)
        val combined = equipmentLayers.fold(baseLayer) { acc, layer -> acc + layer }
        return StatCalculator.compute(type, combined, arrowType)
    }

    // ======================== 内部存储 ========================

    private fun load(player: Player) {
        profiles[player.uniqueId] = loadOrDefault(player)
    }

    private fun loadOrDefault(player: Player): PlayerProfile {
        val json = player.persistentDataContainer.get(dataKey, PersistentDataType.STRING)
            ?: return PlayerProfile(player.uniqueId)
        return fromJson(player.uniqueId, json)
    }

    private fun save(player: Player) {
        val profile = profiles[player.uniqueId] ?: return
        player.persistentDataContainer.set(dataKey, PersistentDataType.STRING, toJson(profile))
    }

    private fun toJson(profile: PlayerProfile): String {
        val map = mapOf(
            "job" to profile.job.name,
            "race" to profile.race.name,
            "level" to profile.level,
            "jobPromotion" to profile.jobPromotion,
            "exp" to profile.exp,
            "baseStats" to profile.baseStats.entries.associate { (k, v) -> k.name to v }
        )
        return gson.toJson(map)
    }

    private fun fromJson(uuid: UUID, json: String): PlayerProfile {
        val profile = PlayerProfile(uuid)
        runCatching {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val map: Map<String, Any> = gson.fromJson(json, type)

            (map["job"] as? String)?.let { runCatching { profile.job = Job.valueOf(it) } }
            (map["race"] as? String)?.let { runCatching { profile.race = Race.valueOf(it) } }
            (map["level"] as? Double)?.let { profile.level = it.toInt() }
            (map["jobPromotion"] as? Double)?.let { profile.jobPromotion = it.toInt() }
            (map["exp"] as? Double)?.let { profile.exp = it.toLong() }

            @Suppress("UNCHECKED_CAST")
            (map["baseStats"] as? Map<String, Double>)?.forEach { (k, v) ->
                runCatching { profile.setBase(StatType.valueOf(k), v) }
            }
        }
        return profile
    }
}
