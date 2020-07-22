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
		List<? extends Value> function,
		List<? extends Value> aluControlUnit,
		List<? extends MutableValue> result
	) {
		var carry = BusKt.bus(32);
		var select = BusKt.bus(4);
		AluControlUnit.aluControlUnit(aluControlUnit, function, select);
		var resAdd = BusKt.bus(32);
		AddSub(input1, input2, ValueKt.mut(false), resAdd, null);
		var resSub = BusKt.bus(32);
		AddSub(input1, input2, ValueKt.mut(false), resSub, null);
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

		Multiplexer.aluResult(aluControlUnit, resAdd, resSub, resAnd, resOr, resSetLes, resShift_L, resShift_R, result);
	}
}
