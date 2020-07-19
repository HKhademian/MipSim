package mipsim.units

import sim.base.*

/// each byte is 8 bit of data
const val BYTE_SIZE = 8

/// each word is 4 byte of data
const val WORD_SIZE = 4 * BYTE_SIZE

/**
 *  a memory bit is a simple D-FF to store values
 *  to store a value on next clock edge, just use `MemBit`.`set`
 *  and use `MemBit`.`get` to read from it.
 */
class MemBit : Element, MutableValue {
	private val curr = Variable(false)
	private val next = Variable(false)

	override fun set(value: Value) =
		next.set(value)

	override fun get() =
		curr.get()

	override fun eval() =
		// at moment of clock pos-edge, we calculate next value and store it to cache
		next.const().also { curr.set(it) }
}

/**
 *  a memory is just a list of mem-bits,
 *  InstructionMemory and DataMemory is not a simple memory,
 *  but they use memory to stores data
 */
class Memory private constructor(private val bits: List<MemBit>) : List<MemBit> by bits {
	internal constructor(bitCount: Int) : this((0 until bitCount).map { MemBit() })
}

inline val List<Value>.bitCount: Int get() = this.size
inline val List<Value>.byteCount: Int get() = this.bitCount / BYTE_SIZE
inline val List<Value>.wordCount: Int get() = this.bitCount / WORD_SIZE

fun List<Value>.getByte(i: Int): List<Value> = this.subList(BYTE_SIZE * i, BYTE_SIZE * i + BYTE_SIZE)
fun List<Value>.getWord(i: Int): List<Value> = this.subList(WORD_SIZE * i, WORD_SIZE * i + WORD_SIZE)

fun createMemory(n: Int = 1) = Memory(n)
fun createBytes(n: Int = 1) = createMemory(n * BYTE_SIZE)
fun createWords(n: Int = 1) = createMemory(n * WORD_SIZE)


fun Int.Bytes() = this
fun Int.KBytes() = this.Bytes() shl 10
fun Int.MBytes() = this.KBytes() shl 10
fun Int.GBytes() = this.MBytes() shl 10
