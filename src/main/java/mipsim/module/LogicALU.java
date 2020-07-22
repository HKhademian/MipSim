package mipsim.module;

import mipsim.units.AluControlUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.ValueKt;
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
		List<? extends MutableValue> result
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
		thirtyTwoBitNor(input1, input2
			, resNor);
		ShiftHelper.thirtyTwoBitShifterRight(input1, shiftMa, resShift_R);
		ShiftHelper.thirtyTwoBitShifterLeft(input1, shiftMa, resShift_L);
		setLess(input1, input2, resSetLes);


		Multiplexer.aluResult(aluOp, resAdd, resSub, resAnd, resOr, resSetLes, resShift_L, resShift_R, result);
	}
}
