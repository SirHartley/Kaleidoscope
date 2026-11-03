# Architecture

Technical routing for the current implementation, checked against game `0.98a-RC8`. Code paths below are relative to `jars/src/kaleidoscope/`; data paths are repository-relative. Search the named owner before adding another implementation.

| Reference | Scope |
|---|---|
| [CLAUDE.md](../CLAUDE.md) | Workflow, build gate and document upkeep |
| [RELEASE.md](RELEASE.md) | Version numbers, release builder, package contents and publishing |
| [CHANGELOG.md](CHANGELOG.md) | Change-note format and upkeep for every implementation task |

## Start here

| Change / symptom | Route |
|---|---|
| A variant never shows up | `planet_texture_data.csv` `planet_type` -> `ImageDataEntry.matches` -> `PlanetTextureApplicator.run` location filter; [matching](#matching) |
| Planet invisible or black after an update | Texture not preloaded: `ModPlugin.onApplicationLoad` -> `Importer.loadImageData`; [save data](#save-data) |
| Crash at startup naming a texture | `ModPlugin.onApplicationLoad` rethrows the `loadTexture` `IOException`; a CSV path points to a missing file |
| Wrong name or description on a planet | `planet_texture_data.csv` `type_name` / `desc` -> `ImageDataEntry.applyToPlanet` -> `data/strings/descriptions.csv` |
| Forced resource condition missing or doubled | `force_condition` -> `ImageDataEntry.applyToPlanet` -> `PlanetTextureApplicator.addResourceCondition` |
| Glow lit on the wrong side | `reverse_glow` -> `PlanetSpecAPI.setUseReverseLightForGlow` |
| Variants not applied to an existing save | `$KaleidoscopeSetupComplete` -> `ModPlugin.onGameLoad` |

## Registration and lifecycle

| Registry / hook | Owners |
|---|---|
| `mod_info.json` | `modPlugin` `kaleidoscope.plugins.ModPlugin`; jar `jars/kaleidoscope.jar`; dependencies LazyLib, LunaLib |
| `ModPlugin.onApplicationLoad()` | Loads every texture, glow and cloud path in the table with `SettingsAPI.loadTexture`, `DO_NOT_ASSIGN` rows included |
| `ModPlugin.onNewGameAfterEconomyLoad()` | New game: `PlanetTextureApplicator.run` |
| `ModPlugin.onGameLoad(boolean)` | Runs `PlanetTextureApplicator.run` when `$KaleidoscopeSetupComplete` is not set (mod added to an existing save) |
| `data/config/planet_texture_data.csv` | The variant table, read by `Importer.loadImageData`; [texture table](#texture-table) |
| `data/strings/descriptions.csv` | `PLANET` descriptions, one per variant, ids `kaleidoscope_*` |
| `data/config/LunaSettingsConfig.json` | LunaLib settings icon only; the mod has no settings |
| `data/config/version/version_files.csv` | Version Checker entry for `kaleidoscope.version` |

Order on a new game: `onApplicationLoad` (preload) -> `onNewGameAfterEconomyLoad` (assign, set key) -> `onGameLoad(true)` (key set, nothing to do). The assignment runs once per save; planets in systems created later are never changed.

### Build and authoring tools

`tools/ReleaseBuild.java` (`build`, `package`) and `tools/ReleaseBuildChecks.java` compile against `lib/` and never ship in the release jar; they, `lib/` and `.github/workflows/release.yml` are owned by [RELEASE.md](RELEASE.md). `package` also checks that every `graphics/kaleidoscope/` path named in `data/` is packaged.

## Texture table

`data/config/planet_texture_data.csv`, read with `SettingsAPI.getMergedSpreadsheetDataForMod("id", ...)`, so other mods can add or replace rows by `id`.

| Column | Use |
|---|---|
| `id` | Integer, unique, stable. Shown in the dev mode name |
| `image_name` | Planet texture path |
| `glow_name` | Optional glow texture path |
| `cloud_name` | Optional cloud texture path. Raises the spec's `cloudAlpha` to 0.4 when below 0.3 and sets a white `cloudColor` when none is set |
| `planet_type` | Vanilla planet type id the variant applies to, or `DO_NOT_ASSIGN` for a retired row that is only preloaded |
| `reverse_glow` | `TRUE` or empty |
| `force_condition` | Space separated condition ids, see [resource conditions](#resource-conditions) |
| `type_name` | Planet type name shown in game |
| `desc` | Description id in `data/strings/descriptions.csv` |

Whitespace is stripped from every column but `type_name`. Textures live in `graphics/kaleidoscope/planets/`; retired ones in `graphics/kaleidoscope/planets/archive/`.

## Data identity

| Stable value | Where it lives |
|---|---|
| `$KaleidoscopeSetupComplete` (`Ids.KEY_SETUP_COMPLETE`) | Sector memory, set after the one assignment pass |
| Texture, glow and cloud paths | Each changed planet's saved spec |
| `type_name` | Each changed planet's saved spec name; renaming a row does not rename planets in existing saves |
| `desc` ids (`kaleidoscope_*`) | Each changed planet's saved spec |

## Source owners

Use `rg --files jars/src/kaleidoscope` for the complete inventory.

| File | Owner / connection |
|---|---|
| `plugins/ModPlugin.java` | Lifecycle hooks above, the `log` helper (dev mode only), and the dev showcase `spawnSuperSystem` |
| `plugins/PlanetTextureApplicator.java` | The assignment pass: location filter, one pick per planet, the setup key, and `addResourceCondition` |
| `loading/Importer.java` | Reads the table into `ImageDataEntry` objects; logs and returns what it has on an `IOException` or `JSONException` |
| `loading/ImageDataEntry.java` | One row: `matches` (planet type test) and `applyToPlanet` (spec changes, forced conditions, random pitch, `applySpecChanges`) |
| `ids/Ids.java` | Memory keys, the table path and the mod id |

## Contracts

### Assignment

- `PlanetTextureApplicator.run` skips a location that has no planets, has an economy market (`Misc.getMarketsInLocation`), is hyperspace, or is tagged `Tags.SYSTEM_CUT_OFF_FROM_HYPER`, `Tags.THEME_HIDDEN`, `Tags.THEME_SPECIAL` or `Tags.SYSTEM_ABYSSAL`. Inhabited systems and story locations keep vanilla looks.
- Each planet picks from its matching rows plus `null`, all weight 1, so a planet with n matching variants keeps its vanilla texture with chance 1 / (n + 1).
- `LocationAPI.getPlanets()` includes stars. No row targets a star type.
- `applyToPlanet` writes the name, `cloudAlpha` and the texture fields through the game's `com.fs.starfarer.loading.specs.PlanetSpec` (a `DoNotObfuscate` class with readable names), because `PlanetSpecAPI` has no setter for the name or `cloudAlpha`. This is why the build needs `starfarer_obf.jar` and `fs.common_obf.jar`.
- `StarSystemAPI.addPlanet` gives every non-star planet a condition market (`Misc.initConditionMarket` in `BaseLocation.addPlanet`), so `planet.getMarket()` is set for every planet a row can match.

### Matching

`ImageDataEntry.matches` compares `PlanetAPI.getTypeId()` with `planet_type` after cutting any trailing digits, so vanilla's numbered types (`barren2`, `barren3`, `frozen1` to `frozen3`, `desert1` in `data/config/planets.json`) match the rows for `barren`, `frozen` and `desert`. Procgen picks the numbered types as often as the plain ones (`data/campaign/procgen/planet_gen_data.csv`).

### Resource conditions

`addResourceCondition` handles a condition id starting with `ore`, `rare`, `organics`, `volatiles` or `farmland`: it removes every condition of that group from the planet's market, then adds the one given. A condition with any other prefix is skipped without a log line, so `inimical_biosphere` in `force_condition` is never added. A condition the market already has is not touched.

### Save data

- `CampaignPlanet` keeps its spec transient and saves a `PlanetSpecDiff` against the base spec of its type (`writeReplace` / `readResolve`), so a changed planet's texture, glow and cloud paths, name, description id, cloud values and pitch are in the save.
- The textures are not registered in `settings.json`, so the game only has them because `onApplicationLoad` preloads every row. A texture a save names but nothing preloads draws as an invisible planet (the gas giant bug fixed in 1.0.e). To retire a variant, set its `planet_type` to `DO_NOT_ASSIGN` and keep its row and file at the path released versions used.

## Dead or dormant

| Component | State |
|---|---|
| `Ids.KEY_1_0_B_FIX` | Unused key from 1.0.b |
| `Importer.isNumber` | Unused |
| `ModPlugin.spawnSuperSystem`, `ImageDataEntry.tally` | Dev only: run when dev mode is on and the constant `ModPlugin.dev` is `true` (it is `false`). Moves one planet per variant into Duzahk |
