package calc.view;

import java.util.HashMap;

import calc.controller.CalculatorController;
import calc.model.CalculatorModel;
import calc.model.ModelEvent;
import calc.noSwing.ActionEvent;
import calc.noSwing.ActionListener;
import calc.noSwing.BorderLayout;
import calc.noSwing.GridLayout;
import calc.noSwing.JButton;
import calc.noSwing.JPanel;
import calc.noSwing.JTextField;

public class CalculatorView extends JFrameView {
	public static final String PLUS = "+";
	public static final String MINUS = "-";
	public static final String CLEAR = "Clr";
	public static final String EQUALS = "=";
	public static final String HELLO = "HI";//FAKE
	public static final String BOO = "BOO";//FAKE
	
	public static int fake_state = 0;
	private JTextField textField = new JTextField();
	public  JButton jButton1 = new JButton("1");
	public  JButton jButton2 = new JButton("2");
	public  JButton jButton3 = new JButton("3");
	public  JButton jButton4 = new JButton("4");
	public  JButton jButton5 = new JButton("5");
	public  JButton jButton6 = new JButton("6");
	public  JButton jButton7 = new JButton("7");
	public  JButton jButton8 = new JButton("8");
	public  JButton jButton9 = new JButton("9");
	public  JButton jButton0 = new JButton("0");
	public  JButton minusButton = new JButton(MINUS);
	public  JButton plusButton = new JButton(PLUS);
	public  JButton clearButton = new JButton(CLEAR);
	public  JButton equalsButton = new JButton(EQUALS);
	public  JButton helloButton = new JButton(HELLO);
	public  JButton booButton = new JButton(BOO);
	public Handler handler = new Handler();
	static CalculatorView cv;
	// use this hash to see if a method should be invoked.
	static HashMap<String, Boolean> methodEn_hm = new HashMap<String, Boolean>();
	
	public void setMethodEn(String mname, Boolean val) {
		methodEn_hm.put(mname, val);
	}

	public CalculatorView(CalculatorModel model, CalculatorController controller) {
		super(model, controller);
		System.out.println("Initial");
		textField.setText("0");
		this.getContentPane().add(textField, BorderLayout.NORTH);
		JPanel buttonPanel = new JPanel();
		jButton1.addActionListener(handler);
		jButton2.addActionListener(handler);
		jButton3.addActionListener(handler);
		jButton4.addActionListener(handler);
		jButton5.addActionListener(handler);
		jButton6.addActionListener(handler);
		jButton7.addActionListener(handler);
		jButton8.addActionListener(handler);
		jButton9.addActionListener(handler);
		jButton0.addActionListener(handler);
		minusButton.addActionListener(handler);
		plusButton.addActionListener(handler);
		clearButton.addActionListener(handler);
		equalsButton.addActionListener(handler);
		helloButton.addActionListener(handler);
		booButton.addActionListener(handler);
		buttonPanel.setLayout(new GridLayout(4, 4, 5, 5));
		this.getContentPane().add(buttonPanel, BorderLayout.CENTER);
		buttonPanel.add(jButton1, null);
		buttonPanel.add(jButton2, null);
		buttonPanel.add(jButton3, null);
		buttonPanel.add(jButton4, null);
		buttonPanel.add(jButton5, null);
		buttonPanel.add(jButton6, null);
		buttonPanel.add(jButton7, null);
		buttonPanel.add(jButton8, null);
		buttonPanel.add(jButton9, null);
		buttonPanel.add(jButton0, null);
		buttonPanel.add(minusButton, null);
		buttonPanel.add(plusButton, null);
		buttonPanel.add(clearButton, null);
		buttonPanel.add(equalsButton, null);
		pack();

	}

	// Now implement the necessary event handling code
	public void modelChanged(ModelEvent event) {
		if (methodEn_hm.get("calc.view.CalculatorView.modelChanged(Lcalc/model/ModelEvent;)V")) {		
					String msg = event.getAmount() + "";
		textField.setText(msg);
		//System.out.println("modeChanged::msg = "+msg);
	}
	}

