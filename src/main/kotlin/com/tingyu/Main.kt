package com.tingyu

import org.bukkit.Bukkit
import taboolib.common.platform.Plugin


object Main : Plugin() {
    val console = Bukkit.getConsoleSender()
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

}
