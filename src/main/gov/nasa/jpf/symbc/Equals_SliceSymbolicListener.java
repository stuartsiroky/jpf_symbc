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

import java.util.ArrayList;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

//import gov.nasa.jpf.symbc.numeric.SymbolicInteger;

public class Equals_SliceSymbolicListener extends SliceSymbolicListener {

	ArrayList<String> gmConst;

	public Equals_SliceSymbolicListener(Config conf, JPF jpf) {
		super(conf, jpf);
		initPathConst();
	}

	protected void CheckPath() {
		boolean result = true;
		Object oA[] = MethodCallStack.toArray();
		ArrayList<String> sA = new ArrayList<String>();
		for (int i = 0; i < oA.length; i++) {
			sA.add((String) oA[i]);
		}

		ArrayList<String> pp = new ArrayList<String>();
		int lastIndex = gmConst.size() - 1;
		String lastMethName = gmConst.get(lastIndex);

		if (sA.contains(lastMethName)) {
			for (String s : sA) {
				if (gmConst.contains(s)) {
					pp.add(s);
				}
			}

//			System.out.println("STUART path is           " + sA.toString());
//			System.out.println("STUART contraint path is " + pp.toString());

			if (pp.size() != gmConst.size()) {
				result = false;
				System.out.println("Path is " + pp.size() + " expected "
						+ gmConst.size());
			}
			if (result == true) {
				for (int i = 0; i < pp.size(); i++) {
					if (!pp.get(i).equals(gmConst.get(i))) {
						System.out.println("Failed to match at " + i + " "
								+ pp.get(i));
						result = false;
						break;
					}
				}
			} 
			if (result == false) {
				System.out
						.println("======= FAILED CONSTRAINT PATH ===========");
				System.out.println("\t" + pp.toString());
				System.out
						.println("should have been \n\t" + gmConst.toString());
				System.out
						.println("======= FAILED CONSTRAINT PATH ===========");
			}
		}

	}

	private void initPathConst() {
		gmConst = new ArrayList<String>();
		gmConst.add("calc.view.CalculatorView.equalsMethod(Lcalc/view/CalculatorView;)V");
		gmConst.add("buttons.EqualsButton.pushed()V");
		gmConst.add("calc.view.CalculatorView$EqHandler.actionPerformed(Lcalc/noSwing/ActionEvent;)V");
		gmConst.add("calc.controller.CalculatorController.equalsOperation()V");
		gmConst.add("calc.model.CalculatorModel.equalsOp()V");
	}
}
