package com.tingyu.command

import com.tingyu.ui.ForgeUI
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

@CommandHeader("OpenUi", permission = "panling.admin")
object OpenUi {
    @CommandBody(permission = "panling.admin")
    val OpenUi = mainCommand {
        execute<Player> { player, context, argument ->
            ForgeUI.openUI( player)
        }
    }
}