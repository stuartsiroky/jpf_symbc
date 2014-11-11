package bfsNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BFSAdjacencyList {

	private Map<BFSNode, List<BFSEdge>> adjacencies = new HashMap<BFSNode, List<BFSEdge>>();
	
	public void addEdge(BFSNode source, BFSNode target, int weight){
		List<BFSEdge> list;
		if(!adjacencies.containsKey(source)){
			list = new ArrayList<BFSEdge>();
			adjacencies.put(source, list);
		}else{
			list = adjacencies.get(source);
		}
		BFSEdge e = new BFSEdge(source,target,weight);
		if(!list.contains((BFSEdge) e)) {
			list.add(e);
		}
		else {
			int indx = list.indexOf(e);
			BFSEdge ee = list.remove(indx);
			ee.addProb();
			list.add(ee);	
		}
	}

	public void addEdgeProb(BFSNode source, BFSNode target, int weight, int prob) {
		this.addEdge(source,target,weight);
		BFSEdge e = new BFSEdge(source,target,weight);
		List<BFSEdge> list = adjacencies.get(source);
		int indx = list.indexOf(e);
		BFSEdge ee = list.remove(indx);
		ee.setProb(prob);
		list.add(ee);			
	}
	
	public void remove(BFSEdge edge) {
		List<BFSEdge> list;
		BFSNode from = edge.getFrom();
		if(adjacencies.containsKey(from)){
			list = adjacencies.get(from);
			list.remove(edge);
			adjacencies.put(from, list);
		}
	}
	
	public List<BFSEdge> getAdjacent(BFSNode source){
		if(adjacencies.containsKey(source)) {
			return adjacencies.get(source);
		}
		else {
			return new ArrayList<BFSEdge>();
		}
	}

	public void reverseEdge(BFSEdge e){
		adjacencies.get(e.from).remove(e);
		addEdge(e.getTo(), e.from, e.weight);
		
	}

	public void reverseGraph(){
		adjacencies = getReversedList().adjacencies;
	}

	public BFSAdjacencyList getReversedList(){
		BFSAdjacencyList newlist = new BFSAdjacencyList();
		for(List<BFSEdge> edges : adjacencies.values()){
			for(BFSEdge e : edges){
				newlist.addEdge(e.getTo(), e.getFrom(), e.weight);
			}
		}
		return newlist;
	}

	public Set<BFSNode> getSourceNodeSet(){
		return adjacencies.keySet();
	}

	public Collection<BFSEdge> getAllEdges(){
		List<BFSEdge> edges = new ArrayList<BFSEdge>();
		for(List<BFSEdge> e : adjacencies.values()){
			edges.addAll(e);
		}
		return edges;
	}

	public boolean containsNode(BFSNode n) {
		return adjacencies.containsKey(n);
	}

	public void followReversePath(BFSNode to, BFSNode from, BFSAdjacencyList alist){
		List<BFSEdge> toList = getAdjacent(from);
		if (toList != null) {
			for (BFSEdge e : toList) {
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

	public BFSAdjacencyList getPath(BFSNode from, BFSNode to) {
		BFSAdjacencyList pathList  = new BFSAdjacencyList();
		reverseGraph();
		followReversePath(from, to, pathList);
		reverseGraph();
		pathList.reverseGraph();
		return pathList;
	}

	public String printFromTo(BFSNode from, BFSNode to) {
		List<BFSEdge> toList = getAdjacent(from);
		String s = "";
		if (toList != null) {
			for (BFSEdge e : toList) {	
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
		Set<BFSNode> sourceNodes;
		sourceNodes = getSourceNodeSet();
		for (BFSNode n : sourceNodes) {
			List<BFSEdge> toEdges;
			toEdges = getAdjacent(n);
			for (BFSEdge e : toEdges) {
				out += "\t" + e.toString() + "\n";
			}
			//out += "\n";
		}
		return out;
	}


}
