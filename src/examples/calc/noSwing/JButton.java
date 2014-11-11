package calc.noSwing;

public class JButton {
	private String str;
	private ActionListener hdlr;
	
	public JButton(String string) {
		str = string;
	}

	public void addActionListener(ActionListener handler) {
		hdlr = handler;
	}

	public void pushed() {
		ActionEvent e = new ActionEvent(this,1,str);
		hdlr.actionPerformed(e);
	}
}
