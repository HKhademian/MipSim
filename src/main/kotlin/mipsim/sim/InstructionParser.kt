package mipsim.sim

import mipsim.Processor
import mipsim.units.InstructionMemory
import mipsim.units.Memory
import mipsim.units.reset
import mipsim.units.writeWords
import java.io.File

/** load instructions from a file and write to memory */
fun Memory.loadInstructions(instructionFile: File) =
	loadInstructions(instructionFile.readLines())

//fun Simulator.loadInstructions(instructions: String) =
//	loadInstructions(instructions.lines())

/** parse instructions and write to instructionMemory */
fun Processor.loadInstructions(instructionLines: List<String>) =
	instructionMemory.loadInstructions(instructionLines)

/** parse instructions and write to instructionMemory */
fun InstructionMemory.loadInstructions(instructionLines: List<String>) =
	_memory.loadInstructions(instructionLines)

/** parse instructions and write to instructionMemory */
fun Memory.loadInstructions(instructionLines: List<String>) {
	val instructions = instructionLines.map { parseInstruction(it) } // convert to int
	val memory = this
	memory.reset()
	memory.writeWords(instructions)
}

private fun parseInstruction(instruction: String): Int {
	val inst = instruction.toLowerCase().trim().split(" |,").filterNot { it.isBlank() }
	val commandStr = inst[0]
	val command = commands.find { it.command == commandStr } ?: return 0
	return command.parse(inst)
}

/** supported commands with this parser */
private val commands = listOf(
	Command("ADD", Format.R, "0", "20"),
	Command("SUB", Format.R, "0", "22"),
	Command("SLL", Format.R, "0", "0", shamt = true),
	Command("SRL", Format.R, "0", "2", shamt = true),
	Command("SLT", Format.R, "0", "2A"),
	Command("AND", Format.R, "0", "24"),
	Command("OR", Format.R, "8", "25"),
	Command("ADDI", Format.I, "F"),
//	Command("LUI", Format.I, "23"),
	Command("LW", Format.I, "2B"),
	Command("SW", Format.I, "4"),
	Command("BEQ", Format.I, "5"),
	Command("J", Format.J, "2")
)

/** registerName to real number equivalent */
private val registers: Map<String, Int> = mutableMapOf<String, Int>().also { regs ->
	// special keys
	regs["\$zero"] = 0
	regs["\$at"] = 1
	regs["\$gp"] = 28
	regs["\$sp"] = 29
	regs["\$fp"] = 30
	regs["\$ra"] = 31

	for (i in 0..31) regs["\$$i"] = i // first add numbered keys
	for (i in 0..1) regs["\$v$i"] = i + 2  // s0 .. s1
	for (i in 0..3) regs["\$a$i"] = i + 4  // a0 .. a3
	for (i in 0..7) regs["\$t$i"] = i + 8  // t0 .. t7
	for (i in 0..7) regs["\$s$i"] = i + 16 // s0 .. s7
	for (i in 8..9) regs["\$t$i"] = i + 16 // t8 .. t9
	for (i in 0..1) regs["\$k$i"] = i + 26 // k0 .. k1
}

/** parse string to register number */
private fun parseRegister(text: String): Int {
	return registers[text] ?: throw RuntimeException("Register '$text' is not valid.")
}

/** parse string to eq constant limited to `size` number of bits */
private fun parseConstant(text: String, size: Int): Int {
	val value = text.toIntOrNull() ?: throw RuntimeException("Constant '$text' is not valid.")
	return value and ((1 shl size) - 1)
}

/** used for LW and SW to separate offset and register */
private fun splitAddress(rsAndOffset: String): List<String> {
	var offset = ""
	for (i in rsAndOffset.indices)
		offset += if (rsAndOffset[i] == '(')
			return listOf(rsAndOffset.substring(i + 1, rsAndOffset.length - 1), offset)
		else rsAndOffset[i]
	throw java.lang.Exception("Bad I-Format")
}


private enum class Format { R, I, J }

private class Command(val command: String, val format: Format, val opCode: Int, val func: Int = 0, val shamt: Boolean = false) {
	constructor(command: String, format: Format, opCode: String = "", func: String = "", shamt: Boolean = false) :
		this(command, format, opCode.toInt(16), func.toIntOrNull(16) ?: 0, shamt)

	/** parse a command to int eq */
	fun parse(inst: List<String>): Int {
		var res = 0

		when (format) {

			Format.R -> {
				if (inst.size != 4) throw Exception("Bad R-format instruction")
				res = res or (opCode shl 26)
				res = res or func

				res = res or (parseRegister(inst[1]) shl 11)
				res = res or (parseRegister(inst[2]) shl 21)

				res = res or if (shamt) {
					(parseConstant(inst[3], 5) shl 6)
				} else {
					(parseRegister(inst[3]) shl 16)
				}
			}


			Format.I -> {
				res = res or (opCode shl 26)

				if (inst[0] in listOf("LW", "SW", "LUI")) {
					if (inst.size != 3) throw RuntimeException("Bad I-format instruction: " + inst[0])
					if (inst[0] == "LUI") {
						res = res or parseConstant(inst[1], 16) //constant
					} else {
						res = res or (parseRegister(inst[1]) shl 16) //rt - source/destination
						val rsAndOffset = splitAddress(inst[2])
						res = res or (parseRegister(rsAndOffset[0]) shl 21) //rs - base address
						res = res or parseConstant(rsAndOffset[1], 16) //constant - offset
					}
				} else {
					if (inst.size != 4) throw RuntimeException("Bad I-format instruction: " + inst[0])
					val rs = if (inst[0] in listOf("BEQ", "BNE")) 1 else 2
					val rt = (rs - 1 xor 1) + 1
					res = res or (parseRegister(inst[rs]) shl 21) //rs
					res = res or (parseRegister(inst[rt]) shl 16) //rt
					res = res or parseConstant(inst[3], 16)   //address or constant
				}
			}


			Format.J -> {
				if (inst.size != 2) throw RuntimeException("Bad J-format instruction")
				res = res or (opCode shl 26)
				res = res or parseConstant(inst[1], 16) //address
			}

		}

		return res
	}
}

