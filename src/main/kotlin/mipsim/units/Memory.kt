package mipsim.units

import sim.base.*

/// each byte is 8 bit of data
const val BYTE_SIZE = 8

/// each word is 4 byte of data
const val WORD_SIZE = 4 * BYTE_SIZE

inline val List<Value>.bitCount: Int get() = this.size
inline val List<Value>.byteCount: Int get() = this.bitCount / BYTE_SIZE
inline val List<Value>.wordCount: Int get() = this.bitCount / WORD_SIZE

fun <T : Value> List<T>.getByte(i: Int): List<T> = this.subList(BYTE_SIZE * i, BYTE_SIZE * i + BYTE_SIZE)
fun <T : Value> List<T>.getWord(i: Int): List<T> = this.subList(WORD_SIZE * i, WORD_SIZE * i + WORD_SIZE)

/** convert a int to byte eq bytes */
fun Int.Bytes() = this

/** convert a int to kilo-byte eq bytes */
fun Int.KBytes() = this.Bytes() shl 10

/** convert a int to mega-byte eq bytes */
fun Int.MBytes() = this.KBytes() shl 10

/** convert a int to giga-byte eq bytes */
fun Int.GBytes() = this.MBytes() shl 10
