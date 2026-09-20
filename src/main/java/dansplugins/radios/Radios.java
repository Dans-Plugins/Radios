package dansplugins.radios;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin entry point.
 *
 * <p>This is the scaffold: it loads the default configuration and answers {@code /radios help}.
 * The behaviour described in MVP.md is built on top of it, in the packages laid out in ARCHITECTURE.md.
 */
public final class Radios extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Radios " + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Radios disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Radios " + getDescription().getVersion());
        sender.sendMessage("/" + label + " help - show this message");
        sender.sendMessage("See https://github.com/Dans-Plugins/Radios for the roadmap.");
        return true;
    }
}
