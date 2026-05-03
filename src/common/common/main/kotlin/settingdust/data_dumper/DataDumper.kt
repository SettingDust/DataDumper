package settingdust.data_dumper

import org.apache.logging.log4j.LogManager
import settingdust.data_dumper.util.CommonIdentifier

object DataDumper {
    const val ID = "data_dumper"

    val LOGGER = LogManager.getLogger()

    fun id(path: String): CommonIdentifier = CommonIdentifier.of(ID, path)
}
