package mipsim.sim

import mipsim.Processor
import mipsim.units.InstructionMemoryUnit
import mipsim.units.RealMemory
import java.io.File

// const val NOP = "sll $0, $0, 0"
const val NOP = "NOP"
const val NOP_BIN = 0

const val HALT = "HALT"
const val HALT_BIN = -1


/** supported commands with this parser */
val commands = listOf(
	Command("SLL", Format.R, "0", "0", shamt = true),
	Command("SRL", Format.R, "0", "2", shamt = true),
	Command("ADD", Format.R, "0", "20"),
	Command("SUB", Format.R, "0", "22"),
	Command("AND", Format.R, "0", "24"),
	Command("OR", Format.R, "0", "25"),
	Command("XOR", Format.R, "0", "26"),
	Command("NOR", Format.R, "0", "27"),
	Command("SLT", Format.R, "0", "2A"),
	Command("JR", Format.R, "0", "8"),
	Command("BEQ", Format.I, "4"),
	Command("BNQ", Format.I, "5"),
	Command("ADDI", Format.I, "8"),
	Command("ANDI", Format.I, "C"),
	Command("ORI", Format.I, "D"),
	Command("LUI", Format.I, "F"),
	Command("LW", Format.I, "23"),
	Command("SW", Format.I, "2B"),
	Command("SUI", Format.I, "3F"),
	Command("J", Format.J, "2")
)

/** registerName to real number equivalent */
val registers: Map<String, Int> = mutableMapOf<String, Int>().also { regs ->
	for (i in 0..31) regs["\$$i"] = i // first add numbered keys

	// special keys
	regs["\$zero"] = 0
	regs["\$at"] = 1
	regs["\$gp"] = 28
	regs["\$sp"] = 29
	regs["\$fp"] = 30
	regs["\$ra"] = 31

	for (i in 0..1) regs["\$v$i"] = i + 2  // s0 .. s1
	for (i in 0..3) regs["\$a$i"] = i + 4  // a0 .. a3
	for (i in 0..7) regs["\$t$i"] = i + 8  // t0 .. t7
	for (i in 0..7) regs["\$s$i"] = i + 16 // s0 .. s7
	for (i in 8..9) regs["\$t$i"] = i + 16 // t8 .. t9
	for (i in 0..1) regs["\$k$i"] = i + 26 // k0 .. k1
}


/** parse string to register number */
fun parseRegister(text: String): Int {
	return registers[text.toLowerCase()] ?: throw RuntimeException("Register '$text' is not valid.")
}

/** parse string to eq constant limited to `size` number of bits */
fun parseConstant(text: String, size: Int): Int {
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


enum class Format {
	R {
		override fun parseInstructionToBin(command: Command, inst: List<String>, nop: Boolean): List<Int> {
			var res = (command.opCode shl 26)
			res = res or command.func

			if (command.name == "JR") {
				if (inst.size != 2) throw Exception("Bad R-format instruction: " + inst[0])
				res = res or (parseRegister(inst[1]) shl 21) // rs
			} else {
				if (inst.size != 4) throw Exception("Bad R-format instruction: " + inst[0])

				res = res or (parseRegister(inst[1]) shl 11) // rd
				res = res or (parseRegister(inst[2]) shl 21) // rs

				res = res or if (command.shamt) {
					(parseConstant(inst[3], 5) shl 6) // shift amount
				} else {
					(parseRegister(inst[3]) shl 16) // rt
				}
			}

			return listOf(res)
		}

		override fun parseBinToInstruction(command: Command, binary: Int): String {
			if (binary == NOP_BIN) return NOP

			val regMask = ((1 shl 5) - 1)
			val rd = (binary ushr 11) and regMask
			val rs = (binary ushr 21) and regMask
			val rt = (binary ushr 16) and regMask
			val sa = (binary ushr 6) and regMask

			val rdStr = registers.entries.find { it.value == rd }!!.component1()
			val rsStr = registers.entries.find { it.value == rs }!!.component1()

			if (command.name == "JR") {
				return "JR $rsStr"
			}

			return if (command.shamt) {
				"${command.name} $rdStr, $rsStr, $sa"
			} else {
				val rtStr = registers.entries.find { it.value == rt }!!.component1()
				"${command.name} $rdStr, $rsStr, $rtStr"
			}
		}
	},

	I {
		override fun parseInstructionToBin(command: Command, inst: List<String>, nop: Boolean): List<Int> {
			var res = (command.opCode shl 26)
			val branch = inst[0] in listOf("BEQ", "BNE")

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
				val rs = if (branch) 1 else 2
				val rt = (rs - 1 xor 1) + 1
				res = res or (parseRegister(inst[rs]) shl 21) //rs
				res = res or (parseRegister(inst[rt]) shl 16) //rt
				res = res or parseConstant(inst[3], 16)   //address or constant
			}

			return if (nop && branch)
				listOf(res, NOP_BIN, NOP_BIN)
			else
				listOf(res)
		}

		override fun parseBinToInstruction(command: Command, binary: Int): String {
			val regMask = ((1 shl 5) - 1)
			if (command.name == "LUI") {
				TODO("Not yet implemented")
			}

			if (command.name in listOf("LW", "SW")) {
				val rd = (binary ushr 16) and regMask
				val rs = (binary ushr 21) and regMask
				val imm = binary and ((1 shl 16) - 1)

				val rdStr = registers.entries.find { it.value == rd }!!.component1()
				val rsStr = registers.entries.find { it.value == rs }!!.component1()

				return "${command.name} $rdStr, $imm($rsStr)"
			}

			val regs = listOf(0, 16, 21)
			val rsI = if (command.name in listOf("BEQ", "BNE")) 1 else 2
			val rtI = (rsI - 1 xor 1) + 1
			val rs = (binary ushr regs[rsI]) and regMask
			val rt = (binary ushr regs[rtI]) and regMask
			val imm = binary and ((1 shl 16) - 1)

			val rtStr = registers.entries.find { it.value == rt }!!.component1()
			val rsStr = registers.entries.find { it.value == rs }!!.component1()

			return "${command.name} $rtStr, $rsStr, $imm"
		}
	},

	J {
		override fun parseInstructionToBin(command: Command, inst: List<String>, nop: Boolean): List<Int> {
			if (inst.size != 2) throw RuntimeException("Bad J-format instruction: " + inst[0])
			var res = (command.opCode shl 26)
			res = res or parseConstant(inst[1], 26) //address

			return if (nop)
				listOf(res, NOP_BIN, NOP_BIN)
			else
				listOf(res)
		}

		override fun parseBinToInstruction(command: Command, binary: Int): String {
			val imm = binary and ((1 shl 26) - 1)
			return "${command.name} ${imm.toString(16)}H"
		}
	};

	/** parse a command to int eq */
	abstract fun parseInstructionToBin(command: Command, inst: List<String>, nop: Boolean): List<Int>

	/** parse a binary number to eq. instruction */
	abstract fun parseBinToInstruction(command: Command, binary: Int): String
}

