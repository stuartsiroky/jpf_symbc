package codeExamples;


public class DD {
	F fclass = new F();
	
	public void main(String[] a) {
	
		
		int i =0;
		if(i>1){
			foo(i);
			fclass.dummy1();
		}
		else {
			bar(i);
		}
	}

	private void bar(int i) {
		if(i>1) {
			foo(i);
		}
		else if(i<=1 & i>=0) {
			bar(i+1);
		}
		else if(istrue()){
			foo_bar();
		}
	}
	void foo(int i) {
		if(i>2) {
			foo_bar();
		}
		else {
			i = fclass.dummy2(i);
			return;
		}
	}
	
	void foo_bar() {}

	boolean istrue() {
		return true;
	}
}
