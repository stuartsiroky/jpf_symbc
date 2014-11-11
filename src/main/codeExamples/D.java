package codeExamples;


public class D {
	public static void main(String[] s)  {
		int i =0;
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
		else if(i<1 & i>0) {
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
