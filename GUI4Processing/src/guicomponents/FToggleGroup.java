package guicomponents;

import java.util.ArrayList;

public class FToggleGroup {



	private ArrayList<FToggleControl> toggles;

	private FToggleControl selected = null;
	private FToggleControl deselected = null;
	
	public FToggleGroup(){
		toggles = new ArrayList<FToggleControl>();
	}

	public void addControl(FToggleControl tc){
		tc.setToggleGroup(this);
		toggles.add(tc);
	}
	
	void makeSelected(FToggleControl tc){
		System.out.println(deselected + " << " + selected + " << " + tc);
		deselected = selected;
		if(deselected != null)
			deselected.setSelected(false);
		selected = tc;
	}
}
