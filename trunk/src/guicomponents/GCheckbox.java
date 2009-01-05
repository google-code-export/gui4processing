package guicomponents;

import java.awt.Point;
import java.awt.event.MouseEvent;

import processing.core.PApplet;
import processing.core.PImage;

public class GCheckbox extends GComponent {

	protected boolean selected;
	
	protected static PImage imgSelected;
	protected static PImage imgCleared;
	
	public GCheckbox(PApplet theApplet, String text, int x, int y, int width,
			GColor colorScheme, GFont fontScheme){
		super(theApplet, x, y, colorScheme, fontScheme);
		setText(text);
		this.width = width;
		height = localFont.gpFontSize + 2;
		if(imgSelected == null)
			imgSelected = app.loadImage("check1.png");
		if(imgCleared == null)
			imgCleared = app.loadImage("check0.png");
		createEventHandler(theApplet);
		app.registerDraw(this);
		app.registerMouseEvent(this);
	}

	public GCheckbox(PApplet theApplet, String text, int x, int y, int width){
		super(theApplet, x, y);
		setText(text);
		this.width = width;
		height = localFont.gpFontSize + 2;
		if(imgSelected == null)
			imgSelected = app.loadImage("check1.png");
		if(imgCleared == null)
			imgCleared = app.loadImage("check0.png");
		createEventHandler(theApplet);
		app.registerDraw(this);
		app.registerMouseEvent(this);
	}

	public void addEventHandler(Object obj, String methodName){
		try{
			this.eventHandler = obj.getClass().getMethod(methodName, new Class[] { GCheckbox.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
			System.out.println("with a single parameter of type GCheckbox");
			eventHandlerObject = null;
		}
	}
	
	protected void createEventHandler(Object obj){
		try{
			this.eventHandler = obj.getClass().getMethod("handleCheckboxEvents", new Class[] { GCheckbox.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			eventHandlerObject = null;
			System.out.println("You might want to add a method to handle \ncheckbox events the syntax is");
			System.out.println("void handleCheckboxEvents(GCheckbox cbox){\n   ...\n}\n\n");
		}
	}
	
	public void draw(){
		if(visible){
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			if (!text.equals("")){
				app.fill(localColor.panelTabFont);
				app.textFont(localFont.gpFont, localFont.gpFontSize);
				app.text(text, pos.x + 16, pos.y + 1, width, height);
				System.out.println(text);
			}
			if(selected)
				app.image(imgSelected, pos.x, pos.y);
			else
				app.image(imgCleared, pos.x, pos.y);
		}
	}

	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(mouseFocusOn == null && isOver(app.mouseX, app.mouseY))
				mouseFocusOn = this;
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(mouseFocusOn == null && isOver(app.mouseX, app.mouseY)){
				selected = !selected;
				fireEvent();
			}
			mouseFocusOn = null;
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(mouseFocusOn == this){
				mouseFocusOn = null;
			}
			break;
		}
	}

	public boolean isSelected(){
		return selected;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
}
