package mipsim;

import sim.base.Variable;
import sim.gates.AndGate;
import sim.gates.NotGate;
import sim.gates.OrGate;

public class Com2bit {
	private Variable firstBit;
	private Variable secondBit;
	private boolean less;
	private boolean bigger;
	private boolean equal;

	public Com2bit() {
		this.firstBit = new Variable(false);
		this.secondBit = new Variable(false);
		this.bigger = false;
		this.less = false;
		this.equal = true;
	}
	public Com2bit(Variable firstBit, Variable secondBit) {
		this.firstBit = firstBit;
		this.secondBit = secondBit;
		findSituation();
	}

	public boolean isLess() {
		return less;
	}
	public boolean isBigger() {
		return bigger;
	}
	public void setFirSec(Variable firstBit , Variable secondBit){
			this.firstBit = firstBit;
			this.secondBit = secondBit;
			findSituation();
	}

	private void findSituation() {
		this.less = new AndGate(new Variable(new NotGate(this.firstBit)),this.secondBit).get();
		this.bigger = new OrGate(new AndGate(new Variable(new NotGate(this.firstBit)),new Variable(new NotGate(this.secondBit))), new AndGate(this.firstBit,this.secondBit)).get();
		this.equal = new AndGate(this.firstBit,new Variable(new NotGate(this.secondBit).get())).get();
	}
}
