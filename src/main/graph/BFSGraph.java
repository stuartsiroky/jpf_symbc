package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import bfsNode.BFSAdjacencyList;
import bfsNode.BFSEdge;
import bfsNode.BFSNode;
import bfsNode.BFSNode.COLOR;

import java.io.*;

import bfsNode.FunctionNode;
import bfsNode.ConditionNode;

import java.util.regex.*;

public class BFSGraph {

	ArrayList<BFSNode> nodeList = new ArrayList<BFSNode>();
	BFSAdjacencyList adjList = new BFSAdjacencyList();
	private Queue<BFSNode> workListQ = new LinkedList<BFSNode>();

	public BFSGraph() {
	}

	public String toStringPathCond() {
		String path;
		path = "--- Path Contions ---\n";
		for(BFSNode n: nodeList) {
			path+=n.toString()+"\n";
			for(String s: n.getPath_condition()) {
				path+="\t"+s+"\n";
			}
		}
		return path;
	}
	public BFSGraph(ArrayList<BFSNode> nodes, BFSAdjacencyList edges) {
		nodeList = nodes;
		for (BFSNode n : nodes) {
			List<BFSEdge> toList = edges.getAdjacent(n);
			if (toList != null) {
				for (BFSEdge e : toList) {
					BFSNode v = (BFSNode) e.getTo();
					if (nodeList.contains(v)) {
						addEdge(n, (BFSNode) v);
					}
				}
			}
		}
	}

	public boolean contains(BFSNode node) {
		for (BFSNode n : nodeList) {
			if (node.equals(n))
				return true;
		}
		return false;
	}

	public boolean addNode(BFSNode n) {
		if (!contains(n)) {
			nodeList.add(n);
			return true;
		} else {
			BFSNode nn = getNodeMatching(n.getNodeName());
			nn.addWeight();
		}
		return false;
	}

	public boolean removeNode(BFSNode n) {
		return nodeList.remove(n);
	}
	
	public BFSNode createNode(String name) {
		BFSNode n = new BFSNode(name);
		if (!contains(n)) {
			nodeList.add(n);
			return n;
		} else {
			BFSNode nn = getNodeMatching(n.getNodeName());
			nn.addWeight();
			return nn;
		}
	}
	
	public FunctionNode createFNode(String name) {
		FunctionNode n = new FunctionNode(name);
		if (!contains(n)) {
			nodeList.add(n);
			return n;
		} else {
			FunctionNode fn = (FunctionNode) getNodeMatching(n.getNodeName());
			fn.addWeight();
			return fn;
		}
	}
	
	public ConditionNode createCNode(String name, String cond) {
		ConditionNode n = new ConditionNode(name,cond);
		if (!contains(n)) {
			nodeList.add(n);
			return n;
		} else {
			ConditionNode cn = (ConditionNode) getNodeMatching(n.getNodeName());
			cn.addWeight();
			return cn;
		}
	}
	
	public void addEdge(BFSNode from, BFSNode to) {
		adjList.addEdge(from, to, 1);
	}

	public void addEdgeProb(BFSNode from, BFSNode to, int prob) {
		adjList.addEdgeProb(from, to, 1,prob);
	}

	public void removeEdge(BFSEdge edge) {
		adjList.remove(edge);
	}

	public String toString() {
		String out = "";
		for (BFSNode n : nodeList) {
			out += n.toString() + "\n";
		}
		out += "\n\n";
		out += adjList.toString();
		return out;
	}

	public void printPath(BFSNode from, BFSNode to) {
		if (from.equals(to)) {
			System.out.println(from.toString());
		} else if (to.getFirstPredecessor() == null) {
			System.out.println("No path from " + from.toString() + " to "
					+ to.toString());
		} else {
			printPath(from, to.getFirstPredecessor());
			System.out.println(to.toString());
		}
	}

	public boolean pathExists(BFSNode from, BFSNode to) {
		if (from.equals(to)) {
			return true;
		} else if (to.getFirstPredecessor() == null) {
			return false;
		} else {
			return pathExists(from, to.getFirstPredecessor());
		}
	}

	public boolean pathRevExists(BFSNode from, BFSNode to) {
		if (from.equals(to)) {
			return true;
		} else if (to.getFirstNext() == null) {
			return false;
		} else {
			return pathRevExists(from, to.getFirstNext());
		}
	}

	// public void printPath(BFSNode from, BFSNode to) {
	// System.out.println("Stuart printing path from "+from.toString()+" to "+to.toString());
	// AdjacencyList newlist = new AdjacencyList();
	// newlist = adjList.getPath(from, to);
	// System.out.println(newlist.printFromTo(from,to)+"\n");
	// //System.out.println(adjList.getPath(from, to));
	// }

