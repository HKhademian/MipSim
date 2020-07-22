package mipsim.units;

import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.ValueKt;

import java.util.List;

import static sim.gates.GatesKt.*;
//test:done
public final class ControlUnit {
	public static void control(
		List<? extends Value> opcode,
		MutableValue regDst,
		MutableValue ALUsrc,
		MutableValue memToReg,
		MutableValue regWrite,
		MutableValue memRead,
		MutableValue memWrite,
		MutableValue branch,
		MutableValue jump,
		List<? extends MutableValue> aluOp//alu op is two bit
	) {
		//todo: use this link create control http://fourier.eng.hmc.edu/e85_old/lectures/processor/node5.html

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
		ALUsrc.set(or(opcode.get(3), opcode.get(5)));
		memToReg.set(and(opcode.get(5), not(opcode.get(3))));
		regWrite.set(not(
			or(and(opcode.get(5), opcode.get(3)), opcode.get(2), and(opcode.get(1), not(opcode.get(5)))
			)
		));
		memRead.set(and(opcode.get(5), not(opcode.get(3))));
		memWrite.set(and(opcode.get(5), opcode.get(3)));
		branch.set(opcode.get(2));
		aluOp.get(1).set(not(or(opcode)));
		aluOp.get(0).set(opcode.get(2));
		jump.set(and(not(opcode.get(5)), opcode.get(1)));
	}

	public static void main(String[] args) {
		var opcode = BusKt.bus(6);
		opcode.set(0, ValueKt.mut(false));
		opcode.set(1,ValueKt.mut(true));
		opcode.set(2,ValueKt.mut(false));
		opcode.set(3,ValueKt.mut(false));
		opcode.set(4,ValueKt.mut(false));
		opcode.set(5,ValueKt.mut(false));


		var regDst = ValueKt.mut();
		var ALUsrc = ValueKt.mut();
		var memToReg = ValueKt.mut();
		var regWrite = ValueKt.mut();
		var memRead = ValueKt.mut();
		var memWrite = ValueKt.mut();
		var branch = ValueKt.mut();
		var jump = ValueKt.mut();
		var aluOp = BusKt.bus(2);
		ControlUnit.control(opcode,regDst,ALUsrc,memToReg,regWrite,memRead,memWrite,branch,jump,aluOp);

		System.out.println("regDst: "+regDst);
		System.out.println("ALUsrc: "+ALUsrc);
		System.out.println("memToReg: "+memToReg);
		System.out.println("regWrite: "+regWrite);
		System.out.println("memRead: "+memRead);
		System.out.println("memWrite: "+memWrite);
		System.out.println("branch: "+branch);
		System.out.println("jump: "+jump);
		System.out.println("aluOp: "+aluOp.get(1)+""+aluOp.get(0));


	}
}
