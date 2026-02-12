package com.tingyu

import org.bukkit.Bukkit
import org.bukkit.World
import taboolib.common.platform.Plugin


object Main : Plugin() {
    val console = Bukkit.getConsoleSender()
    val onlinePlayer = Bukkit.getOnlinePlayers()
    var world: World? = Bukkit.getWorld("world")
    override fun onEnable() {
        console.sendMessage(
            """
        §a ██████╗ ███████╗██╗    ██╗ ██████╗ ██████╗ ██████╗ ███████╗
        §a ██╔══██╗██╔════╝██║    ██║██╔════╝██╔═══██╗██╔══██╗██╔════╝
        §a ██║  ██║█████╗  ██║ █╗ ██║██║     ██║   ██║██████╔╝█████╗  
        §a ██║  ██║██╔══╝  ██║███╗██║██║     ██║   ██║██╔══██╗██╔══╝  
        §a ██████╔╝███████╗╚███╔███╔╝╚██████╗╚██████╔╝██║  ██║███████╗
        §a ╚═════╝ ╚══════╝ ╚══╝╚══╝  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝                
        §9 ███████    盘古开天，万物始生。朝露承灵，古域重现    ███████
        """
        )
    }

    override fun onDisable() {
        console.sendMessage("§a 插件已卸载")
    }

}