	public void BFSearch(BFSNode startNode) {
		Queue<BFSNode> q = new LinkedList<BFSNode>();
		int cnt = 0;
		initSearch(startNode);
		q.add(startNode);
		while (!q.isEmpty()) {
			BFSNode n = q.remove();
			cnt++;
			List<BFSEdge> toList = adjList.getAdjacent(n);
			if (toList != null) {
				for (BFSEdge e : toList) {
					BFSNode v = (BFSNode) e.getTo();
					if (v.getColor() == COLOR.WHITE) {
						v.setColor(COLOR.GRAY);
						v.setDistance(n.getDistance() + 1);
						v.addPredecessor(n);
						q.add(v);
						workListQ.add(v);
					} // if WHITE
					else {
						v.addPredecessor(n);// add all other predecessors
					}// else if GREY
				} // for edge
			}// not null
			n.setColor(COLOR.BLACK);
		} // !q.isEmpty
		System.out.println("BFSearch nodes visited = " + cnt);
	}

	private void initSearch(BFSNode startNode) {
		for (BFSNode n : nodeList) {
			n.setColor(COLOR.WHITE);
			n.setDistance(-1);
			n.clearPredecessors();
			n.clearNext();
		}
		startNode.setColor(COLOR.GRAY);
		startNode.setDistance(0);
		startNode.clearPredecessors();
		startNode.clearNext();
		workListQ.clear();
	}

	int nodevisitcnt;

	public void FromPath(BFSNode startNode, BFSNode finishNode) {
		// do a BF search and then go backwards on the predecessors of the nodes
		BFSearch(startNode);
		if (pathExists(startNode, finishNode)) {
			nodevisitcnt = 0;
			ArrayList<BFSNode> callTrace = new ArrayList<BFSNode>();
			traceBack(callTrace, finishNode);

			System.out.println("STUART ========\n" + callTrace.toString());
		}
		System.out.println("FromPath nodes visited = " + nodevisitcnt);
	}

	public void traceBack(ArrayList<BFSNode> callTrace, BFSNode finishNode) {
		if (!callTrace.contains(finishNode)) {
			callTrace.add(finishNode);
			nodevisitcnt++;
			ArrayList<BFSNode> fromList = finishNode.getPredecessors();
			for (BFSNode from : fromList) {
				traceBack(callTrace, from);
			}
		}
	}

	public void BFSearchRev(BFSNode finalNode) {
		Queue<BFSNode> q = new LinkedList<BFSNode>();
		System.out.println("Number of node in starting graph "
				+ nodeList.size());
		int cnt = 0;
		initSearch(finalNode);
		q.add(finalNode);
		BFSAdjacencyList RL = adjList.getReversedList();
		while (!q.isEmpty()) {
			BFSNode n = q.remove();
			cnt++;
			List<BFSEdge> fromList = RL.getAdjacent(n);
			if (fromList != null) {
				 //System.out.println(" "+fromList.toString());
				 for (BFSEdge e : fromList) {
					BFSNode v = (BFSNode) e.getTo();
					if (v.getColor() == COLOR.WHITE) {
						v.setColor(COLOR.GRAY);
						v.setDistance(n.getDistance() + 1);
						v.addNext(n);
						q.add(v);
					} // if WHITE
					else {
						v.addNext(n);// add all other predecessors
					}// else if GREY
				} // for edge
			}// not null
			n.setColor(COLOR.BLACK);
		} // !q.isEmpty
		System.out.println("BFSeachRev nodes visited = " + cnt);
		
	}

	public ArrayList<BFSNode> BFSearchStart(BFSNode startNode) {
		Queue<BFSNode> q = new LinkedList<BFSNode>();
		ArrayList<BFSNode> trace = new ArrayList<BFSNode>();
		int cnt = 0;
		// only clear color and dist
		clearColorDist(startNode);

		q.add(startNode);
		trace.add(startNode);
		while (!q.isEmpty()) {
			BFSNode n = q.remove();
			cnt++;
			List<BFSNode> toList = n.getNext();
			if (toList != null) {
				for (BFSNode v : toList) {
					if (v.getColor() == COLOR.WHITE) {
						v.setColor(COLOR.GRAY);
						v.setDistance(n.getDistance() + 1);
						v.addPredecessor(n);
						q.add(v);
						trace.add(v);
						workListQ.add(v);
					} // if WHITE
						// else {
						// v.addPredecessor(n);//add all other predecessors
						// }// else if GREY
				} // for edge
			}// not null
			n.setColor(COLOR.BLACK);
		} // !q.isEmpty
		System.out.println("BFSearchStart nodes visited = " + cnt);
		return trace;
	}

