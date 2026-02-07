package com.tingyu.command

import com.tingyu.common.pdc.PlayerDataManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

@CommandHeader(name = "dew")
object DewCommand {

    @CommandBody(permission = "dew.use")
    val main = mainCommand {
        createHelper()
    }

    /**
     * 在线玩家名补全
     */
    private fun onlinePlayers(): List<String> = Bukkit.getOnlinePlayers().map { it.name }

    /**
     * 根据名字获取在线玩家，失败时给发送者提示
     */
    private fun resolveTarget(sender: Player, name: String): Player? {
        return Bukkit.getPlayerExact(name) ?: run {
            sender.sendMessage("§c玩家 $name 不在线")
            null
        }
    }

    // /dew set <key> <value> [玩家名]
    @CommandBody(permission = "dew.debug")
    val set = subCommand {
        dynamic("key") {
            dynamic("value") {
                execute<Player> { player, context, _ ->
                    val data = PlayerDataManager.dataOf(player)
                    data[context["key"]] = context["value"]
                    player.sendMessage("§a已设置 ${context["key"]} = ${context["value"]}")
                }
                dynamic("target") {
                    suggestion<Player> { _, _ -> onlinePlayers() }
                    execute<Player> { player, context, _ ->
                        val target = resolveTarget(player, context["target"]) ?: return@execute
                        val data = PlayerDataManager.dataOf(target)
                        data[context["key"]] = context["value"]
                        player.sendMessage("§a已设置 ${target.name} 的 ${context["key"]} = ${context["value"]}")
                    }
                }
            }
        }
    }

    // /dew get <key> [玩家名]
    @CommandBody(permission = "dew.debug")
    val get = subCommand {
        dynamic("key") {
            execute<Player> { player, context, _ ->
                val data = PlayerDataManager.dataOf(player)
                player.sendMessage("§e${context["key"]} = ${data[context["key"]]}")
            }
            dynamic("target") {
                suggestion<Player> { _, _ -> onlinePlayers() }
                execute<Player> { player, context, _ ->
                    val target = resolveTarget(player, context["target"]) ?: return@execute
                    val data = PlayerDataManager.dataOf(target)
                    player.sendMessage("§e${target.name} 的 ${context["key"]} = ${data[context["key"]]}")
                }
            }
        }
    }

    // /dew getall [玩家名]
    @CommandBody(permission = "dew.debug")
    val getall = subCommand {
        execute<Player> { player, _, _ ->
            showAll(player, player)
        }
        dynamic("target") {
            suggestion<Player> { _, _ -> onlinePlayers() }
            execute<Player> { player, context, _ ->
                val target = resolveTarget(player, context["target"]) ?: return@execute
                showAll(player, target)
            }
        }
    }

    private fun showAll(sender: Player, target: Player) {
        val data = PlayerDataManager.dataOf(target)
        val all = data.getAll()
        if (all.isEmpty()) {
            sender.sendMessage("§7${target.name} 无数据")
        } else {
            sender.sendMessage("§6===== ${target.name} 的PDC数据 =====")
            all.forEach { (k, v) ->
                sender.sendMessage("§e$k §7= §f$v")
            }
        }
    }

    // /dew remove <key> [玩家名]
    @CommandBody(permission = "dew.debug")
    val remove = subCommand {
        dynamic("key") {
            execute<Player> { player, context, _ ->
                val data = PlayerDataManager.dataOf(player)
                val removed = data.remove(context["key"])
                player.sendMessage(if (removed) "§a已删除 ${context["key"]}" else "§c未找到 ${context["key"]}")
            }
            dynamic("target") {
                suggestion<Player> { _, _ -> onlinePlayers() }
                execute<Player> { player, context, _ ->
                    val target = resolveTarget(player, context["target"]) ?: return@execute
                    val data = PlayerDataManager.dataOf(target)
                    val removed = data.remove(context["key"])
                    player.sendMessage(if (removed) "§a已删除 ${target.name} 的 ${context["key"]}" else "§c未找到 ${target.name} 的 ${context["key"]}")
                }
            }
        }
    }

    // /dew clear [玩家名]
    @CommandBody(permission = "dew.debug")
    val clear = subCommand {
        execute<Player> { player, _, _ ->
            PlayerDataManager.dataOf(player).clear()
            player.sendMessage("§a已清空所有数据")
        }
        dynamic("target") {
            suggestion<Player> { _, _ -> onlinePlayers() }
            execute<Player> { player, context, _ ->
                val target = resolveTarget(player, context["target"]) ?: return@execute
                PlayerDataManager.dataOf(target).clear()
                player.sendMessage("§a已清空 ${target.name} 的所有数据")
            }
        }
    }

    // /dew json [玩家名]
    @CommandBody(permission = "dew.debug")
    val json = subCommand {
        execute<Player> { player, _, _ ->
            player.sendMessage("§6JSON: §f${PlayerDataManager.dataOf(player).toJson()}")
        }
        dynamic("target") {
            suggestion<Player> { _, _ -> onlinePlayers() }
            execute<Player> { player, context, _ ->
                val target = resolveTarget(player, context["target"]) ?: return@execute
                player.sendMessage("§6${target.name} JSON: §f${PlayerDataManager.dataOf(target).toJson()}")
            }
        }
    }

    // /dew save [玩家名]
    @CommandBody(permission = "dew.debug")
    val save = subCommand {
        execute<Player> { player, _, _ ->
            PlayerDataManager.saveNow(player)
            player.sendMessage("§a已保存数据到PDC")
        }
        dynamic("target") {
            suggestion<Player> { _, _ -> onlinePlayers() }
            execute<Player> { player, context, _ ->
                val target = resolveTarget(player, context["target"]) ?: return@execute
                PlayerDataManager.saveNow(target)
                player.sendMessage("§a已保存 ${target.name} 的数据到PDC")
            }
        }
    }

    // /dew reload [玩家名]
    @CommandBody(permission = "dew.debug")
    val reload = subCommand {
        execute<Player> { player, _, _ ->
            PlayerDataManager.loadNow(player)
            player.sendMessage("§a已从PDC重新加载数据")
        }
        dynamic("target") {
            suggestion<Player> { _, _ -> onlinePlayers() }
            execute<Player> { player, context, _ ->
                val target = resolveTarget(player, context["target"]) ?: return@execute
                PlayerDataManager.loadNow(target)
                player.sendMessage("§a已从PDC重新加载 ${target.name} 的数据")
            }
        }
    }
}