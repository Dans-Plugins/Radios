package dansplugins.radios;

import org.bukkit.plugin.java.JavaPlugin;

public final class Radios extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Radios " + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Radios disabled.");
    }
}
