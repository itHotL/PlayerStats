This was ai generated i'm forking it at 1 am to take a look and see if i can update it

# PlayerStats Architecture (Plain English)

PlayerStats is a Minecraft server plugin. Players type a command, the plugin looks up stats from existing player data, and it prints a nice chat message. No database setup.

---

## Big picture

```
Player types /stat
        ↓
Commands figure out what they want
        ↓
Background threads do the heavy lifting
        ↓
Statistic code reads player data
        ↓
Message code formats the answer
        ↓
Chat shows the result (optional share button)
```

---

## Where things live

Everything useful sits under `src/main/java/.../playerstats/`.

| Folder | What it's for |
|--------|----------------|
| `core/` | The actual plugin (startup, commands, logic) |
| `api/` | Public face for other plugins to use PlayerStats |
| `src/main/resources/` | Config files, language text, plugin.yml |

---

## Startup (`core/Main.java`)

When the server enables the plugin, `Main` does this in order:

1. Load config and helpers (language, offline players, messages, sharing)
2. Set up stat requests and the thread manager
3. Register commands (`/statistic`, `/statshare`, `/statreload`, `/statexclude`)
4. Listen for players joining
5. Start optional metrics (bStats)

On disable, anything marked “closable” gets cleaned up. Reload reloads config first, then everything that registered itself as reloadable.

---

## Folders inside `core/` — what happens where

### `commands/`
Entry point for players and console.

- **`StatCommand`** — main `/stat` command. Parses args, builds a request, or shows help/examples.
- **`ShareCommand`** — shares the last lookup in public chat.
- **`ReloadCommand`** — reloads config without restarting the server.
- **`ExcludeCommand`** — hide specific players from top/server totals.
- **`TabCompleter`** — suggestions while typing.

### `multithreading/`
Keeps the server responsive.

Stat lookups (especially top-10 / whole-server) can touch a lot of players. That work runs on background threads so chat spam can’t freeze the server. One player is limited to one lookup at a time. Reloads and lookups are coordinated so they don’t step on each other.

### `statistic/`
Turns “I want diamonds mined, top 10” into real numbers.

- Builds different request types: **one player**, **server total**, or **top list**
- **`BukkitProcessor`** reads Bukkit/Spigot player stats (from existing player files)
- **`StatRequestManager`** is the hub that runs those requests

### `msg/`
Everything that gets printed to chat.

- **`OutputManager`** — the only place messages are actually sent
- **`MessageBuilder`** + `components/` — build the colored/hoverable text (including festive themes)
- **`msgutils/`** — number formatting, language keys, fonts, small helpers

Players get fancy hover text; console gets a simpler version that still looks okay.

### `config/`
Reads and updates `config.yml` (colors, units, who to include/exclude, share limits, etc.).

### `sharing/`
Remembers a player’s last successful lookup so they can hit “share” and post it in chat (with cooldowns / permissions from config).

### `listeners/`
Currently: when someone joins, related player-list handling can update so new players show up in stats correctly.

### `utils/`
Shared helpers: offline player lists, YAML files, logging, enums, command usage counting, etc.

### `enums/`
Internal labels (colors, debug level, standard message types).

---

## The API (`api/`)

Other plugins don’t dig through `core/`. They call:

```java
PlayerStats api = PlayerStats.getAPI();
```

From there they can:

- Ask for stats (`StatManager`)
- Format text the same way PlayerStats does (`StatTextFormatter`)
- Format numbers (`StatNumberFormatter`)
- Listen for events when a stat is calculated or shared (`api/events/`)

Think of `api/` as the front door and `core/` as the kitchen.

---

## Config files (`src/main/resources/`)

| File | Role |
|------|------|
| `plugin.yml` | Plugin name, commands, permissions |
| `config.yml` | Behavior and look-and-feel |
| `language.yml` | Custom names / wording for stats |
| `excluded_players.yml` | Players hidden from top/server results |

These get copied into the server’s plugin folder when the plugin first runs.

---

## Typical request path (one sentence each)

1. Someone runs `/stat mined diamond top`.
2. **`StatCommand`** understands that as a top-list request.
3. **`ThreadManager`** starts a background job.
4. **`statistic/`** loads the relevant players and totals their diamond-mined stats.
5. **`msg/`** turns the numbers into a chat message (units, colors, hover text).
6. **`OutputManager`** sends it privately; if sharing is on, a share button can post it for everyone.

---

## Mental model

- **Commands** = “what did the player ask?”
- **Threads** = “don’t freeze the server while we figure it out”
- **Statistic** = “get the numbers from Minecraft’s own data”
- **Msg** = “make it pretty and send it”
- **Config / language** = “how should it behave and sound?”
- **API** = “other plugins can ask for the same stuff cleanly”

That’s the whole architecture in one pass.
