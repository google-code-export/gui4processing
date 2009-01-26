package guicomponents;

import java.awt.Point;
import java.awt.event.MouseEvent;

import processing.core.PApplet;

public class GButton extends GComponent implements GUI{
	
	private int status;
	
	public GButton(PApplet theApplet, String text, int x, int y, int width, int height,
			GColor colorScheme, GFont fontScheme){
		super(theApplet, x, y, colorScheme, fontScheme);
		buttonCtorCore(text, width, height);
	}

	public GButton(PApplet theApplet, String text, int x, int y, int width, int height){
		super(theApplet, x, y);
		buttonCtorCore(text, width, height);
	}

	private void buttonCtorCore(String text, int width, int height) {
		setText(text);
		this.width = Math.max(width, textWidth + PADH * 2);
		this.height = Math.max(height, localFont.gpFontSize);
		createEventHandler(app);
		app.registerDraw(this);
		app.registerMouseEvent(this);
	}
	
	public void addEventHandler(Object obj, String methodName){
		try{
			this.eventHandler = obj.getClass().getMethod(methodName, new Class[] { GButton.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
			System.out.println("with a parameter of type GButton");
			eventHandlerObject = null;
		}
	}
	
	protected void createEventHandler(Object obj){
		try{
			this.eventHandler = obj.getClass().getMethod("handleButtonEvents", new Class[] { GButton.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			eventHandlerObject = null;
			System.out.println("You might want to add a method to handle \noption events the syntax is");
			System.out.println("void handleButtonEvents(GButton button){\n   ...\n}\n\n");
		}
	}
	
	public void draw(){
		if(visible){
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			// Select draw color
			int col;
			switch(status){
			case GUI.OVER:
				col = localColor.buttonOver;
				break;
			case GUI.DOWN:
				col = localColor.buttonDown;
				break;
			case GUI.OFF:
			default:
				col = localColor.buttonOff;
			}
			
			app.strokeWeight(1);
			app.stroke(localColor.buttonFont);			
			app.fill(col);
			app.rect(pos.x,pos.y,width,height);
			app.noStroke();
			app.fill(localColor.buttonFont);
			app.textFont(localFont.gpFont, localFont.gpFontSize);
			app.text(getText(), pos.x + (width - textWidth)/2, pos.y + (height - localFont.gpFontSize)/2, width, height);
		}
	}

	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith == null && isOver(app.mouseX, app.mouseY)){
				status = GUI.DOWN;
				this.takeFocus();
//				focusIsWith = this;
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == null && isOver(app.mouseX, app.mouseY)){
				status = GUI.OFF;
				fireEvent();
			}
			this.looseFocus();
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(focusIsWith == this){
				looseFocus();
				status = GUI.OFF;
			}
			break;
		case MouseEvent.MOUSE_MOVED:
			if(isOver(app.mouseX, app.mouseY))
				status = GUI.OVER;
			else
				status = GUI.OFF;
//			System.out.println("Button status = " + status);
		}
	}

	
}
