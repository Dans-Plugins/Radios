package dansplugins.radios.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesTest {

    @Test
    void translatesColourCodesAndPlaceholders() {
        Messages messages = Messages.fromRoot(TestConfigs.bundled());
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("frequency", "101.5");
        placeholders.put("message", "hello");

        String text = messages.get("broadcast-format", placeholders);

        assertEquals(ChatColor.translateAlternateColorCodes('&', "&8[&6Radio 101.5&8] &fhello"), text);
        assertFalse(text.contains("&"));
    }

    @Test
    void operatorTextOverridesTheDefault() {
        Messages messages = Messages.fromRoot(TestConfigs.withDefaults("/config-overrides.yml"));

        assertEquals(ChatColor.GRAY + "Click.", messages.get("receiver-off"));
        assertTrue(messages.has("receiver-on"), "keys absent from the operator file come from the defaults");
    }

    @Test
    void missingKeyRendersAVisibleMarker() {
        Messages messages = Messages.fromRoot(new YamlConfiguration());

        assertFalse(messages.has("no-such-key"));
        assertTrue(messages.get("no-such-key").contains("<missing message: no-such-key>"));
    }

    @Test
    void everyMessageKeyTheCodeUsesExistsInTheBundledFile() {
        Messages messages = Messages.fromRoot(TestConfigs.bundled());
        for (String key : new String[] {"help-header", "help-entry", "help.help", "help.reload",
                "unknown-subcommand", "reloaded", "no-permission", "broadcast-format", "receiver-on",
                "receiver-off", "tuned", "not-holding-receiver", "invalid-frequency"}) {
            assertTrue(messages.has(key), key);
        }
    }
}
