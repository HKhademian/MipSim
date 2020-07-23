package mipsim.units

import sim.DebugWriter
import sim.base.*
import sim.test.testOn

/// each byte is 8 bit of data
const val BYTE_SIZE = 8

/// each word is 4 byte of data
const val WORD_SIZE = 4 * BYTE_SIZE

/**
 *  a memory bit is a simple D-FF to store values
 *  to store a value on next clock edge, just use `MemBit`.`set`
 *  and use `MemBit`.`get` to read from it.
 */
class MemBit : Eval, MutableValue {
	override val title = "MemBit"

	/** whether to write input to output or nuo */
	val memWrite = mut(true)

	private var curr = false
	private var lastEval = 0L

	private val next = mut(false)
	private val lock = sim.expriment.SimpleLock()

	override fun set(value: Value) =
		next.set(value)

	override fun get() =
		curr

	override fun eval(time: Long) {
		if (time <= lastEval)
			return
		lock.lock {
			lastEval = time
			next.eval(time)
			// at moment of clock pos-edge, we calculate next value and store it to cache
			if (memWrite.get())
				curr = next.get()
		}
	}

	override fun toString() =
		//"$curr <- $next"
		curr.toString()
}

/**
 *  a memory is just a list of mem-bits,
 *  InstructionMemory and DataMemory is not a simple memory,
 *  but they use memory to stores data
 */
class Memory private constructor(private val bits: List<MemBit>) : Eval, List<MemBit> by bits, DebugWriter {
	internal constructor(bitCount: Int) : this((0 until bitCount).map { MemBit() })

	override fun eval(time: Long) =
		bits.eval(time)

	override fun writeDebug(buffer: StringBuffer) {
		val wordCount = this.wordCount
		for (i in 0 until wordCount) {
			buffer
				.append("WORD#")
				.append(i)
				.append("=")
				.append(Integer.toHexString(getWord(i).toInt()))
				.append("\t\t")
		}
	}
}

inline val List<Value>.bitCount: Int get() = this.size
inline val List<Value>.byteCount: Int get() = this.bitCount / BYTE_SIZE
inline val List<Value>.wordCount: Int get() = this.bitCount / WORD_SIZE

fun <T : Value> List<T>.getByte(i: Int): List<T> = this.subList(BYTE_SIZE * i, BYTE_SIZE * i + BYTE_SIZE)
fun <T : Value> List<T>.getWord(i: Int): List<T> = this.subList(WORD_SIZE * i, WORD_SIZE * i + WORD_SIZE)

fun createMemory(n: Int = 1) = Memory(n)
fun createBytes(n: Int = 1) = createMemory(n * BYTE_SIZE)
fun createWords(n: Int = 1) = createMemory(n * WORD_SIZE)


/** convert a int to byte eq bytes */
fun Int.Bytes() = this

/** convert a int to kilo-byte eq bytes */
fun Int.KBytes() = this.Bytes() shl 10

/** convert a int to mega-byte eq bytes */
fun Int.MBytes() = this.KBytes() shl 10

/** convert a int to giga-byte eq bytes */
fun Int.GBytes() = this.MBytes() shl 10


/** reset all values in bus */
fun List<MutableValue>.reset() =
	forEach { it.reset() }

/** write some words to bus */
fun List<MutableValue>.writeWords(words: List<Int>, time: Long?) =
	words.asSequence()
		.map { it.toBus(32) }
		.forEachIndexed { i, word ->
			val wordBus = getWord(i)
			wordBus.set(word)
			if (time != null) wordBus.eval(time)
		}

/** set all `memBit`s memWrite flags to given value */
fun List<MemBit>.setMemWrite(memWrite: Value) =
	this.forEach { it.memWrite.set(memWrite) }

/** set all `memBit`s memWrite flags to given value */
fun List<MemBit>.setMemWrite(memWrite: Boolean) =
	this.setMemWrite(memWrite.toValue())

internal fun main() {
	val x = MemBit()
	x.set(not(x))

	testOn(x, "init")

	repeat(10) {
		testOn(x, "test") {
			x.eval(System.nanoTime())
		}
	}
}
