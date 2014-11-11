// taken from wiki-algorithms
package data_obj;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;

/** 
 * This was taken from wiki-algorithm
 * @author wiki-algorithms
 *
 */
public class AdjacencyList {

	private Map<Node, List<Edge>> adjacencies = new HashMap<Node, List<Edge>>();
	
	public void addEdge(Node source, Node target, int weight){
		List<Edge> list;
		if(!adjacencies.containsKey(source)){
			list = new ArrayList<Edge>();
			adjacencies.put(source, list);
		}else{
			list = adjacencies.get(source);
		}
		Edge e = new Edge(source,target,weight);
		if(!list.contains(e)) {
			list.add(e);
		}
//	    list.add(new Edge(source, target, weight));
	}

	public List<Edge> getAdjacent(Node source){
		return adjacencies.get(source);
	}

	public void reverseEdge(Edge e){
		adjacencies.get(e.from).remove(e);
		addEdge(e.getTo(), e.from, e.weight);
		
	}

	public void reverseGraph(){
		adjacencies = getReversedList().adjacencies;
	}

	public AdjacencyList getReversedList(){
		AdjacencyList newlist = new AdjacencyList();
		for(List<Edge> edges : adjacencies.values()){
			for(Edge e : edges){
				newlist.addEdge(e.getTo(), e.from, e.weight);
			}
		}
		return newlist;
	}

	public Set<Node> getSourceNodeSet(){
		return adjacencies.keySet();
	}

	public Collection<Edge> getAllEdges(){
		List<Edge> edges = new ArrayList<Edge>();
		for(List<Edge> e : adjacencies.values()){
			edges.addAll(e);
		}
		return edges;
	}

	public boolean containsNode(Node n) {
		return adjacencies.containsKey(n);
	}

	public void followReversePath(Node to, Node from, AdjacencyList alist){
		List<Edge> toList = getAdjacent(from);
		if (toList != null) {
			for (Edge e : toList) {
				if (to.equals(e.getTo())) {
					// Add to to the list Stop case
					alist.addEdge(from,to,0);
				}
				else if(e.getTo() != null) {
					followReversePath(to,e.getTo(),alist);
					if(alist.containsNode(e.getTo())) {
						alist.addEdge(from, e.getTo(), 0);
					}
				}
			}
		}
	}

	public AdjacencyList getPath(Node from, Node to) {
		AdjacencyList pathList  = new AdjacencyList();
		reverseGraph();
		followReversePath(from, to, pathList);
		reverseGraph();
		pathList.reverseGraph();
		return pathList;
	}

	public String printFromTo(Node from, Node to) {
		List<Edge> toList = getAdjacent(from);
		String s = "";
		if (toList != null) {
			for (Edge e : toList) {	
				s = from.toString() + " -> " + printFromTo(e.getTo(),to);
			}
		}
		else if(from.equals(to)) {
			s = from.toString();
		}
		else if((toList == null) && (!from.equals(to))) { 
			s = "===NO_PATH===";
		}
		
		return s;
	}

	public String toString() {
		String out = "";
		Set<Node> sourceNodes;
		sourceNodes = getSourceNodeSet();
		for (Node n : sourceNodes) {
			List<Edge> toEdges;
			toEdges = getAdjacent(n);
			for (Edge e : toEdges) {
				out += "\t" + e.toString() + "\n";
			}
			out += "\n";
		}
		return out;
	}
}
