package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReferenceType;

import bfsNode.BFSEdge;
import bfsNode.BFSNode;
import bfsNode.FunctionNode;

public class GraphGenerator {

	// any interfaces (calls) found will be added to this list so we can replace
	// with actual implementation later.
	private ArrayList<String> interfaces_used = new ArrayList<String>();
	// when we encounter a class that implements an interface we will add that
	// here to help us
	// fix up the graph if any unimplemented interfaces are in the graph
	// the key is the interface and a list of what implements that interface
	private Map<String, List<String>> implemented_interfaces = new HashMap<String, List<String>>();

	public void createCFG(BFSGraph cfg, String className)
			throws ClassNotFoundException {

		JavaClass jc = Repository.lookupClass(className);
		getInterfaces(className);
		ClassGen cg = new ClassGen(jc);
		// ConstantPool cp = jc.getConstantPool();
		ConstantPoolGen cpg = cg.getConstantPool();
		for (Method m : cg.getMethods()) {
			MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
			// System.out.println("+++++++++++++++++++++++++++++++++++++");//STUART
			// System.out.println(cg.getClassName()+"."+m.getName()+m.getSignature());//STUART
			InstructionList il = mg.getInstructionList();
			InstructionHandle[] handles = il.getInstructionHandles();
			String fNodeName = cg.getClassName() + "." + m.getName()
					+ m.getSignature();
			FunctionNode fromNode = new FunctionNode(fNodeName);
			BFSNode fNode;
			if (cfg.addNode(fromNode)) {
				System.out.println("ADDED node " + fromNode.toString());
			}
			fNode = cfg.getNodeMatching(fNodeName);

			for (InstructionHandle ih : handles) {
				Instruction insn = ih.getInstruction();

				if (insn instanceof InvokeInstruction) {
					InvokeInstruction ii = (InvokeInstruction) insn;
					ReferenceType rt = ii.getReferenceType(cpg);
					String cname = rt.toString();
					String methname = ii.getMethodName(cpg);
					String signame = ii.getSignature(cpg);
					String tNodeName = cname + "." + methname + signame;
					// System.out.println("    " + cname + "." + methname
					// + signame);// STUART
					if (ii instanceof INVOKEINTERFACE) {
						// INVOKEINTERFACE inter = (INVOKEINTERFACE) ii;
						interfaces_used.add(tNodeName);
						// System.out.println("==INTERFACE " + tNodeName);
						// //STUART
					}
					FunctionNode toNode = new FunctionNode(tNodeName);
					BFSNode tNode;
					if (cfg.addNode(toNode)) {
						System.out.println("ADDED node " + toNode.toString());
					} else {
						// to = cfg.getNodeMatching(tNodeName);
						// System.out.println("ALREADY EXISTS NODE " +
						// tNodeName);
					}
					// cfg.addNode(toNode);
					tNode = cfg.getNodeMatching(tNodeName);
					cfg.addEdge(fNode, tNode);
				}
			} // handles
		} // methods
	} // createCFG

	private void getInterfaces(String className) throws ClassNotFoundException {
		JavaClass jcinterfaces[] = Repository.getInterfaces(className);
		for (JavaClass j : jcinterfaces) {
			JavaClass[] ji = j.getAllInterfaces();
			// System.out.println("Interfaces====\n" + j.toString()+"\n=====");
			for (JavaClass i : ji) {
				for (Method m : i.getMethods()) {
					String i_name = i.getClassName();
					String m_name = "." + m.getName() + m.getSignature();
					insert_interface_pair(i_name + m_name, className + m_name);
					// System.out.println("3 "+ i_name+m_name);
				}
				// System.out.println("Each==\n"+i.toString()+"\n==");
			} // i: ji
		} // j: jcinterfaces
			// print_impl_interface();
	}

	private void insert_interface_pair(String intf, String impl) {
		List<String> list;
		if (!implemented_interfaces.containsKey(intf)) {
			list = new ArrayList<String>();
			list.add(impl);
			implemented_interfaces.put(intf, list);
		} else {
			list = implemented_interfaces.get(intf);
			if (!list.contains(impl)) {
				list.add(impl);
				implemented_interfaces.put(intf, list);
			}
		}
	}

	// private void print_impl_interface() {
	// Set <String> intf_set = implemented_interfaces.keySet();
	// for(String ss: intf_set) {
	// List <String> impl_list = implemented_interfaces.get(ss);
	// System.out.println("==INTERFACE== "+ss);//STUART
	// for(String si: impl_list) {
	// System.out.println("\tIMPL== "+si);//STUART
	// }
	// }
	// }

	public void cleanup_any_pure_interface(BFSGraph cfg) {
		for (String i_used : interfaces_used) {
			if (implemented_interfaces.containsKey(i_used)) {
				BFSNode intfNode = cfg.getNodeMatching(i_used);
				List<String> s_list = implemented_interfaces.get(i_used);
				for (String s_im : s_list) {
					System.out.println("\tREPLACE " + i_used + " with " + s_im);
					// create the node for s_im
					FunctionNode iNode = new FunctionNode(s_im);
					cfg.addNode(iNode);
					BFSNode implNode = cfg.getNodeMatching(s_im);
					// get all the edges from i_used and to i_used
					// List<BFSEdge> e_from = cfg.adjList.getAdjacent(intfNode);

					Collection<BFSEdge> e_all = cfg.adjList.getAllEdges();
					ArrayList<BFSEdge> e_from = new ArrayList<BFSEdge>();
					ArrayList<BFSEdge> e_to = new ArrayList<BFSEdge>();
					// create edges that match the list to and from i_used
					// if (e_from != null) {
					// for (BFSEdge e : e_from) {
					// BFSNode to = e.getTo();
					//
					// cfg.addEdge(implNode, to);
					// }
					// }
					if (!e_all.isEmpty()) {
						for (BFSEdge e : e_all) {
							int e_prob = e.getProb();
							BFSNode to = e.getTo();
							BFSNode from = e.getFrom();
							int to_prob = to.getWeight();
							int from_prob = from.getWeight();
							if (to.equals(intfNode)) {
								intfNode.setWeight(to_prob);
								e_to.add(e);
								cfg.addEdgeProb(from, implNode,e_prob);
								
							}
							if (from.equals(intfNode)) {
								intfNode.setWeight(from_prob);
								e_from.add(e);
								cfg.addEdgeProb(implNode, to,e_prob);
							}
						}
					}
					// remove the i_used node and edges
					cfg.removeNode(intfNode);
					if (!e_from.isEmpty()) {
						for (BFSEdge e : e_from) {
							cfg.removeEdge(e);
						}
					}
					if (!e_to.isEmpty()) {
						for (BFSEdge e : e_to) {
							cfg.removeEdge(e);
						}
					}
				}
			}
		}// i_used: interfaces_used
	}

}
