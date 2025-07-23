package ltd.mc233

import org.bukkit.Bukkit
import taboolib.common.platform.Plugin


object DewCore : Plugin() {
    val console = Bukkit.getConsoleSender()
    val onlinePlayer = Bukkit.getOnlinePlayers()
    val plugin = this
    val world = Bukkit.getWorld("world")

    override fun onEnable() {
        console.sendMessage(
            """
        §a ██████╗ ███████╗██╗    ██╗ ██████╗ ██████╗ ██████╗ ███████╗
        §a ██╔══██╗██╔════╝██║    ██║██╔════╝██╔═══██╗██╔══██╗██╔════╝
        §a ██║  ██║█████╗  ██║ █╗ ██║██║     ██║   ██║██████╔╝█████╗  
        §a ██║  ██║██╔══╝  ██║███╗██║██║     ██║   ██║██╔══██╗██╔══╝  
        §a ██████╔╝███████╗╚███╔███╔╝╚██████╗╚██████╔╝██║  ██║███████╗
        §a ╚═════╝ ╚══════╝ ╚══╝╚══╝  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝                
        §9 ██████████████████ 朝露盘灵核心加载完成 █████████████████████
        """
        )

    }
}