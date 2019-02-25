package net.bjoernpetersen.musicbot.sysvolume

import net.bjoernpetersen.musicbot.api.config.ChoiceBox
import net.bjoernpetersen.musicbot.api.config.Config
import net.bjoernpetersen.musicbot.api.config.NonnullConfigChecker
import net.bjoernpetersen.musicbot.api.config.TextBox
import net.bjoernpetersen.musicbot.spi.plugin.management.InitStateWriter
import net.bjoernpetersen.musicbot.spi.plugin.volume.VolumeHandler

class SysVolumeHandler : VolumeHandler {
    override val name: String = "System master volume control"
    override val description: String = "Controls the system's master volume by calling" +
        " a custom command."

    private lateinit var valueMode: Config.SerializedEntry<ValueMode>

    private lateinit var getCommand: Config.StringEntry
    private lateinit var getPattern: Config.StringEntry

    private lateinit var setCommand: Config.StringEntry

    override fun createConfigEntries(config: Config): List<Config.Entry<*>> {
        valueMode = config.SerializedEntry(
            key = "valueMode",
            description = "How the volume value is represented. Either a percentage (0-100)," +
                " a decimal (0.0-1.0), or a 16-bit number (0-65535).",
            serializer = ValueMode.Serializer,
            configChecker = NonnullConfigChecker,
            uiNode = ChoiceBox({ it.description }, { ValueMode.values().toList() }),
            default = ValueMode.Percent
        )

        getCommand = config.StringEntry(
            key = "getCommand",
            description = "A command to get the current volume",
            configChecker = NonnullConfigChecker,
            uiNode = TextBox
        )
        getPattern = config.StringEntry(
            key = "getPattern",
            description = "A regex to extract the volume from the getCommand output",
            configChecker = NonnullConfigChecker,
            uiNode = TextBox
        )

        setCommand = config.StringEntry(
            key = "setCommand",
            description = "A command to set the volume. <volume> will be replaced by the desired volume.",
            configChecker = NonnullConfigChecker,
            uiNode = TextBox
        )

        return listOf(valueMode, getCommand, getPattern, setCommand)
    }

    private fun String.toCommandList(): List<String> {
        return split(" ")
    }

    override var volume: Int
        get() = GetVolume(
            getCommand.get()!!.toCommandList(),
            getPattern.get()!!,
            valueMode.get()!!
        ).get()
        set(value) {
            SetVolume(setCommand.get()!!.toCommandList(), valueMode.get()!!, value).await()
        }

    override fun createSecretEntries(secrets: Config): List<Config.Entry<*>> = emptyList()
    override fun createStateEntries(state: Config) {}
    override fun initialize(initStateWriter: InitStateWriter) {}
    override fun close() {}
}
