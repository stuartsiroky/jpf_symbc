package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//import java.util.Queue;
import java.util.Set;

import data_obj.*;
import bfsNode.*;
import bfsNode.BFSNode.COLOR;

public class PathSearch {
	private BFSGraph graph;
	int nodesVisited, dseNodeCnt;
	private ArrayList<BFSNode> workList = new ArrayList<BFSNode>();
	private LinkedList<BFSNode> workListQ = new LinkedList<BFSNode>();
	private BFSNode endFunction;
	private Map<BFSNode, ArrayList<Path>> pathMap = new HashMap<BFSNode, ArrayList<Path>>();
	private Map<Edge, ArrayList<Path>> fpathMap = new HashMap<Edge, ArrayList<Path>>();

	public PathSearch(BFSGraph b) {
		graph = b;
		nodesVisited = 0;
		workList.clear();
		workListQ.clear();
		endFunction = null;
	}

	private void prevSearch(BFSNode n) {
		graph.BFSearch(n);
	}

	public void printPred() {
		String str = "";
		for (BFSNode n : graph.nodeList) {
			str = "Node " + n.toString();
			ArrayList<BFSNode> plist = n.getPredecessors();
			for (BFSNode p : plist) {
				str += " Pred " + p.toString();
			}
			System.out.println(str);
		}
	}

	public void PrintNxt() {
		String str = "";
		for (BFSNode n : graph.nodeList) {
			str = "Node " + n.toString();
			ArrayList<BFSNode> nlist = n.getNext();
			for (BFSNode p : nlist) {
				str += " Nxt " + p.toString();
			}
			System.out.println(str);
		}
	}

	// private boolean checkPath(ArrayList<BFSNode> path) {
	// FIXME int curr_prop = -1;
	// FIXME for (BFSNode n : path) {
	// FIXME if (n.getName() > curr_prop) {
	// FIXME curr_prop = n.getName();
	// FIXME } else {
	// FIXME return false;
	// FIXME }
	// FIXME }
	// return true;
	// }

	/*
	 * private boolean followNxt(ArrayList<BFSNode> path) { BFSNode last =
	 * path.get(path.size() - 1); List<BFSNode> toList = last.getNext(); boolean
	 * rtn_val = true; nodesVisited++; if (toList.size() != 0) { for (BFSNode v
	 * : toList) { if (rtn_val == true) { path.add(v); if (checkPath(path)) {
	 * rtn_val = followNxt(path); } else { System.out.println("FAILING PATH" +
	 * path.toString()); nodesVisited++; rtn_val = false; return rtn_val; } } }
	 * // for list } // tolist == null else { System.out.println("PASSING PATH"
	 * + path.toString()); } // remove the last item. path.remove(path.size() -
	 * 1); return rtn_val;
	 * 
	 * }
	 * 
	 * public boolean seachCheckPath(BFSNode startNode) {
	 * System.out.println("seachCheckPath number of nodes in graph " +
	 * graph.size()); ArrayList<BFSNode> list = new ArrayList<BFSNode>(); // add
	 * the first element if (graph.contains(startNode)) { list.add(startNode);
	 * if (!followNxt(list)) { // print failing path
	 * System.out.println("seachCheckPath nodes visited = " + nodesVisited);
	 * return false; } else {
	 * System.out.println("seachCheckPath nodes visited = " + nodesVisited);
	 * return true; } } // graph contain start else { System.out
	 * .println("seachCheckPath Recursive and Non-Optimal nodes visited = " +
	 * nodesVisited); return false; }
	 * 
	 * } // searchCheckPath
	 */

	// ///////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////
	// // Directed symbolic execution algorithm ////
	// ///////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////

	public void DSE(BFSNode finalNode, BFSNode startNode) {
		if (graph != null) {
			System.out.println("DSE number of nodes in graph " + graph.size());
			workListQ.clear();
			workList.clear();
			dseNodeCnt = 0;
			if (graph.contains(finalNode) && graph.contains(startNode)) {
				prevSearch(startNode); // do this since we did not do our prev
										// search of the graph can be removed if
										// already searched
				// printPred();
				graph.clearColor(startNode);
				endFunction = finalNode;
				// FIXME createWorklist(finalNode);
				addCallersWorklist(finalNode);
				while (!isWorklistEmpty()) {
					BFSNode n = workList.remove(0);
					// BFSNode n = workListQ.remove();
					n.setColor(COLOR.GRAY);
					manageTargets(n);
					// System.out.println("DSE worklist  = "+workList.toString());
					// System.out.println("DSE worklistQ = " +
					// workListQ.toString());
					// System.out.println("DSE pathMap = " +
					// pathMap.toString());
					n.setColor(COLOR.BLACK);
				}
				System.out.println("STUART DSE nodes Visited = " + dseNodeCnt);
				System.out.println("Feasible Paths from "
						+ startNode.toString() + " to " + finalNode.toString());
				printPaths(startNode);
			}
		} else {
			System.out.println(" No path found between " + startNode.toString()
					+ " and " + finalNode.toString());
		}
	}

