package dansplugins.radios.config;

import dansplugins.radios.frequency.Frequency;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginConfigTest {

    @Test
    void bundledDefaultsLoadWithoutWarnings() {
        List<String> warnings = new ArrayList<>();
        PluginConfig config = PluginConfig.from(TestConfigs.bundled(), warnings::add);

        assertTrue(warnings.isEmpty(), warnings.toString());
        assertEquals(PluginConfig.CURRENT_VERSION, config.configVersion());
        assertEquals(Material.CLOCK, config.receiverMaterial());
        assertEquals(Frequency.of(100.0), config.defaultFrequency());
        assertTrue(config.receiverCraftable());
        assertEquals(88.0, config.band().min());
        assertEquals(108.0, config.band().max());
        assertEquals(0.1, config.band().step());
        assertEquals(PluginConfig.Scope.WORLD, config.broadcastScope());
    }

    @Test
    void invalidValuesFallBackAndAreReported() {
        List<String> warnings = new ArrayList<>();
        PluginConfig config = PluginConfig.from(TestConfigs.withDefaults("/config-overrides.yml"), warnings::add);

        assertEquals(Material.CLOCK, config.receiverMaterial(), "unknown material falls back");
        assertEquals(88.0, config.band().min(), "an inverted band falls back to the bundled band");
        assertEquals(Frequency.of(88.0), config.defaultFrequency(), "an out-of-band default falls back to band min");
        assertEquals(PluginConfig.Scope.WORLD, config.broadcastScope(), "unknown scope falls back to world");
        assertEquals(4, warnings.size(), warnings.toString());
        assertTrue(warnings.get(0).contains("receiver.material"));
        assertTrue(warnings.get(1).contains("frequency band"));
        assertTrue(warnings.get(2).contains("default-frequency"));
        assertTrue(warnings.get(3).contains("broadcast.scope"));
    }

    @Test
    void operatorValuesWinOverDefaults() {
        PluginConfig config = PluginConfig.from(TestConfigs.withDefaults("/config-overrides.yml"), s -> { });

        assertFalse(config.receiverCraftable());
        assertEquals(0.1, config.band().step(), "a key missing from the operator file comes from the defaults");
    }

    @Test
    void serverScopeIsCaseInsensitive() {
        var file = TestConfigs.bundled();
        file.set("broadcast.scope", "Server");
        List<String> warnings = new ArrayList<>();

        assertEquals(PluginConfig.Scope.SERVER, PluginConfig.from(file, warnings::add).broadcastScope());
        assertTrue(warnings.isEmpty());
    }

    @Test
    void newerFileVersionIsReportedNotRejected() {
        List<String> warnings = new ArrayList<>();
        var file = TestConfigs.bundled();
        file.set("config-version", PluginConfig.CURRENT_VERSION + 1);

        PluginConfig config = PluginConfig.from(file, warnings::add);

        assertEquals(PluginConfig.CURRENT_VERSION + 1, config.configVersion());
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("version"));
    }
}
