package bfsNode;

public class FunctionNode extends BFSNode {

	public FunctionNode(String name) {
		super(name);
	}
	public String toString() {
		return "F{" + super.toString()+ "}F";
	}
}
