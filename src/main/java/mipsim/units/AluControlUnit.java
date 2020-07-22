package mipsim.units;

import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.ValueKt;

import java.util.List;

import static sim.gates.GatesKt.*;

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

	public static void main(String[] args) {
		var aluO = BusKt.bus(2);
		aluO.set(0, ValueKt.mut(false));
		aluO.set(1,ValueKt.mut(true));

		var func = BusKt.bus(6);
		func.set(0, ValueKt.mut(false));
		func.set(1,ValueKt.mut(true));
		func.set(2,ValueKt.mut(false));
		func.set(3,ValueKt.mut(false));
		func.set(4,ValueKt.mut(false));
		func.set(5,ValueKt.mut(false));

		var aluControlInput = BusKt.bus(4);
		AluControlUnit.aluControlUnit(aluO,func,aluControlInput);
		System.out.println(aluControlInput.get(3)+""+aluControlInput.get(2)+""+aluControlInput.get(1)+""+aluControlInput.get(0));

	}
}
