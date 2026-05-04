package settingdust.data_dumper.forge.test

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraftforge.gametest.GameTestHolder
import settingdust.data_dumper.test.DataDumperTests

@GameTestHolder("data_dumper")
class DataDumperGameTest : DataDumperTests {

    @GameTest(template = "empty")
    override fun testRegistriesCommand(helper: GameTestHelper) = super.testRegistriesCommand(helper)

    @GameTest(template = "empty")
    override fun testRegistryCommand(helper: GameTestHelper) = super.testRegistryCommand(helper)

    @GameTest(template = "empty")
    override fun testRegistryTagCommand(helper: GameTestHelper) = super.testRegistryTagCommand(helper)

    @GameTest(template = "empty")
    override fun testRegistryRegexCommand(helper: GameTestHelper) = super.testRegistryRegexCommand(helper)

    @GameTest(template = "empty")
    override fun testEntryCommand(helper: GameTestHelper) = super.testEntryCommand(helper)

    @GameTest(template = "empty")
    override fun testEntryTagCommand(helper: GameTestHelper) = super.testEntryTagCommand(helper)

    @GameTest(template = "empty")
    override fun testEntryRegexCommand(helper: GameTestHelper) = super.testEntryRegexCommand(helper)
}
