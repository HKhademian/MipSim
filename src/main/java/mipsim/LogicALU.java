package mipsim;

import sim.base.Variable;
import sim.gates.AndGate;
import sim.gates.NotGate;
import sim.gates.OrGate;

public final class LogicALU {
	public static void  thirtyTwoBitOr(Variable[] A,Variable[] B,Variable[] outPut)
	{
		for(int i = 0 ; i < 32 ; i++)
				outPut[i].set(new OrGate(A[i],B[i]));


	/*
	outPut[0].set(new OrGate(A[0],B[0]));
	outPut[1].set(new OrGate(A[1],B[1]));
	outPut[2].set(new OrGate(A[2],B[2]));
	outPut[3].set(new OrGate(A[3],B[3]));
	outPut[4].set(new OrGate(A[4],B[4]));
	outPut[5].set(new OrGate(A[5],B[5]));
	outPut[6].set(new OrGate(A[6],B[6]));
	outPut[7].set(new OrGate(A[7],B[7]));

	outPut[8].set(new OrGate(A[8],B[8]));
	outPut[9].set(new OrGate(A[9],B[9]));
	outPut[10].set(new OrGate(A[10],B[10]));
	outPut[11].set(new OrGate(A[11],B[11]));
	outPut[12].set(new OrGate(A[12],B[12]));
	outPut[13].set(new OrGate(A[13],B[13]));
	outPut[14].set(new OrGate(A[14],B[14]));
	outPut[15].set(new OrGate(A[15],B[15]));


	outPut[16].set(new OrGate(A[16],B[16]));
	outPut[17].set(new OrGate(A[17],B[17]));
	outPut[18].set(new OrGate(A[18],B[18]));
	outPut[19].set(new OrGate(A[19],B[19]));
	outPut[20].set(new OrGate(A[20],B[20]));
	outPut[21].set(new OrGate(A[21],B[21]));
	outPut[22].set(new OrGate(A[22],B[22]));
	outPut[23].set(new OrGate(A[23],B[23]));

	outPut[24].set(new OrGate(A[24],B[24]));
	outPut[25].set(new OrGate(A[25],B[25]));
	outPut[26].set(new OrGate(A[26],B[26]));
	outPut[27].set(new OrGate(A[27],B[27]));
	outPut[28].set(new OrGate(A[28],B[28]));
	outPut[29].set(new OrGate(A[29],B[29]));
	outPut[30].set(new OrGate(A[30],B[30]));
	outPut[31].set(new OrGate(A[31],B[31]));

	 */

	}
	public static void  thirtyTwoBitNor(Variable[] A,Variable[] B,Variable[] outPut)
	{
		for(int i = 0 ; i < 32 ; i++)
			outPut[i].set(new NotGate(new OrGate(A[i],B[i])));


	/*
	outPut[0].set(new NotGate(new OrGate(A[0],B[0])));
	outPut[1].set(new NotGate(new OrGate(A[1],B[1])));
	outPut[2].set(new NotGate(new OrGate(A[2],B[2])));
	outPut[3].set(new NotGate(new OrGate(A[3],B[3])));
	outPut[4].set(new NotGate(new OrGate(A[4],B[4])));
	outPut[5].set(new NotGate(new OrGate(A[5],B[5])));
	outPut[6].set(new NotGate(new OrGate(A[6],B[6])));
	outPut[7].set(new NotGate(new OrGate(A[7],B[7])));

	outPut[8].set(new NotGate(new NotGate(new OrGate(A[8],B[8])));
	outPut[9].set(new NotGate(new NotGate(new OrGate(A[9],B[9])));
	outPut[10].set(new NotGate(new OrGate(A[10],B[10])));
	outPut[11].set(new NotGate(new OrGate(A[11],B[11])));
	outPut[12].set(new NotGate(new OrGate(A[12],B[12])));
	outPut[13].set(new NotGate(new OrGate(A[13],B[13])));
	outPut[14].set(new NotGate(new OrGate(A[14],B[14])));
	outPut[15].set(new NotGate(new OrGate(A[15],B[15])));


	outPut[16].set(new NotGate(new OrGate(A[16],B[16])));
	outPut[17].set(new NotGate(new OrGate(A[17],B[17])));
	outPut[18].set(new NotGate(new OrGate(A[18],B[18])));
	outPut[19].set(new NotGate(new OrGate(A[19],B[19])));
	outPut[20].set(new NotGate(new OrGate(A[20],B[20])));
	outPut[21].set(new NotGate(new OrGate(A[21],B[21])));
	outPut[22].set(new NotGate(new OrGate(A[22],B[22])));
	outPut[23].set(new NotGate(new OrGate(A[23],B[23])));

	outPut[24].set(new NotGate(new OrGate(A[24],B[24])));
	outPut[25].set(new NotGate(new OrGate(A[25],B[25])));
	outPut[26].set(new NotGate(new OrGate(A[26],B[26])));
	outPut[27].set(new NotGate(new OrGate(A[27],B[27])));
	outPut[28].set(new NotGate(new OrGate(A[28],B[28])));
	outPut[29].set(new NotGate(new OrGate(A[29],B[29])));
	outPut[30].set(new NotGate(new OrGate(A[30],B[30])));
	outPut[31].set(new NotGate(new OrGate(A[31],B[31])));

	 */

	}
	public static void  thirtyTwoBitAnd(Variable[] A,Variable[] B,Variable[] outPut)
	{
		for(int i = 0 ; i < 32 ; i++)
			outPut[i].set(new AndGate(A[i],B[i]));


	/*
	outPut[0].set(new AndGate(A[0],B[0]));
	outPut[1].set(new AndGate(A[1],B[1]));
	outPut[2].set(new AndGate(A[2],B[2]));
	outPut[3].set(new AndGate(A[3],B[3]));
	outPut[4].set(new AndGate(A[4],B[4]));
	outPut[5].set(new AndGate(A[5],B[5]));
	outPut[6].set(new AndGate(A[6],B[6]));
	outPut[7].set(new AndGate(A[7],B[7]));

	outPut[8].set(new AndGate(A[8],B[8]));
	outPut[9].set(new AndGate(A[9],B[9]));
	outPut[10].set(new AndGate(A[10],B[10]));
	outPut[11].set(new AndGate(A[11],B[11]));
	outPut[12].set(new AndGate(A[12],B[12]));
	outPut[13].set(new AndGate(A[13],B[13]));
	outPut[14].set(new AndGate(A[14],B[14]));
	outPut[15].set(new AndGate(A[15],B[15]));


	outPut[16].set(new AndGate(A[16],B[16]));
	outPut[17].set(new AndGate(A[17],B[17]));
	outPut[18].set(new AndGate(A[18],B[18]));
	outPut[19].set(new AndGate(A[19],B[19]));
	outPut[20].set(new AndGate(A[20],B[20]));
	outPut[21].set(new AndGate(A[21],B[21]));
	outPut[22].set(new AndGate(A[22],B[22]));
	outPut[23].set(new AndGate(A[23],B[23]));

	outPut[24].set(new AndGate(A[24],B[24]));
	outPut[25].set(new AndGate(A[25],B[25]));
	outPut[26].set(new AndGate(A[26],B[26]));
	outPut[27].set(new AndGate(A[27],B[27]));
	outPut[28].set(new AndGate(A[28],B[28]));
	outPut[29].set(new AndGate(A[29],B[29]));
	outPut[30].set(new AndGate(A[30],B[30]));
	outPut[31].set(new AndGate(A[31],B[31]));

	 */

	}

}