	// Inner classes for Event Handling
	public class Handler implements ActionListener {
		// Event handling is handled locally
		public void actionPerformed(ActionEvent e) {
//		vv3();
			if(fake_state == 1){
				ModelEvent me = new ModelEvent(this, 101, "invalid path", -101);
				modelChanged(me);
			} else {
			CalculatorController c = (CalculatorController) getController();
			String ae = e.getActionCommand();
			c.operation(ae);
			((CalculatorController) getController()).operation(e
					.getActionCommand());
		    }
		}
	}

	public static void main(String[] args) {
		int a = 1;
		int b = 3;	

		CalculatorController cc = new CalculatorController();
		cv = (CalculatorView) cc.getView();

		start(a,b);
	}
	static void start ( int a, int b) {
		if (methodEn_hm.get("calc.view.CalculatorView.start(II)V")) {		
					CalculatorView calc = cv;
		//equals(calc);
		if(a > b){
			fake_state = 0;
			addition(calc,a,b);	
		} else if(a < b) {
			fake_state = 0;
			subtraction(calc,b,a);
		} else {
			fake_state = 1;
			ActionEvent e = new ActionEvent(calc, 101, "101");
			calc.handler.actionPerformed(e);
		} 
//		vv6();
		equals(calc);
		if (a == 1) {
			hello(calc);
		} else if (b == 1) {
			boo(calc);
		} else {
//			vv0();
		}
		}
	}
	static void start_equals() {
		if (methodEn_hm.get("calc.view.CalculatorView.start_equals()V")) {		
					equals(cv);
		}
	}
	
	static void addition(CalculatorView calc, int a, int b){
		if (methodEn_hm.get("calc.view.CalculatorView.addition(Lcalc/view/CalculatorView;II)V")) {		
					pickButton(calc,a);
		calc.plusButton.pushed();
		pickButton(calc,b);
		}
	}
	
	static void subtraction(CalculatorView calc, int a, int b){
		if (methodEn_hm.get("calc.view.CalculatorView.subtraction(Lcalc/view/CalculatorView;II)V")) {		
					pickButton(calc,b);
		calc.minusButton.pushed();
		pickButton(calc,a);
		}
	}
	
	static void equals(CalculatorView calc){
		if (methodEn_hm.get("calc.view.CalculatorView.equals(Lcalc/view/CalculatorView;)V")) {		
					calc.equalsButton.pushed();
		}
	}


	
	static void pickButton(CalculatorView calc, int val){
		if (methodEn_hm.get("calc.view.CalculatorView.pickButton(Lcalc/view/CalculatorView;I)V")) {		
					switch(val) {
		case 0: calc.jButton0.pushed(); break;
		case 1: calc.jButton1.pushed(); break;
		case 2: calc.jButton2.pushed(); break;
		case 3: calc.jButton3.pushed(); break;
		case 4: calc.jButton4.pushed(); break;
		case 5: calc.jButton5.pushed(); break;
		case 6: calc.jButton6.pushed(); break;
		case 7: calc.jButton7.pushed(); break;
		case 8: calc.jButton8.pushed(); break;
		case 9: calc.jButton9.pushed(); break;
		}
		}
	}

	static void hello(CalculatorView calc){
		if (methodEn_hm.get("calc.view.CalculatorView.hello(Lcalc/view/CalculatorView;)V")) {		
					pickButton(calc,0);
		pickButton(calc,1);
		pickButton(calc,1);
		pickButton(calc,3);
		pickButton(calc,4);
		calc.helloButton.pushed();
//		vv3();
		}
	}

	static void boo(CalculatorView calc){
		if (methodEn_hm.get("calc.view.CalculatorView.boo(Lcalc/view/CalculatorView;)V")) {		
					pickButton(calc,0);
		pickButton(calc,0);
		pickButton(calc,8);
		calc.booButton.pushed();
		}
	}	
//	private static void vv0() {vv1();}
//	private static void vv1() {vv2();}
//	private static void vv2() {vv3();}
//	private static void vv3() {vv4();}
//	private static void vv4() {vv5();}
//	private static void vv5() {vv6();}
//	private static void vv6() {vv7();}
//	private static void vv7() {vv8();}
//	private static void vv8() {vv9();}
//	private static void vv9() {}


}
