package com.tingyu.command

import com.tingyu.player.PlayerManager
import com.tingyu.player.StatApplier
import com.tingyu.player.stat.StatCalculator
import com.tingyu.player.stat.StatType
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper

@CommandHeader("dewstat", permission = "dewcore.admin", aliases = ["ds"])
object DewStatCommand {

    private val VANILLA_MAP = mapOf(
        StatType.HP               to Attribute.GENERIC_MAX_HEALTH,
        StatType.ARMOR            to Attribute.GENERIC_ARMOR,
        StatType.ARMOR_TOUGHNESS  to Attribute.GENERIC_ARMOR_TOUGHNESS,
        StatType.MOVE_SPEED       to Attribute.GENERIC_MOVEMENT_SPEED,
        StatType.KNOCKBACK_RESIST to Attribute.GENERIC_KNOCKBACK_RESISTANCE,
    )

    @CommandBody
    val main = mainCommand {
        createHelper()

        // ── info [玩家] ──────────────────────────────────────────────
        literal("info") {
            dynamic("target") {
                suggestion<Player> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                execute<Player> { sender, context, _ ->
                    val target = resolvePlayer(sender, context["target"]) ?: return@execute
                    printStats(sender, target)
                }
            }
            execute<Player> { sender, _, _ -> printStats(sender, sender) }
        }

        // ── set <属性> <值> [玩家] ────────────────────────────────────
        literal("set") {
            dynamic("stat") {
                suggestion<Player> { _, _ -> StatType.entries.map { it.name.lowercase() } }
                dynamic("value") {
                    suggestion<Player>(uncheck = true) { _, _ -> listOf("0", "1", "10", "100", "0.5", "1.5") }
                    dynamic("target") {
                        suggestion<Player> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                        execute<Player> { sender, context, _ ->
                            val target = resolvePlayer(sender, context["target"]) ?: return@execute
                            setStat(sender, target, context["stat"], context["value"])
                        }
                    }
                    execute<Player> { sender, context, _ ->
                        setStat(sender, sender, context["stat"], context["value"])
                    }
                }
            }
        }

        // ── add <属性> <值> [玩家] ────────────────────────────────────
        literal("add") {
            dynamic("stat") {
                suggestion<Player> { _, _ -> StatType.entries.map { it.name.lowercase() } }
                dynamic("value") {
                    suggestion<Player>(uncheck = true) { _, _ -> listOf("-10", "-1", "-0.5", "0.5", "1", "10") }
                    dynamic("target") {
                        suggestion<Player> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                        execute<Player> { sender, context, _ ->
                            val target = resolvePlayer(sender, context["target"]) ?: return@execute
                            addStat(sender, target, context["stat"], context["value"])
                        }
                    }
                    execute<Player> { sender, context, _ ->
                        addStat(sender, sender, context["stat"], context["value"])
                    }
                }
            }
        }

        // ── reset [玩家] ──────────────────────────────────────────────
        literal("reset") {
            dynamic("target") {
                suggestion<Player> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                execute<Player> { sender, context, _ ->
                    val target = resolvePlayer(sender, context["target"]) ?: return@execute
                    resetStats(sender, target)
                }
            }
            execute<Player> { sender, _, _ -> resetStats(sender, sender) }
        }

        // ── refresh [玩家] ────────────────────────────────────────────
        literal("refresh") {
            dynamic("target") {
                suggestion<Player> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                execute<Player> { sender, context, _ ->
                    val target = resolvePlayer(sender, context["target"]) ?: return@execute
                    StatApplier.apply(target)
                    sender.sendMessage("§a已刷新 §e${target.name} §a的属性")
                }
            }
            execute<Player> { sender, _, _ ->
                StatApplier.apply(sender)
                sender.sendMessage("§a属性已刷新")
            }
        }
    }

    // ======================== 内部逻辑 ========================

