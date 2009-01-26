package guicomponents;

import java.awt.Point;
import java.awt.event.MouseEvent;

import processing.core.PApplet;
import processing.core.PImage;

public class GOption extends GComponent implements Comparable {
	/**
	 * All GOption objects should belong to a group
	 */
	protected GOptionGroup group;
	
	// Images used for selected/deselected option
	protected static PImage imgSelected;
	protected static PImage imgCleared;
	
	
	public GOption(PApplet theApplet, String text, int x, int y, int width, int align,
			GColor colorScheme, GFont fontScheme){
		super(theApplet, x, y, colorScheme, fontScheme);
		optionCtorCore(text, width, align);
	}

	public GOption(PApplet theApplet, String text, int x, int y, int width,
			GColor colorScheme, GFont fontScheme){
		super(theApplet, x, y, colorScheme, fontScheme);
		optionCtorCore(text, width, GUI.LEFT);
	}

	public GOption(PApplet theApplet, String text, int x, int y, int width){
		super(theApplet, x, y);
		optionCtorCore(text, width, GUI.LEFT);
	}

	public GOption(PApplet theApplet, String text, int x, int y, int width, int align){
		super(theApplet, x, y);
		optionCtorCore(text, width, align);
	}

	private void optionCtorCore(String text, int width, int align){
		this.width = width;
		height = localFont.gpFontSize + 2;
		setText(text, align);
		if(imgSelected == null)
			imgSelected = app.loadImage("radio1.png");
		if(imgCleared == null)
			imgCleared = app.loadImage("radio0.png");
		createEventHandler(app);
		app.registerDraw(this);
		app.registerMouseEvent(this);
	}
	
	public void addEventHandler(Object obj, String methodName){
		try{
			this.eventHandler = obj.getClass().getMethod(methodName, new Class[] { GOption.class, GOption.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
			System.out.println("with a two parameters of type GOption");
			eventHandlerObject = null;
		}
	}
	
	protected void createEventHandler(Object obj){
		try{
			this.eventHandler = obj.getClass().getMethod("handleOptionEvents", new Class[] { GOption.class, GOption.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			eventHandlerObject = null;
			System.out.println("You might want to add a method to handle \noption events the syntax is");
			System.out.println("void handleOptionEvents(GOption selected, GOption deselected){\n   ...\n}\n\n");
		}
	}
	
	public void draw(){
		if(visible){
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			if (!getText().equals("")){
				app.noStroke();
				app.fill(localColor.panelTabFont);
				app.textFont(localFont.gpFont, localFont.gpFontSize);
				app.text(getText(), pos.x + 20, pos.y + PADV, textWidth, height);
			}
			app.fill(app.color(255,255));
			if(group != null && group.getSelected() == this)
				app.image(imgSelected, pos.x, pos.y);
			else
				app.image(imgCleared, pos.x, pos.y);
		}
	}

	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		// If this option does not belong to a group then ignore mouseEvents
		if(group == null)
			return;
		
		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith == null && isOver(app.mouseX, app.mouseY))
				this.takeFocus();
//				focusIsWith = this;
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == null && isOver(app.mouseX, app.mouseY)){
				group.setSelected(this);
				fireEvent();
			}
			this.looseFocus();
//			focusIsWith = null;
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(focusIsWith == this){
				this.looseFocus();
//				focusIsWith = null;
			}
			break;
		}
	}

	/**
	 * Attempt to fire an event for this component
	 * 
	 */
	protected void fireEvent(){
		if(eventHandler != null){
			try {
				eventHandler.invoke(eventHandlerObject, 
						new Object[] { this, group.getDeselected() });
			} catch (Exception e) {
				System.out.println("Disabling " + eventHandler.getName() + " due to an error");
				eventHandler = null;
				eventHandlerObject = null;
			}
		}		
	}

	public boolean isSelected(){
		return (group != null && group.getSelected() == this);
	}
	
	public boolean isNotSelected(){
		return !(group != null && group.getSelected() == this);		
	}
	
	public void setSelected(boolean selected){
		if(group != null){
			group.setSelected(this);
		}
	}
	
	public GOptionGroup getGroup(){
		return group;
	}
	
	/**
	 * Set the option group - at the present this method does not allow the option
	 * to be moved from one group to another.
	 * @param group
	 */
	public void setGroup(GOptionGroup group){
		this.group = group;
		//group.addOption(this);
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return new Integer(this.hashCode()).compareTo(new Integer(o.hashCode()));
	}

	
}
