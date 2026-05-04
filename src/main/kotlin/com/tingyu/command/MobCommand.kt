package com.tingyu.command

import com.tingyu.mob.MobManager
import com.tingyu.mob.MobRegistry
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper

@CommandHeader("dewmob", permission = "dewcore.admin", aliases = ["dm"])
object MobCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()

        // /dewmob spawn <id> [数量]
        literal("spawn") {
            dynamic("id") {
                suggestion<Player> { _, _ -> MobRegistry.values().map { it.id } }
                dynamic("amount") {
                    suggestion<Player>(uncheck = true) { _, _ -> listOf("1", "3", "5") }
                    execute<Player> { sender, context, _ ->
                        spawnMob(sender, context["id"], context["amount"].toIntOrNull() ?: 1)
                    }
                }
                execute<Player> { sender, context, _ ->
                    spawnMob(sender, context["id"], 1)
                }
            }
        }

        // /dewmob list
        literal("list") {
            execute<Player> { sender, _, _ ->
                val mobs = MobRegistry.values()
                sender.sendMessage("§8§m--------------------")
                sender.sendMessage(" §b已注册怪物 §8(${mobs.size})")
                mobs.forEach { profile ->
                    val entityStr = if (profile.entityType != null) "§7[${profile.entityType}]" else "§8[手动]"
                    sender.sendMessage("  §f${profile.id}  $entityStr  §e${profile.xpReward} XP")
                }
                sender.sendMessage("§8§m--------------------")
            }
        }
    }

    // ======================== 内部逻辑 ========================

    private fun spawnMob(sender: Player, id: String, amount: Int) {
        val profile = MobRegistry.fromId(id)
        if (profile == null) {
            sender.sendMessage("§c未知怪物 ID: §e$id")
            sender.sendMessage("§7用 §f/dewmob list §7查看所有可用 ID")
            return
        }

        val spawnType = profile.spawnType
        if (spawnType == null) {
            sender.sendMessage("§c怪物 §e${profile.id} §c未设置召唤实体类型，请在注册时调用 §f.spawn(EntityType.XXX)")
            return
        }

        val count = amount.coerceIn(1, 20)
        var spawned = 0

        repeat(count) {
            val entity = sender.world.spawnEntity(sender.location, spawnType) as? LivingEntity
            if (entity != null) {
                (entity as? Mob)?.target = null
                MobManager.apply(entity, profile)
                spawned++
            }
        }

        sender.sendMessage("§a已在当前位置召唤 §e$spawned §a只 §f${profile.id}")
    }
}
