package dansplugins.radios.commands;

import dansplugins.radios.config.ConfigService;
import dansplugins.radios.config.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RadiosCommandTest {

    private ConfigService configService;
    private CommandSender sender;
    private Command command;
    private RadiosCommand dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        YamlConfiguration bundled;
        try (var reader = new InputStreamReader(
                getClass().getResourceAsStream("/config.yml"), StandardCharsets.UTF_8)) {
            bundled = YamlConfiguration.loadConfiguration(reader);
        }
        configService = mock(ConfigService.class);
        when(configService.messages()).thenReturn(Messages.fromRoot(bundled));
        sender = mock(CommandSender.class);
        when(sender.hasPermission(anyString())).thenReturn(true);
        command = mock(Command.class);
        dispatcher = new RadiosCommand(configService);
        dispatcher.register(new HelpSubcommand(configService, () -> dispatcher, "9.9.9"))
                .register(new ReloadSubcommand(configService));
    }

    private List<String> sent() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(sender, org.mockito.Mockito.atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    void noArgumentsShowsHelpWithVersionAndEveryPermittedSubcommand() {
        dispatcher.onCommand(sender, command, "radios", new String[0]);

        List<String> lines = sent();
        assertTrue(lines.get(0).contains("9.9.9"), lines.get(0));
        assertEquals(3, lines.size(), lines.toString());
        assertTrue(lines.get(1).contains("/radios help"));
        assertTrue(lines.get(2).contains("/radios reload"));
        assertTrue(lines.get(2).contains("Reload config.yml."));
    }

    @Test
    void helpHidesSubcommandsTheSenderMayNotUse() {
        when(sender.hasPermission("radios.admin")).thenReturn(false);

        dispatcher.onCommand(sender, command, "radio", new String[] {"help"});

        List<String> lines = sent();
        assertEquals(2, lines.size(), lines.toString());
        assertTrue(lines.get(1).contains("/radio help"));
    }

    @Test
    void unknownSubcommandIsAnswered() {
        dispatcher.onCommand(sender, command, "radios", new String[] {"bogus"});

        assertEquals(ChatColor.RED + "Unknown subcommand. Try " + ChatColor.WHITE + "/radios help"
                + ChatColor.RED + ".", sent().get(0));
    }

    @Test
    void permissionIsCheckedBeforeExecuting() {
        when(sender.hasPermission("radios.admin")).thenReturn(false);

        dispatcher.onCommand(sender, command, "radios", new String[] {"reload"});

        verify(configService, never()).load();
        assertEquals(ChatColor.RED + "You do not have permission to do that.", sent().get(0));
    }

    @Test
    void reloadReloadsThenConfirms() {
        dispatcher.onCommand(sender, command, "radios", new String[] {"RELOAD"});

        verify(configService).load();
        assertEquals(ChatColor.GREEN + "Radios configuration reloaded.", sent().get(0));
    }

    @Test
    void tabCompletionOffersOnlyPermittedSubcommands() {
        when(sender.hasPermission("radios.admin")).thenReturn(false);

        assertEquals(Arrays.asList("help"), dispatcher.onTabComplete(sender, command, "radios", new String[] {""}));
        when(sender.hasPermission("radios.admin")).thenReturn(true);
        assertEquals(Arrays.asList("reload"), dispatcher.onTabComplete(sender, command, "radios", new String[] {"re"}));
        assertTrue(dispatcher.onTabComplete(sender, command, "radios", new String[] {"bogus", ""}).isEmpty());
    }
}
