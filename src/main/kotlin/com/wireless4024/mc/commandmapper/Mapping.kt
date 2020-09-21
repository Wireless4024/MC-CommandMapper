package com.wireless4024.mc.commandmapper

import org.bukkit.configuration.Configuration
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer
import kotlin.collections.HashMap
import kotlin.collections.set
import kotlin.reflect.KProperty

private fun <T> rLazy(initializer: () -> T) = ResetableDelegate(initializer)

private class ResetableDelegate<T>(private val initializer: () -> T) {
	private val lazyRef: AtomicReference<Lazy<T>> = AtomicReference(
		lazy(
			initializer
		)
	)

	operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
		return lazyRef.get().getValue(thisRef, property)
	}

	fun reset() {
		lazyRef.set(lazy(initializer))
	}
}

object Mapping {
	private val mapping = HashMap<String, HashMap<String, String>>()
	private val _w_null_c_null = rLazy { ArrayList<String>().apply { mapping.values.forEach(Consumer { addAll(it.keys) }) } }
	private val w_null_c_null by _w_null_c_null
	private val global = HashMap<String, String>()

	private fun replaceCommand(s: String, command: String): String {
		val where = s.indexOf(' ')
		return if (where < 0) "/$command" else "/$command" + s.substring(where)
	}

	private fun command(s: String): String {
		val where = s.indexOf(' ')
		return if (where < 0) s.substring(1) else s.substring(1, where)
	}

	fun add(world: String, from: String, to: String) {
		reset()
		(mapping[world] ?: HashMap<String, String>().also {
			mapping[world] = it
		})[from.toLowerCase()] = to.toLowerCase()
	}

	fun remove(world: String, command: String) = mapping[world]?.remove(command)

	fun load(cfg: Configuration) {
		reset(true)
		mapping["*"] = global
		cfg.getConfigurationSection("mapping")?.let {
			for (worldName in it.getKeys(false)) {
				/********* nothing just intellij wrapping ********/
				val cfg = it.getConfigurationSection(worldName) ?: continue
				val mmap = (mapping[worldName] ?: (HashMap<String, String>().also { mapping[worldName] = it }))
				for (command in cfg.getKeys(false)) {
					mmap[command] = cfg.getString(command)?.toLowerCase() ?: continue
				}
			}
		}
	}

	fun list(world: String?, command: String?): MutableList<String> {
		val target = ArrayList<String>()
		if (world == null) {
			if (command == null || command.isEmpty()) w_null_c_null
			else w_null_c_null.forEach(Consumer {
				if (it.startsWith(command, false)) target.add(it)
			})
		} else {
			if (command == null || command.isEmpty()) target.addAll(mapping[world]?.keys ?: emptyList())
			else mapping[world]?.keys?.forEach(Consumer {
				if (it.startsWith(command, false)) target.add(it)
			})
		}
		return target
	}

	fun reset(hard: Boolean = false) {
		if (hard) {
			mapping.clear()
			global.clear()
		}
		_w_null_c_null.reset()
	}

	fun clear() = mapping.clear()

	private var last: String = ""
	fun process(e: PlayerCommandPreprocessEvent) {
		val msg = e.message
		val c = command(msg)
		val newCommand = global[c] ?: mapping[e.player.location.world?.name]?.get(c) ?: return
		if (newCommand == "nothing") {
			e.isCancelled = true
			if (c != last) { // prevent console flood
				last = c
				CommandMapper.instance.info("command $c is disabled")
			}
			return
		}
		e.message = replaceCommand(msg, newCommand)
	}
}