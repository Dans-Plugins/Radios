# Radios — Architecture

How the plugin described in [MVP.md](MVP.md) is built. This is the reference for anyone adding to it, human or agent; it says where things go and which rules are not negotiable.

## Stack

| | |
|---|---|
| Language | Java 21 (no Kotlin) |
| Build | Gradle 8.14, Kotlin DSL (`build.gradle.kts`); `./gradlew clean build` produces `build/libs/Radios-<version>.jar` |
| API | `org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT`, `compileOnly`; `api-version: 1.21` |
| Tests | JUnit 5 + Mockito, under `src/test/java` |
| Dependencies at runtime | none; nothing is shaded |
| CI | `.github/workflows/build.yml` (JDK 21) on push/PR to `main`; `release.yml` attaches the jar to a published release; `dev-release.yml` republishes the rolling `dev` pre-release |
| Verification | [release-gates](https://github.com/Dans-Plugins/release-gates) boot gate on Spigot 26.2; the save-compat gate does not apply (no server-side state) |

The version lives only in `build.gradle.kts`; `plugin.yml` reads it through resource expansion.

## Package layout

```
dansplugins.radios
├── Radios                plugin entry point: wires everything below, nothing else
├── api/                  RadiosApi (interface, registered with the ServicesManager), RadioBroadcastEvent
├── config/               typed access to config.yml (PluginConfig) and message formatting (Messages)
├── items/                ItemTags (namespaced keys), ReceiverItem (create, read state, write state, refresh lore), RecipeRegistrar
├── frequency/            Frequency (value type, canonical form), Band (min/max/step validation)
├── broadcast/            Broadcast (record), Audience (pure: given inventories, who hears F), Broadcaster (fires the event, resolves audience, delivers)
├── listeners/            ReceiverInteractListener (right-click toggle)
└── commands/             RadiosCommand and one class per subcommand
```

Rules:

- `frequency.*` and `broadcast.Audience` have **no Bukkit imports** beyond plain values so they are unit-testable without a server. `Audience` works on a small `ReceiverView` record (`frequency`, `on`, `held`) that `ReceiverItem` produces from an inventory.
- The entry point only constructs and registers. No logic in `Radios.java`.
- Every user-facing string comes from `config.yml` → `Messages`. No hard-coded chat text.
- Radios registers **no chat listener** of any kind.

## Items

A receiver is identified and described entirely by its `PersistentDataContainer`:

| Key | Type | Meaning |
|---|---|---|
| `radios:receiver` | `BYTE` = 1 | this item is a receiver |
| `radios:frequency` | `STRING` | canonical frequency, e.g. `101.5` |
| `radios:on` | `BYTE` 0/1 | switched on (listens from anywhere in the inventory) |
| `radios:item-version` | `INTEGER` | layout version of the tag set, for future migrations |

`ReceiverItem` is the only class that reads or writes these keys. Every write is followed by `refreshLore`, which regenerates the display name and lore from the tag and the current config; nothing ever parses lore. A receiver from an older `item-version` is upgraded in place the first time it is read.

The frequency is stored as its canonical string, not a double, so an item written by one JVM is read identically by another and so a future band change cannot silently retune existing receivers.

## Frequencies

`frequency.Frequency` (exists in the scaffold, with tests) normalises to one decimal place and is the only thing compared when deciding who hears a broadcast. `Band` validates against `frequency.min/max/step` on tune and on broadcast; the step is enforced on tune only (a broadcast on 101.55 is allowed and simply reaches nobody, which is a configuration mistake the log points out).

## Broadcast pipeline

```
RadiosApi.broadcast(frequency, message, world?)     ← command, console, other plugins
  └─ Broadcaster
       1. RadioBroadcastEvent(frequency, message, world, source) — fired sync; cancellable; message mutable
       2. audience = for each online player p (in world, or all worlds per scope):
              views = ReceiverItem.viewsOf(p.getInventory())  // main/off hand marked held
              Audience.hears(views, frequency)                 // pure
       3. for each p in audience: p.sendMessage(Messages.broadcast(frequency, message))
```

- **`Audience.hears`** is `views.stream().anyMatch(v -> v.frequency().equals(f) && (v.held() || v.on()))`. A player is in the audience at most once however many receivers they carry.
- **Scope**: `world` delivers to players in the broadcast's world; a console broadcast has no world and reaches every world. `server` ignores worlds.
- **Threading**: broadcasts are delivered on the main thread. `RadiosApi.broadcast` called from another thread schedules onto the main thread; the API documents this.
- **Cost**: one inventory scan per online player per broadcast. Broadcasts are rare and human-paced; no caching until a real server shows the need.

### Right-click toggle

`ReceiverInteractListener` handles `PlayerInteractEvent` for `RIGHT_CLICK_AIR` and `RIGHT_CLICK_BLOCK` when the item in the event's hand is a receiver. It flips `radios:on`, refreshes lore, plays `UI_BUTTON_CLICK` at the player, sends `receiver-on`/`receiver-off`, and cancels the event. Only the hand that triggered the event is considered (`event.getHand()`), so main- and off-hand receivers are toggled independently and the event is not handled twice.

## Public API

Registered on enable with `Bukkit.getServicesManager().register(RadiosApi.class, impl, plugin, ServicePriority.Normal)`.

```java
public interface RadiosApi {
    /** Broadcasts to every listener on the frequency, in every world (subject to broadcast.scope). */
    void broadcast(String frequency, String message);
    /** Broadcasts to listeners in one world. */
    void broadcast(World world, String frequency, String message);
    /** Whether a frequency is inside the configured band. */
    boolean isValidFrequency(String frequency);
}
```

`RadioBroadcastEvent extends Event implements Cancellable` carries `frequency`, `message` (mutable), `world` (nullable), and `source` (`CommandSender` or `null` for a plugin). Consumers that want to log, filter, or rewrite broadcasts listen here. The package `dansplugins.radios.api` is the whole public surface; everything else may change between minor releases.

A consumer depends on Radios with `softdepend: [Radios]` and looks the service up at enable time.

## Commands

`RadiosCommand` implements `TabExecutor`, parses the first argument, and dispatches to one small class per subcommand under `commands/`. Each subcommand declares its permission and checks it before doing anything. `tune` and `info` need a receiver in hand and answer `not-holding-receiver` otherwise. Tab completion covers subcommand names, player names, and — for `tune` and `broadcast` — a few band values as hints.

## Configuration

`config/PluginConfig` reads `config.yml` once per (re)load into a typed object; nothing else calls `getConfig()`. On load, missing keys are added from the bundled default (`saveDefaultConfig`, then `options().copyDefaults(true)` + `saveConfig()`), and a `config-version` older than the plugin's is migrated key by key with a log line per change. Keys are never removed.

The material is validated on load; an invalid material logs a warning and falls back to the bundled default rather than disabling the plugin. A band where `min > max` or `step <= 0` is rejected the same way.

## Persistence

None on the server. All receiver state is in item tags, which Minecraft persists with the player's inventory. There is no data folder content beyond `config.yml`, so the save-compatibility gate is skipped and the release notes say so.

## Testing

| Area | How |
|---|---|
| `Frequency`, `Band` | JUnit, no mocks (`FrequencyTest` exists) |
| `Audience` | JUnit over hand-built `ReceiverView` lists: held vs on vs off, duplicates, wrong frequency |
| `Broadcaster` | JUnit with Mockito: mocked players/inventories, asserts who gets `sendMessage` and that a cancelled event delivers nothing |
| `ReceiverItem` | JUnit with a mocked `ItemMeta`/`PersistentDataContainer`, asserting tag reads and writes and that lore is derived |
| Commands | Mockito `CommandSender`/`Player` |
| Right-click, recipes, real delivery | The mineflayer scenario in MVP.md's acceptance criteria and the release-gates boot gate |

## Usage reporting

Like every Dans-Plugins plugin, Radios will vendor the one-file [trace](https://github.com/Stephenson-Software/trace) Java client and report startups, with the loud opt-out (`plugins/Radios/config.yml` → `usage-reporting: false`, the `TRACE_USAGE_REPORTING` env var, or `DO_NOT_TRACK`) the org standardised on. The write key is minted per program on the trace box and is never committed. This is wired as a follow-up issue, not in the scaffold.

## Decisions log

| Decision | Why |
|---|---|
| Receive-only | It is the whole idea: a broadcast medium, not another chat channel. Two-way radio would need range, interference, and channel-etiquette design that a first release does not need |
| State on the item, not the server | Nothing to migrate or corrupt, works across servers sharing player data, and a receiver behaves the same when traded or dropped |
| Canonical string frequencies | Double equality is a bug waiting to happen; a string with one decimal is what players type and what they see |
| `held` counts even when off | Holding a radio to your ear is the intuitive "I'm listening"; the on switch exists for hands-free use |
| Service + event API from day one | The best broadcasters are other plugins (Herald, factions announcements); an API added later tends to leak internals |
| World scope by default | Matches how the rest of the org's plugins treat worlds and keeps a creative-world event from spamming survival |
