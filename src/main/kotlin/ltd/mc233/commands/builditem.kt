package ltd.mc233.commands


import ltd.mc233.util.itemedit.Option
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

@CommandHeader("builditem", permission = "panling.admin.build")
object builditem {
    @CommandBody(permission = "panling.admin.build")
    val builditem = mainCommand {
        execute<Player> { sender, context, argument ->
            Option.open(sender)
        }
    }
}