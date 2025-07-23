package ltd.mc233.commands

import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

@CommandHeader("spawn", permission = "panling.admin.spawn")
object Spawn {
    @CommandBody(permission = "panling.admin.spawn")
    val spawn = mainCommand {
        execute<Player> { sender, context, argument ->
            val loc: Location = Location(sender.world, 179.0, 44.0, 63.0)
            sender.teleport(loc)
        }
    }
}