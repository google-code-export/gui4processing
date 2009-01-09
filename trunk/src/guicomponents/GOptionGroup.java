package guicomponents;

import java.util.TreeSet;

import processing.core.PApplet;

public class GOptionGroup {

	protected PApplet app;
	
	protected GOption selected;
	protected GOption deselected;
	
	protected TreeSet<GOption> options;
	
	protected int nbrOptions;
	
	public GOptionGroup(PApplet theApplet){
		app = theApplet;
		options = new TreeSet<GOption>();
		selected = null;
		deselected = null;
	}
	
	public void addOption(GOption option){
		if(options.isEmpty())
			selected = option;
		nbrOptions++;
		options.add(option);
		option.setGroup(this);
	}
	
	public void setSelected(GOption option){
		deselected = selected;
		selected = option;
	}
	
	public GOption getSelected(){
		return selected;
	}
	
	public GOption getDeselected(){
		return deselected;
	}
	
}
