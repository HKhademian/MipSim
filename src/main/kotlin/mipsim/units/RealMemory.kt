package mipsim.units

import sim.base.*
import sim.base.Value.Companion.ZERO
import sim.complex.dec
import sim.complex.flipflopRE
import sim.complex.mux
import sim.tool.DebugWriter
import sim.tool.keepSource
import sim.tool.println
import sim.tool.test
import java.lang.Integer.min
import kotlin.math.log2
import kotlin.test.assertEquals

fun memoryBlock(writeEnabled: Value, writeData: List<Value>): List<Value> =
	flipflopRE(writeEnabled, writeData)

/**
 * this module creates a M*N bit memory
 * M is number of memory blocks, M is 2 ^ size of selector bus
 * N is size of each memory block, N is size of input(data) bus
 *
 * it's important to mention that select bus is async and does not depends
 * on the clock, it always works
 *
 * @param clock memory clock wire
 * @param write whether or not to write input on the selected block
 * @param select selected module to read/write
 * @param input data to write on the selected memory block
 * @return current value of selected memory block
 */
fun memoryWithDecoder(clock: Value, write: Value, select: List<Value>, input: List<Value>): Pair<List<List<Value>>, List<Value>> {
	val decoder = dec(Value.ONE, select) // creates a m bit decoder

	val blocks = (0 until (1 shl select.size)).map { i ->
		val isSelected = if (i == 0) ZERO else decoder[i] // pick target wire, which tells if the memory block is this memory block or not
		val isEnabled = and(isSelected, clock, write) // is write to block enabled or not
		memoryBlock(isEnabled, input) // creates memory block
	}.toList()

	return blocks to mux(select, blocks) // choose selected memory block
}

/**
 * @see memoryWithDecoder
 */
fun memoryWithCmp(clock: Value, write: Value, select: List<Value>, input: List<Value>): Pair<List<List<Value>>, List<Value>> {
	val blocks = (0 until (1 shl select.size)).map { i ->
		val index = i.toBus(select.size)
		val isSelected = if (i == 0) ZERO else nor(index xor select) // pick target wire, which tells if the memory block is this memory block or not
		val isEnabled = and(isSelected, clock, write) // is write to block enabled or not
		memoryBlock(isEnabled, input) // creates memory block
	}.toList()

	return blocks to mux(select, blocks) // choose selected memory block
}

/**
 * @see memoryWithDecoder
 * this module is like normal memory, except it's selector bus is now another memory module and it caches selector
 * you can pass
 */
fun lockedMemory(selectorClock: Value, selectorWrite: List<Value>, clock: Value, write: Value, writeData: List<Value>): Triple<List<Value>, List<List<Value>>, List<Value>> {
	val selectorRead = flipflopRE(selectorClock, selectorWrite)
	val (blocks, readData) = memoryWithDecoder(clock, write, selectorRead, writeData)
	return Triple(selectorRead, blocks, readData)
}

/**
 * write a value to memory block
 */
fun writeOnMemoryBlock(enabled: MutableValue, writeData: List<MutableValue>, readData: List<Value>, value: Int) {
	enabled.reset()
	readData.read()
	writeData.set(value)
	enabled.set()
	readData.read()
	enabled.reset()
	readData.read()
}

/**
 * fill memory with all values
 * attention: because mem#0 is always 0, so this function starts to write from word 1
 */
fun writeBulkOnMemory(clock: Value, write: MutableValue, writeData: List<MutableValue>, select: List<Value>, readData: List<Value>, values: List<Int>) {
	clock.keepSource(mut(false, "LocalClock")) { _, _, localClock ->
		write.keepSource(mut(false, "LocalWrite")) { _, _, localWrite ->
			writeData.keepSource(bus(writeData.size, "LocalWriteData")) { _, _, localWriteData ->
				select.keepSource(bus(select.size, "LocalSelect")) { _, _, localSelect ->

					localWrite.set()

					// do bulk write
					values.forEachIndexed { i, value ->
						localSelect.set(i + 1)
						writeOnMemoryBlock(localClock, localWriteData, readData, value)
					}

				}
			}
		}
	}
}

