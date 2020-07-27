package mipsim.units

import sim.base.*
import sim.complex.dec
import sim.complex.flipflopRE
import sim.complex.mux
import sim.tool.println
import sim.tool.test
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
		val isSelected = decoder[i] // pick target wire, which tells if the memory block is this memory block or not
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
		val isSelected = nor(index xor select) // pick target wire, which tells if the memory block is this memory block or not
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

fun writeOnMemoryBlock(enabled: MutableValue, writeData: List<MutableValue>, readData: List<Value>, value: Int) {
	enabled.reset()
	readData.read()
	writeData.set(value)
	enabled.set()
	readData.read()
	enabled.reset()
	readData.read()
}

fun writeBulkOnMemoryBlock(clock: Value, write: MutableValue, writeData: List<MutableValue>, select: List<Value>, blocks: List<List<Value>>, values: List<Int>) {
	val localClock = mut(false, "LocalClock")
	val localWrite = const(true, "LocalWrite")
	val localWriteData = bus(writeData.size, "LocalWriteData")
	val localSelect = bus(select.size, "LocalSelect")

	// store source values
	val _clock = (clock as MutableSingleInputElement).let { val source = it.input; it.input = localClock; source }
	val _write = (write as MutableSingleInputElement).let { val source = it.input; it.input = localWrite; source }
	val _writeData = writeData.mapIndexed { i, it ->
		(it as MutableSingleInputElement)
		val source = it.input
		it.input = localWriteData[i]
		source
	}
	val _select = select.mapIndexed { i, it ->
		(it as MutableSingleInputElement)
		val source = it.input
		it.input = localSelect[i]
		source
	}

	// do bulk write
	blocks.zip(values).forEachIndexed { i, (block, value) ->
		localSelect.set(i)
		writeOnMemoryBlock(localClock, localWriteData, block, value)
	}

	// restore source values
	(clock as MutableSingleInputElement).input = _clock
	(write as MutableSingleInputElement).input = _write
	writeData.mapIndexed { i, it -> (it as MutableSingleInputElement).input = _writeData[i] }
	select.mapIndexed { i, it -> (it as MutableSingleInputElement).input = _select[i] }
}

internal fun main() {
	test("*** test1 ***") { test1() }
	test("*** testLockedSelect ***") { testLockedSelect() }
	test("*** testLockedSelectSame ***") { testLockedSelectSame() }
	test("*** testBulkWrite ***") { testBulkWrite() }
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

private fun testBulkWrite() {
	val clock = mut(false, "Clock")
	val write = mut(false, "Write")
	val writeData = bus(8, "WriteData")
	val select = bus(3, "Select")
	val (blocks, readData) = memoryWithDecoder(clock, write, select, writeData)

	writeBulkOnMemoryBlock(clock, write, writeData, select, blocks, (1..8).toList())

	select.set(1)
	assertEquals(readData.toInt(), 2)

	select.set(3)
	assertEquals(readData.toInt(), 4)
}
