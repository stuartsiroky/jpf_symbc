package calc.controller;

import calc.model.CalculatorModel;
import calc.view.CalculatorView;
import calc.view.JFrameView;

public class CalculatorController extends AbstractController {
	public CalculatorController(){
		CalculatorModel cm = new CalculatorModel();
		setModel(cm);
		CalculatorView cv = new CalculatorView(cm,this);
		setView(cv);
		((JFrameView)getView()).setVisible(true);
	}

	public void operation(String option){
		if(option.equals(CalculatorView.MINUS)){
			CalculatorModel cm = (CalculatorModel)getModel();
			cm.subtract();
		}else if(option.equals(CalculatorView.PLUS)){
			CalculatorModel cm = (CalculatorModel)getModel();
			cm.add();
		}else if(option.equals(CalculatorView.CLEAR)){
			CalculatorModel cm = (CalculatorModel)getModel();
			cm.clear();
		}else if(option.equals(CalculatorView.EQUALS)){
			CalculatorModel cm = (CalculatorModel)getModel();
			cm.equals();
		}else if(option.equals(CalculatorView.HELLO)){
			CalculatorModel cm = (CalculatorModel)getModel();
			cm.hello();
			//fc9();
		}else if(option.equals(CalculatorView.BOO)){
			CalculatorModel cm = (CalculatorModel)getModel();
			cm.boo();
			//c0();
			//fc5();
		}else {
			CalculatorModel cm = (CalculatorModel)getModel();
			int val = Integer.parseInt(option);
			cm.store(val);
		}
		//fc0();
	}

//	private void fc0() {fc1();}
//	private void fc1() {fc2();}
//	private void fc2() {fc3();}
//	private void fc3() {fc4();}
//	private void fc4() {fc5();}
//	private void fc5() {fc6();}
//	private void fc6() {fc7();}
//	private void fc7() {fc8();}
//	private void fc8() {fc9();}
//	private void fc9() {}
//	private void c0() { c1(); }
//	private void c1() { c2(); }
//	private void c2() { c3(); }
//	private void c3() { c4(); }
//	private void c4() { c5(); }
//	private void c5() { c6(); }
//	private void c6() { c7(); }
//	private void c7() { c8(); }
//	private void c8() { c9(); }
//	private void c9() { c10(); }
//	private void c10() { c11(); }
//	private void c11() { c12(); }
//	private void c12() { c13(); }
//	private void c13() { c14(); }
//	private void c14() { c15(); }
//	private void c15() { c16(); }
//	private void c16() { c17(); }
//	private void c17() { c18(); }
//	private void c18() { c19(); }
//	private void c19() { c20(); }
//	private void c20() { c21(); }
//	private void c21() { c22(); }
//	private void c22() { c23(); }
//	private void c23() { c24(); }
//	private void c24() { c25(); }
//	private void c25() { c26(); }
//	private void c26() { c27(); }
//	private void c27() { c28(); }
//	private void c28() { c29(); }
//	private void c29() { 
//		CalculatorModel cm = (CalculatorModel)getModel();
//		cm.boo(); 
//	}		
}
