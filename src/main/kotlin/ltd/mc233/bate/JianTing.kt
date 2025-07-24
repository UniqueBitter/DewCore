package ltd.mc233.bate

import ltd.mc233.util.Execute
import ltd.mc233.util.PlayerGet.job
import ltd.mc233.util.PlayerGet.race
import org.bukkit.Material
import org.bukkit.entity.Player
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
        Triple(1324, 63, 40), //战士
        Triple(1324, 63, 42),//游侠
        Triple(1324, 63, 44),//炼丹
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
        val cooldownTime = 1000L  // 冷却时间：5秒（5000毫秒）

        // 检查玩家是否在冷却期内
        playerCooldown[playerUUID]?.let { lastTriggerTime ->
            if (currentTime - lastTriggerTime < cooldownTime) {
                // 还在冷却期内，直接返回不处理
                event.isCancelled = true
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
            // forEachIndexed 可以同时获取索引(race)和值(x,y,z)
            tabanCoords.forEachIndexed { race, (x, y, z) ->
                if (location.blockX == x && location.blockY == y && location.blockZ == z) {
                    player.sendMessage("§a踩到了索引 $race 的压力板")
                    // ✨ 记录触发时间 - 开始5秒冷却计时
                    playerCooldown[playerUUID] = currentTime
                    // ✨ 根据踏板索引执行不同命令
                    when (race) {
                        0 -> {
                            event.isCancelled = true
                            bukaifang(player)
                            //player.race = Race.SHEN
                        }

                        1 -> {
                            event.isCancelled = true
                            bukaifang(player)
                            //Execute.command(player, "function panling:command_blocks/leading/race/yao")
                            //player.race = Race.YAO
                        }

                        2 -> {
                            event.isCancelled = true
                            Execute.command(player, "function panling:command_blocks/leading/race/xian")
                            player.race = Race.XIAN
                        }

                        3 -> {
                            event.isCancelled = true
                            bukaifang(player)
                            //Execute.command(player, "function panling:command_blocks/leading/race/zhan")
                            //player.race = Race.ZHAN
                        }

                        4 -> {
                            event.isCancelled = true
                            bukaifang(player)
                            //Execute.command(player, "function panling:command_blocks/leading/race/ren")
                            //player.race = Race.REN
                        }

                        5 -> {
                            event.isCancelled = true
                            player.job = Jobs.ZHANSHI
                            Execute.command(player, "function panling:command_blocks/leading/job/warrior")
                        }

                        6 -> {
                            event.isCancelled = true
                            player.job = Jobs.YOUXIA
                            player.sendMessage("§7抱歉，当前职业暂未开放")
                        }

                        7 -> {
                            event.isCancelled = true
                            player.job = Jobs.LIANDAN
                            player.sendMessage("§7抱歉，当前职业暂未开放")
                        }
                    }
                    event.isCancelled = true
                    return
                }
            }
        }
    }

    fun bukaifang(player: Player) {
        player.sendMessage("§7抱歉，当前暂未开放")
        Execute.command(player, "tp @s 1315 76 42 -90 0")
    }
}