	// private void createWorklist(BFSNode finalNode) {
	// Queue<BFSNode> tmp_wl = graph.getWorkListQ();
	// while (!tmp_wl.isEmpty()) {
	// BFSNode n = tmp_wl.remove();
	// if (!n.equals(finalNode)) {
	// workListQ.addFirst(n);
	// }
	// }
	// workListQ.addFirst(finalNode);
	// System.out.println("STUART Qworklist = " + workListQ.toString());
	//
	// }

	public void printPaths(BFSNode n) {
		int num_paths = 0;
		int path_length = 0;
		int longest_path = 0;
		int shortest_path = 2 ^ 32;
		if (pathMap.containsKey(n)) {
			ArrayList<Path> paths = pathMap.get(n);
			for (Path p : paths) {
				int plength = p.get_PathLength();
				num_paths++;
				path_length += plength;
				if (plength > longest_path) {
					longest_path = plength;
				}
				if (plength < shortest_path) {
					shortest_path = plength;
				}
				System.out.println("Path Length = " + plength + "\n"
						+ p.toString() + "\n\tPath Condition:: "
						+ p.getCondition());
			}
		}
		System.out.println("\tNumber of Paths Found = " + num_paths);
		System.out.println("\tLongest Path          = " + longest_path);
		System.out.println("\tShortest Path         = " + shortest_path);
		System.out.println("\tAve Path Length       = " + path_length
				/ num_paths);
	}

	/*********************************************************************
	 * manage_targets() paths = get_paths(node); foreach(path: paths) f =
	 * path.path_end(); if(path.path_end() = startnode) //final method we want
	 * to reach update_paths(node,paths) else if(f.functionCall() and
	 * f.haspaths()) paths_prime = get_paths(f) if(feasible(paths+paths_prime))
	 * update(node,paths)
	 * 
	 * update_paths(f,p) add_path(f,p) c = callers(f) foreach(caller : c)
	 * if(!c.haspaths()) worklist.add(c)
	 * 
	 *************************************************************************/

	private boolean pathFeasible(BFSNode n, Path p) {
		boolean result = true;
		// FIXME ArrayList<BFSNode> pp = p.getPath();
		// FIXME for (BFSNode nn : pp) {
		// FIXME result &= nn.condition();
		// FIXME }
		// FIXME result &= n.condition();
		// System.out.println("STUART pathFeasible from "+n.toString()+
		// " = "+result);

		return result;
	}

	/*
	 * private ArrayList <Path> buildPathFrom(BFSNode from) { ArrayList<Path>
	 * paths = new ArrayList<Path>(); ArrayList<Path> fpaths;
	 * 
	 * if(hasPath(from)) { fpaths = pathMap.get(from); //
	 * System.out.println("updatePaths-hasPath"); //need to keep calling
	 * if(fpaths.size()>1){ // copy current path the number of times into the
	 * array for(Path p: fpaths) { paths.add(cpath); } for(Path p: paths) {
	 * 
	 * } } else {
	 * 
	 * } } else { // if the node is not in the hashmap it must be an end node
	 * Path cpath = new Path(); cpath.addPath(from); paths.add(cpath); }
	 * 
	 * return paths; }
	 */

	private void updatePaths(BFSNode from, BFSNode to, Path topath) {
		// System.out.println("update from "+from.toString()+" to "+to.toString()+" path "+topath.toString());
		ArrayList<Path> paths = new ArrayList<Path>();
		if (hasPath(from)) {
			paths = pathMap.get(from);
			// System.out.println("updatePaths-hasPath");
		}
		Path npath = new Path(topath);
		npath.addPath(from);
		boolean contains_path = false;
		for (Path p : paths) {
			if (p.equals(npath))
				contains_path = true;
		}
		if (!contains_path) {
			paths.add(npath);
			pathMap.put(from, paths);
			addCallersWorklist(from);
		}

	}

