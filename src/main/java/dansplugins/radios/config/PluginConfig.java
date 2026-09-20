package dansplugins.radios.config;

import dansplugins.radios.frequency.Band;
import dansplugins.radios.frequency.Frequency;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Consumer;

/**
 * Typed, immutable view of {@code config.yml}. Built once per (re)load; nothing else reads the raw
 * configuration.
 *
 * <p>Values are read with the one-argument getters so that a key missing from the operator's file
 * falls through to the bundled defaults (the two-argument getters ignore jar defaults). Invalid
 * values are reported through {@code warn} and replaced by the bundled default rather than
 * disabling the plugin.
 */
public final class PluginConfig {

    /** Layout version this build understands; {@code config-version} in the file. */
    public static final int CURRENT_VERSION = 1;

    static final Material DEFAULT_RECEIVER_MATERIAL = Material.CLOCK;
    static final Band DEFAULT_BAND = new Band(88.0, 108.0, 0.1);

    /** Broadcast scopes understood by this build. */
    public enum Scope { WORLD, SERVER }

    private final int configVersion;
    private final Material receiverMaterial;
    private final Frequency defaultFrequency;
    private final boolean receiverCraftable;
    private final Band band;
    private final Scope broadcastScope;

    private PluginConfig(int configVersion, Material receiverMaterial, Frequency defaultFrequency,
                         boolean receiverCraftable, Band band, Scope broadcastScope) {
        this.configVersion = configVersion;
        this.receiverMaterial = receiverMaterial;
        this.defaultFrequency = defaultFrequency;
        this.receiverCraftable = receiverCraftable;
        this.band = band;
        this.broadcastScope = broadcastScope;
    }

    /**
     * Reads a configuration. {@code warn} receives one line per value that was invalid and replaced.
     */
    public static PluginConfig from(ConfigurationSection config, Consumer<String> warn) {
        int version = config.getInt("config-version");
        if (version > CURRENT_VERSION) {
            warn.accept("config.yml is version " + version + " but this build understands version "
                    + CURRENT_VERSION + "; keys it does not know are ignored.");
        }
        Material receiver = material(config, "receiver.material", DEFAULT_RECEIVER_MATERIAL, warn);
        Band band = band(config, warn);
        Frequency defaultFrequency = Frequency.parse(config.getString("receiver.default-frequency"));
        if (defaultFrequency == null || !band.contains(defaultFrequency)) {
            Frequency fallback = Frequency.of(band.min());
            warn.accept("receiver.default-frequency '" + config.getString("receiver.default-frequency")
                    + "' is not inside the band; using " + fallback + ".");
            defaultFrequency = fallback;
        }
        String scopeName = config.getString("broadcast.scope");
        Scope scope;
        try {
            scope = Scope.valueOf(String.valueOf(scopeName).toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException e) {
            warn.accept("broadcast.scope '" + scopeName + "' is not 'world' or 'server'; using 'world'.");
            scope = Scope.WORLD;
        }
        return new PluginConfig(version, receiver, defaultFrequency, config.getBoolean("receiver.craftable"),
                band, scope);
    }

    private static Band band(ConfigurationSection config, Consumer<String> warn) {
        try {
            return new Band(config.getDouble("frequency.min"), config.getDouble("frequency.max"),
                    config.getDouble("frequency.step"));
        } catch (IllegalArgumentException e) {
            warn.accept("frequency band is invalid (" + e.getMessage() + "); using "
                    + DEFAULT_BAND.min() + "-" + DEFAULT_BAND.max() + " step " + DEFAULT_BAND.step() + ".");
            return DEFAULT_BAND;
        }
    }

    private static Material material(ConfigurationSection config, String path, Material fallback,
                                     Consumer<String> warn) {
        String name = config.getString(path);
        Material material = name == null ? null : Material.matchMaterial(name);
        // Material#isItem/#isAir consult a running server's registry, so the check is by identity:
        // the three air constants cannot be items, and legacy names are not accepted.
        if (material == null || material == Material.AIR || material == Material.CAVE_AIR
                || material == Material.VOID_AIR || material.isLegacy()) {
            warn.accept(path + " '" + name + "' is not a usable item material; using " + fallback.name() + ".");
            return fallback;
        }
        return material;
    }

    public int configVersion() {
        return configVersion;
    }

    public Material receiverMaterial() {
        return receiverMaterial;
    }

    public Frequency defaultFrequency() {
        return defaultFrequency;
    }

    public boolean receiverCraftable() {
        return receiverCraftable;
    }

    public Band band() {
        return band;
    }

    public Scope broadcastScope() {
        return broadcastScope;
    }
}