/**
 * read all values in memory block
 */
fun readBulkOnMemory(
	clock: Value,
	write: MutableValue,
	select: List<Value>,
	readData: List<Value>,
	max: Int
): List<Int> {
	return clock.keepSource(mut(false, "LocalClock")) { _, _, localClock ->
		write.keepSource(mut(false, "LocalWrite")) { _, _, localWrite ->
			select.keepSource(bus(select.size, "LocalSelect")) { _, _, localSelect ->

				localWrite.reset()

				val count = min(max, 1 shl select.size)
				// do bulk write
				(0 until count).map { i ->
					localSelect.set(i)
					localClock.reset()
					readData.toInt()
					localClock.set()
					readData.toInt()
				}

			}
		}
	}
}

/**
 * a word-aligned memory module with 2 read and 1 separate write inputs
 * it uses flipflips to create memory grid
 * every thing in this module is gate-level
 * (programmin logics uses to wire things together but not to calculate/evaluate values)
 */
class RealMemory(private val wordCount: Int, private val additionalReader: Boolean = false, inputClock: Value = ZERO) : Eval, DebugWriter {
	private val selectorSize: Int = log2(wordCount.toDouble()).toInt()
	private val clock: MutableValue = mut(false, "Clock").also { it.set(inputClock) }
	val write: MutableValue = mut(false, "Write")
	val writeData: List<MutableValue> = bus(32, "WriteData")
	val writeSelect: List<MutableValue> = bus(selectorSize, "WriteSelect")
	val readSelect1: List<MutableValue> = bus(selectorSize, "ReadSelect1")
	val readSelect2: List<MutableValue> = if (!additionalReader) emptyList() else bus(selectorSize, "ReadSelect2")
	private val blocks: List<List<Value>>
	private val readData: List<Value>
	val readData1: List<Value>
	val readData2: List<Value>

	init {
		val mem = memoryWithDecoder(clock, write, writeSelect, writeData)
		blocks = mem.first
		readData = mem.second
		readData1 = mux(readSelect1, blocks) // separate reads and writes selectors
		readData2 = if (!additionalReader) emptyList() else mux(readSelect2, blocks)
	}

	/** read all values in memory flipflops before simulate */
	fun bulkRead(count: Int): List<Int> =
		readBulkOnMemory(clock, write, writeSelect, readData, count)

	/** write given values to memory flipflops before simulate */
	fun bulkWrite(values: List<Int>) =
		writeBulkOnMemory(clock, write, writeData, writeSelect, readData, values)

	/** fill memory with zeros */
	@JvmOverloads
	fun clear(max: Int = -1) {
		val count = if (max in 1 until wordCount) max else wordCount
		bulkWrite((0 until count).map { 0 })
	}

	override fun eval(time: Long) {
		readSelect1.read()
		readData1.read()
		if (additionalReader) {
			readSelect2.read()
			readData2.read()
		}
	}

	fun forceEval() {
		clock.keepSource(mut(false, "LocalClock")) { _, _, localClock ->
			localClock.reset()
			eval(System.nanoTime())
			localClock.set()
			eval(System.nanoTime())
			localClock.reset()
			eval(System.nanoTime())
		}
	}


	override fun writeDebug(buffer: StringBuffer) {
		val wordCount = this.wordCount
		val count = min(16, wordCount)
		val values = bulkRead(count)
		values.forEachIndexed { i, it ->
			buffer
				.append("WORD#")
				.append(i)
				.append("=")
				.append(Integer.toHexString(it))
				.append("\t\t")
		}
	}
}

internal fun main() {
	test("*** test1 ***") { test1() }
	test("*** testLockedSelect ***") { testLockedSelect() }
	test("*** testLockedSelectSame ***") { testLockedSelectSame() }
	test("*** testBulkReadWrite ***") { testBulkReadWrite() }
}