	public void clearColorDist(BFSNode startNode) {
		for (BFSNode n : nodeList) {
			n.setColor(COLOR.WHITE);
			n.setDistance(-1);
		}
		startNode.setColor(COLOR.GRAY);
		startNode.setDistance(0);
	}

	public void clearColor(BFSNode startNode) {
		for (BFSNode n : nodeList) {
			n.setColor(COLOR.WHITE);
		}
		startNode.setColor(COLOR.GRAY);
	}

	public BFSGraph getPaths(BFSNode startNode, BFSNode finalNode) {
		System.out.println("getPaths Starting Graph\n" + toString());
		BFSearchRev(finalNode);
System.out.println("==Reduced Graph \n"+toString());
		if (pathRevExists(finalNode, startNode)) {
			return new BFSGraph(BFSearchStart(startNode), adjList);
		} else {
			System.out.println("FAILURE: failed to find path from "
					+ startNode.toString() + " to " + finalNode.toString());
			return null;
		}
	}

	public int size() {
		return nodeList.size();
	}

	public Queue<BFSNode> getWorkListQ() {
		return workListQ;
	}

	public BFSNode getNodeMatching(String str) {
		BFSNode tmp = new BFSNode(str);
		for (BFSNode n : nodeList) {
			if (n.equals(tmp))
				return n;
		}
		return null;
	}

	public void write_to_file(String outfile) throws IOException {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(outfile));
			out.print(this.toString());
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public void read_from_file(String infile) throws IOException {
		BufferedReader in = null;

		try {
			in = new BufferedReader(new FileReader(infile));
			String text = in.readLine();
			while(text != null) {
				if(matchesTrans(text)) {
				}
				else if(matchesFNode(text)) {
				}
				else if(matchesCNode(text)) {
				}
				text = in.readLine();
			}
			System.out.println("READIN\n"+toString());
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	private boolean matchesFNode(String str) {
		Pattern p = Pattern.compile("F\\{\\s+(?<node1>.*)\\s+\\}F");
		Matcher m;
		m = p.matcher(str);
		if(m.matches()) {
			createFNode(m.group("node1"));
			//System.out.println("matches function node "+m.group("node1"));	
			return true;
		}
		else {
			return false;
		}
	}

	private boolean matchesCNode(String str) {
		Pattern p = Pattern.compile("C\\{\\s+(?<node1>.*)\\s+\\}C");
		Matcher m;
		m = p.matcher(str);
		if(m.matches()) {
			String s[] = (m.group("node1").split("::"));
			createCNode(s[0],s[1]);
			//System.out.println("matches condition node "+m.group("node1"));	
			return true;
		}
		else {
			return false;
		}
	}

	private boolean matchesTrans(String str) {
		String n1 = "[FC]\\{\\s+(?<node1>.*)\\s+\\}[FC]";
		String n2 = "[FC]\\{\\s+(?<node2>.*)\\s+\\}[FC]";
		String regex = "\\s+"+n1+"\\s+->\\s+"+n2+"";
		Pattern p = Pattern.compile(regex);
		Matcher m;
		m = p.matcher(str);
		if(m.matches()) {
			BFSNode from = getNodeMatching(m.group("node1"));
			BFSNode to   = getNodeMatching(m.group("node2"));
			if((from != null) && (to != null)) {
				addEdge(from,to);
				//System.out.println("matches transition "+m.group("node1")+" -> "+m.group("node2"));	
				return true;
			} else {
				System.out.println("One or more mode not found for transition.");
				return false;
			}
		}
		else {
			return false;
		}
	}

	public boolean compare(BFSGraph cTo) {
		boolean result = true;
		Collection<BFSEdge> eF = adjList.getAllEdges();
		Collection<BFSEdge> eT = cTo.adjList.getAllEdges();

		if(size() != cTo.size()) {
			result = false;
			System.out.println("Number of nodes not the same.\n"
					+size()+ " Actual "+cTo.size()+"\n");
		}
		else {
			for(BFSNode n: nodeList) {
				if(!cTo.contains(n)) {
					result = false;
					System.out.println("Node "+n.toString()+ " is not contained in actual graph\n");
				}
			}
		}
		if(eF.size() != eT.size()) {
			result = false;
			System.out.println("Number of edges not the same.\n"
					+eF.size()+ " Actual "+eT.size()+"\n");
		} else {
			for(BFSEdge e: eF) {
				if(!eT.contains(e)){
					result = false;
					System.out.println("Edge "+e.toString()+" is not contained in actual.\n");
				}
			}
		}
		return result;
	}

}
