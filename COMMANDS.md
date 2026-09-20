# Commands

All commands are under `/radios` (alias `/radio`). This lists the commands of the first release as scoped in [MVP.md](MVP.md); it is updated as they land.

| Command | Permission | Description |
|---|---|---|
| `/radios help` | `radios.use` | List commands |
| `/radios info` | `radios.use` | Frequency and power state of the receiver in hand |
| `/radios tune <frequency>` | `radios.use` | Retune the receiver in hand |
| `/radios broadcast <frequency> <message…>` | `radios.broadcast` | Send a broadcast on a frequency. From the console it reaches every world |
| `/radios give [player]` | `radios.admin` | Hand out a receiver |
| `/radios reload` | `radios.admin` | Reload `config.yml` |

## Permissions

| Permission | Default | Grants |
|---|---|---|
| `radios.use` | everyone | Carrying, switching on, tuning a receiver; `help`, `info`, `tune` |
| `radios.broadcast` | op | `broadcast` |
| `radios.admin` | op | Everything above plus `give` and `reload` |