private fun test1() {
	val clock = mut(false)
	val write = mut(false)
	val input = bus(32)
	val select = bus(8)

	val (_, output) = memoryWithDecoder(clock, write, select, input)

	clock.reset()
	write.set()
	input.set(74)
	select.set(1)

	output.println()
	assertEquals(output.toInt(), 0)

	clock.set()
	output.println()
	assertEquals(output.toInt(), 74)

	input.set(12)
	output.println()
	assertEquals(output.toInt(), 74)

	clock.reset()
	output.println()
	assertEquals(output.toInt(), 74)

	clock.set()
	output.println()
	assertEquals(output.toInt(), 12)

	clock.reset()
	output.println()
	assertEquals(output.toInt(), 12)

	select.set(2)
	output.println()
	assertEquals(output.toInt(), 0)


	input.set(19)
	clock.set()
	output.println()
	assertEquals(output.toInt(), 19)
}

private fun testLockedSelect() {
	val selectorClock = mut(false)
	val selectorWrite = bus(5)
	val clock = mut(false)
	val write = mut(false)
	val writeData = bus(32)
	val (selectorRead, _, readData) = lockedMemory(selectorClock, selectorWrite, clock, write, writeData)

	clock.reset()
	write.set()
	writeData.set(74)
	writeOnMemoryBlock(selectorClock, selectorWrite, selectorRead, 1)
	readData.println()
	assertEquals(readData.toInt(), 0)

	clock.set()
	readData.println()
	assertEquals(readData.toInt(), 74)

	writeData.set(12)
	readData.println()
	assertEquals(readData.toInt(), 74)

	clock.reset()
	readData.println()
	assertEquals(readData.toInt(), 74)

	clock.set()
	readData.println()
	assertEquals(readData.toInt(), 12)

	clock.reset()
	readData.println()
	assertEquals(readData.toInt(), 12)

	selectorWrite.set(10)
	readData.println()
	assertEquals(readData.toInt(), 12) // select bus is not async!

	writeOnMemoryBlock(selectorClock, selectorWrite, selectorRead, 2)
	readData.println()
	assertEquals(readData.toInt(), 0)

	writeData.set(19)
	clock.set()
	readData.println()
	assertEquals(readData.toInt(), 19)
}

private fun testLockedSelectSame() {
	val selectorWrite = bus(5)
	val clock = mut(false)
	val write = mut(false)
	val writeData = bus(32)
	val (selectorRead, _, readData) = lockedMemory(clock, selectorWrite, clock, write, writeData)

	clock.reset()
	write.set()
	writeData.set(74)
	writeOnMemoryBlock(clock, selectorWrite, selectorRead, 1)

	readData.println()
	assertEquals(readData.toInt(), 0)

	clock.set()
	readData.println()
	assertEquals(readData.toInt(), 74)

	writeData.set(12)
	readData.println()
	assertEquals(readData.toInt(), 74)

	clock.reset()
	readData.println()
	assertEquals(readData.toInt(), 74)

	clock.set()
	readData.println()
	assertEquals(readData.toInt(), 12)

	clock.reset()
	readData.println()
	assertEquals(readData.toInt(), 12)

	selectorWrite.set(10)
	readData.println()
	assertEquals(readData.toInt(), 12) // select bus is not async!

	writeOnMemoryBlock(clock, selectorWrite, selectorRead, 2)
	readData.println()
	assertEquals(readData.toInt(), 0)

	writeData.set(19)
	clock.set()
	readData.println()
	assertEquals(readData.toInt(), 19)
}

private fun testBulkReadWrite() {
	val clock = mut(false, "Clock")
	val write = mut(false, "Write")
	val writeData = bus(8, "WriteData")
	val select = bus(3, "Select")
	val (_, readData) = memoryWithDecoder(clock, write, select, writeData)

	writeBulkOnMemory(clock, write, writeData, select, readData, (1..8).toList())

	select.set(1)
	readData.println()
	assertEquals(readData.toInt(), 2)

	select.set(3)
	readData.println()
	assertEquals(readData.toInt(), 4)

	readBulkOnMemory(clock, write, select, readData, 10).println()
}
