package com.tingyu.map.spawn

import com.tingyu.mob.MobManager
import com.tingyu.mob.MobRegistry
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submitAsync
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object SpawnManager {

    private val spawns = mutableListOf<ZoneSpawn>()

    // spawnId → 当前存活的实体 UUID 列表
    private val alive = ConcurrentHashMap<String, MutableList<UUID>>()

    // spawnId → 下次允许刷新的 tick 时间戳
    private val cooldown = ConcurrentHashMap<String, Long>()

    private var currentTick = 0L

    // ======================== 注册 ========================

    fun register(spawn: ZoneSpawn) {
        spawns.add(spawn)
        alive.getOrPut(spawn.id) { mutableListOf() }
    }

    fun register(vararg spawn: ZoneSpawn) = spawn.forEach { register(it) }

    fun unregister(spawnId: String) {
        spawns.removeIf { it.id == spawnId }
        alive.remove(spawnId)
        cooldown.remove(spawnId)
    }

    // ======================== 调度 ========================

    @Awake(LifeCycle.ENABLE)
    fun startScheduler() {
        submitAsync(period = 20L) {    // 每秒检查一次
            currentTick += 20
            tick()
        }
    }

    private fun tick() {
        for (spawn in spawns) {
            val liveList = alive.getOrPut(spawn.id) { mutableListOf() }

            // 清理已死亡/消失的实体
            liveList.removeIf { uuid ->
                val entity = Bukkit.getEntity(uuid) as? LivingEntity
                if (entity == null || entity.isDead) {
                    // 实体死亡后设置冷却
                    cooldown[spawn.id] = currentTick + spawn.respawnTicks
                    true
                } else false
            }

            // 未达到上限 且 冷却已过 则生成
            if (liveList.size < spawn.maxCount) {
                val cd = cooldown[spawn.id] ?: 0L
                if (currentTick >= cd) {
                    spawnOne(spawn)?.let { liveList.add(it) }
                }
            }
        }
    }

    private fun spawnOne(spawn: ZoneSpawn): UUID? {
        val world = Bukkit.getWorld(spawn.world) ?: return null
        val profile = MobRegistry.fromId(spawn.mobId) ?: return null
        val loc = Location(world, spawn.x, spawn.y, spawn.z)

        val entityType = profile.entityType ?: return null
        val entity = world.spawnEntity(loc, entityType) as? LivingEntity ?: return null
        MobManager.apply(entity, profile)
        return entity.uniqueId
    }

    // ======================== 查询 ========================

    fun getSpawns(zoneId: String): List<ZoneSpawn> =
        spawns.filter { it.zoneId == zoneId }

    fun getLiveCount(spawnId: String): Int =
        alive[spawnId]?.size ?: 0
}
