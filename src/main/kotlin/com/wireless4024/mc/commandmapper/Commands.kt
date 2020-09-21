package com.wireless4024.mc.commandmapper

import com.wireless4024.mc.bukcore.api.CommandBase
import com.wireless4024.mc.bukcore.api.KotlinPlugin
import com.wireless4024.mc.bukcore.utils.Utils.Companion.dropJoinToString
import com.wireless4024.mc.bukcore.utils.Utils.Companion.filterToMutable
import com.wireless4024.mc.bukcore.utils.server
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class cmap(override val plugin: KotlinPlugin) : CommandBase {

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
		if (!sender.hasPermission("CommandMapper.cmap")) return true
		if (args.isNotEmpty()) {
			when (args.first()) {
				"reload" -> {
					CommandMapper.instance.reload()
					sender.sendMessage("${ChatColor.GREEN}reload successfully")
				}
				"add" -> {
					var world = (sender as? Player)?.location?.world?.name
					val command: String
					val to: String
					if (world == null) {
						world = args.elementAtOrNull(1) ?: return false
						command = args.elementAtOrNull(2) ?: return false
						to = args.dropJoinToString(3)
					} else {
						command = args.elementAtOrNull(1) ?: return false
						to = args.dropJoinToString(2)
					}
					if (world.isEmpty() || command.isEmpty() || to.isEmpty()) return false
					CommandMapper.instance.addMapping(world, command, to)
					if (world == "*") {
						if (to.equals("nothing", false)) sender.sendMessage("${ChatColor.GREEN}disabled command '/$command'")
						else sender.sendMessage("${ChatColor.GREEN}add mapping for '/$command' to '/$to'")
					} else {
						if (to.equals("nothing", false)) sender.sendMessage("${ChatColor.GREEN}disabled command '/$command' at world '$world'")
						else sender.sendMessage("${ChatColor.GREEN}add mapping for '/$command' to '/$to' at world '$world'")
					}
				}
				"remove", "rem" -> {
					var world = (sender as? Player)?.location?.world?.name
					val command: String
					if (world == null) {
						world = args.elementAtOrNull(1) ?: return false
						command = args.elementAtOrNull(2) ?: return false
					} else {
						command = args.elementAtOrNull(1) ?: return false
					}
					if (world.isEmpty() || command.isEmpty()) return false
					if (world == "*") {
						if (CommandMapper.instance.removeMapping(world, command)) sender.sendMessage("${ChatColor.GREEN}removed mapping for '/$command'")
						else sender.sendMessage("${ChatColor.RED}unable to remove '/$command'")
					} else {
						if (CommandMapper.instance.removeMapping(world, command)) sender.sendMessage("${ChatColor.GREEN}removed mapping for '/$command' at world '$world'")
						else sender.sendMessage("${ChatColor.RED}unable to remove '/$command' at world '$world' (not existed)")
					}
				}
				else            -> return false
			}
		} else return false
		return true
	}

	val commandList = mutableListOf("add", "remove", "reload")

	override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): MutableList<String>? {
		if (!sender.hasPermission("CommandMapper.cmap")) return null
		return if (args.size < 2)
		/********* nothing just intellij wrapping ********/
			if (args.isNotEmpty()) commandList.filterToMutable { it.startsWith(args.first()) }
			else commandList
		else if (args.size < 4) {
			when {
				args.first() == "add" && args.size < 4    -> server {
					helpMap.helpTopics.mapNotNullTo(ArrayList()) {
						if (it.name.startsWith(args.last(), 1, false)) it.name.drop(1).intern() else null
					}
				}
				args.first() == "remove" && args.size < 3 -> {
					var world = (sender as? Player)?.location?.world?.name
					val command: String
					if (world == null) {
						world = args.elementAtOrNull(1) ?: ""
						command = args.elementAtOrNull(2) ?: ""
					} else command = args.elementAtOrNull(1) ?: ""

					Mapping.list(world, command)
				}
				else                                      -> null
			}
		} else null
	}
}