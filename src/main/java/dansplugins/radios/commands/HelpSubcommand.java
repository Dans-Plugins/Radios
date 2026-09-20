package dansplugins.radios.commands;

import dansplugins.radios.config.ConfigService;
import dansplugins.radios.config.Messages;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * {@code /radios help}: lists every registered subcommand the sender may use, with the
 * description from {@code messages.help.<name>}.
 */
public final class HelpSubcommand implements Subcommand {

    private final ConfigService configService;
    private final Supplier<RadiosCommand> dispatcher;
    private final String version;

    public HelpSubcommand(ConfigService configService, Supplier<RadiosCommand> dispatcher, String version) {
        this.configService = configService;
        this.dispatcher = dispatcher;
        this.version = version;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String permission() {
        return "radios.use";
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Messages messages = configService.messages();
        Map<String, Object> header = new HashMap<>();
        header.put("version", version);
        sender.sendMessage(messages.get("help-header", header));
        for (Subcommand subcommand : dispatcher.get().subcommands()) {
            if (!sender.hasPermission(subcommand.permission())) {
                continue;
            }
            Map<String, Object> entry = new HashMap<>();
            entry.put("label", label);
            entry.put("name", subcommand.name());
            entry.put("description", messages.get("help." + subcommand.name()));
            sender.sendMessage(messages.get("help-entry", entry));
        }
    }
}
