package settingdust.data_dumper.test

import net.minecraft.gametest.framework.GameTestHelper

/**
 * Shared GameTest logic for all platforms.
 * Each platform's test entry point implements this interface to inherit all test methods.
 *
 * Tests are at command level — verifying that each registered command executes successfully
 * with real arguments, rather than testing internal SPI/Mixin details.
 */
interface DataDumperTests {
    companion object : DataDumperTests

    // region 1. datadumper registries

    /** Test /datadumper registries — list all registries */
    fun testRegistriesCommand(helper: GameTestHelper) {
        val server = helper.level.server
        val source = server.createCommandSourceStack()
        val dispatcher = server.commands.dispatcher
        val result = dispatcher.execute("datadumper registries", source)
        helper.assertTrue(result > 0, "registries command should return positive count")
        helper.succeed()
    }

    // endregion

    // region 2. datadumper registry <key>

    /** Test /datadumper registry minecraft:block — query a specific registry */
    fun testRegistryCommand(helper: GameTestHelper) {
        val server = helper.level.server
        val source = server.createCommandSourceStack()
        val dispatcher = server.commands.dispatcher
        val result = dispatcher.execute("datadumper registry minecraft:block", source)
        helper.assertTrue(result > 0, "registry block command should return positive count")
        helper.succeed()
    }

    // endregion

    // region 3. datadumper registry <key> tag

    /** Test /datadumper registry minecraft:block #minecraft:logs — tag filter on registry */
    fun testRegistryTagCommand(helper: GameTestHelper) {
        val server = helper.level.server
        val source = server.createCommandSourceStack()
        val dispatcher = server.commands.dispatcher
        val result = dispatcher.execute("datadumper registry minecraft:block #minecraft:logs", source)
        helper.assertTrue(result > 0, "registry tag command should return positive count")
        helper.succeed()
    }

    // endregion

    // region 4. datadumper registry <key> regex

    /** Test /datadumper registry minecraft:block "stone.*" — regex filter on registry */
    fun testRegistryRegexCommand(helper: GameTestHelper) {
        val server = helper.level.server
        val source = server.createCommandSourceStack()
        val dispatcher = server.commands.dispatcher
        val result = dispatcher.execute("datadumper registry minecraft:block \"stone.*\"", source)
        helper.assertTrue(result > 0, "registry regex command should return positive count")
        helper.succeed()
    }

    // endregion

    // region 5. datadumper entry <key>

    /** Test /datadumper entry minecraft:worldgen/biome — dump entries from a registry */
    fun testEntryCommand(helper: GameTestHelper) {
        val server = helper.level.server
        val source = server.createCommandSourceStack()
        val dispatcher = server.commands.dispatcher
        val result = dispatcher.execute("datadumper entry minecraft:worldgen/biome", source)
        helper.assertTrue(result > 0, "entry command should return positive count")
        helper.succeed()
    }

    // endregion

    // region 6. datadumper entry <key> tag

    /** Test /datadumper entry minecraft:block #minecraft:logs — tag filter on entry dump */
    fun testEntryTagCommand(helper: GameTestHelper) {
        val server = helper.level.server
        val source = server.createCommandSourceStack()
        val dispatcher = server.commands.dispatcher
        val result = dispatcher.execute("datadumper entry minecraft:block #minecraft:logs", source)
        helper.assertTrue(result > 0, "entry tag command should return positive count")
        helper.succeed()
    }

    // endregion

    // region 7. datadumper entry <key> regex

    /** Test /datadumper entry minecraft:block "stone.*" — regex filter on entry dump */
    fun testEntryRegexCommand(helper: GameTestHelper) {
        val server = helper.level.server
        val source = server.createCommandSourceStack()
        val dispatcher = server.commands.dispatcher
        val result = dispatcher.execute("datadumper entry minecraft:block \"stone.*\"", source)
        helper.assertTrue(result > 0, "entry regex command should return positive count")
        helper.succeed()
    }

    // endregion
}
