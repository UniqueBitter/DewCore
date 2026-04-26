package com.tingyu.map.zone

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ZoneManager {

    private val zones = LinkedHashMap<String, Zone>()

    // 玩家当前所在区域（可同时在多个嵌套区域内）
    private val playerZones = ConcurrentHashMap<UUID, Set<String>>()

    // 事件回调（按区域 id 注册）
    val onEnter = mutableMapOf<String, (Player, Zone) -> Unit>()
    val onLeave = mutableMapOf<String, (Player, Zone) -> Unit>()

    private val gson = Gson()
    private val file get() = File(getDataFolder(), "zones.json")

    // ======================== 生命周期 ========================

    @Awake(LifeCycle.ENABLE)
    fun load() {
        zones.clear()
        if (!file.exists()) return
        runCatching {
            val type = object : TypeToken<List<Zone>>() {}.type
            val list: List<Zone> = gson.fromJson(file.readText(), type)
            list.forEach { zones[it.id] = it }
            info("[ZoneManager] 已加载 ${zones.size} 个区域")
        }.onFailure { info("[ZoneManager] 加载区域失败: ${it.message}") }
    }

    @Awake(LifeCycle.DISABLE)
    fun save() {
        file.parentFile.mkdirs()
        file.writeText(gson.toJson(zones.values.toList()))
        info("[ZoneManager] 已保存 ${zones.size} 个区域")
    }

    // ======================== CRUD ========================

    fun register(zone: Zone) { zones[zone.id] = zone }

    fun remove(id: String) = zones.remove(id)

    fun get(id: String): Zone? = zones[id]

    fun all(): Collection<Zone> = zones.values

    fun reload() { save(); load() }

    // ======================== 查询 ========================

    /** 返回该位置所在的所有区域（支持嵌套） */
    fun getZonesAt(loc: Location): List<Zone> =
        zones.values.filter { it.contains(loc) }

    /** 返回优先级最高的区域（取最小体积，即最精确） */
    fun getPrimaryZone(loc: Location): Zone? =
        getZonesAt(loc).minByOrNull {
            val dx = it.maxX - it.minX
            val dy = it.maxY - it.minY
            val dz = it.maxZ - it.minZ
            dx * dy * dz.toLong()
        }

    /** 玩家当前所在的所有区域 id */
    fun getPlayerZoneIds(player: Player): Set<String> =
        playerZones[player.uniqueId] ?: emptySet()

    fun getPlayerZones(player: Player): List<Zone> =
        getPlayerZoneIds(player).mapNotNull { zones[it] }

    // ======================== 事件 ========================

    @SubscribeEvent
    fun onMove(event: PlayerMoveEvent) {
        // 只在跨方块时处理，避免头部旋转触发
        if (event.from.blockX == event.to.blockX
            && event.from.blockY == event.to.blockY
            && event.from.blockZ == event.to.blockZ) return

        val player = event.player
        val prev = playerZones[player.uniqueId] ?: emptySet()
        val curr = getZonesAt(event.to).map { it.id }.toSet()

        if (prev == curr) return
        playerZones[player.uniqueId] = curr

        // 进入新区域
        (curr - prev).forEach { id ->
            val zone = zones[id] ?: return@forEach
            zone.enterMessage?.let { player.sendMessage(it) }
            onEnter[id]?.invoke(player, zone)
        }

        // 离开旧区域
        (prev - curr).forEach { id ->
            val zone = zones[id] ?: return@forEach
            zone.exitMessage?.let { player.sendMessage(it) }
            onLeave[id]?.invoke(player, zone)
        }
    }

    @SubscribeEvent
    fun onQuit(event: PlayerQuitEvent) {
        playerZones.remove(event.player.uniqueId)
    }
}
