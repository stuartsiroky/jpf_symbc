package bfsNode;

public class BFSEdge implements Comparable<BFSEdge> {

	final BFSNode from;
	private final BFSNode to;
	final int weight;
	private int prob = 0;
	
	public int getWeight() {
		return weight;
	}

	public BFSEdge(final BFSNode argFrom, final BFSNode argTo, final int argWeight){
		from = argFrom;
		to = argTo;
		weight = argWeight;
		prob++;
	}

	public int compareTo(final BFSEdge argEdge){
		return weight - argEdge.weight;
	}

	public BFSNode getTo() {
		return to;
	}

	public BFSNode getFrom() {
		return from;
	}

	public String toString() {
		return from.toString()+" -> "+to.toString();
	}
	
	public boolean equals(Object e) {
		return equals((BFSEdge)e);
	}
	
	public boolean equals(BFSEdge e) {
		return from.equals(e.getFrom()) && to.equals(e.getTo());
	}

	public int getProb() {
		return prob;
	}

	public void setProb(int prob) {
		this.prob = prob;
	}
	
	public void addProb() {
		this.prob++;
	}
	
}