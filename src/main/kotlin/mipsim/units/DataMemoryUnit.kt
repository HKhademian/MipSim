package mipsim.units

import sim.base.*
import sim.tool.DebugWriter

class DataMemoryUnit(clock: Value, wordCount: Int = 1024) : Eval, DebugWriter {
	@JvmField
	val _memory = RealMemory(wordCount)

	@JvmField
	val memWrite: MutableValue = mut(false)

	@JvmField
	val memRead: MutableValue = mut(false)

	@JvmField
	val address: List<MutableValue> = bus(32)

	@JvmField
	val writeData: List<MutableValue> = bus(32)

	@JvmField
	val readData: List<Value>

	init {
		readData = _memory.readData1
		_memory.clock.set(clock)
		_memory.write.set(memWrite)
		_memory.select1.set(address.slice(2)) // it's a 2^N word memory, first two bits are dumped because it's word aligned
		_memory.writeData.set(writeData) // it's a 2^N word memory, first two bits are dumped because it's word aligned
	}

	override fun eval(time: Long) {
		_memory.eval(time)
	}

	override fun writeDebug(buffer: StringBuffer) {
		_memory.writeDebug(buffer)
	}
}
