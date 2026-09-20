package com.dansplugins.radios

import org.bukkit.plugin.java.JavaPlugin

class Radios : JavaPlugin() {

    override fun onEnable() {
        logger.info("Radios ${description.version} enabled.")
    }

    override fun onDisable() {
        logger.info("Radios disabled.")
    }
}
