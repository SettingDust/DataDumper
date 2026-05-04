package settingdust.data_dumper.fabric.v21_1.test

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import settingdust.data_dumper.test.DataDumperTests

class DataDumperGameTest : FabricGameTest, DataDumperTests {

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    override fun testRegistriesCommand(helper: GameTestHelper) = super.testRegistriesCommand(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    override fun testRegistryCommand(helper: GameTestHelper) = super.testRegistryCommand(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    override fun testRegistryTagCommand(helper: GameTestHelper) = super.testRegistryTagCommand(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    override fun testRegistryRegexCommand(helper: GameTestHelper) = super.testRegistryRegexCommand(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    override fun testEntryCommand(helper: GameTestHelper) = super.testEntryCommand(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    override fun testEntryTagCommand(helper: GameTestHelper) = super.testEntryTagCommand(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    override fun testEntryRegexCommand(helper: GameTestHelper) = super.testEntryRegexCommand(helper)
}
