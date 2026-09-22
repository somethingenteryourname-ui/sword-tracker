# TrackerSword

A Paper **1.21.11** plugin that adds a custom sword — the **Tracker Blade** — with two abilities:

| Input | Effect |
|---|---|
| **Right-click** (not sneaking) | Strength III, Resistance II, Regeneration II for **10 seconds**, then a **1:30** cooldown |
| **Shift + Right-click** | Opens a GUI of player heads — click one to start tracking them |

While tracking, the wielder sees a live action bar with a direction arrow, the target's name, and
distance in blocks, e.g. `↗ Steve  42m`. Tracking follows a player across the same world; it pauses
(and tells you) if the target logs off or changes worlds, and only shows while you're holding the
sword in your main hand.

## Get the sword

```
/trackersword            # gives it to yourself (needs trackersword.give, default: op)
/trackersword <player>   # gives it to someone else
```

## Build

### Option A — GitHub Actions (no local setup needed)

This repo includes `.github/workflows/build.yml`, which builds the jar on GitHub's own servers
(they can reach `repo.papermc.io`, which some sandboxed/offline environments can't):

1. Create a new repo on GitHub and push this project to it:
   ```bash
   cd TrackerSword
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/<you>/<repo>.git
   git push -u origin main
   ```
2. Open the **Actions** tab on the repo — a "Build TrackerSword" run starts automatically.
3. When it finishes, open the run and download **TrackerSword-jar** from the "Artifacts" section
   at the bottom of the page. That's the ready-to-use plugin jar.

Prefer a proper GitHub Release instead of an Actions artifact? Push a version tag and the same
workflow will attach the jar to a Release for you:
```bash
git tag v1.0.0
git push origin v1.0.0
```
Then grab the jar from the repo's **Releases** page.

### Option B — Build locally

Requires **JDK 21** and internet access (to pull `paper-api` from `repo.papermc.io`):

```bash
mvn clean package
```

The compiled plugin jar will be at `target/TrackerSword-1.0.0.jar`. Drop it in your server's
`plugins/` folder and restart (or `/reload`, though a restart is safer).

## Configuration (`config.yml`, generated on first run)

```yaml
buff-ability:
  duration-seconds: 10
  cooldown-seconds: 90
  strength-level: 3
  resistance-level: 2
  regeneration-level: 2

tracking-ability:
  update-interval-ticks: 10
  require-holding-sword: true

item:
  custom-model-data: 0
```

Change values and run `/trackersword reload` style behavior isn't wired to a command by default —
restart the server (or call `TrackerSwordPlugin#reloadSettings()` yourself) after editing.

## Notes / design choices

- The sword is identified via a `PersistentDataContainer` tag, not its display name, so renaming it
  in an anvil won't break the abilities — but it does mean any Netherite Sword isn't automatically
  "the" tracker sword; only ones created by `/trackersword` or `TrackerSwordItem.create()` work.
- The buff cooldown is tracked in memory per-player (not Bukkit's built-in item-cooldown swirl),
  so it resets on server restart and won't visually grey out other Netherite Swords the player is
  carrying.
- Tracking data (`who's tracking whom`) is also in-memory only; it clears on restart/disable.
- Built against `paper-api 1.21.11-R0.1-SNAPSHOT`. If your server jar is a different 1.21.x patch,
  you can usually bump the version in `pom.xml` (or even leave a slightly older one — the Bukkit API
  surface used here has been stable across 1.21.x) and it will still work at runtime.
