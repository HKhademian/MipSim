package mipsim.units

import sim.base.*
import sim.tool.DebugWriter
import java.lang.Integer.min
import kotlin.math.log2

class DataMemoryUnit(val clock: Value, val wordCount: Int = 1024) : Eval, DebugWriter {
	val selectorSize = log2(wordCount.toDouble()).toInt()

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

	private val selector: List<Value>
	private val _memoryBlocks: List<List<Value>>

	init {
		selector = address.slice(2, 2 + selectorSize) // it's a 2^N word memory, first two bits are dumped because it's word aligned
		val mem = memoryWithDecoder(clock, memWrite, selector, writeData)
		readData = mem.second
		_memoryBlocks = mem.first
	}

	override fun eval(time: Long) {
		super.eval(time)
		selector.read()
		readData.read()
	}

	override fun writeDebug(buffer: StringBuffer) {
		val wordCount = this.wordCount
		for (i in 0 until min(10, wordCount)) {
			buffer
				.append("WORD#")
				.append(i)
				.append("=")
				.append(Integer.toHexString(_memoryBlocks[i].toInt()))
				.append("\t\t")
		}
	}
}
