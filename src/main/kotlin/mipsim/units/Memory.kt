package mipsim.units

import sim.base.*

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

/** literally, it's 8 MemBit */
class MemByte private constructor(private val bits: List<MemBit>) : List<Value> by bits {
	constructor() : this((0 until 8).map { MemBit() })
}

/** literally, it's 4 MemByte */
class MemWord private constructor(private val bytes: List<MemByte>) : List<MemByte> by bytes {
	constructor() : this((0 until 8).map { MemByte() })
}
