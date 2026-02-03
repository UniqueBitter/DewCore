package com.tingyu.common.database

import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.module.database.ColumnOptionSQLite
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.Table
import taboolib.module.database.getHost


object DatabaseManager {
    //获取数据库连接地址
    val host = newFile(getDataFolder(), "packdata.db").getHost()

    // 使用 Host 创建数据库连接池
    val dataSource by lazy { host.createDataSource() }

    val table = Table("dewdata", host) {
        add { id() }
        add("uuid") {
            type(ColumnTypeSQLite.TEXT) {
                options(ColumnOptionSQLite.PRIMARY_KEY)
            }
            add("name") { type(ColumnTypeSQLite.TEXT) }
        }
        add("name") {
            type(ColumnTypeSQLite.TEXT)
        }
    }

    init {
        // 初始化表结构
        table.createTable(dataSource)
    }



}