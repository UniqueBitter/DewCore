package com.tingyu.map.command

import com.tingyu.map.zone.Zone
import com.tingyu.map.zone.ZoneManager
import com.tingyu.map.zone.ZoneType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import java.util.UUID

@CommandHeader("zone", permission = "dewcore.admin", aliases = ["z"])
object ZoneCommand {

    // 每个玩家当前框选的两个角点
    private val selections = mutableMapOf<UUID, Pair<Location?, Location?>>()

    @CommandBody
    val main = mainCommand {
        createHelper()

        // ── pos1 / pos2 ── 设置选区角点（当前站立位置）─────────────
        literal("pos1") {
            execute<Player> { sender, _, _ ->
                val sel = selections.getOrDefault(sender.uniqueId, Pair(null, null))
                selections[sender.uniqueId] = sel.copy(first = sender.location)
                sender.sendMessage("§a已设置角点1: §f${fmtLoc(sender.location)}")
            }
        }

        literal("pos2") {
            execute<Player> { sender, _, _ ->
                val sel = selections.getOrDefault(sender.uniqueId, Pair(null, null))
                selections[sender.uniqueId] = sel.copy(second = sender.location)
                sender.sendMessage("§a已设置角点2: §f${fmtLoc(sender.location)}")
            }
        }

        // ── create <id> <displayName> ─────────────────────────────
        literal("create") {
            dynamic("id") {
                dynamic("name") {
                    execute<Player> { sender, context, _ ->
                        val (p1, p2) = selections[sender.uniqueId] ?: Pair(null, null)
                        if (p1 == null || p2 == null) {
                            sender.sendMessage("§c请先用 /zone pos1 和 /zone pos2 设置选区")
                            return@execute
                        }
                        if (p1.world?.name != p2.world?.name) {
                            sender.sendMessage("§c两个角点必须在同一个世界")
                            return@execute
                        }
                        val id = context["id"]
                        if (ZoneManager.get(id) != null) {
                            sender.sendMessage("§c区域 §e$id §c已存在，请换一个 id")
                            return@execute
                        }
                        val zone = Zone(
                            id = id,
                            displayName = context["name"],
                            world = p1.world!!.name,
                            minX = minOf(p1.blockX, p2.blockX),
                            minY = minOf(p1.blockY, p2.blockY),
                            minZ = minOf(p1.blockZ, p2.blockZ),
                            maxX = maxOf(p1.blockX, p2.blockX),
                            maxY = maxOf(p1.blockY, p2.blockY),
                            maxZ = maxOf(p1.blockZ, p2.blockZ)
                        )
                        ZoneManager.register(zone)
                        ZoneManager.save()
                        selections.remove(sender.uniqueId)
                        sender.sendMessage("§a区域 §e${zone.displayName} §7(§f${zone.id}§7) §a已创建")
                    }
                }
            }
        }

        // ── delete <id> ───────────────────────────────────────────
        literal("delete") {
            dynamic("id") {
                suggestion<Player> { _, _ -> ZoneManager.all().map { it.id } }
                execute<Player> { sender, context, _ ->
                    val id = context["id"]
                    if (ZoneManager.remove(id) != null) {
                        ZoneManager.save()
                        sender.sendMessage("§a区域 §e$id §a已删除")
                    } else {
                        sender.sendMessage("§c区域 §e$id §c不存在")
                    }
                }
            }
        }

        // ── info [id] ─────────────────────────────────────────────
        literal("info") {
            dynamic("id") {
                suggestion<Player> { _, _ -> ZoneManager.all().map { it.id } }
                execute<Player> { sender, context, _ ->
                    printInfo(sender, context["id"])
                }
            }
            execute<Player> { sender, _, _ ->
                val zone = ZoneManager.getPrimaryZone(sender.location)
                if (zone == null) sender.sendMessage("§7你当前不在任何区域内")
                else printInfo(sender, zone.id)
            }
        }

        // ── list ──────────────────────────────────────────────────
        literal("list") {
            execute<Player> { sender, _, _ ->
                val all = ZoneManager.all()
                if (all.isEmpty()) { sender.sendMessage("§7暂无区域"); return@execute }
                sender.sendMessage("§8§m------------------------")
                sender.sendMessage(" §b区域列表 §7(共 ${all.size} 个)")
                all.forEach { z ->
                    sender.sendMessage("  §7${z.type.color}${z.type.displayName} §f${z.displayName} §8[${z.id}] §7等级≥${z.minLevel}")
                }
                sender.sendMessage("§8§m------------------------")
            }
        }

        // ── here ─────────────────────────────────────────────────
        literal("here") {
            execute<Player> { sender, _, _ ->
                val zones = ZoneManager.getZonesAt(sender.location)
                if (zones.isEmpty()) sender.sendMessage("§7你当前不在任何区域内")
                else zones.forEach { printInfo(sender, it.id) }
            }
        }

        // ── setlevel <id> <n> ─────────────────────────────────────
        literal("setlevel") {
            dynamic("id") {
                suggestion<Player> { _, _ -> ZoneManager.all().map { it.id } }
                dynamic("level") {
                    execute<Player> { sender, context, _ ->
                        val zone = resolveZone(sender, context["id"]) ?: return@execute
                        val lvl = context["level"].toIntOrNull()
                        if (lvl == null) { sender.sendMessage("§c无效等级"); return@execute }
                        zone.minLevel = lvl
                        ZoneManager.save()
                        sender.sendMessage("§a${zone.displayName} 最低等级设为 §e$lvl")
                    }
                }
            }
        }

        // ── settype <id> <type> ───────────────────────────────────
        literal("settype") {
            dynamic("id") {
                suggestion<Player> { _, _ -> ZoneManager.all().map { it.id } }
                dynamic("type") {
                    suggestion<Player> { _, _ -> ZoneType.entries.map { it.name.lowercase() } }
                    execute<Player> { sender, context, _ ->
                        val zone = resolveZone(sender, context["id"]) ?: return@execute
                        val type = ZoneType.entries.find { it.name.equals(context["type"], ignoreCase = true) }
                        if (type == null) { sender.sendMessage("§c未知类型"); return@execute }
                        zone.type = type
                        ZoneManager.save()
                        sender.sendMessage("§a${zone.displayName} 类型设为 §e${type.displayName}")
                    }
                }
            }
        }

        // ── setmsg <id> enter|exit <消息> ─────────────────────────
        literal("setmsg") {
            dynamic("id") {
                suggestion<Player> { _, _ -> ZoneManager.all().map { it.id } }
                dynamic("type") {
                    suggestion<Player> { _, _ -> listOf("enter", "exit") }
                    dynamic("message") {
                        execute<Player> { sender, context, _ ->
                            val zone = resolveZone(sender, context["id"]) ?: return@execute
                            val msg = context["message"]
                            when (context["type"]) {
                                "enter" -> zone.enterMessage = msg.replace("&", "§")
                                "exit"  -> zone.exitMessage  = msg.replace("&", "§")
                                else    -> { sender.sendMessage("§c请输入 enter 或 exit"); return@execute }
                            }
                            ZoneManager.save()
                            sender.sendMessage("§a消息已设置")
                        }
                    }
                }
            }
        }

        // ── tp <id> ───────────────────────────────────────────────
        literal("tp") {
            dynamic("id") {
                suggestion<Player> { _, _ -> ZoneManager.all().map { it.id } }
                execute<Player> { sender, context, _ ->
                    val zone = resolveZone(sender, context["id"]) ?: return@execute
                    val center = zone.center()
                    if (center == null) { sender.sendMessage("§c世界未加载"); return@execute }
                    sender.teleport(center)
                    sender.sendMessage("§a已传送到 §e${zone.displayName}")
                }
            }
        }

        // ── reload ────────────────────────────────────────────────
        literal("reload") {
            execute<Player> { sender, _, _ ->
                ZoneManager.reload()
                sender.sendMessage("§a区域配置已重载，共 §e${ZoneManager.all().size} §a个区域")
            }
        }
    }

