# Radios — MVP

This document fixes the scope of the first release. Everything under **In scope** ships in 1.0.0; everything under **Out of scope** does not, however tempting. [ARCHITECTURE.md](ARCHITECTURE.md) says how it is built.

## Problem

A server with local chat has no way to tell everyone something at once without a global chat channel that undoes the point of local chat. Radio is the in-world answer: a one-way broadcast that only reaches players who chose to carry a receiver and tune it in.

## Principles

1. **Receivers only receive.** Nothing a player can do with a receiver sends a message. Transmitting is a privilege (a permission, the console, or a plugin), not an item.
2. **Never touch vanilla chat.** Radios does not listen to, cancel, rewrite, or re-route chat. Broadcasts are plain messages sent to the players who qualify.
3. **Items are identified by tag, never by name.** A receiver is a receiver because of a persistent data tag; its frequency and power state live in that tag. Lore is a display of the tag, never the source.
4. **No server-side state.** Everything a receiver knows travels with the item. There is nothing to migrate, nothing to lose on a crash.

## User stories

- As a player, I can craft a receiver, tune it to a frequency, and hear broadcasts on that frequency while I hold it in either hand.
- As a player, I can right-click my receiver to switch it on, put it in any inventory slot, and keep hearing broadcasts; right-clicking again switches it off.
- As a player, I can look at my receiver (`/radios info`, or its lore) and see its frequency and whether it is on.
- As an operator, I can broadcast a message on a frequency from in-game or from the console.
- As an operator, I can hand out receivers and turn crafting off so receivers are scarce.
- As a plugin developer, I can broadcast on a frequency through a service and react to broadcasts through an event.

## In scope

### Items

| Item | Default material | Tags | How obtained |
|---|---|---|---|
| Radio receiver | `CLOCK` | `radios:receiver`, `radios:frequency`, `radios:on` | Shaped recipe (configurable, can be disabled) or `/radios give [player]` |

A new receiver is tuned to `receiver.default-frequency` and switched off. Display name and lore are set from config for recognisability and are refreshed whenever the tag changes. Materials are configurable; `custom-model-data` is exposed for resource packs.

### Frequencies

- A frequency is a number between `frequency.min` and `frequency.max` (defaults 88.0–108.0), normalised to one decimal place: `101.5`, `101.50` and `101.4999` are the same station.
- `/radios tune <frequency>` retunes the receiver in the player's main hand (off-hand if the main hand is empty). Out-of-band values are refused with `invalid-frequency`.

### Listening

A player **hears** a broadcast on frequency F when at least one of these is true:

- their main hand or off hand holds a receiver tuned to F (on or off — holding it is enough), or
- any slot of their inventory holds a receiver tuned to F that is switched on.

Two receivers on the same frequency deliver the message once.

### Switching on and off

Right-clicking (air or block) with a receiver in hand toggles `radios:on`, updates the lore, plays a short click sound, and tells the player `receiver-on`/`receiver-off`. The interact event is cancelled so the click does nothing else (no block placement, no clock use).

### Broadcasting

- `/radios broadcast <frequency> <message…>` — permission `radios.broadcast` (default op). From a player, the broadcast's world is the player's world; from the console, it reaches every world.
- `broadcast.scope: world` (default) delivers only to listeners in the broadcast's world; `server` delivers to listeners in every world.
- Delivery uses `messages.broadcast-format` with `{frequency}` and `{message}`.
- A `RadioBroadcastEvent` fires before delivery and can be cancelled or have its message changed; a `RadiosApi` service lets other plugins broadcast. See [ARCHITECTURE.md](ARCHITECTURE.md#public-api).

### Commands

| Command | Permission | Description |
|---|---|---|
| `/radios help` | `radios.use` | List commands |
| `/radios info` | `radios.use` | Frequency and power state of the receiver in hand |
| `/radios tune <frequency>` | `radios.use` | Retune the receiver in hand |
| `/radios broadcast <frequency> <message…>` | `radios.broadcast` | Send a broadcast |
| `/radios give [player]` | `radios.admin` | Hand out a receiver |
| `/radios reload` | `radios.admin` | Reload `config.yml` |

Alias: `/radio`. Every command answers `help` for the boot gate.

### Configuration

The keys in [`config.yml`](src/main/resources/config.yml): receiver material, default frequency, craftability; band min/max/step; broadcast scope; every user-facing message. A `config-version` key lets later releases add keys without removing an operator's.

## Out of scope (for the MVP)

- **Transmitter blocks or stations** with a location and range, so a broadcast fades or cuts out with distance. The natural next release; the `broadcast` config section is where it will live.
- Two-way radio, walkie-talkies, or any way for a receiver to send.
- Sneak-right-click or scroll to retune without a command.
- Static, garbling, or signal-strength effects.
- Scheduled or repeating broadcasts, station playlists, or music.
- Per-frequency permissions (private frequencies).
- Limiting vanilla chat range or integrating with chat in any way.
- Integration with Cellphones. The two plugins are independent by design.

## Acceptance criteria

Written so a mineflayer bot scenario and the [release-gates](https://github.com/Dans-Plugins/release-gates) boot gate can check them.

1. **Boot gate** passes: the plugin enables on a fresh Spigot 26.2 server, answers `help` for `radios`, stops cleanly, and enables again. The save-compat gate does not apply (no persistent state); the release notes say so.
2. Bot B holds a receiver tuned to 101.5 in its main hand. Console runs `radios broadcast 101.5 hello`. B receives exactly one message matching `broadcast-format`.
3. B moves the receiver to a hotbar slot it is not holding. Console broadcasts on 101.5. B receives nothing.
4. B holds the receiver, right-clicks it, and puts it back in that slot. Console broadcasts on 101.5. B receives the message.
5. Console broadcasts on 101.6. B receives nothing.
6. B runs `/radios tune 101.6` while holding the receiver. Console broadcasts on 101.6. B receives the message; `/radios info` reports 101.6 and on.
7. B carries two receivers tuned to 101.6, both on. Console broadcasts on 101.6. B receives the message exactly once.
8. B in world A, `broadcast.scope: world`. A player in world B runs `/radios broadcast 101.6 x`. B receives nothing. Console runs the same. B receives it.
9. B relogs. The receiver is still tuned to 101.6 and on.
10. `./gradlew clean test` passes; `Frequency` and the audience resolver are unit-tested.

## Release plan

- `0.1.0` — receiver item, tune, on/off, `broadcast` command, API and event, world scope. Everything above.
- `0.2.0` — transmitter block with range; `/radios info` reports reception.
- Later — sneak-retune, static at the edge of range, private frequencies, scheduled broadcasts, each as its own issue.
