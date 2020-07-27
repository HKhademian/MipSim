package mipsim.units

import mipsim.Processor
import sim.base.*
import sim.tool.DebugWriter
import sim.tool.test
import sim.tool.testOn

class RegisterFileUnit(clock: Value) : Eval, DebugWriter {
	@JvmField
	val _memory = RealMemory(32, true, clock)

	@JvmField
	val readReg1: List<MutableValue> = _memory.readSelect1

	@JvmField
	val readReg2: List<MutableValue> = _memory.readSelect2

	@JvmField
	val writeReg: List<MutableValue> = _memory.writeSelect

	@JvmField
	val writeData: List<MutableValue> = _memory.writeData

	@JvmField
	val regWrite: MutableValue = _memory.write

	@JvmField
	val readData1: List<Value> = _memory.readData1

	@JvmField
	val readData2: List<Value> = _memory.readData2

	override fun eval(time: Long) {
		_memory.eval(time)
	}

	override fun writeDebug(buffer: StringBuffer) {
		val values = _memory.bulkRead(32)
		for (i in 0..31) {
			val value: Int = values[i]
			buffer.append(String.format("$%d=%04xH ", i, value))
		}
		buffer.append("\n")
		buffer.append("ReadReg1: ").append(readReg1.toInt()).append("\t")
		buffer.append("ReadReg2: ").append(readReg2.toInt()).append("\t")
		buffer.append("WriteReg: ").append(writeReg.toInt()).append("\n")
		buffer.append("RegWrite: ").append(regWrite).append("\t")
		buffer.append("ReadData1: ").append(readData1.toInt()).append("\t")
		buffer.append("ReadData2: ").append(readData2.toInt()).append("\t")
		buffer.append("WriteData: ").append(writeData.toInt())
	}

}

internal fun main1() {
	val clock = mut(false, "Clock")

	test("all register with value of a number") {
		val registerFile = RegisterFileUnit(clock)
		registerFile._memory.bulkWrite((31 downTo 0).map { 1 shl it })
		registerFile
	}
	test("swap") {
		val registerFile = RegisterFileUnit(clock)
		registerFile._memory.bulkWrite((1 until 32).map { it * 3 + 1 })
		registerFile
	}
}

internal fun main() {
	val sim = Processor()

	val regFile = sim.registerFile as RegisterFileUnit

	val readReg = bus(5)
	val writeReg = bus(5)
	val writeData = bus(32)

	testOn(regFile, "init") {
		regFile._memory.bulkWrite((0 until 32).map { it * 3 + 1 })
	}

	testOn(regFile, "noEvalTest") {
		writeData.set(74)
		readReg.set(1)
		writeReg.set(1)

		regFile.readReg1.set(readReg)
		regFile.writeReg.set(writeReg)
		regFile.writeData.set(writeData)
	}

	testOn(regFile, "afterEval") {
		regFile._memory.forceEval()
	}

	testOn(regFile, "setRegWrite") {
		regFile.regWrite.set(true)
		regFile._memory.forceEval()
	}

	testOn(regFile, "noRegWrite") {
		writeData.set(12)
		regFile.regWrite.set(false)
		regFile._memory.forceEval()
	}

	testOn(regFile, "readOtherReg") {
		readReg.set(3)
		regFile._memory.forceEval()
	}

	testOn(regFile, "writeOnZero") {
		regFile.regWrite.set(true)
		regFile.writeReg.set(0)
		regFile.writeData.set(74)
		regFile._memory.forceEval()
	}

}
