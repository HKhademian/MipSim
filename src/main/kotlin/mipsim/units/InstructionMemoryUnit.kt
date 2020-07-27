package mipsim.units

import mipsim.sim.parseBinToInstruction
import sim.base.*
import sim.tool.DebugWriter
import sim.tool.testOn

class InstructionMemoryUnit(clock: Value, wordCount: Int = 64) : Eval, DebugWriter {
	@JvmField
	val _memory = RealMemory(wordCount, false, clock)

	@JvmField
	val pc: List<MutableValue> = bus(32)

	@JvmField
	val instruction: List<Value> = _memory.readData1

	init {
		val addr = pc.slice(2) // it's a 2^N word memory, first two bits are dumped because it's word aligned
		_memory.readSelect1.set(addr)
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

/**
 * test in progress by: hossain
 */
internal fun main() {
	val clock = mut(false)
	val size = 64;
	val instMem = InstructionMemoryUnit(clock, size);

	testOn(instMem, "beforeInit")

	testOn(instMem, "init") {
		instMem._memory.bulkWrite((0 until size).map { 2 + it * 3 })
	}

	testOn(instMem, "changePCNoEval") {
		instMem.pc.set(5)
	}

	testOn(instMem, "changePCNoEval") {
		instMem.pc.set(5)
		instMem._memory.forceEval()
	}
}
