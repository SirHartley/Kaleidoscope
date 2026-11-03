# Changelog guide

How agents write entries in the root `Changelog`. Read it for every implementation task. Version numbers, release steps and how the release tooling reads the file are in [RELEASE.md](RELEASE.md).

## When to write an entry

- Every implementation task adds or amends its entries in the current unreleased section before merge, in the same commit as the change.
- Read-only work and documentation-only changes add nothing. Say why in the PR when a change needs no entry.
- Check whether the feature has been released: look for it under every version heading without `UNRELEASED`.
  - Not released yet: edit its `Added -` entry so it describes the feature as it now stands. No `Fixed -`, `Adjusted -` or `Removed -` line for it.
  - Released: the change gets its own entry.
- Record only completed changes from the actual diff. Never list plans or unfinished work.

## File layout

`Changelog` at the repository root, plain text, no extension. The fixed `Known issues` and `Notes` sections come first (`None` when empty), then the `Changelog` section with versions newest first and a blank line between them:

```text
1.0.f - UNRELEASED
Added - Lava Variant: Default Variant 2

1.0.e
Fixed - An issue where gas giants would become invisible when updating the mod
```

- Version heading: the version alone on its line, optionally followed by ` - UNRELEASED` (or ` - CURRENT PUBLIC VERSION`, which the builder also accepts; this changelog does not use it yet). No `v` prefix, no date, no Markdown. The release tooling reads these headings, so keep them in this form.
- Entries: one change per line, starting `Added - `, `Adjusted - `, `Fixed - ` or `Removed - `.
- A `Note - ` line directly below an entry explains a limit or something the player must do.
- Never write or change a save compatibility line unasked.
- Optional category lines when a version has many entries: in-game feature names, `General` first; a hotfix has none. Variant entries name the planet type and the variant (`Added - Terran Variant: Glowing Terran`).
- Use a `Development` category for useful changes to the build or authoring tools. Do not present a refactor or a documentation edit as a gameplay change.
- If no unreleased section exists, add one above the newest version. Ask the user which version comes next if it has not been chosen; do not invent a new version per commit.

## Wording

Entries are player-facing and follow the house-style skill's "Changelog entries" section, which wins over this guide on wording. In short:

- One change per line, `Added`, `Adjusted`, `Fixed`, `Removed` in that order inside a category. Stay under 80 characters; go longer only for a new variant that needs its details.
- Capital letter after the dash, no full stop on a one-clause line, no semicolons, no hyphen as a dash inside the text. In-game names keep their capitals (`Glowing Terran`, `Fungal-Eccentric`).
- What the player sees, never the call chain or class names.
- Added: name, colon, what it is (`Added - Terran Variant: Glowing Terran`).
- Adjusted: numbers as old `->` new with the unit, otherwise a direction word (`Adjusted - Gas giant textures are no longer blurry`).
- Fixed, in the user's forms: `Fixed - No longer removes resources from metallic planets on load`, `Fixed - An issue where gas giants would become invisible when updating the mod`, `Fixed - Crash when ...`, or a short noun phrase.
- Removed: the thing, then the reason in parentheses (`Removed - Water Variant: Reef World (pending rework)`).
- Credits name real people only, never an agent. No agent or model names, links, validation logs or commit hashes, and no hedges about whether a fix works; untested status goes in the PR.
- Keep the user's own lines verbatim. Never tidy, shorten or merge them, and never imitate their jokes or asides.

## Amending versus adding

- Combine entries when several edits fix the same player-visible problem.
- An unreleased feature removed again before release loses its `Added -` entry; it gets no `Removed -` line.
- Never repeat a change once per file, step or PR.

## Maintenance

Update this guide when the `Changelog` layout or entry format changes. Release mechanics belong in [RELEASE.md](RELEASE.md).
