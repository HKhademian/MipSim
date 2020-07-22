package mipsim.units

import sim.base.Bus
import sim.base.MutableBus
import sim.base.bus
import sim.real.mux

open class MuxMemory protected constructor(addressBusSize: Int) {
	protected val wordCount: Int = 1 shl addressBusSize
	protected val memory: Memory = createWords(wordCount)
	protected val words: List<MutableBus> = (0 until wordCount).map { memory.getWord(it) }
	val address: MutableBus = bus(addressBusSize)
	val word: Bus = mux(address, words)
}
