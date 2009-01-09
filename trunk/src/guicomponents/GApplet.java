package guicomponents;

import java.util.HashMap;

import processing.core.PApplet;


public class GApplet extends PApplet {

	protected HashMap<String, GComponent> widgets = new HashMap<String, GComponent>();
	
	public GApplet(){
		super();
	}
	
	protected boolean addComponent(GComponent widget){
		String id = widget.getID();
		if(widget.getID() == null){
			System.out.println("Must specify an ID for this component");
			return false;
		}
		else if(widgets.containsKey(id)){
			System.out.println("Duplicate ID found: "+id+" has been used already");
			return false;
		}
		widgets.put(id, widget);
		return true;
	}
	
	protected GComponent getWidget(String id){
		return widgets.get(id);
	}
	
	public void panel(String id, String text, int x, int y, int width, int height){
		GPanel widget = new GPanel(this, text, x, y, width, height);
		addComponent(widget);
		
	}
	
	public void panel(String id, String text, int x, int y, int width, int height,
			GColor colorScheme, GFont fontScheme){
		GPanel widget = new GPanel(this, text, x, y, width, height, colorScheme, fontScheme);
		addComponent(widget);
	}

	public GPanel panel(String id){
		return (GPanel)getWidget(id);
	}

}
