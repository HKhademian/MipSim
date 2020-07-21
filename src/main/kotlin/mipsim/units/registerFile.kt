package mipsim.units

import mipsim.Simulator
import sim.base.bus
import sim.base.set

private val sim = Simulator()
val regFile = sim.registerFile

fun main() {
	val readReg = bus(5)
	val writeReg = bus(5)
	val writeData = bus(32)

	writeData.set(74);
	readReg.set(1);
	writeReg.set(1);

	regFile.readReg1.set(readReg)
	regFile.writeReg.set(writeReg)
	regFile.writeData.set(writeData)

	println("writeData: ${regFile.writeData}")
	println("Read: ${regFile.readData1}")

	regFile.eval()
	println("****\nwriteData: ${regFile.writeData}")
	println("Read: ${regFile.readData1}")

	regFile.regWrite.set(true);
	regFile.eval()
	println("****\nwriteData: ${regFile.writeData}")
	println("Read: ${regFile.readData1}")

	writeData.set(12);
	regFile.regWrite.set(false)
	regFile.eval()
	println("****\nwriteData: ${regFile.writeData}")
	println("Read: ${regFile.readData1}")

	readReg.set(3);
	regFile.eval()
	println("****\nwriteData: ${regFile.writeData}")
	println("Read: ${regFile.readData1}")
}
