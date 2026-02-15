package com.tingyu.command

import com.tingyu.ui.AlchemyUI
import com.tingyu.ui.ForgeUI
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand

@CommandHeader("OpenUi", permission = "panling.admin")
object OpenUi {

    @CommandBody(permission = "panling.admin")
    val forge = subCommand {
        execute<Player> { player, _, _ ->
            ForgeUI.openUI(player)
        }
    }

    @CommandBody(permission = "panling.admin")
    val alchemy = subCommand {
        execute<Player> { player, _, _ ->
            AlchemyUI.open(player)
        }
    }
}