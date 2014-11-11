//
//Copyright (C) 2007 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
package gov.nasa.jpf.symbc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ChoicePoint;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.SystemState;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.jvm.bytecode.ARETURN;
import gov.nasa.jpf.jvm.bytecode.DRETURN;
import gov.nasa.jpf.jvm.bytecode.FRETURN;
import gov.nasa.jpf.jvm.bytecode.IRETURN;
import gov.nasa.jpf.jvm.bytecode.JVMInvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.LRETURN;
import gov.nasa.jpf.jvm.bytecode.JVMReturnInstruction;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.report.PublisherExtension;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.bytecode.BytecodeUtils;
import gov.nasa.jpf.symbc.bytecode.INVOKESTATIC;
import gov.nasa.jpf.symbc.concolic.PCAnalyzer;

//import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.RealConstant;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
//import gov.nasa.jpf.symbc.numeric.SymbolicReal;

import gov.nasa.jpf.symbc.numeric.SymbolicConstraintsGeneral;
//import gov.nasa.jpf.symbc.numeric.SymbolicInteger;

import gov.nasa.jpf.util.Pair;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jimpleParser.JimpleParser;
import graph.*;
import bfsNode.*;

public class BSFSymbolicListener extends PropertyListenerAdapter implements
		PublisherExtension {

	/*
	 * Locals to preserve the value that was held by JPF prior to changing it in
	 * order to turn off state matching during symbolic execution no longer
	 * necessary because we run spf stateless
	 */
	  // set if we replay a trace
	  ChoicePoint trace;
	  // start the search when reaching the end of the stored trace. If not set,
	  // the listener will just randomly select single choices once the trace
	  // got processed
	  boolean searchAfterTrace;
	  boolean singleChoice = true;
	  
	Stack<BFSNode> MethodCallStack = new Stack<BFSNode>();// added
	BFSNode CalleeMethod;// added
	String currMethodName;
	BFSGraph Bgraph = new BFSGraph(); // added
	BFSGraph GMgraph = new BFSGraph();
	boolean useGM = false;
	ArrayList<BFSNode> path = new ArrayList<BFSNode>();

	String startNodeName = "";
	String finalNodeName = "";
	String searchPrefix = "";
	String[] fileList = new String[0];

	long origStartTime = 0;
	long startTime = 0;
	long stopTime = 0;
	long graphReadTime = 0;
	long graphJPFTime = 0;

	private Map<String, MethodSummary> allSummaries;
	private String currentMethodName = "";

	public BSFSymbolicListener(Config conf, JPF jpf) {
		jpf.addPublisherExtension(ConsolePublisher.class, this);
		allSummaries = new HashMap<String, MethodSummary>();
		startNodeName = "testCase.InfeasablePath.start(I)V";
		finalNodeName = "testCase.InfeasablePath.foo_bar()V";
		searchPrefix = "testCase";
		
	    VM vm = jpf.getVM();
	    Search s = jpf.getSearch();
	   
	    //trace = ChoicePoint.readTrace(conf.getString("choice.use_trace"), vm.getSUTName());
	
	}

	@Override
	public void propertyViolated(Search search) {

		VM vm = search.getVM();

		ChoiceGenerator<?> cg = vm.getChoiceGenerator();
		if (!(cg instanceof PCChoiceGenerator)) {
			ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();
			while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
				prev_cg = prev_cg.getPreviousChoiceGenerator();
			}
			cg = prev_cg;
		}
		if ((cg instanceof PCChoiceGenerator)
				&& ((PCChoiceGenerator) cg).getCurrentPC() != null) {
			PathCondition pc = ((PCChoiceGenerator) cg).getCurrentPC();
			String error = search.getLastError().getDetails();
			error = "\"" + error.substring(0, error.indexOf("\n")) + "...\"";
			// C: not clear where result was used here -- to review
			// PathCondition result = new PathCondition();
			// IntegerExpression sym_err = new SymbolicInteger("ERROR");
			// IntegerExpression sym_value = new SymbolicInteger(error);
			// result._addDet(Comparator.EQ, sym_err, sym_value);
			// solve the path condition, then print it
			// pc.solve();
			if (SymbolicInstructionFactory.concolicMode) {
				SymbolicConstraintsGeneral solver = new SymbolicConstraintsGeneral();
				PCAnalyzer pa = new PCAnalyzer();
				pa.solve(pc, solver);
			} else
				pc.solve();

			Pair<String, String> pcPair = new Pair<String, String>(
					pc.toString(), error);// (pc.toString(),error);

			// String methodName =
			// vm.getLastInstruction().getMethodInfo().getName();
			MethodSummary methodSummary = allSummaries.get(currentMethodName);
			methodSummary.addPathCondition(pcPair);
			allSummaries.put(currentMethodName, methodSummary);
			System.out.println("Property Violated: PC is " + pc.toString());
			System.out.println("Property Violated: result is  " + error);
			System.out.println("****************************");
		}
		// }
	}

	@Override
	public void instructionExecuted(VM vm, ThreadInfo currentThread,
			Instruction nextInstruction, Instruction executedInstruction) {

		if (!vm.getSystemState().isIgnored()) {
			Instruction insn = executedInstruction;
			// SystemState ss = vm.getSystemState();
			ThreadInfo ti = currentThread;
			Config conf = vm.getConfig();

			if (insn instanceof JVMInvokeInstruction) {
				JVMInvokeInstruction md = (JVMInvokeInstruction) insn;
				String methodName = md.getInvokedMethodName();
				int numberOfArgs = md.getArgumentValues(ti).length;

				MethodInfo mi = md.getInvokedMethod();
				currMethodName = mi.getFullName();
				continue_path(vm);
				if (mi.getFullName().contains(searchPrefix)) {
					createBFS(mi);
				}

				ClassInfo ci = mi.getClassInfo();
				String className = ci.getName();
				StackFrame sf = ti.getTopFrame();
				String shortName = methodName;
				String longName = mi.getLongName();
				if (methodName.contains("("))
					shortName = methodName
							.substring(0, methodName.indexOf("("));

				if (!mi.equals(sf.getMethodInfo()))
					return;

				if ((BytecodeUtils.isClassSymbolic(conf, className, mi,
						methodName))
						|| BytecodeUtils.isMethodSymbolic(conf,
								mi.getFullName(), numberOfArgs, null)) {

					MethodSummary methodSummary = new MethodSummary();

					methodSummary.setMethodName(className + "." + shortName);
					Object[] argValues = md.getArgumentValues(ti);
					String argValuesStr = "";
					for (int i = 0; i < argValues.length; i++) {
						argValuesStr = argValuesStr + argValues[i];
						if ((i + 1) < argValues.length)
							argValuesStr = argValuesStr + ",";
					}
					methodSummary.setArgValues(argValuesStr);
					byte[] argTypes = mi.getArgumentTypes();
					String argTypesStr = "";
					for (int i = 0; i < argTypes.length; i++) {
						argTypesStr = argTypesStr + argTypes[i];
						if ((i + 1) < argTypes.length)
							argTypesStr = argTypesStr + ",";
					}
					methodSummary.setArgTypes(argTypesStr);
					// get the symbolic values (changed from constructing them
					// here)
					String symValuesStr = "";
					String symVarNameStr = "";

					LocalVarInfo[] argsInfo = mi.getArgumentLocalVars();

					if (argsInfo == null)
						throw new RuntimeException(
								"ERROR: you need to turn debug option on");

					int sfIndex = 1; // do not consider implicit param "this"
					int namesIndex = 1;
					if (md instanceof INVOKESTATIC) {
						sfIndex = 0; // no "this" for static
						namesIndex = 0;
					}

					for (int i = 0; i < numberOfArgs; i++) {
						Expression expLocal = (Expression) sf
								.getLocalAttr(sfIndex);
						if (expLocal != null) // symbolic
							symVarNameStr = expLocal.toString();
						else
							symVarNameStr = argsInfo[namesIndex].getName()
									+ "_CONCRETE" + ",";
						// what happens if the argument is an array?
						symValuesStr = symValuesStr + symVarNameStr + ",";
						sfIndex++;
						namesIndex++;
						if (argTypes[i] == Types.T_LONG
								|| argTypes[i] == Types.T_DOUBLE)
							sfIndex++;

					}

					// get rid of last ","
					if (symValuesStr.endsWith(",")) {
						symValuesStr = symValuesStr.substring(0,
								symValuesStr.length() - 1);
					}
					methodSummary.setSymValues(symValuesStr);
					// System.out.println("STUART symbVal "+symValuesStr);
					currentMethodName = longName;
					allSummaries.put(longName, methodSummary);
				}
			} else if (insn instanceof JVMReturnInstruction) {
				MethodInfo mi = insn.getMethodInfo();
				ClassInfo ci = mi.getClassInfo();

				if (!MethodCallStack.empty()) {
					// if using golden model can only pop methods used
					String name = mi.getFullName();
					if (useGM == false || GMgraph.getNodeMatching(name) != null) {
						CalleeMethod = MethodCallStack.pop();
						path.remove(path.size() - 1);
					}
				}

				if (null != ci) {
					String className = ci.getName();
					String methodName = mi.getName();
					String longName = mi.getLongName();
					int numberOfArgs = mi.getNumberOfArguments();

					if (((BytecodeUtils.isClassSymbolic(conf, className, mi,
							methodName)) || BytecodeUtils.isMethodSymbolic(
							conf, mi.getFullName(), numberOfArgs, null))) {

						ChoiceGenerator<?> cg = vm.getChoiceGenerator();
						if (!(cg instanceof PCChoiceGenerator)) {
							ChoiceGenerator<?> prev_cg = cg
									.getPreviousChoiceGenerator();
							while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
								prev_cg = prev_cg.getPreviousChoiceGenerator();
							}
							cg = prev_cg;
						}
						if ((cg instanceof PCChoiceGenerator)
								&& ((PCChoiceGenerator) cg).getCurrentPC() != null) {
							PathCondition pc = ((PCChoiceGenerator) cg)
									.getCurrentPC();
							System.out.println("\tSTUART ==" + pc.stringPC());// SingleLine
							System.out.println("=======");
							addPCBFS(mi, pc.stringPC());

							// pc.solve(); //we only solve the pc
							if (SymbolicInstructionFactory.concolicMode) {
								SymbolicConstraintsGeneral solver = new SymbolicConstraintsGeneral();
								PCAnalyzer pa = new PCAnalyzer();
								pa.solve(pc, solver);
							} else
								pc.solve();

							if (!PathCondition.flagSolved) {
								return;
							}

							// after the following statement is executed, the pc
							// loses its solution

							String pcString = pc.toString();
							Pair<String, String> pcPair = null;

							String returnString = "";

							Expression result = null;

							if (insn instanceof IRETURN) {
								IRETURN ireturn = (IRETURN) insn;
								int returnValue = ireturn.getReturnValue();
								IntegerExpression returnAttr = (IntegerExpression) ireturn
										.getReturnAttr(ti);
								if (returnAttr != null) {
									returnString = "Return Value: "
											+ String.valueOf(returnAttr
													.solution());
									result = returnAttr;
								} else { // concrete
									returnString = "Return Value: "
											+ String.valueOf(returnValue);
									result = new IntegerConstant(returnValue);
								}
							} else if (insn instanceof LRETURN) {
								LRETURN lreturn = (LRETURN) insn;
								long returnValue = lreturn.getReturnValue();
								IntegerExpression returnAttr = (IntegerExpression) lreturn
										.getReturnAttr(ti);
								if (returnAttr != null) {
									returnString = "Return Value: "
											+ String.valueOf(returnAttr
													.solution());
									result = returnAttr;
								} else { // concrete
									returnString = "Return Value: "
											+ String.valueOf(returnValue);
									result = new IntegerConstant(
											(int) returnValue);
								}
							} else if (insn instanceof DRETURN) {
								DRETURN dreturn = (DRETURN) insn;
								double returnValue = dreturn.getReturnValue();
								RealExpression returnAttr = (RealExpression) dreturn
										.getReturnAttr(ti);
								if (returnAttr != null) {
									returnString = "Return Value: "
											+ String.valueOf(returnAttr
													.solution());
									result = returnAttr;
								} else { // concrete
									returnString = "Return Value: "
											+ String.valueOf(returnValue);
									result = new RealConstant(returnValue);
								}
							} else if (insn instanceof FRETURN) {

								FRETURN freturn = (FRETURN) insn;
								double returnValue = freturn.getReturnValue();
								RealExpression returnAttr = (RealExpression) freturn
										.getReturnAttr(ti);
								if (returnAttr != null) {
									returnString = "Return Value: "
											+ String.valueOf(returnAttr
													.solution());
									result = returnAttr;
								} else { // concrete
									returnString = "Return Value: "
											+ String.valueOf(returnValue);
									result = new RealConstant(returnValue);
								}

							} else if (insn instanceof ARETURN) {
								ARETURN areturn = (ARETURN) insn;
								IntegerExpression returnAttr = (IntegerExpression) areturn
										.getReturnAttr(ti);
								if (returnAttr != null) {
									returnString = "Return Value: "
											+ String.valueOf(returnAttr
													.solution());
									result = returnAttr;
								} else {// concrete
									Object val = areturn.getReturnValue(ti);
									returnString = "Return Value: "
											+ String.valueOf(val);
									// DynamicElementInfo val =
									// (DynamicElementInfo)areturn.getReturnValue(ti);
									String tmp = String.valueOf(val);
									tmp = tmp
											.substring(tmp.lastIndexOf('.') + 1);
									result = new SymbolicInteger(tmp);

								}
							} else
								// other types of return
								returnString = "Return Value: --";
							// pc.solve();
							// not clear why this part is necessary
							/*
							 * if (SymbolicInstructionFactory.concolicMode) {
							 * SymbolicConstraintsGeneral solver = new
							 * SymbolicConstraintsGeneral(); PCAnalyzer pa = new
							 * PCAnalyzer(); pa.solve(pc,solver); } else
							 * pc.solve();
							 */

							pcString = pc.toString();
							pcPair = new Pair<String, String>(pcString,
									returnString);
							MethodSummary methodSummary = allSummaries
									.get(longName);
							@SuppressWarnings("rawtypes")
							Vector<Pair> pcs = methodSummary
									.getPathConditions();
							if ((!pcs.contains(pcPair))
									&& (pcString.contains("SYM"))) {
								methodSummary.addPathCondition(pcPair);
							}

							if (allSummaries.get(longName) != null) // recursive
																	// call
								longName = longName + methodSummary.hashCode(); // differentiate
																				// the
																				// key
																				// for
																				// recursive
																				// calls
							allSummaries.put(longName, methodSummary);
							if (true) { // STUART if
										// (SymbolicInstructionFactory.debugMode)
										// {
								System.out
										.println("*************Summary***************");
								System.out.println("PC is:" + pc.toString());
								if (result != null) {
									System.out.println("Return is:  " + result);
									System.out
											.println("***********************************");
								}
							}
						}
					}
				}
			}// JVMReturn
		}
	}

	/*
	 * The way this method works is specific to the format of the methodSummary
	 * data structure
	 */

	@SuppressWarnings("rawtypes")
	private void printMethodSummary(PrintWriter pw, MethodSummary methodSummary) {

		System.out.println("Inputs: " + methodSummary.getSymValues());
		Vector<Pair> pathConditions = methodSummary.getPathConditions();
		if (pathConditions.size() > 0) {
			Iterator it = pathConditions.iterator();
			String allTestCases = "";
			while (it.hasNext()) {
				// System.out.println("STUART "+methodSummary.getMethodName());
				String testCase = methodSummary.getMethodName() + "(";
				Pair pcPair = (Pair) it.next();
				String pc = (String) pcPair._1;
				String errorMessage = (String) pcPair._2;
				String symValues = methodSummary.getSymValues();
				String argValues = methodSummary.getArgValues();
				String argTypes = methodSummary.getArgTypes();

				StringTokenizer st = new StringTokenizer(symValues, ",");
				StringTokenizer st2 = new StringTokenizer(argValues, ",");
				StringTokenizer st3 = new StringTokenizer(argTypes, ",");
				if (!argTypes.isEmpty() && argValues.isEmpty()) {
					continue;
				}
				while (st2.hasMoreTokens()) {
					String token = "";
					String actualValue = st2.nextToken();
					byte actualType = Byte.parseByte(st3.nextToken());
					if (st.hasMoreTokens())
						token = st.nextToken();
					if (pc.contains(token)) {
						String temp = pc.substring(pc.indexOf(token));
						if (temp.indexOf(']') < 0) {
							continue;
						}

						String val = temp.substring(temp.indexOf("[") + 1,
								temp.indexOf("]"));

						// if(actualType == Types.T_INT || actualType ==
						// Types.T_FLOAT || actualType == Types.T_LONG ||
						// actualType == Types.T_DOUBLE)
						// testCase = testCase + val + ",";
						if (actualType == Types.T_INT
								|| actualType == Types.T_FLOAT
								|| actualType == Types.T_LONG
								|| actualType == Types.T_DOUBLE) {
							String suffix = "";
							if (actualType == Types.T_LONG) {
								suffix = "l";
							} else if (actualType == Types.T_FLOAT) {
								val = String.valueOf(Double.valueOf(val)
										.floatValue());
								suffix = "f";
							}
							if (val.endsWith("Infinity")) {
								boolean isNegative = val.startsWith("-");
								val = ((actualType == Types.T_DOUBLE) ? "Double"
										: "Float");
								val += isNegative ? ".NEGATIVE_INFINITY"
										: ".POSITIVE_INFINITY";
								suffix = "";
							}
							testCase = testCase + val + suffix + ",";
						} else if (actualType == Types.T_BOOLEAN) { // translate
																	// boolean
																	// values
																	// represented
																	// as ints
							// to "true" or "false"
							if (val.equalsIgnoreCase("0"))
								testCase = testCase + "false" + ",";
							else
								testCase = testCase + "true" + ",";
						} else
							throw new RuntimeException(
									"## Error: listener does not support type other than int, long, float, double and boolean");
						// to extend with arrays
					} else {
						// need to check if value is concrete
						if (token.contains("CONCRETE"))
							testCase = testCase + actualValue + ",";
						else
							testCase = testCase + SymbolicInteger.UNDEFINED
									+ "(don't care),";// not correct in concolic
														// mode
					}
				}
				if (testCase.endsWith(","))
					testCase = testCase.substring(0, testCase.length() - 1);
				testCase = testCase + ")";
				// process global information and append it to the output

				if (!errorMessage.equalsIgnoreCase(""))
					testCase = testCase + "  --> " + errorMessage;
				// do not add duplicate test case
				if (!allTestCases.contains(testCase))
					allTestCases = allTestCases + "\n" + testCase;
			}
			pw.println(allTestCases);
		} else {
			pw.println("No path conditions for "
					+ methodSummary.getMethodName() + "("
					+ methodSummary.getArgValues() + ")");
		}
	}

	// -------- the publisher interface
	@SuppressWarnings("rawtypes")
	@Override
	public void publishFinished(Publisher publisher) {
		String[] dp = SymbolicInstructionFactory.dp;
		if (dp[0].equalsIgnoreCase("no_solver")
				|| dp[0].equalsIgnoreCase("cvc3bitvec"))
			return;

		PrintWriter pw = publisher.getOut();

		publisher.publishTopicStart("Method Summaries");
		Iterator it = allSummaries.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry me = (Map.Entry) it.next();
			MethodSummary methodSummary = (MethodSummary) me.getValue();
			// System.out.println(methodSummary.toString());//STUART
			printMethodSummary(pw, methodSummary);
		}

		printMyGraph();
		stopTime = System.currentTimeMillis();
		graphJPFTime = stopTime - origStartTime - graphReadTime;
		System.out.println("Graph Read Time: " + graphReadTime);
		System.out.println("Graph JPF Time : " + graphJPFTime);
		System.out.println("Total Time     : " + (stopTime - origStartTime));
	}

	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	 
	public void printMyGraph() {

		BFSNode Start = Bgraph.getNodeMatching(startNodeName);
		BFSNode End = Bgraph.getNodeMatching(finalNodeName);
		System.out.println("Looking For StartNode = " + startNodeName);
		System.out.println("Looking For EndNode   = " + finalNodeName);
		if (Start != null && End != null) {
			BFSGraph reducedGraph = Bgraph.getPaths(Start, End);
			if (reducedGraph != null) {
				System.out.println(reducedGraph.toString() + "\n\n");
				System.out.println(reducedGraph.toStringPathCond());
			} else {
				System.out.println("WARNING: no reduced graph found\n");
			}
		} else {
			if (Start == null) {
				System.out.println("WARNING: could not find Start "
						+ startNodeName + "\n");
			}
			if (End == null) {
				System.out.println("WARNING: could not find Final "
						+ finalNodeName + "\n");
			}
			System.out.println("GRAPH:\n" + Bgraph.toString());
		}
	}

	@SuppressWarnings("unused")
	private String formatName(String s) {
		// System.out.println("STUART input  "+s);
		String regex = "\\(\\w";
		s = s.replaceAll(regex, "(");
		regex = "\\)\\w";
		s = s.replaceAll(regex, ")");
		regex = "\\;\\)";
		s = s.replaceAll(regex, ")");
		regex = "\\;\\w";
		s = s.replaceAll(regex, ",");
		regex = "\\;";
		s = s.replaceAll(regex, ",");
		regex = "\\).+";
		s = s.replaceAll(regex, ")");
		regex = "(.+)\\.(.+\\(.*)";
		Pattern p = Pattern.compile(regex);
		// System.out.println("STUART pattern ==="+p.toString()+"===");
		Matcher m;
		m = p.matcher(s);
		if (m.matches()) {
			s = m.group(1) + ":" + m.group(2);
			// System.out.println("STUART   match "+m.group(1)+":"+m.group(2));
		}
		regex = "\\/";
		s = s.replaceAll(regex, ".");
		// System.out.println("STUART result "+s);
		return s;
	}

	private void addPCBFS(MethodInfo mi, String pc) {
		String name = mi.getFullName();
		System.out.println("STUART PC =" + pc + "=");
		BFSNode n;
		n = Bgraph.getNodeMatching(name);
		if (n != null) {
			n.addPath_condition(pc);
		}
	}

	private void createBFS(MethodInfo mi) {

		String name = mi.getFullName();
		//Search.setIgnoredState(check_continue(name));
		// TODO only for JIMPLE use name = formatName(name);
		// Add the node to the graph
		BFSNode to = null;
		if (useGM == false || GMgraph.getNodeMatching(name) != null) {
			// only add nodes in golden model
			FunctionNode fnode = new FunctionNode(name);
			if (Bgraph.addNode(fnode)) {
				System.out.println("ADDED node " + fnode.toString());
			}
			to = Bgraph.getNodeMatching(name);
		}
		if (to != null) {
			if (!MethodCallStack.empty()) {
				BFSNode from = MethodCallStack.peek();
				if (to != null && from != null) {
					Bgraph.addEdge(from, to);
				}
				CalleeMethod = to;
				MethodCallStack.push(CalleeMethod);
				path.add(CalleeMethod);
				if (name.equals(finalNodeName)) {
					checkPathConstraints(to);
				}
			} else {
				CalleeMethod = to;
				MethodCallStack.push(CalleeMethod);
				path.add(CalleeMethod);
			}
		}

	}

	protected void readGoldenCFG(String fname) {
		startTime = System.currentTimeMillis();
		origStartTime = startTime;
		if (useGM) {
			try {
				GMgraph.read_from_file(fname);
				System.out.println("=========================");
				System.out.println(" Golden Model Call Graph");
				System.out.println("=========================\n");
				System.out.println(GMgraph.toString());
				System.out.println("GM StartNode = " + startNodeName);
				System.out.println("GM EndNode   = " + finalNodeName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		stopTime = System.currentTimeMillis();
		graphReadTime = stopTime - startTime;
	}
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	
	protected void readGoldenModelJimple() {
		if (fileList.length != 0) {
			JimpleParser JP = new JimpleParser();
			for (String f : fileList) {
				try {
					if (f != null) {
						JP.ReadJimple(f);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// JP.printCFG();
			GMgraph = JP.getCfg();
			BFSNode Start = GMgraph.getNodeMatching(startNodeName);
			BFSNode End = GMgraph.getNodeMatching(finalNodeName);
			System.out.println("=========================");
			System.out.println(" Golden Model Call Graph");
			System.out.println("=========================\n");
			System.out.println(GMgraph.toString());
			System.out.println("GM Looking For StartNode = " + startNodeName);
			System.out.println("GM Looking For EndNode   = " + finalNodeName);
			if (Start != null && End != null) {
				BFSGraph reducedGraph = GMgraph.getPaths(Start, End);
				if (reducedGraph != null) {
					System.out.println(reducedGraph.toString() + "\n\n");
				} else {
					System.out.println("WARNING: no reduced graph found\n");
				}
			} else {
				if (Start == null) {
					System.out.println("WARNING: could not find Start "
							+ startNodeName + "\n");
				}
				if (End == null) {
					System.out.println("WARNING: could not find Final "
							+ finalNodeName + "\n");
				}
				System.out.println("GRAPH:\n" + Bgraph.toString());
			}
			System.out.println("=========================");
			System.out.println("=========================\n");
		}
	}

	protected void readGoldenModelBCEL() {
		if (fileList.length != 0) {
			GraphGenerator GG = new GraphGenerator();
			for (String f : fileList) {
				if (f != null) {
					try {
						GG.createCFG(GMgraph, f);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			BFSNode Start = GMgraph.getNodeMatching(startNodeName);
			BFSNode End = GMgraph.getNodeMatching(finalNodeName);
			System.out.println("=========================");
			System.out.println(" Golden Model Call Graph");
			System.out.println("=========================\n");
			System.out.println(GMgraph.toString());
			System.out.println("GM Looking For StartNode = " + startNodeName);
			System.out.println("GM Looking For EndNode   = " + finalNodeName);
			if (Start != null && End != null) {
				BFSGraph reducedGraph = GMgraph.getPaths(Start, End);
				if (reducedGraph != null) {
					System.out.println(reducedGraph.toString() + "\n\n");
				} else {
					System.out.println("WARNING: no reduced graph found\n");
				}
			} else {
				if (Start == null) {
					System.out.println("WARNING: could not find Start "
							+ startNodeName + "\n");
				}
				if (End == null) {
					System.out.println("WARNING: could not find Final "
							+ finalNodeName + "\n");
				}
				System.out.println("GRAPH:\n" + Bgraph.toString());
			}
			System.out.println("=========================");
			System.out.println("=========================\n");
		}
	}

	@SuppressWarnings("unused")
	private boolean compareGraphs(BFSGraph one, BFSGraph two) {
		return one.compare(two);
	}

	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	
	private boolean checkPathConstraints(BFSNode n) {
		ArrayList<String> gmConst = new ArrayList<String>();
		gmConst.add("calc.view.CalculatorView.equals(Lcalc/view/CalculatorView;)V");
		gmConst.add("calc.noSwing.JButton.pushed()V");
		gmConst.add("calc.view.CalculatorView$Handler.actionPerformed(Lcalc/noSwing/ActionEvent;)V");
		gmConst.add("calc.controller.CalculatorController.operation(Ljava/lang/String;)V");
		gmConst.add("calc.model.CalculatorModel.notifyChanged(Lcalc/model/ModelEvent;)V");
		gmConst.add("calc.view.CalculatorView.modelChanged(Lcalc/model/ModelEvent;)V");
		ArrayList<String> p = new ArrayList<String>();
		ArrayList<String> pp = new ArrayList<String>();
		boolean result = true;

		for (BFSNode b : path) {
			p.add(b.getNodeName());
		}
		if (p.contains(startNodeName)) { // have final see if we have start
			String name = n.getNodeName();
			for (String s : p) {
				if (gmConst.contains(s)) {
					pp.add(s);
				}
			}

			System.out.println("STUART checking Path from node " + name);
			System.out.println("STUART path is " + p.toString());
			System.out.println("STUART contraint path is " + pp.toString());
			if (!pp.equals(gmConst)) {
				result = false;
			}

			if (result == false) {
				System.out
						.println("======= FAILED CONSTRAINT PATH ===========");// TODO
				// BETTER
				// MESSAGE
				System.out.println("\t " + name + " -> " + p.toString());
				System.out
						.println("should have been \n\t" + gmConst.toString());
				System.out
						.println("======= FAILED CONSTRAINT PATH ===========");
			}
		}
		return result;
	}

//	public boolean check_continue(String m_name) {
//		return true;
//	}
	
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	private void continue_path(VM vm) {
		SystemState s = vm.getSystemState();
		Search ss = vm.getSearch();
		//System.out.println("-----------STUART continue path "+currMethodName+"---------");
		//s.setIgnored(methodNameIgnore());
		//s.setBoring(methodNameIgnore());
		ss.setIgnoredState(methodNameIgnore());
	}

private boolean methodNameIgnore() {
	ArrayList<String> s = new ArrayList<String>();
	s.add("calc.view.CalculatorView.addition(Lcalc/view/CalculatorView;II)V");
	s.add("calc.model.CalculatorModel.hello()V");
	s.add("calc.model.CalculatorModel.boo()V");
	s.add("calc.view.CalculatorView.subtraction(Lcalc/view/CalculatorView;II)V");
	
	//s.add("");
	//s.add("");
	
	for(String ss: s) {
		if(ss.equals(currMethodName)) {
			return true;
		}
	}
	return false;
}
//	@Override
//	public boolean check(Search search, VM vm) {
//		System.out.println("-----------STUART check "+currMethodName+"---------");	
//		return true;
//	}
	
//	@Override
//	public void stateProcessed(Search search) {
//		System.out.println("-----------STUART stateProcessed "+currMethodName+"---------");		
//	}
//	@Override
//	public void statePurged(Search search) {
//		System.out.println("-----------STUART statePurged "+currMethodName+"---------");		
//	}
////	@Override
////	public void stateRestored(Search search) {
////		System.out.println("-----------STUART stateRestored "+currMethodName+"---------");		
////	}
//	@Override
//	public void stateStored(Search search) {
//		System.out.println("-----------STUART stateStored "+currMethodName+"---------");		
//	}
//	@Override
//	public void searchStarted(Search search) {
//		System.out.println("-----------STUART searchStarted "+currMethodName+"---------");		
//	}
//	@Override
//	public void searchFinished(Search search) {
//		System.out.println("-----------STUART searchFinished "+currMethodName+"---------");		
//	}
//	@Override
//	public void searchProbed(Search search) {
//		System.out.println("-----------STUART searchProbed "+currMethodName+"---------");		
//	}
//	@Override
//	  public void stateAdvanced(Search search) {
//		System.out.println("-----------STUART stateAdvance "+currMethodName+"---------");
//		search.setIgnoredState(false);
//
//	  }
//	 @Override
//	  public void stateBacktracked (Search search) {
//		 System.out.println("-----------STUART stateBacktracked "+currMethodName+"---------");  
//	  }
//	  
//	  @Override
//	  public void stateRestored (Search search) {
//		  System.out.println("-----------STUART stateRestored "+currMethodName+"---------"); 
//	  }
//	  	
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	
	protected class MethodSummary {
		private String methodName = "";
		private String argTypes = "";
		private String argValues = "";
		private String symValues = "";
		@SuppressWarnings("rawtypes")
		private Vector<Pair> pathConditions;

		@SuppressWarnings("rawtypes")
		public MethodSummary() {
			pathConditions = new Vector<Pair>();
		}

		public void setMethodName(String mName) {
			this.methodName = mName;
		}

		public String getMethodName() {
			return this.methodName;
		}

		public void setArgTypes(String args) {
			this.argTypes = args;
		}

		public String getArgTypes() {
			return this.argTypes;
		}

		public void setArgValues(String vals) {
			this.argValues = vals;
		}

		public String getArgValues() {
			return this.argValues;
		}

		public void setSymValues(String sym) {
			this.symValues = sym;
		}

		public String getSymValues() {
			return this.symValues;
		}

		@SuppressWarnings("rawtypes")
		public void addPathCondition(Pair pc) {
			pathConditions.add(pc);
		}

		@SuppressWarnings("rawtypes")
		public Vector<Pair> getPathConditions() {
			return this.pathConditions;
		}

	}

}