    // ======================== 工具方法 ========================

    private fun printInfo(sender: Player, id: String) {
        val z = ZoneManager.get(id)
        if (z == null) { sender.sendMessage("§c区域 §e$id §c不存在"); return }
        sender.sendMessage("§8§m------------------------")
        sender.sendMessage(" §b${z.displayName} §8[${z.id}]")
        sender.sendMessage(" §7类型: ${z.type.color}${z.type.displayName}  §7等级限制: §e${z.minLevel}")
        sender.sendMessage(" §7世界: §f${z.world}")
        sender.sendMessage(" §7范围: §f(${z.minX},${z.minY},${z.minZ}) §7→ §f(${z.maxX},${z.maxY},${z.maxZ})")
        z.instanceId?.let { sender.sendMessage(" §7副本ID: §d$it") }
        z.enterMessage?.let { sender.sendMessage(" §7进入消息: §r$it") }
        z.exitMessage?.let  { sender.sendMessage(" §7离开消息: §r$it") }
        sender.sendMessage("§8§m------------------------")
    }

    private fun resolveZone(sender: Player, id: String): Zone? {
        val zone = ZoneManager.get(id)
        if (zone == null) sender.sendMessage("§c区域 §e$id §c不存在")
        return zone
    }

    private fun fmtLoc(loc: Location) =
        "${loc.world?.name} (${loc.blockX}, ${loc.blockY}, ${loc.blockZ})"
}
