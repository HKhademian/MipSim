package mipsim.units;

import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.ValueKt;
import sim.complex.DecoderKt;


import java.util.Base64;
import java.util.List;

import static sim.base.GateKt.*;

public final class AluControlUnit {
	/**
	 * @param aluOp           2 bit aluOp
	 * @param func            6 bit func
	 * @param aluControlInput will create 4 bit aluControlInput
	 */
	public static void aluControlUnit(
		List<? extends Value> aluOp,
		List<? extends Value> func,
		List<? extends MutableValue> aluControlInput
	) {
		/*
		aluControlInput[0] = (aluOp[1] == 1 && ( func[0]== 1 || (func[1] == 1 && (func[3] == 1 || func[5] != 1)) ) )
		aluControlInput[1] = !(aluOp[1] == 1 && (func[5] != 1 || func[2] == 1))
		aluControlInput[2] = (aluOp[0] || (aluOp[1] && (func[5] != 1 || func[0]== 1)))
		aluControlInput[3] = 0
		 */

		aluControlInput.get(0).set(and(aluOp.get(1),
			or(func.get(0)
				, and(func.get(1),
					or(func.get(3),
						not(func.get(5))
					)
				)
			)
		));

		aluControlInput.get(1).set(not(
			and(aluOp.get(1),
				or(func.get(2),
					not(func.get(5))
				)
			)
			)
		);

		aluControlInput.get(2).set(or(aluOp.get(0),
			and(aluOp.get(1),
				or(func.get(1), not(func.get(5)))
			)
			)
		);

		aluControlInput.get(3).set(false);
	}

	public static void aluControlUnitPlus(
		List<? extends Value> aluOp,
		List<? extends Value> func,
		MutableValue jumpReg,
		List<? extends MutableValue> aluControlInput){
		var aluOpDecode = DecoderKt.dec(Value.ONE,aluOp);//00 add 01 and 10 r type 11 or
		var funcDecoder = DecoderKt.dec(Value.ONE,func);

		aluControlInput.get(0).set(or(aluOpDecode.get(3)
			,and(aluOpDecode.get(2)
				,or(funcDecoder.get(37),funcDecoder.get(38),funcDecoder.get(2),funcDecoder.get(42)))));//ori,or xor slr slt

		aluControlInput.get(1).set(or(aluOpDecode.get(0)
			,and(aluOpDecode.get(2)
				,or(funcDecoder.get(32),funcDecoder.get(34),funcDecoder.get(38),funcDecoder.get(42)))));//addi,add  sub xor slt

		aluControlInput.get(2).set(and(aluOpDecode.get(2)
			,or(funcDecoder.get(0),funcDecoder.get(2),funcDecoder.get(34),funcDecoder.get(42))));//sll slr sub slt

		aluControlInput.get(3).set(and(aluOpDecode.get(2),funcDecoder.get(39)));//nor

		jumpReg.set(and(aluOpDecode.get(2),funcDecoder.get(8)));

		/**
		 * and 0000 ,or 0001,add 0010,
		 * xor 0011 ,sll 0100,slr 0101
		 * sub 0110 ,slt 0111,nor 1000
		 */

	}


	/**
	 * test in progress by: @ramin
	 */
	public static void main(String[] args) {
		var aluO = BusKt.toBus(2, 2);
		var func = BusKt.toBus(0, 6);
		var aluControlInput = BusKt.bus(4);
		var jumpReg = ValueKt.mut(false);
		AluControlUnit.aluControlUnit(aluO, func, aluControlInput);
		System.out.println(aluControlInput.get(3) + "" + aluControlInput.get(2) + "" + aluControlInput.get(1) + "" + aluControlInput.get(0)+"        jmp "+jumpReg);

	}
}
