package ltd.mc233.commands

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.function.submit
import java.util.*

@CommandHeader("chunk",aliases = ["qkadd"], permission = "panling.admin.chunk")
object chunk {

    // 存储玩家选择的区块位置
    private val playerSelections = mutableMapOf<UUID, ChunkSelection>()

    data class ChunkSelection(
        var chunkA: Pair<Int, Int>? = null,  // (chunkX, chunkZ)
        var chunkB: Pair<Int, Int>? = null,
        var world: org.bukkit.World? = null
    )

    @CommandBody(permission = "panling.admin.chunk")
    val chunk = mainCommand {
        // 原始命令 - 在当前区块四角生成光柱
        execute<Player> { sender, context, argument ->
            val chunk = sender.location.chunk
            val world = sender.world
            val chunkX = chunk.x
            val chunkZ = chunk.z

            val corners = listOf(
                Location(world, (chunkX * 16 + 0.5).toDouble(), 0.0, (chunkZ * 16 + 0.5).toDouble()),
                Location(world, (chunkX * 16 + 15.5).toDouble(), 0.0, (chunkZ * 16 + 0.5).toDouble()),
                Location(world, (chunkX * 16 + 0.5).toDouble(), 0.0, (chunkZ * 16 + 15.5).toDouble()),
                Location(world, (chunkX * 16 + 15.5).toDouble(), 0.0, (chunkZ * 16 + 15.5).toDouble())
            )

            sender.sendMessage("§a已在区块 (${chunkX}, ${chunkZ}) 的四个角落生成粒子光柱，持续30秒")
            createPillars(corners, sender)
        }

        // 子命令a - 选择第一个区块
        literal("a") {
            execute<Player> { sender, context, argument ->
                val chunk = sender.location.chunk
                val world = sender.world
                val chunkX = chunk.x
                val chunkZ = chunk.z

                val selection = playerSelections.getOrPut(sender.uniqueId) { ChunkSelection() }
                selection.chunkA = Pair(chunkX, chunkZ)
                selection.world = world

                sender.sendMessage("§a已选择区块A: (${chunkX}, ${chunkZ})")

                // 如果已经选择了两个区块，提示玩家可以生成光柱
                if (selection.chunkB != null) {
                    sender.sendMessage("§e两个区块都已选择，使用 §6/chunk generate §e生成光柱")
                } else {
                    sender.sendMessage("§e请使用 §6/chunk b §e选择第二个区块")
                }
            }
        }

        // 子命令b - 选择第二个区块
        literal("b") {
            execute<Player> { sender, context, argument ->
                val chunk = sender.location.chunk
                val world = sender.world
                val chunkX = chunk.x
                val chunkZ = chunk.z

                val selection = playerSelections.getOrPut(sender.uniqueId) { ChunkSelection() }
                selection.chunkB = Pair(chunkX, chunkZ)
                selection.world = world

                sender.sendMessage("§a已选择区块B: (${chunkX}, ${chunkZ})")

                // 如果已经选择了两个区块，提示玩家可以生成光柱
                if (selection.chunkA != null) {
                    sender.sendMessage("§e两个区块都已选择，使用 §6/chunk generate §e生成光柱")
                } else {
                    sender.sendMessage("§e请使用 §6/chunk a §e选择第一个区块")
                }
            }
        }

        // 子命令generate - 生成光柱
        literal("generate") {
            execute<Player> { sender, context, argument ->
                val selection = playerSelections[sender.uniqueId]

                if (selection == null || selection.chunkA == null || selection.chunkB == null || selection.world == null) {
                    sender.sendMessage("§c请先使用 §6/chunk a §c和 §6/chunk b §c选择两个区块来定义区域")
                    return@execute
                }

                val chunkA = selection.chunkA!!
                val chunkB = selection.chunkB!!
                val world = selection.world!!

                // 计算矩形区域的对角坐标
                val minX = kotlin.math.min(chunkA.first, chunkB.first)
                val maxX = kotlin.math.max(chunkA.first, chunkB.first)
                val minZ = kotlin.math.min(chunkA.second, chunkB.second)
                val maxZ = kotlin.math.max(chunkA.second, chunkB.second)

                // 创建矩形区域四个角的位置
                val corners = listOf(
                    Location(world, (minX * 16 + 0.5).toDouble(), 0.0, (minZ * 16 + 0.5).toDouble()), // 西北角
                    Location(world, (maxX * 16 + 15.5).toDouble(), 0.0, (maxZ * 16 + 15.5).toDouble()) // 东南角
                )

                // 计算世界坐标
                val startX = (minX * 16 + 0.5).toInt()
                val startZ = (minZ * 16 + 0.5).toInt()
                val endX = (maxX * 16 + 15.5).toInt()
                val endZ = (maxZ * 16 + 15.5).toInt()

                sender.sendMessage("""
            §a§l区块区域标记已生成
            §7▪ 区块范围: §f$minX,$minZ §7→ §f$maxX,$maxZ
            §7▪ 世界范围: §f$startX,0,$startZ §7→ §f$endX,0,$endZ
            §7▪ 标记将在§a30秒§7后自动消失
            """.trimIndent())

                // 创建可点击的坐标文本
                // 创建包含两个坐标的可点击文本
                val coordText = TextComponent("§a点击复制坐标 §6[$startX, 0, $startZ] §7-> §6[$endX, 0, $endZ]").apply {
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "$startX $startZ $endX $endZ")
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§e点击复制坐标"))
                }
                sender.spigot().sendMessage(coordText)

                createPillars(corners, sender)
            }
        }

