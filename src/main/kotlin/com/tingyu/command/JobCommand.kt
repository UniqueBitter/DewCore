package com.tingyu.command

import com.tingyu.player.LevelManager
import com.tingyu.player.PlayerManager
import com.tingyu.player.job.Job
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper

@CommandHeader("dewjob", permission = "dewcore.admin", aliases = ["dj"])
object JobCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()

        // /dewjob set <职业> [玩家]
        literal("set") {
            dynamic("job") {
                suggestion<Player> { _, _ -> Job.entries.map { it.name.lowercase() } }
                dynamic("target") {
                    suggestion<Player> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                    execute<Player> { sender, context, _ ->
                        val target = resolvePlayer(sender, context["target"]) ?: return@execute
                        setJob(sender, target, context["job"])
                    }
                }
                execute<Player> { sender, context, _ ->
                    setJob(sender, sender, context["job"])
                }
            }
        }

        // /dewjob info [玩家]
        literal("info") {
            dynamic("target") {
                suggestion<Player> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                execute<Player> { sender, context, _ ->
                    val target = resolvePlayer(sender, context["target"]) ?: return@execute
                    printJob(sender, target)
                }
            }
            execute<Player> { sender, _, _ -> printJob(sender, sender) }
        }
    }

    // ======================== 内部逻辑 ========================

    private fun setJob(sender: Player, target: Player, jobName: String) {
        val job = Job.entries.find { it.name.equals(jobName, ignoreCase = true) }
        if (job == null) {
            sender.sendMessage("§c未知职业: §e$jobName")
            sender.sendMessage("§7可用: §f${Job.entries.joinToString("§7, §f") { it.name.lowercase() }}")
            return
        }
        val profile = PlayerManager.get(target)
        val oldJob = profile.job
        profile.job = job
        LevelManager.rebuildBaseStats(profile)
        PlayerManager.refresh(target)

        sender.sendMessage(
            "§a已将 §e${target.name} §a的职业: " +
            "${oldJob.displayColor}${oldJob.displayName} §8→ ${job.displayColor}${job.displayName}"
        )
        if (sender != target) {
            target.sendMessage("§7你的职业已变更为 ${job.displayColor}${job.displayName}")
        }
    }

    private fun printJob(sender: Player, target: Player) {
        val profile = PlayerManager.get(target)
        val needed = LevelManager.expToNextLevel(profile.level)
        val expStr = if (profile.level >= LevelManager.MAX_LEVEL) "§7(满级)" else "§f${profile.exp}§7/§f$needed"
        sender.sendMessage("§8§m--------------------")
        sender.sendMessage(" §e${target.name}  §7Lv.§f${profile.level}")
        sender.sendMessage(" §7职业: ${profile.job.displayColor}${profile.job.displayName}  §7种族: ${profile.race.displayColor}${profile.race.displayName}")
        sender.sendMessage(" §7经验: $expStr")
        sender.sendMessage("§8§m--------------------")
    }

    private fun resolvePlayer(sender: Player, name: String): Player? {
        val target = Bukkit.getPlayerExact(name)
        if (target == null) sender.sendMessage("§c玩家 §e$name §c不在线")
        return target
    }
}
