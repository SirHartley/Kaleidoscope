# Release

How Kaleidoscope is built, packaged and published. Nothing here runs unless a release is asked for: no version bumps, rebuilt jars, tags or published releases for ordinary work. Changelog entry rules are in [CHANGELOG.md](CHANGELOG.md); code owners are in [ARCHITECTURE.md](ARCHITECTURE.md).

## Pieces

| Piece | Path | Role |
|---|---|---|
| Changelog | `Changelog` | Source of the version and the release notes. The newest version heading is the release |
| Mod metadata | `mod_info.json` | Version stamped into the ZIP's copy; loads `jars/kaleidoscope.jar` |
| Version file | `kaleidoscope.version` | Online Version Checker file on `master` (forum thread `31738`), registered in `data/config/version/version_files.csv`; stamped at packaging, committed back after a release |
| Release builder | `jars/src/kaleidoscope/tools/ReleaseBuild.java` | `build` and `package`, run with `java <file>` |
| Builder checks | `jars/src/kaleidoscope/tools/ReleaseBuildChecks.java` | Headless checks of changelog parsing, stamping and file filtering |
| Build libraries | `lib/` | Game jars from `0.98a-RC8` and `LazyLib.jar` (LazyLib 3.0.0), copied from Industrial.Evolution's `lib/` |
| Release jar | `jars/release/kaleidoscope.jar` | Built by `build`, gitignored, runtime classes only; shipped as `jars/kaleidoscope.jar` |
| Release workflow | `.github/workflows/release.yml` | Manual only: preview without a version, publish with one |

The IDE artifact (`jars/kaleidoscope.jar`, gitignored) is separate from the release jar, so an IDE build never replaces it.

The release jar is built in CI from `lib/` and never committed, as in Tidy.Panels. Do not add the house template's committed jar and source hash check.

## Versions

Versions use `major.minor.letter`: the first number for a major release, the second for changes players will notice, the letter for fixes and small patches. Reset the lower parts when a higher one changes. The newest `Changelog` section is marked `- UNRELEASED` until it ships.

## Build the release jar

From the repository root, with JDK 17:

```text
java jars/src/kaleidoscope/tools/ReleaseBuild.java build
```

- Compiles against the jars in `lib/`: `starfarer.api.jar`, `starfarer_obf.jar`, `fs.common_obf.jar`, `json.jar`, `log4j-1.2.9.jar` and `LazyLib.jar`. No game install is needed. `starfarer_obf.jar` and `fs.common_obf.jar` are needed because `ImageDataEntry` casts to the game's `PlanetSpec`; LunaLib is a runtime dependency only and has no jar here.
- Builds into a fresh `out/release-build-*`, keeps only runtime classes (the `tools` package never ships), and replaces the release jar only after a clean compile. The jar must contain `kaleidoscope/plugins/ModPlugin.class`.
- After a game or LazyLib update, copy the new jars into `lib/` and update the versions in the table above.

## Package

```text
java jars/src/kaleidoscope/tools/ReleaseBuild.java package          # preview
java jars/src/kaleidoscope/tools/ReleaseBuild.java package 1.0.f    # release
```

- Needs a release jar from `build`.
- Preview packages the newest changelog section.
- Release requires the version (`1.0.f`, no `v`) to equal the newest changelog heading, and that section must not be `UNRELEASED`.
- Ships Git-tracked files only: `mod_info.json`, `kaleidoscope.version`, `Changelog`, license and credit files if added, `data/` (`csv json shader ship wpn proj system variant skin`), `graphics/` (`png jpg jpeg`, including `planets/archive/`), `sounds/` (`ogg wav`) and the release jar as `jars/kaleidoscope.jar`. Source, `lib/`, `jars/source.url`, docs, IDE, agent and workflow files, and any path under a dot, `unused`, `unedited`, `tools` or `docs` directory are left out.
- Fails if `planet_texture_data.csv`, `descriptions.csv` or the version file is missing, or a `graphics/kaleidoscope/` path named in `data/` is not packaged.
- Output in `out/release/`: `Kaleidoscope-<version>.zip` (one `Kaleidoscope/` folder, sorted, fixed timestamps), `release-notes.md`, the stamped version file and `version.txt`.

Releases up to `1.0.e` were packed by hand as `Kaleidoscope<version>.zip` with a `Kaleidoscope<version>/` folder. From the first release with this builder the folder is `Kaleidoscope/` and the download is `Kaleidoscope-<version>.zip`; the stamped `directDownloadURL` follows it.

## Publish

1. Merge `dev` into `master`. The workflow must exist on `master`, the default branch, before it can be started.
2. Finish the newest `Changelog` section and remove `UNRELEASED`.
3. Run `build` and `package <version>` locally as the preview check.
4. Start the Release workflow on `master` with `version` set: Actions tab, Release, Run workflow, or `gh workflow run release.yml --ref master -f version=<version>`.
5. The workflow runs the builder checks, builds the jar, packages, creates the release and its tag on the packaged commit, then commits the stamped `kaleidoscope.version` to `master`.
6. Check the release ZIP and notes, and that `master`'s version file names the new version. Merge `master` back into `dev`.

The workflow has no push, pull request, tag or schedule trigger. Without a version it only uploads a preview artifact. It refuses to overwrite an existing release, and run on an existing version tag it publishes that tag. It compiles from `lib/` and downloads nothing.

## Maintenance

Update this guide when `ReleaseBuild`, `ReleaseBuildChecks`, `.github/workflows/release.yml`, the version file, the branch setup or the jars in `lib/` change.
