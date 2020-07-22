package mipsim.units

import mipsim.Processor
import mipsim.test.test
import sim.base.bus
import sim.base.set

internal fun main() {
	val sim = Processor()

	val regFile = sim.registerFile as RegisterFile
	val mem = regFile._memory

	val readReg = bus(5)
	val writeReg = bus(5)
	val writeData = bus(32)

	test("init") {
		(0 until 32).forEach { mem.getWord(it).set(it * 3 + 1) }
		mem.eval()
		regFile
	}

	test("noEvalTest") {
		writeData.set(74)
		readReg.set(1)
		writeReg.set(1)

		regFile.readReg1.set(readReg)
		regFile.writeReg.set(writeReg)
		regFile.writeData.set(writeData)

		regFile
	}

	test("afterEval") {
		regFile.eval()

		regFile
	}

	test("setRegWrite") {
		regFile.regWrite.set(true)
		regFile.eval()

		regFile
	}

	test("noRegWrite") {
		writeData.set(12)
		regFile.regWrite.set(false)
		regFile.eval()

		regFile
	}

	test("readOtherReg") {
		readReg.set(3)
		regFile.eval()

		regFile
	}


	test("writeOnZero") {
		regFile.regWrite.set(true)
		regFile.writeReg.set(0)
		regFile.writeData.set(74)
		regFile.eval()

		regFile
	}

}
