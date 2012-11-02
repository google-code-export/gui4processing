package guicomponents;

import java.util.LinkedList;

public class TabManager {
	
	private LinkedList<FEditableTextControl> controls;
	
	public TabManager(){
		controls = new LinkedList<FEditableTextControl>();
	}
	
	public boolean addControls(FEditableTextControl... ctrls){
		boolean result = false;
		for(FEditableTextControl control : ctrls)
			result |= addControl(control);
		return result;
	}
	
	public boolean addControl(FEditableTextControl control){
		if(!controls.contains(control)){
			control.tabManager = this;
			controls.addLast(control);
			return true;
		}
		return false;
	}
	
	public boolean removeControl(FEditableTextControl control){
		int index = controls.lastIndexOf(control);
		if(index > 0){
			control.tabManager = null;
			controls.remove(index);
			return true;
		}
		return false;
	}
	
	public boolean nextControl(FEditableTextControl control){
		int index = controls.lastIndexOf(control);
		if(controls.size() > 1 && index >= 0 && index < controls.size() - 1){
			index++;
			//control = controls.get(index);
			FAbstractControl.controlToTakeFocus = controls.get(index);;
			return true;
		}
		return false;
	}

	public boolean prevControl(FEditableTextControl control){
		int index = controls.lastIndexOf(control);
		if(controls.size() > 1 && index > 0){
			index--;
			//control = controls.get(index);
			FAbstractControl.controlToTakeFocus = controls.get(index);
			return true;
		}
		return false;
	}

	
}
