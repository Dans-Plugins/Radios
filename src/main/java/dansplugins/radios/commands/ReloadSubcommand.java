package dansplugins.radios.commands;

import dansplugins.radios.config.ConfigService;
import org.bukkit.command.CommandSender;

/**
 * {@code /radios reload}: re-reads {@code config.yml}. The message is read after the reload so an
 * operator sees the wording they just saved.
 */
public final class ReloadSubcommand implements Subcommand {

    private final ConfigService configService;

    public ReloadSubcommand(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return "radios.admin";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        configService.load();
        sender.sendMessage(configService.messages().get("reloaded"));
    }
}
