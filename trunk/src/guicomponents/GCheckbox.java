package guicomponents;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import processing.core.PApplet;

public class GCheckbox extends GComponent {

	protected boolean selected;
	
	protected int size;
	
	public GCheckbox(PApplet theApplet, String text, int x, int y, int width, int size, int colorScheme, int fontScheme){
		super(theApplet, x, y, colorScheme, fontScheme);
		this.size = constrain(size, 12, 20);
		this.text = text;
		this.width = width;
		height = localGScheme.gpFontSize;
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
			app.strokeWeight(1);
			app.stroke(0);
			app.fill(255);
			app.rect(pos.x, pos.y, size, size);
			if(selected){
				app.strokeWeight(2);
				app.line(pos.x + 2, pos.y + 2, pos.x + size - 2, pos.y + size - 2);
				app.line(pos.x + size - 2, pos.y + 2, pos.x + 2, pos.y + size - 2);
			}
			if (!text.equals("")){
				app.fill(localGScheme.panelTabFont);
				app.textFont(localGScheme.gpFont, localGScheme.gpFontSize);
				app.text(text, pos.x + size + 4, pos.y + localGScheme.gpFontSize);
			}
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
