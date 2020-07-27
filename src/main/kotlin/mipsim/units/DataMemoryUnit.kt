package mipsim.units

import sim.base.*
import sim.tool.DebugWriter

class DataMemoryUnit(clock: Value, wordCount: Int = 1024) : Eval, DebugWriter {
	@JvmField
	val _memory = RealMemory(wordCount, false, clock)

	@JvmField
	val memWrite: MutableValue = _memory.write

	@JvmField
	val memRead: MutableValue = mut(false)

	@JvmField
	val address: List<MutableValue> = bus(32)

	@JvmField
	val writeData: List<MutableValue> = _memory.writeData

	@JvmField
	val readData: List<Value> = _memory.readData1

	init {
		val addr = address.slice(2) // it's a 2^N word memory, first two bits are dumped because it's word aligned
		_memory.readSelect1.set(addr)
		_memory.writeSelect.set(addr)
	}

	override fun eval(time: Long) {
		_memory.eval(time)
	}

	override fun writeDebug(buffer: StringBuffer) {
		_memory.writeDebug(buffer)
	}
}
