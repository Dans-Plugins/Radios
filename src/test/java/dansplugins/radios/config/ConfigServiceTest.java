package dansplugins.radios.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfigServiceTest {

    @Test
    void loadWritesDefaultsCopiesMissingKeysAndBuildsTypedViews() {
        JavaPlugin plugin = mock(JavaPlugin.class);
        YamlConfiguration file = new YamlConfiguration();
        file.setDefaults(TestConfigs.bundled());
        file.set("receiver.craftable", false);
        when(plugin.getConfig()).thenReturn(file);
        when(plugin.getLogger()).thenReturn(Logger.getLogger("test"));

        ConfigService service = new ConfigService(plugin);
        service.load();

        InOrder order = inOrder(plugin);
        order.verify(plugin).saveDefaultConfig();
        order.verify(plugin).reloadConfig();
        order.verify(plugin).saveConfig();
        assertTrue(file.options().copyDefaults(), "missing keys are copied in from the bundled defaults");
        assertFalse(service.config().receiverCraftable(), "the operator's value wins");
        assertEquals(Material.CLOCK, service.config().receiverMaterial(), "a missing key falls through to the default");
        assertTrue(service.messages().has("broadcast-format"));
    }
}
