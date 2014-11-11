package gov.nasa.jpf.symbc;

public class ExSymExeLongBCwGlob2 {
	
	@Symbolic("true")
	static long staticGlobalLong = 10909;
	@Symbolic("true")
	long globalLong = 898989;
	@Symbolic("true")
	static int staticGlobalInt = 0;
	@Symbolic("true")
	int globalInt = 4;
	
  public static void main (String[] args) {
	  long x = 3;
	  long y = 5;
	  ExSymExeLongBCwGlob2 inst = new ExSymExeLongBCwGlob2();
	  inst.test(x, y);
  }

  /*
   * test LADD, LCMP, LMUL, LNEG, LSUB, invokevirtual bytecodes
   * using globals
   */
  
  public void test (long x, long z) { //invokevirtual
	  
	  System.out.println("Testing ExSymExeLongBCwGlob2");
	  
	  long a = x;
	  long b = z;
	  long c = staticGlobalLong; 

	  long negate = -z; //LNEG
	  
	  long sum = a + b; //LADD
	  long sum2 = z + 9090909L; //LADD
	  long sum3 = c + globalLong; //LADD
	  
	  long diff = a - b; //LSUB
	  long diff2 = b - c; //LSUB
	  long diff3 = 9999999999L - a; //LSUB
	    	  
	  long mul = a * b; //LMUL
	  long mul2 = a * 19999999999L; //LMUL
	  long mul3 = c * b; //LMUL
	  
	  if ( globalLong > sum)
		  System.out.println("branch globalLong > sum");
	  else
		  System.out.println("branch globalLong <= sum");
	  if (x < z)
		  System.out.println("branch x < z");
	  else
		  System.out.println("branch x >= z");
		  
  }
}