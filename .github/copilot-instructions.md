# Copilot Instructions

This repository follows the DPC (Dans Plugins Community) conventions defined at
https://github.com/Dans-Plugins/dpc-conventions. Read those conventions before
making any changes.

Read `MVP.md` (what the first release does and does not do) and `ARCHITECTURE.md`
(where code goes and which rules are fixed) before changing anything. If a change
needs to break a rule in `ARCHITECTURE.md`, change the document in the same pull request.

## Technology Stack

- Language: Java 21
- Build tool: Gradle (Kotlin DSL, `build.gradle.kts`); the version is set there only
- Target platform: Spigot / Paper 1.21+ (`spigot-api` 1.21.11, `compileOnly`)
- Test framework: JUnit 5 with Mockito

## Project Structure

- `src/main/java/dansplugins/radios/` – plugin source, packaged as laid out in `ARCHITECTURE.md`
  (`api`, `config`, `items`, `frequency`, `broadcast`, `listeners`, `commands`)
- `src/main/resources/` – `plugin.yml` and `config.yml`
- `src/test/java/` – unit tests, mirroring the main tree

## Coding Conventions

- Receivers are receive-only. Nothing a player does with a receiver ever sends a message.
- Never cancel, rewrite, or change the recipients of a vanilla chat event; Radios does not listen to chat at all.
- Identify receivers by their `PersistentDataContainer` tag, never by display name or lore; the
  power state and frequency live in the tag and the lore is only ever derived from it.
- Frequencies are compared through `frequency.Frequency`'s canonical form, never as raw doubles or user text.
- Every user-facing string comes from `config.yml` through the `Messages` class; never hard-code text.
- Keep `frequency.*` and the `broadcast.Audience` resolver free of Bukkit imports beyond plain values
  so they stay unit-testable.
- The public API is `dansplugins.radios.api` only; other plugins must not need anything outside it.
- No logic in `Radios.java`; it only constructs and registers.

## Contribution Workflow

- Branch from `main` for all changes.
- Open a pull request against `main`; the `Build` workflow must pass.
- Reference the related GitHub issue in every pull request description.
