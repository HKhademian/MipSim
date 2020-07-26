package mipsim.units

import mipsim.sim.parseBinToInstruction
import sim.base.*
import sim.base.Value.Companion.ZERO
import sim.tool.DebugWriter
import kotlin.math.log2

class InstructionMemoryUnit(val clock: Value, val wordCount: Int = 1024) : Eval, DebugWriter {
	val selectorSize = log2(wordCount.toDouble()).toInt()

	@JvmField
	val pc: List<MutableValue> = bus(32)

	@JvmField
	val instruction: List<Value>

	private val selector: List<Value>
	private val _memoryBlocks: List<List<Value>>

	init {
		selector = pc.slice(2, 2 + selectorSize) // it's a 2^N word memory, first two bits are dumped because it's word aligned
		val mem = memoryWithDecoder(clock, ZERO, selector, ZERO_BUS.slice(0, 32))
		instruction = mem.second
		_memoryBlocks = mem.first
	}

	override fun eval(time: Long) {
		super.eval(time)
		selector.read()
		instruction.read()
	}

	override fun writeDebug(buffer: StringBuffer) {
		val pc = pc.toInt()
		val instructionBin = instruction.toInt()
		val instructionStr = parseBinToInstruction(instructionBin)
		buffer
			.append("InstructionMemory:\t")
			.append(String.format("PC=%04xH\t", pc))
			.append(String.format("INST=%08xH=' %s '\t", instructionBin, instructionStr))
	}
}
