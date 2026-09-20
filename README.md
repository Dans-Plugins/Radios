# Radios

[![Build](https://github.com/Dans-Plugins/Radios/actions/workflows/build.yml/badge.svg)](https://github.com/Dans-Plugins/Radios/actions/workflows/build.yml)

## Description

Radios is a Minecraft plugin that adds receive-only radio. Operators, the console, and other plugins broadcast text messages on a frequency; every player holding a radio receiver tuned to that frequency hears them. A receiver works while it is in the player's main or off hand, or — once the player has right-clicked it to switch it on — from anywhere in their inventory.

Receivers never transmit. Radios does not listen to chat, does not limit chat, and does not change who hears an ordinary message; it is a one-way channel from the server to whoever is tuned in. That makes it a fit for servers with local chat that want a way to reach players without a global chat: server announcements, roleplay stations, event countdowns, or a plugin's alerts.

The plugin is at the planning stage. [MVP.md](MVP.md) describes the first release and [ARCHITECTURE.md](ARCHITECTURE.md) describes how it is built. This README documents the **experimental** channel until a stable release exists.

## Installation

### First Time Installation

There is no stable release yet. The experimental build is published from `main` on every change:

1. With [Dan's Plugin Manager](https://github.com/Dans-Plugins/Dans-Plugin-Manager): `/dpm get radios --experimental`, or download the `dev` pre-release from the [releases page](https://github.com/Dans-Plugins/Radios/releases).
2. Place the jar in the `plugins` folder of your server.
3. Restart your server.

Radios requires Spigot or Paper 1.21 or newer and Java 21. It has no plugin dependencies.

### For Plugin Developers

Radios registers a `RadiosApi` service so other plugins can broadcast on a frequency without a command. See [ARCHITECTURE.md](ARCHITECTURE.md#public-api).

## Usage

### Documentation

- [MVP](MVP.md) – What the first release does and does not do
- [Architecture](ARCHITECTURE.md) – How the plugin is built
- [Commands Reference](COMMANDS.md) – Complete list of all commands (written with the first release)
- [Configuration Guide](CONFIG.md) – Detailed configuration options (written with the first release)

### Quick Tour

- Craft or receive a **radio receiver**. Hold it and run `/radios tune 101.5` to pick a frequency.
- Hold it in either hand to listen, or **right-click** it to switch it on and put it away; it keeps listening from your inventory.
- Operators broadcast with `/radios broadcast 101.5 The market opens at dawn.` — everyone tuned to 101.5 hears it.
- `/radios info` tells you what the receiver in your hand is tuned to and whether it is on.

## Support

You can find the support Discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?

Please fill out a bug report [here](https://github.com/Dans-Plugins/Radios/issues/new).

- [Known Bugs](https://github.com/Dans-Plugins/Radios/issues?q=is%3Aissue+is%3Aopen+label%3Abug)

## Contributing

- [CONTRIBUTING.md](CONTRIBUTING.md)

## Testing

### Unit Tests

Linux:

    ./gradlew clean test

Windows:

    .\gradlew.bat clean test

If you see `BUILD SUCCESSFUL`, the tests have passed. The build needs JDK 21.

## Development

### Building

    ./gradlew build

The plugin jar is written to `build/libs/`.

### Test Server

A Docker-based test server with plugin hot-reloading, following the [DPC convention](https://github.com/Dans-Plugins/dpc-conventions/blob/main/docs/TESTING_AND_CI.md), is tracked as a follow-up. Until it lands, drop the jar into any Spigot 1.21+ server's `plugins` folder.

## Authors and Contributors

- Daniel Stephenson (DanTheTechMan)

## License

Radios is released under the [GNU General Public License v3.0](LICENSE).
