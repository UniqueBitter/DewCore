package com.tingyu.map.zone

enum class ZoneType(val displayName: String, val color: String) {
    FIELD("野外", "§a"),          // 普通野外，可战斗
    TOWN("城镇", "§6"),           // 安全区，禁止 PvP/怪物伤害
    SAFE("安全区", "§b"),          // 出生点/皇城等完全安全区域
    DUNGEON("地牢", "§c"),         // 危险区域，怪物密度高
    INSTANCE_PORTAL("副本入口", "§d") // 进入后可触发副本
}
