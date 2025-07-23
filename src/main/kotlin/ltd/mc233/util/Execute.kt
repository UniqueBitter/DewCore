package ltd.mc233.util


import ltd.mc233.Main
import org.bukkit.Bukkit
import org.bukkit.entity.Player



object Execute {
    fun command(player: Player, command: String) {
        val name = player.name
        Bukkit.dispatchCommand(Main.console, "execute as $name at $name run $command")
    }
}