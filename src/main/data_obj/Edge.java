package data_obj;


/**
 * @author wiki-algorithms
 *
 */
public class Edge implements Comparable<Edge> {

	final Node from;
	private final Node to;
	final int weight;

	public int getWeight() {
		return weight;
	}

	public Edge(final Node argFrom, final Node argTo, final int argWeight){
		from = argFrom;
		to = argTo;
		weight = argWeight;
	}

	public int compareTo(final Edge argEdge){
		return weight - argEdge.weight;
	}

	public Node getTo() {
		return to;
	}

	public Node getFrom() {
		return from;
	}

	public String toString() {
		return from.toString()+" -> "+to.toString();
	}
	
	public boolean equals(Edge e) {
		return from.equals(e.getFrom()) && to.equals(e.getTo());
	}
}