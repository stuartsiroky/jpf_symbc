package calc.noSwing;

public class ActionEvent {
private String msg;
	public ActionEvent(Object obj, int id, String message) {
		msg = message;
	}

	public String getActionCommand() {
		return msg;
	}

}
