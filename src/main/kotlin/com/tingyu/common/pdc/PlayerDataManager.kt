package com.tingyu.common.pdc

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tingyu.Main
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.WorldSaveEvent
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import taboolib.common.platform.event.SubscribeEvent
import java.util.*
import kotlin.reflect.KProperty

object PlayerDataManager : Listener {

    /**
     * 玩家数据Map，键为UUID，值为任意可嵌套的数据结构
     */
    private val playerData = mutableMapOf<UUID, MutableMap<String, Any?>>()
    private val gson = Gson()
    private val dataKey = NamespacedKey("Dew", "player_data")

    // ======================== 事件监听 ========================

    /**
     * 当玩家加入服务器时，从PDC加载数据
     */
    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        loadPlayerData(event.player)
    }

    /**
     * 当玩家退出服务器时，保存数据并清除内存
     */
    @SubscribeEvent
    fun onPlayerQuit(event: PlayerQuitEvent) {
        savePlayerData(event.player)
        playerData.remove(event.player.uniqueId)
    }

    /**
     * 当世界保存时，保存所有在线玩家的数据
     */
    @SubscribeEvent
    fun onWorldSave(event: WorldSaveEvent) {
        Bukkit.getOnlinePlayers().forEach { player ->
            savePlayerData(player)
        }
    }

    // ======================== 数据加载/保存 ========================

    /**
     * 从PDC加载玩家数据
     */
    private fun loadPlayerData(player: Player) {
        val pdc: PersistentDataContainer = player.persistentDataContainer
        val json = pdc.get(dataKey, PersistentDataType.STRING)
        if (json != null && json.isNotBlank()) {
            try {
                val type = object : TypeToken<MutableMap<String, Any?>>() {}.type
                val raw = gson.fromJson<MutableMap<String, Any?>>(json, type)
                // 递归转换，修复 Gson 的 Double 和 LinkedTreeMap 问题
                playerData[player.uniqueId] = deepToMutableMap(raw) as MutableMap<String, Any?>
                Main.console.sendMessage("已加载玩家 ${player.name} 的自定义数据")
            } catch (e: Exception) {
                Main.console.sendMessage("解析玩家 ${player.name} 的数据时出错: ${e.message}")
                playerData[player.uniqueId] = mutableMapOf()
            }
        } else {
            playerData[player.uniqueId] = mutableMapOf()
        }
    }

    /**
     * 保存玩家数据到PDC
     */
    private fun savePlayerData(player: Player) {
        val data = playerData[player.uniqueId] ?: return
        val json = gson.toJson(data)
        val pdc: PersistentDataContainer = player.persistentDataContainer
        pdc.set(dataKey, PersistentDataType.STRING, json)
    }

    // ======================== 公开 API ========================

    /**
     * 获取玩家的数据操作器
     */
    fun dataOf(player: Player): PlayerData = PlayerData(player.uniqueId)

    /**
     * 通过UUID获取玩家的数据操作器
     */
    fun dataOf(uuid: UUID): PlayerData = PlayerData(uuid)

    /**
     * 立即保存指定玩家的数据
     */
    fun saveNow(player: Player) = savePlayerData(player)

    /**
     * 立即加载指定玩家的数据
     */
    fun loadNow(player: Player) = loadPlayerData(player)

    /**
     * 清除内存中的玩家数据
     */
    fun clearPlayerData(uuid: UUID) {
        playerData.remove(uuid)
    }

    /**
     * 获取所有在线玩家的数据（只读视图）
     */
    fun getAllPlayerData(): Map<UUID, Map<String, Any?>> = playerData

    // ======================== 工具方法 ========================

    /**
     * 递归将 Gson 反序列化的结果转为 MutableMap，
     * 同时修复整数被解析为 Double 的问题
     */
    private fun deepToMutableMap(value: Any?): Any? {
        return when (value) {
            is Map<*, *> -> {
                val map = mutableMapOf<String, Any?>()
                value.forEach { (k, v) ->
                    map[k.toString()] = deepToMutableMap(v)
                }
                map
            }
            is List<*> -> value.map { deepToMutableMap(it) }.toMutableList()
            is Double -> {
                if (value == value.toLong().toDouble()) value.toLong() else value
            }
            else -> value
        }
    }

    // ======================== 玩家数据操作类 ========================

    /**
     * 玩家数据操作类，支持嵌套读写
     */
    class PlayerData(private val uuid: UUID) {

        /**
         * 获取顶层数据
         * 用法: data["key"]
         */
        operator fun get(key: String): Any? {
            return playerData[uuid]?.get(key)
        }

        /**
         * 设置顶层数据
         * 用法: data["key"] = value
         */
        operator fun set(key: String, value: Any?) {
            val data = playerData.getOrPut(uuid) { mutableMapOf() }
            data[key] = value
        }

        /**
         * 获取嵌套数据
         * 用法: data("a", "b", "c") 相当于 data["a"]["b"]["c"]
         */
        operator fun invoke(vararg keys: String): Any? {
            var current: Any? = playerData[uuid]
            for (key in keys) {
                current = when (current) {
                    is Map<*, *> -> (current as Map<*, Any?>)[key]
                    else -> return null
                }
            }
            return current
        }

        /**
         * 设置嵌套数据，自动创建中间层级
         * 用法: data.set("a", "b", "c", value = 123)
         */
        fun set(vararg keys: String, value: Any?) {
            if (keys.isEmpty()) return
            val data = playerData.getOrPut(uuid) { mutableMapOf() }
            var current: MutableMap<String, Any?> = data

            for (i in 0 until keys.size - 1) {
                val key = keys[i]
                val next = current[key] as? MutableMap<String, Any?>
                    ?: mutableMapOf<String, Any?>().also { current[key] = it }
                current = next
            }
            current[keys.last()] = value
        }

        /**
         * 删除数据（支持嵌套路径）
         */
        fun remove(vararg keys: String): Boolean {
            if (keys.isEmpty()) return false
            val data = playerData[uuid] ?: return false
            var current: MutableMap<String, Any?> = data

            for (i in 0 until keys.size - 1) {
                val next = current[keys[i]] as? MutableMap<String, Any?> ?: return false
                current = next
            }
            return current.remove(keys.last()) != null
        }

        /**
         * 检查是否存在某个键（支持嵌套路径）
         */
        fun has(vararg keys: String): Boolean {
            if (keys.isEmpty()) return false
            var current: Any? = playerData[uuid] ?: return false

            for (key in keys) {
                current = when (current) {
                    is Map<*, *> -> {
                        if (!current.containsKey(key)) return false
                        (current as Map<*, Any?>)[key]
                    }
                    else -> return false
                }
            }
            return true
        }

        /**
         * 获取所有顶层键
         */
        fun keys(): Set<String> = playerData[uuid]?.keys ?: emptySet()

        /**
         * 获取整个数据映射（只读）
         */
        fun getAll(): Map<String, Any?> = playerData[uuid] ?: emptyMap()

        /**
         * 清空所有数据
         */
        fun clear() {
            playerData[uuid]?.clear()
        }

        /**
         * 将数据转为JSON字符串
         */
        fun toJson(): String = gson.toJson(playerData[uuid])

        /**
         * 从JSON字符串加载数据
         */
        fun fromJson(json: String) {
            try {
                val type = object : TypeToken<MutableMap<String, Any?>>() {}.type
                val raw = gson.fromJson<MutableMap<String, Any?>>(json, type)
                playerData[uuid] = deepToMutableMap(raw) as MutableMap<String, Any?>
            } catch (e: Exception) {
                Main.console.sendMessage("从JSON加载数据时出错: ${e.message}")
            }
        }

        override fun toString(): String = toJson()
    }

    // ======================== 委托属性支持 ========================

    /**
     * 顶层数据委托属性
     * 用法: var coins by PlayerDataProperty("coins")
     */
    class PlayerDataProperty(private val key: String) {
        operator fun getValue(playerData: PlayerData, property: KProperty<*>): Any? {
            return playerData[key]
        }

        operator fun setValue(playerData: PlayerData, property: KProperty<*>, value: Any?) {
            playerData[key] = value
        }
    }

    /**
     * 嵌套数据委托属性
     * 用法: var kills by NestedPlayerDataProperty("stats", "kills")
     */
    class NestedPlayerDataProperty(private vararg val keys: String) {
        operator fun getValue(playerData: PlayerData, property: KProperty<*>): Any? {
            return playerData(*keys)
        }

        operator fun setValue(playerData: PlayerData, property: KProperty<*>, value: Any?) {
            playerData.set(*keys, value = value)
        }
    }
}

// ======================== Player 扩展 ========================

/**
 * Player扩展属性，快速获取数据操作器
 * 用法: player.customData["key"] = value
 */
val Player.customData: PlayerDataManager.PlayerData
    get() = PlayerDataManager.dataOf(this)

/*// 主类 onEnable 中注册
PlayerDataManager.get().register()

// 任意地方使用
val data = player.customData
data["coins"] = 100
data.set("stats", "kills", value = 42)  // 嵌套写入
val kills = data("stats", "kills")       // 嵌套读取
 */