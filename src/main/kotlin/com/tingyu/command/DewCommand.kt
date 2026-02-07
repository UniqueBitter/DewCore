package com.tingyu.command

import com.tingyu.common.pdc.PlayerDataManager
import com.tingyu.player.PlayerManager
import com.tingyu.player.job.Job
import com.tingyu.player.race.Race
import com.tingyu.ui.SelfMessage
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

    // ======================== 已知键定义 ========================

    private enum class KnownKey(val type: DataType) {
        NAME(DataType.STRING),
        RACE(DataType.RACE),
        JOB(DataType.JOB),
        COPPER(DataType.LONG),
        ELEMENT(DataType.LONG);

        val key: String get() = name.lowercase()
    }

    private enum class DataType {
        STRING, LONG, RACE, JOB
    }

    private fun knownKeys(): List<String> = KnownKey.entries.map { it.key }

    private fun onlinePlayers(): List<String> = Bukkit.getOnlinePlayers().map { it.name }

    private fun valuesSuggestion(key: String): List<String> {
        val knownKey = KnownKey.entries.find { it.key == key.lowercase() } ?: return emptyList()
        return when (knownKey.type) {
            DataType.RACE -> Race.entries.map { it.name }
            DataType.JOB -> Job.entries.map { it.name }
            DataType.LONG -> emptyList()
            DataType.STRING -> emptyList()
        }
    }

    private fun resolveTarget(sender: Player, name: String): Player? {
        return Bukkit.getPlayerExact(name) ?: run {
            sender.sendMessage("§c玩家 $name 不在线")
            null
        }
    }

    private fun parseValue(key: String, value: String): Any? {
        val knownKey = KnownKey.entries.find { it.key == key.lowercase() }
        return when (knownKey?.type) {
            DataType.LONG -> value.toLongOrNull()
            DataType.RACE -> runCatching { Race.valueOf(value.uppercase()) }.getOrNull()
            DataType.JOB -> runCatching { Job.valueOf(value.uppercase()) }.getOrNull()
            DataType.STRING -> value
            null -> value
        }
    }

    /**
     * 更新 PlayerManager 缓存中的对应字段
     */
    private fun updateCache(player: Player, key: String, value: Any?) {
        val dewPlayer = PlayerManager.get(player)
        when (key.lowercase()) {
            "name" -> if (value is String) dewPlayer.name = value
            "race" -> if (value is Race) dewPlayer.race = value
            "job" -> if (value is Job) dewPlayer.job = value
            "copper" -> if (value is Long) dewPlayer.copper = value
            "element" -> if (value is Long) dewPlayer.element = value
        }
    }

    // ======================== 子命令 ========================

    @CommandBody(permission = "dew.debug")
    val ui = subCommand {
        execute<Player> { player, _, _ ->
            SelfMessage.openMessage(player)
        }
    }

    @CommandBody(permission = "dew.debug")
    val set = subCommand {
        dynamic("key") {
            suggestion<Player> { _, _ -> knownKeys() }
            dynamic("value") {
                suggestion<Player> { _, context ->
                    valuesSuggestion(context["key"])
                }
                execute<Player> { player, context, _ ->
                    val key = context["key"]
                    val valueStr = context["value"]
                    val value = parseValue(key, valueStr)

                    if (value == null) {
                        player.sendMessage("§c无效的值: $valueStr（键: $key）")
                        return@execute
                    }

                    // 更新 PDC 数据
                    val data = PlayerDataManager.dataOf(player)
                    data[key] = value

                    // 同步更新缓存
                    updateCache(player, key, value)

                    player.sendMessage("§a已设置 $key = $value")
                }
                dynamic("target") {
                    suggestion<Player> { _, _ -> onlinePlayers() }
                    execute<Player> { player, context, _ ->
                        val target = resolveTarget(player, context["target"]) ?: return@execute
                        val key = context["key"]
                        val valueStr = context["value"]
                        val value = parseValue(key, valueStr)

                        if (value == null) {
                            player.sendMessage("§c无效的值: $valueStr（键: $key）")
                            return@execute
                        }

                        // 更新 PDC 数据
                        val data = PlayerDataManager.dataOf(target)
                        data[key] = value

                        // 同步更新缓存
                        updateCache(target, key, value)

                        player.sendMessage("§a已设置 ${target.name} 的 $key = $value")
                    }
                }
            }
        }
    }

    @CommandBody(permission = "dew.debug")
    val get = subCommand {
        dynamic("key") {
            suggestion<Player> { _, _ -> knownKeys() }
            execute<Player> { player, context, _ ->
                val data = PlayerDataManager.dataOf(player)
                val value = data[context["key"]]
                val cached = getCachedValue(player, context["key"])
                player.sendMessage("§e${context["key"]} §7= §f$value §8(缓存: $cached)")
            }
            dynamic("target") {
                suggestion<Player> { _, _ -> onlinePlayers() }
                execute<Player> { player, context, _ ->
                    val target = resolveTarget(player, context["target"]) ?: return@execute
                    val data = PlayerDataManager.dataOf(target)
                    val value = data[context["key"]]
                    val cached = getCachedValue(target, context["key"])
                    player.sendMessage("§e${target.name} 的 ${context["key"]} §7= §f$value §8(缓存: $cached)")
                }
            }
        }
    }

    /**
     * 从 PlayerManager 缓存读取值
     */
    private fun getCachedValue(player: Player, key: String): Any? {
        val dewPlayer = PlayerManager.get(player)
        return when (key.lowercase()) {
            "name" -> dewPlayer.name
            "race" -> dewPlayer.race
            "job" -> dewPlayer.job
            "copper" -> dewPlayer.copper
            "element" -> dewPlayer.element
            else -> null
        }
    }

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
        val dewPlayer = PlayerManager.get(target)

        sender.sendMessage("§6===== ${target.name} 的数据 =====")
        sender.sendMessage("§b[缓存数据]")
        sender.sendMessage("§ename §7= §f${dewPlayer.name}")
        sender.sendMessage("§erace §7= §f${dewPlayer.race}")
        sender.sendMessage("§ejob §7= §f${dewPlayer.job}")
        sender.sendMessage("§ecopper §7= §f${dewPlayer.copper}")
        sender.sendMessage("§eelement §7= §f${dewPlayer.element}")

        if (all.isNotEmpty()) {
            sender.sendMessage("§b[PDC数据]")
            all.forEach { (k, v) ->
                val typeHint = KnownKey.entries.find { it.key == k }?.type?.name ?: "UNKNOWN"
                sender.sendMessage("§e$k §7= §f$v §8($typeHint)")
            }
        } else {
            sender.sendMessage("§7PDC 无数据")
        }
    }

    @CommandBody(permission = "dew.debug")
    val remove = subCommand {
        dynamic("key") {
            suggestion<Player> { _, _ -> knownKeys() }
            execute<Player> { player, context, _ ->
                val data = PlayerDataManager.dataOf(player)
                val removed = data.remove(context["key"])

                // 重置缓存为默认值
                if (removed) {
                    resetCacheValue(player, context["key"])
                }

                player.sendMessage(if (removed) "§a已删除 ${context["key"]}" else "§c未找到 ${context["key"]}")
            }
            dynamic("target") {
                suggestion<Player> { _, _ -> onlinePlayers() }
                execute<Player> { player, context, _ ->
                    val target = resolveTarget(player, context["target"]) ?: return@execute
                    val data = PlayerDataManager.dataOf(target)
                    val removed = data.remove(context["key"])

                    if (removed) {
                        resetCacheValue(target, context["key"])
                    }

                    player.sendMessage(if (removed) "§a已删除 ${target.name} 的 ${context["key"]}" else "§c未找到 ${target.name} 的 ${context["key"]}")
                }
            }
        }
    }

    /**
     * 重置缓存值为默认值
     */
    private fun resetCacheValue(player: Player, key: String) {
        val dewPlayer = PlayerManager.get(player)
        when (key.lowercase()) {
            "name" -> dewPlayer.name = player.name
            "race" -> dewPlayer.race = Race.NONE
            "job" -> dewPlayer.job = Job.NONE
            "copper" -> dewPlayer.copper = 0L
            "element" -> dewPlayer.element = 0L
        }
    }

    @CommandBody(permission = "dew.debug")
    val clear = subCommand {
        execute<Player> { player, _, _ ->
            PlayerDataManager.dataOf(player).clear()

            // 重置所有缓存
            val dewPlayer = PlayerManager.get(player)
            dewPlayer.name = player.name
            dewPlayer.race = Race.NONE
            dewPlayer.job = Job.NONE
            dewPlayer.copper = 0L
            dewPlayer.element = 0L

            player.sendMessage("§a已清空所有数据")
        }
        dynamic("target") {
            suggestion<Player> { _, _ -> onlinePlayers() }
            execute<Player> { player, context, _ ->
                val target = resolveTarget(player, context["target"]) ?: return@execute
                PlayerDataManager.dataOf(target).clear()

                // 重置所有缓存
                val dewPlayer = PlayerManager.get(target)
                dewPlayer.name = target.name
                dewPlayer.race = Race.NONE
                dewPlayer.job = Job.NONE
                dewPlayer.copper = 0L
                dewPlayer.element = 0L

                player.sendMessage("§a已清空 ${target.name} 的所有数据")
            }
        }
    }

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

    @CommandBody(permission = "dew.debug")
    val reload = subCommand {
        execute<Player> { player, _, _ ->
            PlayerDataManager.loadNow(player)
            // 重新加载缓存
            PlayerManager.reloadPlayer(player)
            player.sendMessage("§a已从PDC重新加载数据")
        }
        dynamic("target") {
            suggestion<Player> { _, _ -> onlinePlayers() }
            execute<Player> { player, context, _ ->
                val target = resolveTarget(player, context["target"]) ?: return@execute
                PlayerDataManager.loadNow(target)
                // 重新加载缓存
                PlayerManager.reloadPlayer(target)
                player.sendMessage("§a已从PDC重新加载 ${target.name} 的数据")
            }
        }
    }
}