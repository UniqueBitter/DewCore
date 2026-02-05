package com.tingyu.command

import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand


@CommandHeader("double", permission = "panling.admin.double")
object Double {
    @CommandBody(permission = "panling.admin.chest")
    val chest = mainCommand {
        execute<Player> { sender, context, argument ->
            val item = sender.inventory.itemInMainHand
            item.amount *=  2
        }
    }
}