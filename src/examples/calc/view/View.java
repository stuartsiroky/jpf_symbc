package calc.view;
import calc.model.Model;
import calc.controller.Controller;

public interface View {
	Controller getController();
	void setController(Controller controller);
	Model getModel();
	void setModel(Model model);
}