	private void createPath(BFSNode from, BFSNode topath) {
		ArrayList<Path> paths = new ArrayList<Path>();
		if (!hasPath(from)) {
			Path p = new Path(); // create new path
			p.addPath(topath);
			if (pathFeasible(from, p)) {
				p.addPath(from);
				paths.add(p);
				// only add the callers that are not currently in the pathMap.
				addCallersWorklist(from);
			}
			pathMap.put(from, paths);
		}
	}

	private boolean hasPath(BFSNode n) {
		return pathMap.containsKey(n);
	}

	private void manageTargets(BFSNode n) {
		List<BFSNode> fromList = getPaths(n);
		dseNodeCnt++;
		System.out.println("manage_targets cnt =" + dseNodeCnt + " "
				+ n.toString());
		if (fromList.size() != 0) {
			for (BFSNode v : fromList) { // initial paths are the tolist
				// if(end path of n == endFunction) updatePath()
				if (n.equals(endFunction)) {
					// System.out.println("createPath " + n.toString());
					createPath(v, n);
				}
				// else if(end path of n = callTo(f) && hasPath(f))
				else if (hasPath(n)) {
					// System.out.println("manageTargets-hasPath " +
					// n.toString());
					ArrayList<Path> ppaths = pathMap.get(n);
					for (Path p : ppaths) {
						if (pathFeasible(v, p)) {
							// System.out.println("manageTargets-updatePaths"+v.toString()+n.toString()+" path "+p.toString());
							updatePaths(v, n, p);
						}
					}
				}
			}
		}
	}

	private ArrayList<BFSNode> getPaths(BFSNode n) {
		return n.getPredecessors();
	}

	private void addCallersWorklist(BFSNode n) {
		/*
		 * if (n.getColor() == COLOR.WHITE) { // FIXME?? // you want to add in
		 * order of distance // the highest distance needs to be put at the head
		 * of the q boolean Inserted = false; for (int i = 0; i <
		 * workListQ.size() && !Inserted; i++) { BFSNode tnode =
		 * workListQ.get(i); if (n.getDistance() > tnode.getDistance()) {
		 * workListQ.add(i, n); Inserted = true; } } if (!Inserted) {
		 * workListQ.addLast(n); } }
		 */
		workList.add(n);
	}

	private boolean isWorklistEmpty() {
		// return workListQ.isEmpty();
		return workList.isEmpty();
	}

	public void createF2FPath() {
		Set<BFSNode> pkeys = pathMap.keySet();

		for (Iterator<BFSNode> iterator = pkeys.iterator(); iterator.hasNext();) {
			BFSNode n = iterator.next();
			if (n instanceof FunctionNode) {
				ArrayList<Path> paths = pathMap.get(n);
				for (Path p : paths) {
					ArrayList<BFSNode> rp = p.ReversedPath();
					Path np = new Path();
					BFSNode tmpnode = null;
					BFSNode first = null;
					BFSNode last = null;
					if (!rp.isEmpty()) {
						tmpnode = rp.remove(0);
						first = tmpnode;
					}
					boolean isFunction = false;

					while (!rp.isEmpty() && !isFunction) {
						np.addPath(tmpnode);
						tmpnode = rp.remove(0);
						last = tmpnode;
						isFunction = (tmpnode instanceof FunctionNode);
					}
					if (!np.isEmpty()) {
						Edge e = new Edge(first, last, 1);
						np.addPath(last);
						add2FPath(e, np);
					}
				} // for (Path p : paths)
			} // if (n instanceof FunctionNode)
		} // for (Iterator<BFSNode> iterator = pkeys.iterator
	}

	private void add2FPath(Edge key, Path p) {
		// private Map<Edge, ArrayList<Path>> fpathMap = new HashMap<Edge,
		// ArrayList<Path>>();
		ArrayList<Path> alp;
		if (fpathMap.containsKey(key)) {
			// get array list of paths and add the path to it
			alp = fpathMap.get(key);
		} else {
			// create and array list of paths and add the element to the hash
			// map
			alp = new ArrayList<Path>();
		}
		alp.add(p);
		fpathMap.put(key, alp);
	}

	public void printF2FPath() {
		System.out.println(fpathMap.toString()); // TODO Auto-generated method
													// stub

	}
}

// ///////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////
// // Directed symbolic execution algorithm take 2 ////
// ///////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////
// ///////////////////////////////////////////////////////
// use the fpathMap instead of the pathMap //
