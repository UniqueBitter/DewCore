package com.tingyu.common.database

import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.module.database.ColumnOptionSQLite
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.Table
import taboolib.module.database.getHost
import java.util.*

class DatabaseSQL : Database() {

    // 获取数据库连接地址
    val host = newFile(getDataFolder(), "dewdata.db").getHost()

    // 使用 Host 创建数据库连接池
    val dataSource by lazy { host.createDataSource() }

    val table = Table("dewdata", host) {
        add { id() }
        add("uuid") {
            type(ColumnTypeSQLite.TEXT) {
                options(ColumnOptionSQLite.PRIMARY_KEY)
            }
        }
        add("name") { type(ColumnTypeSQLite.TEXT) }
        add("health") { type(ColumnTypeSQLite.REAL) }       // 生命值
        add("attack") { type(ColumnTypeSQLite.REAL) }        // 攻击力
        add("defense") { type(ColumnTypeSQLite.REAL) }        // 防御力
        add("attackSpeed") { type(ColumnTypeSQLite.REAL) }    // 攻击速度
        add("moveSpeed") { type(ColumnTypeSQLite.REAL) }      // 移动速度
        add("mana") { type(ColumnTypeSQLite.REAL) }         // 灵气/法力
    }

    init {
        // 初始化表结构
        table.createTable(dataSource)
        info("DatabaseSQL initialized properly!")
    }

    override fun pull(uuid: UUID): PlayerData? {
        return table.select(dataSource) {
            where("uuid" eq uuid.toString())
        }.firstOrNull {
            PlayerData(
                uuid = UUID.fromString(getString("uuid")),
                name = getString("name"),
                health = getDouble("health"),
                attack = getDouble("attack"),
                defense = getDouble("defense"),
                attackSpeed = getDouble("attackSpeed"),
                moveSpeed = getDouble("moveSpeed"),
                mana = getDouble("mana")
            )
        }
    }

    override fun pullByName(name: String): PlayerData? {
        return table.select(dataSource) {
            where("name" eq name)
        }.firstOrNull {
            PlayerData(
                uuid = UUID.fromString(getString("uuid")),
                name = getString("name"),
                health = getDouble("health"),
                attack = getDouble("attack"),
                defense = getDouble("defense"),
                attackSpeed = getDouble("attackSpeed"),
                moveSpeed = getDouble("moveSpeed"),
                mana = getDouble("mana")
            )
        }
    }

    override fun push(playerData: PlayerData) {
        if (table.find(dataSource) { where("uuid" eq playerData.uuid.toString()) }) {
            // 更新现有数据
            table.update(dataSource) {
                where("uuid" eq playerData.uuid.toString())
                set("name", playerData.name)
                set("health", playerData.health)
                set("attack", playerData.attack)
                set("defense", playerData.defense)
                set("attackSpeed", playerData.attackSpeed)
                set("moveSpeed", playerData.moveSpeed)
                set("mana", playerData.mana)
            }
        } else {
            // 插入新数据
            table.insert(dataSource, "uuid", "name", "health", "attack", "defense", "attackSpeed", "moveSpeed", "mana") {
                value(
                    playerData.uuid.toString(),
                    playerData.name,
                    playerData.health,
                    playerData.attack,
                    playerData.defense,
                    playerData.attackSpeed,
                    playerData.moveSpeed,
                    playerData.mana
                )
            }
        }
    }
}
