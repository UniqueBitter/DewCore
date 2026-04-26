package com.tingyu.map.zone

import org.bukkit.Bukkit
import org.bukkit.Location

data class Zone(
    val id: String,
    var displayName: String,
    var world: String,
    var minX: Int, var minY: Int, var minZ: Int,
    var maxX: Int, var maxY: Int, var maxZ: Int,
    var minLevel: Int = 0,
    var type: ZoneType = ZoneType.FIELD,
    var enterMessage: String? = null,
    var exitMessage: String? = null,
    var instanceId: String? = null      // 仅 INSTANCE_PORTAL 类型使用
) {
    fun contains(loc: Location): Boolean {
        if (loc.world?.name != world) return false
        return loc.blockX in minX..maxX
            && loc.blockY in minY..maxY
            && loc.blockZ in minZ..maxZ
    }

    /** 区域中心点 */
    fun center(): Location? {
        val w = Bukkit.getWorld(world) ?: return null
        return Location(
            w,
            ((minX + maxX) / 2.0),
            ((minY + maxY) / 2.0),
            ((minZ + maxZ) / 2.0)
        )
    }

    fun isSafe() = type == ZoneType.TOWN || type == ZoneType.SAFE
}
