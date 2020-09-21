@file:JvmName("CommandMapper")

package com.wireless4024.mc.commandmapper

import com.wireless4024.mc.bukcore.api.KotlinPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.LOWEST
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class CommandMapper : KotlinPlugin(), Listener {
	companion object {
		private var ins: CommandMapper? = null
		@JvmStatic val instance get() = ins!!
	}

	@EventHandler(priority = LOWEST) fun onChat(e: PlayerCommandPreprocessEvent) = Mapping.process(e)

	fun reload(block: (CommandMapper.() -> Unit)? = null) {
		try {
			reloadConfig()
		} catch (e: Throwable) {
			logger.warning("have error in config")
			logger.warning(e.message)
			logger.warning("resetting config")
			config.options().copyHeader(true).copyDefaults(true)
			return
		}
		block?.invoke(this)
		Mapping.reset(true)
		Mapping.load(config)
		saveConfig()
	}

	fun addMapping(world: String, from: String, to: String) {
		reload {
			config.set("mapping.$world.${from.toLowerCase()}", to.toLowerCase())
		}
	}

	fun removeMapping(world: String, command: String): Boolean {
		var s: Boolean = true
		reload {
			val key = "mapping.$world.${command.toLowerCase()}"
			if (config.isSet(key)) config.set(key, null)
			else s = false
		}
		return s
	}

	override fun onEnable() {
		ins = this
		saveDefaultConfig()
		Mapping.load(config)
		server.pluginManager.registerEvents(this, this)
		cmap(this).register()
	}

	override fun onDisable() {
		Mapping.clear()
	}
}