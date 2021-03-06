package mipsim.module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sim.base.*;
import sim.complex.MuxKt;
import sim.complex.AdderKt;

import java.util.ArrayList;
import java.util.List;

import static sim.base.GateKt.*;
import static mipsim.module.TinyModules.isEqual;
import static sim.complex.MuxKt.mux;

//tested by ramin
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
		@NotNull List<? extends Value> A,
		@NotNull List<? extends Value> B,
		@NotNull Value addSubSelect,
		@NotNull List<? extends MutableValue> result,
		@Nullable MutableValue carryOut
	) {
		final var xB = xor(B, addSubSelect);
		Value currentCarry = ValueKt.constant(addSubSelect);
		MutableValue nextCarry = null;
		for (int i = 0; i < A.size(); i++) {
			nextCarry = ValueKt.mut(false);
			AdderKt.fullAdder(A.get(i), xB.get(i), currentCarry, result.get(i), nextCarry);
			currentCarry = nextCarry;
		}
		if (carryOut != null && nextCarry != null)
			carryOut.set(nextCarry);
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
		List<? extends Value> aluOp,
		List<? extends Value> shiftMa,
		List<? extends MutableValue> result,
		MutableValue zero
	) {
		var resAdd = BusKt.bus(32);
		var resSub = BusKt.bus(32);
		var resOr = BusKt.bus(32);
		var resAnd = BusKt.bus(32);
		var resNor = BusKt.bus(32);
		var resShift_R = BusKt.bus(32);
		var resShift_L = BusKt.bus(32);
		var resSetLes = BusKt.bus(32);

		AddSub(input1, input2, ValueKt.constant(false), resAdd, null);
		AddSub(input1, input2, ValueKt.constant(true), resSub, null);
		thirtyTwoBitOr(input1, input2, resOr);
		thirtyTwoBitAnd(input1, input2, resAnd);
		thirtyTwoBitNor(input1, input2			, resNor);
		ShiftHelper.shifter(true,shiftMa,input1, resShift_R);
		ShiftHelper.shifter(false, shiftMa,input1, resShift_L);
		setLess(input1, input2, resSetLes);

		zero.set(isEqual(input1,input2));

		Multiplexer.aluResult(aluOp, resAdd, resSub, resAnd, resOr, resSetLes, resShift_L, resShift_R, result);
	}








	public static void AluInStagePlus(
		List<? extends Value> input1,
		List<? extends Value> input2,
		List<? extends Value> aluOp,
		List<? extends Value> shiftMa,
		List<? extends MutableValue> result,
		MutableValue zero
	) {
		var resAdd = BusKt.bus(32);
		var resSub = BusKt.bus(32);
		var resOr = BusKt.bus(32);
		var resXor = BusKt.bus(32);
		var resAnd = BusKt.bus(32);
		var resNor = BusKt.bus(32);
		var resShift_R = BusKt.bus(32);
		var resShift_L = BusKt.bus(32);
		var resSetLes = BusKt.bus(32);

		AddSub(input1, input2, ValueKt.constant(false), resAdd, null);
		AddSub(input1, input2, ValueKt.constant(true), resSub, null);
		thirtyTwoBitOr(input1, input2, resOr);
		thirtyTwoBitAnd(input1, input2, resAnd);
		thirtyTwoBitNor(input1, input2, resNor);
		thirtyTwoBitXor(input1, input2, resXor);
		ShiftHelper.shifter(true,shiftMa,input1, resShift_R);
		ShiftHelper.shifter(false, shiftMa,input1, resShift_L);
		setLess(input1, input2, resSetLes);

		zero.set(isEqual(input1,input2));

		Multiplexer.aluResultPlus(aluOp, resAdd, resSub, resAnd, resOr,resNor,resXor, resSetLes, resShift_L, resShift_R, result);
	}







	public static void main(String[] args) {
		var input1 = BusKt.toBus(27, 32);
		var input2 = BusKt.toBus(2, 32);

		// todo: wrong
		var aluOp = BusKt.bus(4);
		aluOp.get(0).set(false);
		aluOp.get(1).set(false);
		aluOp.get(2).set(true);
		aluOp.get(3).set(false);

		var shiftMa = BusKt.toBus(10, 5);

		var result = BusKt.bus(32);
		var zero = ValueKt.mut(false);
		AluInStage(input1, input2, aluOp, shiftMa, result,zero);

		System.out.println(BusKt.toInt(result));

		setLess(input1,input2,result);
		System.out.println("zero "+zero);
	}
}
