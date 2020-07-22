package mipsim.test

import sim.debugWrite

inline fun test(msg: String = "test", crossinline task: () -> Any?) {
	println("***** $msg *****")
	val res = task() ?: return
	println(res.debugWrite())
}
