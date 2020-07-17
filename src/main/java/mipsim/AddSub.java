package mipsim;
import sim.gates.XorGate;
import sim.real.AdderKt;
import sim.base.Variable;

public class AddSub {
	public static void AddSub(Variable[] input1,Variable[] input2,Variable select,Variable[] result,Variable carryOut){
		carryOut = select;
		for(int i = 31 ; i >= 0 ; i--){
			AdderKt.fullAdder(input1[1],new Variable(new XorGate(input2[i],select)),carryOut,result[i],carryOut);
		}
	}

}
