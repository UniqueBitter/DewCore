package com.tingyu.common.database

import com.tingyu.player.DewPlayer
import com.tingyu.player.race.Race
import com.tingyu.player.job.Job
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.module.database.ColumnOptionSQLite
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.Table
import taboolib.module.database.getHost

object DatabaseManager {

    val host = newFile(getDataFolder(), "database.db").getHost()
    val dataSource by lazy { host.createDataSource() }

    val table = Table("user_data", host) {
        add("uuid") {
            type(ColumnTypeSQLite.TEXT) {
                options(ColumnOptionSQLite.PRIMARY_KEY)
            }
        }
        add("name") {
            type(ColumnTypeSQLite.TEXT)
        }
        add("race") {
            type(ColumnTypeSQLite.TEXT)
        }
        add("job") {
            type(ColumnTypeSQLite.TEXT)
        }
        add("copper") {
            type(ColumnTypeSQLite.INTEGER)
        }
    }

    init {
        table.createTable(dataSource)
    }

    fun savePlayer(player: DewPlayer) {
        val exists = table.find(dataSource) {
            where("uuid" eq player.uuid)
        }

        if (exists) {
            table.update(dataSource) {
                set("name", player.name)
                set("race", player.race.name)
                set("job", player.job.name)
                set("copper", player.copper)
                where("uuid" eq player.uuid)
            }
        } else {
            table.insert(dataSource, "uuid", "name", "race", "job", "copper") {
                value(
                    player.uuid,
                    player.name,
                    player.race.name,
                    player.job.name,
                    player.copper
                )
            }
        }
    }

    fun loadPlayer(uuid: String): DewPlayer? {
        return table.select(dataSource) {
            rows("name", "race", "job", "copper")
            where("uuid" eq uuid)
        }.firstOrNull {
            DewPlayer(
                uuid = uuid,
                name = getString("name"),
                race = try { Race.valueOf(getString("race")) } catch (e: Exception) { Race.NONE },
                job = try { Job.valueOf(getString("job")) } catch (e: Exception) { Job.NONE },
                copper = getLong("copper")
            )
        }
    }
}