# Radios

[![Build](https://github.com/Dans-Plugins/Radios/actions/workflows/build.yml/badge.svg)](https://github.com/Dans-Plugins/Radios/actions/workflows/build.yml)

## Description

Receive-only broadcast radio: messages sent on a frequency are heard by every player holding a switched-on radio receiver tuned to it.

The plugin is in early development. There is no release yet.

## Requirements

- Spigot or Paper 1.21 or newer
- Java 21 (the plugin is written in Kotlin; the Kotlin standard library is bundled in the jar)

## Building

Linux / macOS:

    ./gradlew build

Windows:

    .\gradlew.bat build

The plugin jar is written to `build/libs/`. Copy it into the `plugins` folder of your server and restart.

## Testing

    ./gradlew clean test

`BUILD SUCCESSFUL` means the tests passed. The same command runs in CI on every pull request.

## Support

You can find the support Discord server [here](https://discord.gg/xXtuAQ2).

## Contributing

- [CONTRIBUTING.md](CONTRIBUTING.md)

## Authors and Contributors

- Daniel Stephenson (DanTheTechMan)

## License

Radios is released under the [GNU General Public License v3.0](LICENSE).
