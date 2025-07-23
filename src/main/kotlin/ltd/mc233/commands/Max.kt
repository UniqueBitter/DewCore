package ltd.mc233.commands

import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand


@CommandHeader("max", permission = "panling.admin.max")
object Max {
    @CommandBody(permission = "panling.admin.max")
    val max = mainCommand {
        execute<Player> { sender, context, argument ->
            val player = sender as Player ?:return@execute
            val item = player.inventory.itemInMainHand
            item.amount = item.maxStackSize
        }
    }
}