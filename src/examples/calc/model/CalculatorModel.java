package calc.model;

public class CalculatorModel extends AbstractModel {
	private int total = 0;
	private int current = 0;
	private String state = "add";
	
	public void clear(){total = 0; store(0);}
	
	public void store(int value){
		current = value;
		ModelEvent me = new ModelEvent(this, 1, "", current);
		//System.out.println("model.store::"+value);
		notifyChanged(me);
	}
	
	public void notifyChanged(ModelEvent event){
	super.notifyChanged(event);
	}
	public void add()//throws Digit5
	{
		state = "add"; total = current;
		//System.out.println("STUART CalculatorModel.add");
		}
	
	public void subtract(){state = "subtract"; total = current;
	//System.out.println("STUART CalculatorModel.subtract");
	}
	
	public void equals(){
		if(state == "add"){
			total += current;
		}
		else {
			total -= current;
		}
		current = total;
		ModelEvent me = new ModelEvent(this, 1, "", total);
		//System.out.println("model.equals:: state = "+state+" total = "+current);
		notifyChanged(me);
	}

		public int getTotal(){return total;}

		public void boo() {
			ModelEvent me = new ModelEvent(this, 1, "", current);
			//System.out.println("This is scary stuff");
//			b0();
//			dummy();
			notifyChanged(me);
//			b4();
//			b1();
		}
		
		public void hello() {
		ModelEvent me = new ModelEvent(this, 1, "", current);
//		//System.out.println("How are you?");
//		f1();
		notifyChanged(me);
//		f3();
	} 
		
//		private void dummy() {
//			c10();
//			dummy1();
//		}
//		private void dummy1() {
//			ModelEvent me = new ModelEvent(this, 1, "", current);
//			notifyChanged(me);
//		}
//
//		private void f0() {f1();}
//		private void f1() {f2();}
//		private void f2() {
//			f3();	
//			f8();
//		}
//		private void f3() {f4();}
//		private void f4() {f5();}
//		private void f5() {f6();}
//		private void f6() {f7();}
//		private void f7() {
//			f8();
//			b4();
//			c5();
//		}
//		private void f8() {f9();}
//		private void f9() {}
//
//		private void b0() {
//			b1();
//			f0();
//		}
//		private void b1() {b2();}
//		private void b2() {b3();}
//		private void b3() {b4();}
//		private void b4() {b5();}
//		private void b5() {b6();}
//		private void b6() {
//			b7();
//			f9();
//		}
//		private void b7() {
//			b8();
//			c0();
//		}
//		private void b8() {b9();}
//		private void b9() {}
//		private void c0() { c1(); }
//		private void c1() { c2(); }
//		private void c2() { c3(); }
//		private void c3() { c4(); }
//		private void c4() { c5(); }
//		private void c5() { c6(); }
//		private void c6() { c7(); }
//		private void c7() { c8(); }
//		private void c8() { c9(); }
//		private void c9() { c10(); }
//		private void c10() { c11(); }
//		private void c11() { c12(); }
//		private void c12() { c13(); }
//		private void c13() { c14(); }
//		private void c14() { c15(); }
//		private void c15() { c16(); }
//		private void c16() { c17(); }
//		private void c17() { c18(); }
//		private void c18() { c19(); }
//		private void c19() { c20(); }
//		private void c20() { c21(); }
//		private void c21() { c22(); }
//		private void c22() { c23(); }
//		private void c23() { c24(); }
//		private void c24() { c25(); }
//		private void c25() { c26(); }
//		private void c26() { c27(); }
//		private void c27() { c28(); }
//		private void c28() { c29(); }
//		private void c29() {  }


}