    private fun printStats(sender: Player, target: Player) {
        val profile = PlayerManager.get(target)
        val isSelf = sender == target

        sender.sendMessage("§8§m------------------------------------")
        sender.sendMessage(" §b玩家属性 §e${target.name}  §7[Lv.${profile.level} §7转:${profile.jobPromotion}]")
        sender.sendMessage(" §7职业: §f${profile.job.displayColor}${profile.job.displayName}  §7种族: §f${profile.race.displayColor}${profile.race.displayName}")
        sender.sendMessage("§8§m------------------------------------")

        sender.sendMessage(" §e【原版属性 · 实时生效】")
        for ((statType, attribute) in VANILLA_MAP) {
            val base = profile.getBase(statType)
            val computed = StatCalculator.compute(statType, profile.buildLayer(statType))
            val actual = target.getAttribute(attribute)?.value ?: computed
            sender.sendMessage(
                "  §7${statType.displayName.padEnd(6)}  §f${fmt(computed)}  §8(基础:${fmt(base)}  实际:${fmt(actual)})"
            )
        }

        sender.sendMessage(" §e【战斗属性 · 计算值】")
        val combatTypes = StatType.entries.filter { it !in VANILLA_MAP }
        for (statType in combatTypes) {
            val base = profile.getBase(statType)
            val computed = StatCalculator.compute(statType, profile.buildLayer(statType))
            sender.sendMessage(
                "  §7${statType.displayName.padEnd(6)}  §f${fmt(computed)}  §8(基础:${fmt(base)})"
            )
        }

        sender.sendMessage("§8§m------------------------------------")
        if (!isSelf) return
        sender.sendMessage(" §7当前血量: §c${fmt(sender.health)} §8/ §c${fmt(target.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0)}")
        sender.sendMessage("§8§m------------------------------------")
    }

    private fun setStat(sender: Player, target: Player, statName: String, valueStr: String) {
        val statType = resolveStatType(sender, statName) ?: return
        val value = valueStr.toDoubleOrNull()
        if (value == null) {
            sender.sendMessage("§c无效数值: $valueStr")
            return
        }
        val profile = PlayerManager.get(target)
        profile.setBase(statType, value)
        StatApplier.apply(target)
        sender.sendMessage("§a已将 §e${target.name} §7的 §b${statType.displayName} §a基础值设为 §f${fmt(value)}")
        if (sender != target) target.sendMessage("§7你的 §b${statType.displayName} §7基础值被设为 §f${fmt(value)}")
    }

    private fun addStat(sender: Player, target: Player, statName: String, valueStr: String) {
        val statType = resolveStatType(sender, statName) ?: return
        val delta = valueStr.toDoubleOrNull()
        if (delta == null) {
            sender.sendMessage("§c无效数值: $valueStr")
            return
        }
        val profile = PlayerManager.get(target)
        val newBase = profile.getBase(statType) + delta
        profile.setBase(statType, newBase)
        StatApplier.apply(target)
        val sign = if (delta >= 0) "§a+" else "§c"
        sender.sendMessage("§e${target.name} §7的 §b${statType.displayName} §7基础值 $sign${fmt(delta)} §8→ §f${fmt(newBase)}")
        if (sender != target) target.sendMessage("§7你的 §b${statType.displayName} §7基础值 $sign${fmt(delta)} §8→ §f${fmt(newBase)}")
    }

    private fun resetStats(sender: Player, target: Player) {
        val profile = PlayerManager.get(target)
        profile.baseStats.clear()
        StatApplier.apply(target)
        sender.sendMessage("§a已重置 §e${target.name} §a的所有属性基础值")
        if (sender != target) target.sendMessage("§c你的所有属性基础值已被重置")
    }

    // ======================== 工具方法 ========================

    private fun resolvePlayer(sender: Player, name: String): Player? {
        val target = Bukkit.getPlayerExact(name)
        if (target == null) sender.sendMessage("§c玩家 §e$name §c不在线")
        return target
    }

    private fun resolveStatType(sender: Player, name: String): StatType? {
        val type = StatType.entries.find { it.name.equals(name, ignoreCase = true) }
        if (type == null) {
            sender.sendMessage("§c未知属性: §e$name")
            sender.sendMessage("§7可用属性: §f${StatType.entries.joinToString("§7, §f") { it.name.lowercase() }}")
        }
        return type
    }

    private fun fmt(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString()
        else "%.2f".format(value)
}
