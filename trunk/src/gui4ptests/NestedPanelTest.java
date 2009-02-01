package gui4ptests;

import processing.core.PApplet;
import guicomponents.GPanel;
import guicomponents.GTextField;

@SuppressWarnings("serial")
public class NestedPanelTest extends PApplet {
	private GPanel p1, p2;
	private GTextField tf1, tf2;
	
	public void setup(){
		size(600,600);
		
		p1 = new GPanel(this, "Panel 1", 10,10,360,340);
		p2 = new GPanel(this, "Panel 2", 10,100,160,140);
		p1.add(p2);
		p1.add(p2);
		p1.setOpaque(true);
		p2.setOpaque(true);
		tf1 = new GTextField(this, "Peter Lager", 10,10,100,10);
		tf2 = new GTextField(this, "Peter Kenneth Lager", 10,10,100,10);
		p1.add(tf1);
		p2.add(tf2);
	}
	
	
	public void draw(){
		background(240);
	}
}
