package mipsim.module;

import mipsim.units.AluControlUnit;
import org.jetbrains.annotations.NotNull;
import sim.base.*;
import sim.complex.MuxKt;
import sim.real.AdderKt;

import java.util.List;

import static sim.gates.GatesKt.*;

public final class LogicALU {
	public static void thirtyTwoBitOr(List<Value> A, List<Value> B, List<MutableValue> outPut) {
		BusKt.set(outPut, or(A, B));
	}

	public static void thirtyTwoBitNor(List<Value> A, List<Value> B, List<MutableValue> outPut) {
		BusKt.set(outPut, nor(A, B));
	}

	public static void thirtyTwoBitAnd(List<Value> A, List<Value> B, List<MutableValue> outPut) {
		BusKt.set(outPut, and(A, B));
	}

	public static void thirtyTwoBitXor(List<Value> A, List<Value> B, List<MutableValue> outPut) {
		BusKt.set(outPut, xor(A, B));
	}

	/**
	 * in this function we Make a decision to add or sub
	 * if select variable is true or (1) we do subtract
	 * else we do add
	 * carry out when we need to set carry flag
	 */
	public static void AddSub(List<Value> input1, List<Value> input2, Value select, List<MutableValue> result, List<MutableValue> carryOut) {
		carryOut.get(0).set(select.get());
		for (int i = 31; i >= 0; i--) {
			AdderKt.fullAdder(input1.get(i), (xor(input2.get(i), select)), carryOut.get(i), result.get(i), carryOut.get(i + 1));
		}
	}


	public static void setLess(List<Value> input1, List<Value> input2, List<MutableValue> result) {
		var E = ValueKt.mut(false);
		var L = ValueKt.mut(false);
		var G = ValueKt.mut(false);
		Compare.com32Bit(input1, input2, E, L, G);
		BusKt.set(result, (MuxKt.mux2(L, BusKt.toBus(0, 32), BusKt.toBus(1, 32))));
		//Todo check it friends
	}


	public static void AluInStage(List<Value> input1, List<Value> input2, List<Value> function, List<Value> aluControlUnit, List<MutableValue> result, Variable zero) {


		var select = BusKt.bus(4);
		AluControlUnit.aluControlUnit(function, aluControlUnit, select);
		var resAdd = BusKt.bus(32);
		//AddSub(input1,input2,new Variable(false,""),resAdd,BusKt.toBus(0,32));
		var resSub = BusKt.bus(32);
		//AddSub(input1,input2,new Variable(true,""),resSub,BusKt.toBus(0,32));
		var resOr = BusKt.bus(32);
		thirtyTwoBitOr(input1, input2, resOr);
		var resAnd = BusKt.bus(32);
		thirtyTwoBitAnd(input1, input2, resAnd);
		var resNor = BusKt.bus(32);
		thirtyTwoBitNor(input1, input2, resNor);
		var resShift_R = BusKt.bus(32);
		//ShiftLogical.thirtyTwoBitShifterRight(input1,input2,resShift_R);
		var resShift_L = BusKt.bus(32);
		//ShiftLogical.thirtyTwoBitShifterLeft(input1,input2,resShift_L);
		var resSetLes = BusKt.bus(32);
		setLess(input1, input2, resSetLes);
		var resXor = BusKt.bus(32);


		for (int i = 0; i <= 31; i++) {
			result.get(i).set(MuxKt.mux(select, resAdd.get(i), resSub.get(i), resOr.get(i), resAnd.get(i), resXor.get(i), resNor.get(i), resShift_L.get(i), resShift_R.get(i)));
		}
	}


	/**
	 * todo: merge with previous adder/sub unit
	 */
	public static void adder(@NotNull List<? extends Value> input1, @NotNull List<? extends Value> input2, @NotNull List<? extends MutableValue> result) {
		Value currentCarry = ValueKt.constant(false);
		MutableValue nextCarry = ValueKt.mut(false);
		for (int i = 0; i < input1.size(); i++) {
			AdderKt.fullAdder(input1.get(i), input2.get(i), currentCarry, result.get(i), nextCarry);
			currentCarry = nextCarry;
			nextCarry = ValueKt.mut(false);
		}
	}

	/**
	 * this module, creates an adder with a constant value.
	 * it's completely gate-level adder,
	 * inputVal2 converts to a equivalent value bus in compile/bake/first time
	 */
	public static void adder(@NotNull List<? extends Value> input1, int inputVal2, @NotNull List<? extends MutableValue> result) {
		var input2 = BusKt.toBus(inputVal2, input1.size());
		adder(input1, input2, result);
	}

	/**
	 * like quick-adder but returns result
	 */
	public static List<MutableValue> adder(@NotNull List<? extends Value> input1, int inputVal2) {
		var result = BusKt.bus(input1.size());
		adder(input1, inputVal2, result);
		return result;
	}
}


