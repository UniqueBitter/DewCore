package ltd.mc233.bate

import ltd.mc233.util.Execute
import ltd.mc233.util.PlayerGet.job
import ltd.mc233.util.PlayerGet.race
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.SubscribeEvent

object JianTing {
    // 踏板坐标列表
    private val tabanCoords = listOf(
        Triple(1233, 76, 42),   // 神1
        Triple(1366, 77, 13),   // 妖2
        Triple(1277, 77, 105),  // 仙3
        Triple(1366, 77, 70),   // 战4
        Triple(1282, 78, -8),   // 人5
    )

    // 玩家冷却记录 - 防止重复触发
    private val playerCooldown = mutableMapOf<String, Long>()

    //玩家踩踏tabanCoords里面的踏板取消事件
    @SubscribeEvent
    fun zhiyexuanze(event: PlayerInteractEvent) {
        // Action.PHYSICAL 表示玩家踩到压力板、绊线钩等物理触发器
        // 如果不是物理交互，直接返回不处理
        if (event.action != Action.PHYSICAL) return

        //事件玩家
        val player = event.player

        // ✨ 冷却检查 - 防止刷屏
        val playerUUID = player.uniqueId.toString()  // 获取玩家唯一ID
        val currentTime = System.currentTimeMillis() // 获取当前时间（毫秒）
        val cooldownTime = 5000L  // 冷却时间：5秒（5000毫秒）

        // 检查玩家是否在冷却期内
        playerCooldown[playerUUID]?.let { lastTriggerTime ->
            if (currentTime - lastTriggerTime < cooldownTime) {
                // 还在冷却期内，直接返回不处理
                return
            }
        }

        //踩踏的方块对象
        val block = event.clickedBlock ?: return

        // location包含了方块的X、Y、Z坐标和所在世界
        val location = block.location

        // 获取被踩踏方块的材质类型,用来判断这个方块是不是石质压力板
        val material = block.type

        if (material == Material.STONE_PRESSURE_PLATE) {
            // forEachIndexed 可以同时获取索引(index)和值(x,y,z)
            tabanCoords.forEachIndexed { index, (x, y, z) ->
                if (location.blockX == x && location.blockY == y && location.blockZ == z) {
                    // ✨ 记录触发时间 - 开始5秒冷却计时
                    playerCooldown[playerUUID] = currentTime

                    // ✨ 根据踏板索引执行不同命令
                    when (index) {
                        0 -> {
                            Execute.command(player, "say 我选择了神族!")
                            player.race = Race.SHEN
                        }
                        1 -> {
                            Execute.command(player, "say 我选择了妖族!")
                            player.race = Race.YAO
                        }
                        2 -> {
                            Execute.command(player, "say 我选择了仙族!")
                            player.race = Race.XIAN
                        }
                        3 -> {
                            Execute.command(player, "say 我选择了战神族!")
                            player.race = Race.ZHAN
                        }
                        4 -> {
                            Execute.command(player, "say 我选择了人族!")
                            player.race = Race.REN
                        }
                    }

                    event.isCancelled = true
                    return
                }
            }
        }
    }
}