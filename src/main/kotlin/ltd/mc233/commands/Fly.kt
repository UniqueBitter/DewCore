package ltd.mc233.commands


import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

@CommandHeader("fly", permission = "panling.admin.fly")
object Fly {
    @CommandBody(permission = "panling.admin.fly")
    val fly = mainCommand {
        execute<Player> { sender, context, argument ->
            sender.allowFlight = !sender.allowFlight
        }
    }
}