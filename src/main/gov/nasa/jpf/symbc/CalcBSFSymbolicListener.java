//TODO: needs to be simplified;

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

public class CalcBSFSymbolicListener extends BSFSymbolicListener {

	public CalcBSFSymbolicListener(Config conf, JPF jpf) {
		super(conf,jpf);
		startNodeName = "calc.view.CalculatorView.equals(Lcalc/view/CalculatorView;)V";
		finalNodeName = "calc.view.CalculatorView.modelChanged(Lcalc/model/ModelEvent;)V";
		searchPrefix = ".";
		//useGM = true;
		readGoldenCFG("C:\\Users\\StuartSiroky\\Documents\\calcNoSwing_Reduced.txt");
		
	}
	
	public boolean check_continue(String m_name) {
		
		String s1 = "calc.view.CalculatorView.subtraction(Lcalc/view/CalculatorView;II)V";
		String s2 = "calc.view.CalculatorView.boo(Lcalc/view/CalculatorView;)V";
		String s3 = "calc.view.CalculatorView.addition(Lcalc/view/CalculatorView;II)V";
		String s4 = "calc.view.CalculatorView.hello(Lcalc/view/CalculatorView;)V";
		if(m_name.equals(s1)) return false;
		if(m_name.equals(s2)) return false;
		if(m_name.equals(s3)) return false; 
		if(m_name.equals(s4)) return false;
		return true;
	}

}
