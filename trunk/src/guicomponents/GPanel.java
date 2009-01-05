package guicomponents;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;

/**
 * Core component
 * 
 * @author Peter Lager
 *
 */
public class GPanel extends GComponent {

	/** Whether the panel is displayed in full or tab only */
	protected boolean tabOnly = true;

	/** Is panel body opaque */
	protected boolean opaque = false;
	
	/** Surface area of this component */
	//protected Rectangle area = new Rectangle();


	/**
	 * A list of child panels
	 * Only true for PPanel classes (change to true in ctors for this class)
	 */
	private ArrayList<GComponent> children = new ArrayList<GComponent>();

	/**
	 * Create a Panel that comprises of 2 parts the tab which is used to 
	 * select and move the panel and the container window below the tab which 
	 * is used to hold other components.
	 * The size of the container window will grow to fit components added
	 * provided that it does not exceed the width and height of the applet
	 * window.
	 *  
	 * @param theApplet the PApplet reference
	 * @param text to appear on tab
	 * @param x horizontal position
	 * @param y vertical position
	 * @param width minimum width of the panel
	 * @param height minimum height of the panel (excl. tab)
	 * @param colorScheme color to be used
	 * @param fontScheme font to be used
	 */
	public GPanel(PApplet theApplet, String text, int x, int y, int width, int height,
			GColor colorScheme, GFont fontScheme){
		super(theApplet, x, y, colorScheme, fontScheme);
		this.text = text;
//		this.minWidth = width;
//		this.minHeight = height;
		this.width = width;
		this.height = height;
		app.registerDraw(this);
		app.registerMouseEvent(this);
	}

	/**
	 * Create a Panel that comprises of 2 parts the tab which is used to 
	 * select and move the panel and the container window below the tab which 
	 * is used to hold other components.
	 * The size of the container window will grow to fit components added
	 * provided that it does not exceed the width and height of the applet
	 * window.
	 *  
	 * @param theApplet the PApplet reference
	 * @param text to appear on tab
	 * @param x horizontal position
	 * @param y vertical position
	 * @param width minimum width of the panel
	 * @param height minimum height of the panel (excl. tab)
	 */
	public GPanel(PApplet theApplet, String text, int x, int y, int width, int height){
		super(theApplet, x, y);
		this.text = text;
//		this.minWidth = width;
//		this.minHeight = height;
		this.width = width;
		this.height = height;
		app.registerDraw(this);
		app.registerMouseEvent(this);
	}

	/**
	 * Add a GUI component to this Panel at the position specified by
	 * component being added.
	 * Unregister the component for drawing this is managed by the 
	 * Panel draw method to preserve z-ordering
	 * 
	 * @return always true
	 */
	public boolean addComponent(GComponent component){
		// TODO need to validate addition based on size
		component.parent = this;
		children.add(component);
		app.unregisterDraw(component);
		return true;
	}

	/**
	 * Draw the panel tab.
	 * If tabOnly == false then also draw all child (added) components
	 */
	public void draw(){
		if(visible){
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			app.noStroke();
			app.fill(localColor.panelTabBG);
			app.rect(pos.x, pos.y - (localFont.gpFontSize + 4), width, (localFont.gpFontSize + 4));
			app.fill(localColor.panelTabFont);
			app.textFont(localFont.gpFont, localFont.gpFontSize);
			app.text(text, pos.x + 4, pos.y - localFont.gpFontSize / 4);
			if(!tabOnly){
				if(opaque){
					app.fill(localColor.panelBG);
					app.rect(pos.x, pos.y, width, height);
				}
				Iterator<GComponent> iter = children.iterator();
				while(iter.hasNext()){
					iter.next().draw();
				}
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
				tabOnly = !tabOnly;
				if(!tabOnly && y + height > app.getHeight())
					y = app.getHeight() - height;
			}
			mouseFocusOn = null;
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(mouseFocusOn == this){
				mouseFocusOn = null;
			}
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(mouseFocusOn == this && parent == null){
				x += (app.mouseX - app.pmouseX);
				y += (app.mouseY - app.pmouseY);
				// Constrain horizontally
				if(x < 0) 
					x = 0;
				else if(x + width > app.getWidth()) 
					x = app.getWidth() - width;
				// Constrain vertically
				if(y - (localFont.gpFontSize + 4) < 0) 
					y = (localFont.gpFontSize + 4);
				else if(tabOnly && y > app.getHeight())
					y = app.getHeight();
				else if(!tabOnly && y + height > app.getHeight()) 
					y = app.getHeight() - height;
			}
			break;
		}
	}


	/**
	 * Determines whether the position ax, ay is over the tab
	 * of this GPanel.
	 * @return true if mouse is over the panel tab else fale
	 */
	public boolean isOver(int ax, int ay){
		Point p = new Point(0,0);
		calcAbsPosition(p);
		if(ax >= p.x && ax <= p.x + width && ay >= p.y - (localFont.gpFontSize + 4) && ay <= p.y)
			return true;
		else 
			return false;
	}


	/**
	 * For GPanel set children
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setOpaque(boolean opaque){
		this.opaque = opaque;
	}
	
	/**
	 * Used for debugging only
	 */
	public void display(){
		Point p = new Point(0,0);
		calcAbsPosition(p);
		System.out.print(this + "    ABS ( "+p.x+" "+p.y+" )   ");
		System.out.println(" FOCUS "+mouseFocusOn);
	}

	public String toString(){
		return new String("GPanel "+text + "  ( "+x+" "+y+" ) ");
	}

} // end of class
