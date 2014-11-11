package data_obj;

/**
 * @author wiki-algorithm
 *
 */
public class Node implements Comparable<Node> {

	final int name;
	boolean visited = false;   // used for Kosaraju's algorithm and Edmonds's algorithm
	private int lowlink = -1;          // used for Tarjan's algorithm
	private int index = -1;            // used for Tarjan's algorithm

	public Node(final int argName) {
		name = argName;
	}

	public int compareTo(final Node argNode) {
		return argNode == this ? 0 : -1;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setLowlink(int lowlink) {
		this.lowlink = lowlink;
	}

	public int getLowlink() {
		return lowlink;
	}
	
	public void resetNode() {
		visited = false;   // used for Kosaraju's algorithm and Edmonds's algorithm
		lowlink = -1;          // used for Tarjan's algorithm
		index = -1;            // used for Tarjan's algorithm	
	}
	
	public int getName() {
		return name;
	}

}
