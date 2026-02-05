package com.tingyu.common.database

import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.module.database.ColumnOptionSQLite
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.Table
import taboolib.module.database.getHost

/**
 * 数据库管理器
 *
 * 职责：
 * - 管理数据库连接
 * - 定义表结构
 * - 提供数据的存取方法
 *
 * 使用 object 单例模式，全局只有一个实例
 */
object DatabaseManager {

    /**
     * 数据库连接地址（Host）
     *
     * getDataFolder() → 获取插件数据文件夹，即 plugins/DewCore/
     * newFile(..., "database.db") → 指向该文件夹下的 database.db 文件
     * .getHost() → 将文件路径转换为 TabooLib 可用的数据库连接配置
     *
     * 最终效果：数据库文件位于 plugins/DewCore/database.db
     */
    val host = newFile(getDataFolder(), "database.db").getHost()

    /**
     * 表结构定义
     *
     * 表名：user_data
     *
     * 结构：
     * ┌─────────────────────┬────────────────┐
     * │        name         │      uuid      │
     * │    (主键, TEXT)      │    (TEXT)      │
     * ├─────────────────────┼────────────────┤
     * │      玩家名字        │    玩家UUID    │
     * └─────────────────────┴────────────────┘
     *
     * 注意：这里只是"定义"表长什么样，还没有真正创建
     */
    val table = Table("user_data", host) {
        // 第一列：name（玩家名字）
        // - 类型：TEXT（文本）
        // - 约束：PRIMARY_KEY（主键，唯一标识每一行）
        add("name") {
            type(ColumnTypeSQLite.TEXT) {
                options(ColumnOptionSQLite.PRIMARY_KEY)
            }
        }
        // 第二列：uuid（玩家UUID）
        // - 类型：TEXT（文本）
        // - 无特殊约束
        add("uuid") {
            type(ColumnTypeSQLite.TEXT) {
            }
        }
    }

    /**
     * 数据源（DataSource）
     *
     * 作用：真正用来执行数据库操作的对象
     *
     * by lazy：延迟初始化
     * - 不会在插件加载时立即创建连接
     * - 等到第一次使用时才创建
     * - 好处：避免数据库未就绪时插件加载失败
     *
     * createDataSource() 会自动创建 HikariCP 连接池
     */
    val dataSource by lazy { host.createDataSource() }

    /**
     * 初始化块
     *
     * 当 DatabaseManager 第一次被访问时自动执行
     *
     * createTable() 的作用：
     * - 检查 user_data 表是否存在
     * - 如果不存在 → 创建表
     * - 如果已存在 → 什么都不做
     *
     * 这样保证了表一定存在，后续操作不会报错
     */
    init {
        table.createTable(dataSource)
    }

    /**
     * 保存玩家数据
     *
     * @param uuid 玩家的 UUID
     * @param name 玩家的名字
     *
     * 逻辑：
     * - 如果这个 name 不存在 → 插入新行
     * - 如果这个 name 已存在 → 更新该行的数据
     *
     * 使用场景：玩家退出服务器时调用
     */
    fun savePlayer(uuid: String, name: String) {
        // insert() 参数说明：
        // - dataSource：数据源
        // - "uuid", "name"：要插入的列名
        table.insert(dataSource, "uuid", "name") {
            // value() 参数说明：
            // - 值的顺序必须和上面列名顺序一致
            // - 即 uuid 列 = uuid 参数，name 列 = name 参数
            value(uuid, name)

            // 当主键（name）冲突时的处理
            // 即：如果这个 name 已经存在于表中
            onDuplicateKeyUpdate {
                // 更新 name 列的值
                // （虽然这里 name 没变，但如果以后加更多字段就有用了）
                update("name", name)
            }
        }
    }

    /**
     * 读取玩家数据
     *
     * @param uuid 玩家的 UUID
     * @return 玩家的名字，如果不存在返回 null
     *
     * 逻辑：
     * - 在表中查找 uuid 等于传入值的行
     * - 找到 → 返回该行的 name
     * - 没找到 → 返回 null（说明是新玩家）
     *
     * 使用场景：玩家进入服务器时调用
     */
    fun loadPlayer(uuid: String): String? {
        return table.select(dataSource) {
            // rows()：指定要查询的列
            // 这里只查 name 列
            rows("name")

            // where()：过滤条件
            // "uuid" eq uuid 表示：uuid 列的值 = 传入的 uuid 参数
            where("uuid" eq uuid)

        }.firstOrNull {
            // firstOrNull：取查询结果的第一条
            // - 如果有结果 → 执行大括号里的代码
            // - 如果没结果 → 返回 null

            // getString("name")：从结果中取出 name 列的值
            getString("name")
        }
    }
}