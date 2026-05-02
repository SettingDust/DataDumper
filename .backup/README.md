- Dump data from registry with regex and tag to use in data pack.
- Dump JSON that `Failed to parse`. Useful for debug mod like https://modrinth.com/mod/hocon-resource-loader
- Avoid crash when create level if data pack invalid

Recommend use with https://modrinth.com/mod/mod-sets to disable the developing mods in the published pack.

![img.png](https://github.com/SettingDust/DataDumper/blob/main/img.png?raw=true)
<details>
minecraft:oak_planks<br>
minecraft:spruce_planks<br>
minecraft:birch_planks<br>
minecraft:jungle_planks<br>
minecraft:acacia_planks<br>
minecraft:cherry_planks<br>
minecraft:dark_oak_planks<br>
minecraft:mangrove_planks<br>
minecraft:bamboo_planks<br>
minecraft:crimson_planks<br>
minecraft:warped_planks
</details>

![img_1.png](https://github.com/SettingDust/DataDumper/blob/main/img_1.png?raw=true)
<details>
minecraft:chest<br>
minecraft:ender_chest<br>
minecraft:trapped_chest
</details>

## Commands

- `/datadumper registries` Dump all registries keys
- `/datadumper registry <key of the registry> [tag/regex]` Dump registry keys
- `/datadumper entry <key of the registry> [tag/regex]` Dump data json files
