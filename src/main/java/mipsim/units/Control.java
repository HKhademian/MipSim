package mipsim.units;

import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

import static sim.gates.GatesKt.*;

public class Control {
	public static void control(List<Value> opcode, MutableValue regDst,MutableValue ALUsrc,MutableValue memToReg
		,MutableValue regWrite,MutableValue memRead,MutableValue memWrite,MutableValue branch,
														 MutableValue jump,List<MutableValue> aluOp)//alu op is two bit
	{
		//todo use this link create control http://fourier.eng.hmc.edu/e85_old/lectures/processor/node5.html

		/*
		opcode is 6 bit and opCode.get(5) is most significant

		regDst = (all bit all 0 )?1:0;
		ALUsrc = (opcode[3] == 1 || opcode[0] == 1)?1:0;
		memToReg = (opcode[5] == 1 && opcode[3] != 1)?1:0;
		regWrite = not( ((opcode[5]==1 && opcode[3] == 1) || opcode[2] == 1 ||( opcode[1] == 1 && opcode[5]!= 1))?1:0 )
		memRead = like memToReg
		memWrite = opcode[5] == 1 && opcode[3] == 1)?1:0;
		branch = (opcode[2] === 1)?1:0;
		ALUop[1] = (all bit all 0 )?1:0;
		ALUop[0] = like branch;
		jmp = (opCode[5] != 1 && opcode[1] == 1)?1:0;
		 */
		regDst.set(not(or(opcode)));
		ALUsrc.set(or(opcode.get(3),opcode.get(5)));
		memToReg.set(and(opcode.get(5),not(opcode.get(3))));
		regWrite.set( not(
			or(and(opcode.get(5),opcode.get(3)),opcode.get(2),and(opcode.get(1),not(opcode.get(5)))
			)
		) );
		memRead.set(and(opcode.get(5),not(opcode.get(3))));
		memWrite.set(and(opcode.get(5),opcode.get(3)));
		branch.set(opcode.get(2));
		aluOp.get(1).set(not(or(opcode)));
		aluOp.get(0).set(opcode.get(2));
		jump.set(and(not(opcode.get(5)),opcode.get(1)));
	}
}
