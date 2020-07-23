package mipsim.units

import sim.base.MutableBus
import sim.base.Value
import sim.base.bus
import sim.real.mux

class MuxBusSelector<T : Value>(private val source: List<T>, private val addressBusSize: Int) {
	private val wordCount: Int = 1 shl addressBusSize
	private val words: List<List<T>> = (0 until wordCount).map { source.getWord(it) }

	val wordAddress: MutableBus = bus(addressBusSize)

	@Suppress("UNCHECKED_CAST")
	val word: MutableBus = mux(wordAddress, words) as MutableBus
}
