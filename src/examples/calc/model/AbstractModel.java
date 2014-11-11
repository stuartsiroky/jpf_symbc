package calc.model;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractModel implements Model {
	@SuppressWarnings("rawtypes")
	private ArrayList listeners = new ArrayList(5);
	
	public void notifyChanged(ModelEvent event){
		@SuppressWarnings("rawtypes")
		ArrayList list = (ArrayList)listeners.clone();
		@SuppressWarnings("rawtypes")
		Iterator it = list.iterator();
		while(it.hasNext()){
			ModelListener ml = (ModelListener)it.next();
			ml.modelChanged(event);
		}
	}
	@SuppressWarnings("unchecked")
	public void addModelListener(ModelListener l){
		listeners.add(l);
	}
	public void removeModelListener(ModelListener l){
		listeners.remove(l);
	}
}
