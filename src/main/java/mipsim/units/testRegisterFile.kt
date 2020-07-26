package mipsim.units

import mipsim.Processor
import sim.base.bus
import sim.base.eval
import sim.base.set
import sim.tool.testOn

internal fun main() {
	val sim = Processor()

	val regFile = sim.registerFile as RegisterFile
	val mem = regFile._memory

	val readReg = bus(5)
	val writeReg = bus(5)
	val writeData = bus(32)

	testOn(regFile, "init") {
		(0 until 32).forEach { mem.getWord(it).set(it * 3 + 1) }
		mem.eval(System.nanoTime())
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
		val time = System.currentTimeMillis()
		regFile.eval(time)
	}

	testOn(regFile, "setRegWrite") {
		regFile.regWrite.set(true)
		regFile.eval(System.nanoTime())
	}

	testOn(regFile, "noRegWrite") {
		writeData.set(12)
		regFile.regWrite.set(false)
		regFile.eval(System.nanoTime())
	}

	testOn(regFile, "readOtherReg") {
		readReg.set(3)
		regFile.eval(System.nanoTime())
	}

	testOn(regFile, "writeOnZero") {
		regFile.regWrite.set(true)
		regFile.writeReg.set(0)
		regFile.writeData.set(74)
		regFile.eval(System.nanoTime())
	}

}
