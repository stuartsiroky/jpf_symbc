package bfsNode;

public class ConditionNode extends BFSNode {

	String SymbCondition = "";
	
	public ConditionNode(String name, String cond) {
		super(name+"::"+cond);
		SymbCondition = cond;
	}
	
	public String getSymbCondition() {
		return SymbCondition;
	}

	public String toString() {
		return "C{" + super.toString()+ "}C";
	}
}
