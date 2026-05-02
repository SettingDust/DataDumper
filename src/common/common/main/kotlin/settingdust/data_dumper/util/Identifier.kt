package settingdust.data_dumper.util

data class Identifier(
    val namespace: String,
    val path: String,
) {
    override fun toString(): String = "$namespace:$path"

    companion object {
        fun parse(value: String): Identifier {
            val separator = value.indexOf(':')
            return if (separator == -1) {
                Identifier("minecraft", value)
            } else {
                Identifier(value.substring(0, separator), value.substring(separator + 1))
            }
        }
    }
}
