package mipsim.units

import junit.framework.Assert.assertEquals
import sim.base.*
import sim.complex.dec
import sim.complex.flipflopRE
import sim.complex.mux
import sim.tool.println

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
fun memory(clock: Value, write: Value, select: List<Value>, input: List<Value>): List<Value> {
	val decoder = dec(Value.ONE, select) // creates a m bit decoder

	val memories = (0 until (1 shl select.size)).map { index ->
		val isSelected = decoder[index] // pick target wire, which tells if the memory block is this memory block or not
		val isEnabled = and(isSelected, clock, write) // is write to block enabled or not
		val memory = flipflopRE(isEnabled, input) // creates memory block
		memory
	}.toList()

	val output = mux(select, memories) // choose selected memory block
	return output
}

internal fun main() {
	test1()
}


internal fun test1() {

	val clock = mut(false)
	val write = mut(false)
	val input = bus(32)
	val select = bus(8)

	val mem = memory(clock, write, select, input)

	clock.reset()
	write.set()
	input.set(74)
	select.set(1)

	mem.println()
	assertEquals(mem.toInt(), 0)

	clock.set()
	mem.println()
	assertEquals(mem.toInt(), 74)

	input.set(12)
	mem.println()
	assertEquals(mem.toInt(), 74)

	clock.reset()
	mem.println()
	assertEquals(mem.toInt(), 74)

	clock.set()
	mem.println()
	assertEquals(mem.toInt(), 12)

	clock.reset()
	mem.println()
	assertEquals(mem.toInt(), 12)

	select.set(2)
	mem.println()
	assertEquals(mem.toInt(), 0)


	input.set(19)
	clock.set()
	mem.println()
	assertEquals(mem.toInt(), 19)
}

