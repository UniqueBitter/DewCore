package ltd.mc233.commands


import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand


@CommandHeader("double", permission = "panling.admin.double")
object double {
    @CommandBody(permission = "panling.admin.chest")
    val chest = mainCommand {
        execute<Player> { sender, context, argument ->
            val player = sender as Player ?:return@execute
            val item = player.inventory.itemInMainHand
            item.amount *=  2
        }
    }
}