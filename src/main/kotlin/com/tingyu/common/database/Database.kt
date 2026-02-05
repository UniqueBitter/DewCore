package com.tingyu.common.database

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class Database {

    companion object {
        val INSTANCE by lazy {
            DatabaseSQL()
        }
    }

    val cache = ConcurrentHashMap<UUID, PlayerData>() // 储存每个玩家对应的所有数据

    abstract fun pull(uuid: UUID): PlayerData?
    abstract fun pullByName(name: String): PlayerData?
    abstract fun push(playerData: PlayerData)
}
