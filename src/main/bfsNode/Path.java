package bfsNode;

import java.util.ArrayList;

public class Path {
	private ArrayList<BFSNode> path = new ArrayList<BFSNode> ();
	private String condition;
	private int pindex = 0;
	
	public Path() {
		path.clear();
		condition = "true";
	}
	
	public Path(Path p) {
		path.clear();
		condition = "true";
		for(BFSNode n: p.getPath()) {
			path.add(n);
			buildCondition(n);
		}
	}

	private void buildCondition(BFSNode p) {
		if(p instanceof ConditionNode) {
			condition += " & "+((ConditionNode) p).getSymbCondition();
		}
	}
	
	public ArrayList<BFSNode> getPath() {
		return path;
	}
	
	public void setPath(ArrayList<BFSNode> path) {
		this.path = path;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void addPath(BFSNode p){
		if(!path.contains(p)){
			path.add(p);
			buildCondition(p);
		}
	}

	public boolean isEmpty() {
		return path.isEmpty();
	}
	
	public String toString() {
		String str = "";
		int cnt = 0;
		for(BFSNode n: path) {
			if(cnt != 0) str += " <- ";
			str += n.toString();
			cnt++;
		}
		return str;
	}

	public boolean equals(Path p) {
		if(path.size() != p.getPath().size()) return false;
		ArrayList<BFSNode> pl = p.getPath();
		for(int i=0;i<path.size();i++) {
			if(!path.get(i).equals(pl.get(i))) return false;
		}
		return true;
	}
	
	public ArrayList<BFSNode> ReversedPath() {
		ArrayList<BFSNode> rpath = new ArrayList<BFSNode>();
		for(int i=path.size()-1;i>=0;i--) {
			rpath.add(path.get(i));
		}
		return rpath;
	}

	public BFSNode getNxtPathNode() {
		if(!isEmpty() && (path.size()<pindex)) {
			return path.get(pindex);
		} 
		else {
			return null;
		}
	}
	
	public BFSNode getFirstPathNode() {
		pindex = 0;
		return getNxtPathNode();
	}
	
	public int get_PathLength() {
		return path.size();
	}

}
