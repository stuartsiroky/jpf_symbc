//
// Copyright (C) 2006 United States Government as represented by the
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
//
package gov.nasa.jpf.symbc.bytecode;


import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;


/**
 * Divide long
 * ..., value1, value2 => ..., result
 */
public class LDIV extends gov.nasa.jpf.jvm.bytecode.LDIV  {

	@Override
	public Instruction execute (ThreadInfo th) {
		StackFrame sf = th.getModifiableTopFrame();
		IntegerExpression sym_v1 = (IntegerExpression) sf.getOperandAttr(1);
		IntegerExpression sym_v2 = (IntegerExpression) sf.getOperandAttr(3);
		long v1, v2;

		if(sym_v1==null && sym_v2==null)
			return super.execute(th);// we'll still do the concrete execution

		// result is symbolic

		if(sym_v1==null && sym_v2!=null) {
			v1 = sf.popLong();
			v2 = sf.popLong();
			if(v1==0)
				return th.createAndThrowException("java.lang.ArithmeticException","div by 0");
			sf.pushLong(0);
			IntegerExpression result = sym_v2._div(v1);
			sf.setLongOperandAttr(result);
			return getNext(th);
		}

		// div by zero check affects path condition
		// sym_v1 is non-null and should be checked against zero

		ChoiceGenerator<?> cg;
		boolean condition;

		if (!th.isFirstStepInsn()) { // first time around
			cg = new PCChoiceGenerator(2);
			((PCChoiceGenerator)cg).setOffset(this.position);
			((PCChoiceGenerator)cg).setMethodName(this.getMethodInfo().getFullName());
			th.getVM().getSystemState().setNextChoiceGenerator(cg);
			return this;
		} else {  // this is what really returns results
			cg = th.getVM().getSystemState().getChoiceGenerator();
			assert (cg instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: " + cg;
			condition = (Integer)cg.getNextChoice()==0 ? false: true;
		}


		v1 = sf.popLong();
		v2 = sf.popLong();
		sf.pushLong(0);

		PathCondition pc;
		ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();

		while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
			prev_cg = prev_cg.getPreviousChoiceGenerator();
		}
		if (prev_cg == null)
			pc = new PathCondition();
		else
			pc = ((PCChoiceGenerator)prev_cg).getCurrentPC();

		assert pc != null;

		if(condition) { // check div by zero
			pc._addDet(Comparator.EQ, sym_v1, 0);
			if(pc.simplify())  { // satisfiable
				((PCChoiceGenerator) cg).setCurrentPC(pc);

				return th.createAndThrowException("java.lang.ArithmeticException","div by 0");
			}
			else {
				th.getVM().getSystemState().setIgnored(true);
				return getNext(th);
			}
		}
		else {
			pc._addDet(Comparator.NE, sym_v1, 0);
			if(pc.simplify())  { // satisfiable
				((PCChoiceGenerator) cg).setCurrentPC(pc);

				// set the result
				IntegerExpression result;
				if(sym_v2!=null)
					result = sym_v2._div(sym_v1);
				else
					result = sym_v1._div_reverse(v2);

				sf = th.getModifiableTopFrame();
				sf.setLongOperandAttr(result);
				return getNext(th);

			}
			else {
				th.getVM().getSystemState().setIgnored(true);
				return getNext(th);
			}
		}


	}


}
