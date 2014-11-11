package codeExamples;

//import gov.nasa.jpf.vm.Verify;

public class InfeasablePath {
	public static void main(String[] a) {
//		int i = Verify.getInt(-2,5);
	
		(new InfeasablePath()).start(2);
		//InfeasablePath test = new InfeasablePath();
		  // test.start(i);
	}
	
	public void start(int i) {
		if(i>1){
			foo(i);
		}
		else {
			bar(i);
		}
	}

	static void bar(int i) {
		if(i>1) {
			foo(i);
		}
		else if(i<1 & i>-1) {
			bar(i+1);
		}
		else {
			foo_bar();
		}
	}
	static void foo(int i) {
		if(i>2) {
			foo_bar();
		}
		else {
			return;
		}
	}
	
	static void foo_bar() {}
	
}
