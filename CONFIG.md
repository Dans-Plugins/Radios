# Configuration

`plugins/Radios/config.yml` is created on first start. Missing keys are added on every start; keys are never removed. This documents the first release's keys as scoped in [MVP.md](MVP.md).

| Key | Default | Meaning |
|---|---|---|
| `config-version` | `1` | Layout version of the file. Do not edit |
| `receiver.material` | `CLOCK` | Material of the receiver item. Tagged, so ordinary items of this material are not receivers |
| `receiver.default-frequency` | `100.0` | Frequency a new receiver is tuned to |
| `receiver.craftable` | `true` | Whether players can craft receivers. When `false`, only `/radios give` hands them out |
| `frequency.min` | `88.0` | Lowest frequency a receiver can be tuned to |
| `frequency.max` | `108.0` | Highest frequency a receiver can be tuned to |
| `frequency.step` | `0.1` | Granularity of tuning |
| `broadcast.scope` | `world` | `world`: heard only in the world the broadcast was sent from (console reaches all). `server`: heard everywhere |
| `messages.*` | see file | Every user-facing message. Colour codes use `&`. Placeholders are listed next to each message |

Reload with `/radios reload`.
