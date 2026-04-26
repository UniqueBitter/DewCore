package com.tingyu.command


import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper


@CommandHeader("Dewitem", permission = "panling.admin")
object DewitemCommand {

    @CommandBody
    val main = mainCommand {
        // 自动生成帮助信息
        createHelper()

        // 子命令：give <id> [amount] [target]
        literal("give") {
            // 物品 ID 参数
            dynamic("id") {
                suggestion<Player> { _, _ ->
                    DewItem.values().map { it.data.id }
                }

                // 数量参数 (可选)
                dynamic("amount") {
                    suggestion<Player>(uncheck = true) { _, _ -> listOf() }

                    // 目标玩家参数 (可选)
                    dynamic("target") {
                        suggestion<Player> { _, _ -> Bukkit.getOnlinePlayers().map { it.name } }
                        execute<Player> { sender, context, _ ->
                            val id = context["id"]
                            val amount = context["amount"].toIntOrNull() ?: 1
                            val targetName = context["target"]
                            val target = Bukkit.getPlayerExact(targetName)

                            if (target == null) {
                                sender.sendMessage("§c玩家 $targetName 不在线")
                                return@execute
                            }

                            giveItem(sender, target, id, amount)
                        }
                    }

                    // 执行：/dewitem give <id> <amount> (给自己)
                    execute<Player> { sender, context, _ ->
                        val id = context["id"]
                        val amount = context["amount"].toIntOrNull() ?: 1
                        giveItem(sender, sender, id, amount)
                    }
                }

                // 执行：/dewitem give <id> (给 1 个自己)
                execute<Player> { sender, context, _ ->
                    giveItem(sender, sender, context["id"], 1)
                }
            }
        }
    }

    /**
     * 核心发放物品函数
     */
    private fun giveItem(sender: Player, target: Player, id: String, amount: Int) {
        val dewItem = DewItem.fromId(id)
        if (dewItem == null) {
            sender.sendMessage("§c物品 ID '$id' 不存在")
            return
        }

        val item = dewItem.getItem(amount)
        // 给玩家物品，并处理背包满的情况
        val leftover = target.inventory.addItem(item)
        leftover.values.forEach { target.world.dropItemNaturally(target.location, it) }

        if (sender == target) {
            sender.sendMessage("§a已获得 $amount 个 ${dewItem.data.displayName}")
        } else {
            sender.sendMessage("§a已给予 ${target.name} $amount 个 ${dewItem.data.displayName}")
            target.sendMessage("§a你收到了 $amount 个 ${dewItem.data.displayName}")
        }
    }
}