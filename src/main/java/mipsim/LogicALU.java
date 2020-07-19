package mipsim;

import mipsim.units.Compare;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.Variable;
import sim.complex.MuxKt;
import sim.gates.*;
import sim.real.AdderKt;

import java.util.List;

import static sim.gates.GatesKt.*;

public final class LogicALU {
	public static void  thirtyTwoBitOr(List<Value> A,List<Value> B,List<MutableValue> outPut)
	{
		BusKt.set(outPut,or(A,B));
  }
	public static void  thirtyTwoBitNor(List<Value>A,List<Value> B,List<MutableValue>outPut)
	{
			BusKt.set(outPut,nor(A,B));
	}
	public static void  thirtyTwoBitAnd(List<Value> A, List<Value> B, List<MutableValue> outPut)
	{
		BusKt.set(outPut,and(A,B));
	}
	public  static  void  thirtyTwoBitXor(List<Value> A,List<Value> B,List<MutableValue> outPut)
	{
		BusKt.set(outPut,xor(A,B));
	}
	/**
	 * in this function we Make a decision to add or sub
	 * if select variable is true or (1) we do subtract
	 * else we do add
	 * carry out when we need to set carry flag
	 */

	public static void AddSub(List<Value> input1,List<Value> input2,Value select,List<Variable> result,List<Variable> carryOut){
		carryOut.get(0).set(select.get());
		for(int i = 31 ; i >= 0 ; i--) {
			AdderKt.fullAdder(input1.get(i),(xor(input2.get(i),select)),carryOut.get(i),result.get(i),carryOut.get(i+1));
		}
	}
	public static void setLess(List<Value> input1 , List<Value> input2 ,List<MutableValue> result){
		var E = new Variable(false);
		var L = new Variable(false);
		var G = new Variable(false);
		Compare.com32Bit(input1,input2,E,L,G);
		BusKt.set(result,(MuxKt.mux2(L,BusKt.toBus(0,32),BusKt.toBus(1,32))));
		//Todo check it friends
	}


//	public static void AluInStage(List<Value> input1,List<Value> input2,List<Value> function,List<Value> AluControlUnit,List<MutableValue> result,Variable zero){
//
//			var select = BusKt.bus(4);
//			Decod(function,AluControlUnit,select);
//
//			var resAdd = BusKt.bus(32);
//			AddSub(input1,input2,new Variable(false),resAdd,new Variable(false));
//			var resSub = BusKt.bus(32);
//			AddSub(input1,input2,new Variable(true),resSub,new Variable(false));
//			var resOr = BusKt.bus(32);
//			thirtyTwoBitOr(input1,input2,resOr);
//			var resAnd = BusKt.bus(32);
//			thirtyTwoBitAnd(input1,input2,resAnd);
//			var resNor = BusKt.bus(32);
//			thirtyTwoBitNor(input1,input2,resNor);
//			var resShift_R = BusKt.bus(32);
//			ShiftLogical.thirtyTwoBitShifterRight(input1,input2,resShift_R);
//			var resShift_L = BusKt.bus(32);
//			ShiftLogical.thirtyTwoBitShifterLeft(input1,input2,resShift_L);
//			var resSetLes = BusKt.bus(32);
//			setLess(input1,input2,resSetLes);
//			var resXor = BusKt.bus(32);
//
//
//			for (int i = 0 ; i <= 31 ; i++){
//				result.get(i).set(MuxKt.mux(select,resAdd.get(i),resSub.get(i),resOr.get(i),resAnd.get(i),resXor.get(i),resNor.get(i),resShift_L.get(i),resShift_R.get(i)));
//			}
//	}

	private static void Decod(List<Value> function, List<Value> aluControlUnit, List<MutableValue> select) {
		//Todo we must to make new Decoder to make from 6 function and 2 alucontroul 4 bit for multiplexer
	}

}


