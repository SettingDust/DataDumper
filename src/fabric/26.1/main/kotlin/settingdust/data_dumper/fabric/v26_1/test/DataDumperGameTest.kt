package settingdust.data_dumper.fabric.v26_1.test

import java.lang.reflect.Method
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker
import net.fabricmc.fabric.api.gametest.v1.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import settingdust.data_dumper.test.DataDumperTests

class DataDumperGameTest : CustomTestMethodInvoker, DataDumperTests {
    override fun invokeTestMethod(
        helper: GameTestHelper,
        method: Method
    ) {
        method.invoke(this, helper)
    }

    @GameTest
    override fun testRegistriesCommand(helper: GameTestHelper) = super.testRegistriesCommand(helper)

    @GameTest
    override fun testRegistryCommand(helper: GameTestHelper) = super.testRegistryCommand(helper)

    @GameTest
    override fun testRegistryTagCommand(helper: GameTestHelper) = super.testRegistryTagCommand(helper)

    @GameTest
    override fun testRegistryRegexCommand(helper: GameTestHelper) = super.testRegistryRegexCommand(helper)

    @GameTest
    override fun testEntryCommand(helper: GameTestHelper) = super.testEntryCommand(helper)

    @GameTest
    override fun testEntryTagCommand(helper: GameTestHelper) = super.testEntryTagCommand(helper)

    @GameTest
    override fun testEntryRegexCommand(helper: GameTestHelper) = super.testEntryRegexCommand(helper)
}
