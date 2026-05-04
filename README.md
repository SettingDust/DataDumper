<h1>DataDumper</h1>

<p>
  A Minecraft mod that helps datapack developers quickly browse and export registry data.
</p>

<p>
  <a href="https://modrinth.com/mod/data-dumper"><img alt="Modrinth Downloads" src="https://img.shields.io/modrinth/dt/data-dumper?style=for-the-badge&logo=modrinth&link=https%3A%2F%2Fmodrinth.com%2Fmod%2Fdata-dumper" />
</a>
  <a href="https://www.curseforge.com/minecraft/mc-mods/data-dumper"><img alt="CurseForge Downloads" src="https://img.shields.io/curseforge/dt/1118762?style=for-the-badge&logo=curseforge&color=%23F16436" />
</a>
  <a href="https://github.com/SettingDust/DataDumper"><img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/SettingDust/DataDumper?style=for-the-badge&logo=github">
</a>
</p>

---

## About

Ever found yourself constantly needing to check what's inside a registry while developing datapacks? Or wanting to filter for specific blocks and items?

**DataDumper** lets you browse registries directly in-game via commands, with support for tag and regex filtering. You can also export registry entries as JSON files for easy debugging.

On top of that, DataDumper **prevents game crashes caused by invalid datapacks**, so you can debug with peace of mind.

## Features

- List all registries in the game
- Filter registry contents by tag or regex
- Export registry entries as JSON files
- Export datapack entries that failed to parse, making it easy to track down errors
- Prevent crashes when creating a world with invalid datapacks
- Click chat messages to copy all entries at once

## Commands

| Command                                | Description                           |
|----------------------------------------|---------------------------------------|
| `/datadumper registries`               | List all registries                   |
| `/datadumper registry <key>`           | View all entries in a registry        |
| `/datadumper registry <key> #<tag>`    | Filter registry entries by tag        |
| `/datadumper registry <key> "<regex>"` | Filter registry entries by regex      |
| `/datadumper entry <key>`              | Export registry entries as JSON files |
| `/datadumper entry <key> #<tag>`       | Filter and export by tag              |
| `/datadumper entry <key> "<regex>"`    | Filter and export by regex            |

Exported files are saved in the `.datadumper/` folder under the server's working directory.

## Screenshots

**Regex filtering** — Find all blocks containing "planks":

![/datadumper registry minecraft:block ".*?planks.*?"](https://raw.githubusercontent.com/SettingDust/DataDumper/main/docs/screenshot-regex-filter.png)

**Tag filtering** — Find all blocks tagged `#c:chests`:

![/datadumper registry minecraft:block #c:chests](https://raw.githubusercontent.com/SettingDust/DataDumper/main/docs/screenshot-tag-filter.png)

## Supported Versions

| Minecraft Version | Fabric | Forge | NeoForge |
|:-----------------:|:------:|:-----:|:--------:|
|      1.20.1       |   ✅    |   ✅   |    —     |
|      1.21.1       |   ✅    |   —   |    ✅     |
|       26.1        |   ✅    |   —   |    ✅     |

## Recommended Companions

- [Mod Sets](https://modrinth.com/mod/mod-sets) — Easily disable development mods in published modpacks
