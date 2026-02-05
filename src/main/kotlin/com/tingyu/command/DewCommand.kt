package com.tingyu.command

import com.tingyu.common.database.DatabaseManager
import com.tingyu.player.DewPlayer
import com.tingyu.player.PlayerManager
import com.tingyu.player.job.Job
import com.tingyu.player.race.Race
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

/**
 * 主命令
 */
@CommandHeader(name = "dew")
object DewCommand {

    @CommandBody(permission = "panling.use")
    val main = mainCommand {
        createHelper()
    }

    /**
     * 查看自己的数据
     * /dew info
     */
    @CommandBody(permission = "panling.use")
    val info = subCommand {
        execute<Player> { sender, _, _ ->
            val player = PlayerManager.get(sender)
            if (player == null) {
                sender.sendMessage("§c数据加载失败！")
                return@execute
            }
            sender.sendMessage("§6========== 你的数据 ==========")
            sender.sendMessage("§e名字: §f${player.name}")
            sender.sendMessage("§e种族: §f${player.race}")
            sender.sendMessage("§e职业: §f${player.job}")
            sender.sendMessage("§e铜币: §f${player.copper}")
            sender.sendMessage("§6==============================")
            DatabaseManager.savePlayer(player)
        }

    }

    /**
     * 设置种族
     * /dew setrace <种族>
     */
    @CommandBody(permission = "panling.admin")
    val setrace = subCommand {
        // 动态补全种族列表
        dynamic("种族") {
            suggestion<Player> { _, _ ->
                Race.values().map { it.name }
            }
            execute<Player> { sender, _, argument ->
                val player = PlayerManager.get(sender) ?: return@execute

                val race = try {
                    Race.valueOf(argument.uppercase())
                } catch (e: Exception) {
                    sender.sendMessage("§c无效的种族！可选: ${Race.entries.joinToString(", ")}")
                    DatabaseManager.savePlayer(player)
                    return@execute
                }

                player.race = race
                sender.sendMessage("§a种族已设置为: $race")
                DatabaseManager.savePlayer(player)
            }
        }
    }

    /**
     * 设置职业
     * /dew setjob <职业>
     */
    @CommandBody(permission = "panling.admin")
    val setjob = subCommand {
        dynamic("职业") {
            suggestion<Player> { _, _ ->
                Job.entries.map { it.name }
            }
            execute<Player> { sender, _, argument ->
                val player = PlayerManager.get(sender) ?: return@execute

                val job = try {
                    Job.valueOf(argument.uppercase())
                } catch (e: Exception) {
                    sender.sendMessage("§c无效的职业！可选: ${Job.entries.joinToString(", ")}")
                    return@execute
                }

                player.job = job
                DatabaseManager.savePlayer(player)
                sender.sendMessage("§a职业已设置为: $job")

            }
        }
    }

    /**
     * 设置铜币
     * /dew setcopper <数量>
     */
    @CommandBody(permission = "panling.admin")
    val setcopper = subCommand {
        dynamic("数量") {
            execute<Player> { sender, _, argument ->
                val player = PlayerManager.get(sender) ?: return@execute

                val amount = argument.toLongOrNull()
                if (amount == null || amount < 0) {
                    sender.sendMessage("§c请输入有效的数量！")
                    return@execute
                }

                player.copper = amount
                DatabaseManager.savePlayer(player)
                sender.sendMessage("§a铜币已设置为: ${player.copper}")
            }
        }
    }
}