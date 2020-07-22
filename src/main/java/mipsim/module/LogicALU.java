package mipsim.module;

import mipsim.units.AluControlUnit;
import sim.base.*;
import sim.complex.MuxKt;
import sim.real.AdderKt;

import java.util.List;

import static sim.gates.GatesKt.*;

public final class LogicALU {
	public static void thirtyTwoBitOr(
		List<? extends Value> A,
		List<? extends Value> B,
		List<? extends MutableValue> outPut
	) {
		BusKt.set(outPut, or(A, B));
	}

	public static void thirtyTwoBitNor(
		List<? extends Value> A,
		List<? extends Value> B,
		List<? extends MutableValue> outPut
	) {
		BusKt.set(outPut, nor(A, B));
	}

	public static void thirtyTwoBitAnd(
		List<? extends Value> A,
		List<? extends Value> B,
		List<? extends MutableValue> outPut
	) {
		BusKt.set(outPut, and(A, B));
	}

	public static void thirtyTwoBitXor(
		List<? extends Value> A,
		List<? extends Value> B,
		List<? extends MutableValue> outPut
	) {
		BusKt.set(outPut, xor(A, B));
	}


	/**
	 * in this function we Make a decision to add or sub
	 * if select variable is true or (1) we do subtract
	 * else we do add
	 * carry out when we need to set carry flag
	 */
	public static void AddSub(
		List<? extends Value> input1,
		List<? extends Value> input2,
		Value select,
		List<? extends MutableValue> result,
		List<? extends MutableValue> carryOut
	) {
		carryOut.get(0).set(select.get());
		for (int i = 31; i >= 0; i--) {
			AdderKt.fullAdder(input1.get(i), (xor(input2.get(i), select)), carryOut.get(i), result.get(i), carryOut.get(i + 1));
		}
	}


	public static void setLess(
		List<? extends Value> input1,
		List<? extends Value> input2,
		List<? extends MutableValue> result
	) {
		var E = ValueKt.mut(false);
		var L = ValueKt.mut(false);
		var G = ValueKt.mut(false);
		Compare.com32Bit(input1, input2, E, L, G);
		BusKt.set(result, (MuxKt.mux2(L, BusKt.toBus(0, 32), BusKt.toBus(1, 32))));
		//Todo check it friends
	}


	public static void AluInStage(
		List<? extends Value> input1,
		List<? extends Value> input2,
		List<? extends Value> function,
		List<? extends Value> aluControlUnit,
		List<? extends MutableValue> result
	) {
		var carry = BusKt.bus(32);
		var select = BusKt.bus(4);
		AluControlUnit.aluControlUnit(function, aluControlUnit, select);
		var resAdd = BusKt.bus(32);
		AddSub(input1, input2, new Variable(false, ""), resAdd, carry);
		var resSub = BusKt.bus(32);
		AddSub(input1, input2, new Variable(true, ""), resSub, carry);
		var resOr = BusKt.bus(32);
		thirtyTwoBitOr(input1, input2, resOr);
		var resAnd = BusKt.bus(32);
		thirtyTwoBitAnd(input1, input2, resAnd);
		var resNor = BusKt.bus(32);
		thirtyTwoBitNor(input1, input2, resNor);
		var resShift_R = BusKt.bus(32);
		ShiftHelper.thirtyTwoBitShifterRight(input1, input2, resShift_R);
		var resShift_L = BusKt.bus(32);
		ShiftHelper.thirtyTwoBitShifterLeft(input1, input2, resShift_L);
		var resSetLes = BusKt.bus(32);
		setLess(input1, input2, resSetLes);
		var resXor = BusKt.bus(32);
		thirtyTwoBitXor(input1,input2,resXor);
		Multiplexer.aluResult(aluControlUnit, resAdd, resSub, resAnd, resOr, resSetLes, resShift_L, resShift_R, result);
	}
	/**
	 * mehdi Fe check
	 */
	static void main(String args[]){


	}



}