class Command(val name: String, val format: Format, val opCode: Int, val func: Int = 0, val shamt: Boolean = false) {
	constructor(name: String, format: Format, opCode: String = "", func: String = "", shamt: Boolean = false) :
		this(name, format, opCode.toInt(16), func.toIntOrNull(16) ?: 0, shamt)
}

/** parse instructions and write to instructionMemory */
fun Processor.loadInstructions(instructionFile: File, nop: Boolean = false) =
	instructionMemory.loadInstructions(instructionFile, nop)

/** parse instructions and write to instructionMemory */
fun InstructionMemoryUnit.loadInstructions(instructionFile: File, nop: Boolean = false) =
	loadInstructions(instructionFile.readLines(), nop)

/** parse instructions and write to instructionMemory */
fun Processor.loadInstructions(instructionLines: List<String>, nop: Boolean = false) =
	instructionMemory.loadInstructions(instructionLines, nop)

/** parse instructions and write to instructionMemory */
fun InstructionMemoryUnit.loadInstructions(instructionLines: List<String>, nop: Boolean = false) {
	_memory.loadInstructions(instructionLines, nop)
}

/** parse instructions and write to instructionMemory */
fun RealMemory.loadInstructions(instructionLines: List<String>, nop: Boolean = false) {
	val instructions = instructionLines.asSequence()
		.map { it.substringBefore(';') } // remove comment after ;
		.map { it.trim() } // remove extra spaces
		.filterNot { it.isBlank() } // drop empty lines
		.map { parseInstructionToBin(it, nop) } // convert to int
		.flatten()
		.toList()
	loadInstructions(instructions)
}

/** parse instructions and write to instructionMemory */
fun RealMemory.loadInstructions(instructions: List<Int>) {
	bulkWrite(instructions)
}


fun parseInstructionToBin(instruction: String): Int = parseInstructionToBin(instruction, false).first()

fun parseInstructionToBin(instruction: String, nop: Boolean): List<Int> {
	val inst = instruction.toUpperCase().trim().split(",", " ")
		.map { it.trim() }
		.filterNot { it.isBlank() }
	val commandStr = inst[0]

	if (HALT.contentEquals(commandStr)) return listOf(HALT_BIN)
	if (NOP.contentEquals(commandStr)) return listOf(NOP_BIN)
	if (commandStr.startsWith(";")) return emptyList()

	val command = commands.find { it.name.toUpperCase() == commandStr }
		?: throw RuntimeException("unsupported command: $commandStr")
	val format = command.format
	return format.parseInstructionToBin(command, inst, nop)
}

fun parseBinToInstruction(binaryInstruction: Int): String {
	if (binaryInstruction == NOP_BIN) return NOP
	if (binaryInstruction == HALT_BIN) return HALT
	val opcode = binaryInstruction ushr 26
	val func = binaryInstruction and ((1 shl 6) - 1)
	val command = commands.find { it.opCode == opcode && (opcode != 0 || it.func == func) }
	//?: throw RuntimeException("unsupported opCode")
		?: return "{UNKNOWN_INSTRUCTION}"
	val format = command.format
	return format.parseBinToInstruction(command, binaryInstruction)
}

internal fun main() {
	fun testInst(inst: String) {
		println("\ntesting '$inst'")
		val instBin = parseInstructionToBin(inst)
		println(instBin)
		val instStr = parseBinToInstruction(instBin)
		println(instStr)
	}

	testInst("add \$zero $1 \$fp")
	testInst("addi \$t1, \$t2, 1374")
	testInst("addi \$s1, \$t2, 74")
	testInst("lw \$t1, 4(\$t2)")
	testInst("J 1000")
	testInst("JR \$ra")

}
