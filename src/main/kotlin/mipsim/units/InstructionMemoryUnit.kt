package mipsim.units

import mipsim.sim.parseBinToInstruction
import sim.base.*
import sim.tool.DebugWriter

class InstructionMemoryUnit(clock: Value, wordCount: Int = 1024) : Eval, DebugWriter {
	@JvmField
	val _memory = RealMemory(wordCount)

	@JvmField
	val pc: List<MutableValue> = bus(32)

	@JvmField
	val instruction: List<Value>

	init {
		instruction = _memory.readData1
		_memory.clock.set(clock)
		_memory.select1.set(pc.slice(2)) // it's a 2^N word memory, first two bits are dumped because it's word aligned
	}

	override fun eval(time: Long) {
		_memory.eval(time)
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
