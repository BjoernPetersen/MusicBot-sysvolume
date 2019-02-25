package net.bjoernpetersen.musicbot.sysvolume

import mu.KotlinLogging
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

internal class GetVolume(
    command: List<String>,
    private val pattern: String,
    private val valueMode: ValueMode
) {

    private val logger = KotlinLogging.logger { }

    private val process = ProcessBuilder(command).start()

    private val lock: Lock = ReentrantLock()
    private lateinit var output: String

    init {
        thread(name = "GetVolume-error", isDaemon = true) {
            process.errorStream.bufferedReader().forEachLine {}
        }
        val started = lock.newCondition()
        lock.withLock {
            thread(name = "GetVolume-out") {
                lock.withLock {
                    started.signal()
                    output = process.inputStream.bufferedReader().readText()
                }
            }
            started.await()
        }
    }

    private fun parse(output: String): Int {
        val value = Regex(pattern)
            .find(output)
            ?.groupValues
            ?.getOrNull(1)
            ?: throw IllegalStateException()

        logger.debug { "GetVolume match: $value" }

        return valueMode.toPercent(value)
    }

    fun get(): Int {
        lock.withLock {
            return try {
                parse(output)
            } catch (e: NumberFormatException) {
                throw IllegalStateException(e)
            }
        }
    }
}

private fun buildProcess(command: List<String>, valueMode: ValueMode, volume: Int): Process {
    val parsedCommand = command
        .map { it.replace("<volume>", valueMode.fromPercent(volume)) }
    return ProcessBuilder(parsedCommand).start()
}

internal class SetVolume(command: List<String>, valueMode: ValueMode, volume: Int) {

    private val process = buildProcess(command, valueMode, volume)

    private val lock: Lock = ReentrantLock()

    init {
        thread(name = "SetVolume-error", isDaemon = true) {
            process.errorStream.bufferedReader().forEachLine {}
        }
        val started = lock.newCondition()
        lock.withLock {
            thread(name = "SetVolume-out") {
                lock.withLock {
                    started.signal()
                    process.inputStream.bufferedReader().forEachLine {}
                }
            }
            started.await()
        }
    }

    fun await(): Boolean {
        lock.withLock {
            return process.exitValue() == 0
        }
    }
}