        // 子命令clear - 清除选择
        literal("clear") {
            execute<Player> { sender, context, argument ->
                playerSelections.remove(sender.uniqueId)
                sender.sendMessage("§a已清除你的区块选择")
            }
        }

        // 子命令info - 显示当前选择信息
        literal("info") {
            execute<Player> { sender, context, argument ->
                val selection = playerSelections[sender.uniqueId]
                if (selection == null) {
                    sender.sendMessage("§c你还没有选择任何区块")
                    return@execute
                }

                sender.sendMessage("§e当前选择状态:")
                if (selection.chunkA != null) {
                    sender.sendMessage("§a区块A: (${selection.chunkA!!.first}, ${selection.chunkA!!.second})")
                } else {
                    sender.sendMessage("§c区块A: 未选择")
                }

                if (selection.chunkB != null) {
                    sender.sendMessage("§a区块B: (${selection.chunkB!!.first}, ${selection.chunkB!!.second})")
                } else {
                    sender.sendMessage("§c区块B: 未选择")
                }
            }
        }
    }

    /**
     * 创建光柱的通用方法
     */
    private fun createPillars(corners: List<Location>, player: Player) {
        var tickCount = 0
        val maxTicks = 150 // 30秒 / 0.2秒 = 150次

        submit(
            now = false,
            async = false,
            delay = 0L,
            period = 4L,
        ) {
            if (tickCount >= maxTicks) {
                cancel()
                player.sendMessage("§c区块光柱已消失")
                return@submit
            }

            corners.forEach { corner ->
                createParticlePillar(corner, player)
            }
            tickCount++
        }
    }

    /**
     * 在指定位置创建粒子光柱（只对执行者可见）- 通天彻地版本
     */
    private fun createParticlePillar(location: Location, player: Player) {
        val world = location.world ?: return

        // 通天彻地 - 从世界最低点到最高点
        val minHeight = -64.0  // 世界最低点 (1.18+)
        val maxHeight = 320.0  // 世界最高点 (1.18+)

        // 创建垂直的粒子柱（只对执行者可见）- 从基岩到天空
        for (y in minHeight.toInt()..maxHeight.toInt() step 2) {
            val particleLocation = Location(world, location.x, y.toDouble(), location.z)

            // 主要光柱效果
            player.spawnParticle(
                Particle.EGG_CRACK,
                particleLocation,
                3,      // 粒子数量
                0.1,    // X偏移
                0.1,    // Y偏移
                0.1,    // Z偏移
                0.01    // 速度
            )

            // 添加发光效果
            player.spawnParticle(
                Particle.END_ROD,
                particleLocation,
                1,
                0.05,
                0.05,
                0.05,
                0.02
            )
        }
    }
}