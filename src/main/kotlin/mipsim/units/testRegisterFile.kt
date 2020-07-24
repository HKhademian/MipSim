package mipsim.units

import mipsim.Processor
import sim.base.bus
import sim.base.eval
import sim.base.set
import sim.tool.test

internal fun main() {
	val sim = Processor()

	val regFile = sim.registerFile as RegisterFile
	val mem = regFile._memory

	val readReg = bus(5)
	val writeReg = bus(5)
	val writeData = bus(32)

	test("init") {
		val time = System.currentTimeMillis()
		(0 until 32).forEach { mem.getWord(it).set(it * 3 + 1) }
		mem.eval(time)
		regFile
	}

	test("noEvalTest") {
		//val time = System.currentTimeMillis()

		writeData.set(74)
		readReg.set(1)
		writeReg.set(1)

		regFile.readReg1.set(readReg)
		regFile.writeReg.set(writeReg)
		regFile.writeData.set(writeData)

		regFile
	}

	test("afterEval") {
		val time = System.currentTimeMillis()
		regFile.eval(time)

		regFile
	}

	test("setRegWrite") {
		val time = System.currentTimeMillis()
		regFile.regWrite.set(true)
		regFile.eval(time)

		regFile
	}

	test("noRegWrite") {
		val time = System.currentTimeMillis()
		writeData.set(12)
		regFile.regWrite.set(false)
		regFile.eval(time)

		regFile
	}

	test("readOtherReg") {
		val time = System.currentTimeMillis()
		readReg.set(3)
		regFile.eval(time)

		regFile
	}


	test("writeOnZero") {
		val time = System.currentTimeMillis()
		regFile.regWrite.set(true)
		regFile.writeReg.set(0)
		regFile.writeData.set(74)
		regFile.eval(time)

		regFile
	}

}
