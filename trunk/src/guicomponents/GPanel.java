package guicomponents;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import processing.core.*;

/**
 * Core component
 * 
 * @author Peter Lager
 *
 */
public class GPanel extends GComponent {

	/** The height of the tab calculated from font size */
	protected int tabHeight;

	/** Whether the panel is displayed in full or tab only */
	protected boolean tabOnly = true;

	/** Minimum width and height of component in pixels based on child components */
	protected int minWidth = 20, minHeight = 20;

	/** Surface area of this component */
	protected Rectangle area = new Rectangle();


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
	 * @param colorScheme colors to be used
	 * @param fontScheme font to be used
	 * @param x horizontal position
	 * @param y vertical position
	 * @param minWidth minimum width of the panel
	 * @param minHeight minimum height of the panel (excl. tab)
	 */
	public GPanel(PApplet theApplet, String text, int colorScheme, int fontScheme, 
			int x, int y, int minWidth, int minHeight){
		super(theApplet, colorScheme, fontScheme, x, y);
		this.text = text;
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		this.width = minWidth;
		this.height = minHeight;
		tabHeight = globalGScheme.gpFontSize + 4;
		childrenPremitted = true;
		app.registerDraw(this);
		app.registerMouseEvent(this);
	}

	public boolean addComponent(GComponent component){
		component.parent = this;
		children.add(component);
		app.unregisterDraw(component);
		return true;
	}

	public boolean addComponent(GComponent component, int posX, int posY){
		return true;
	}

	public void draw(){
		Point pos = new Point(0,0);
		calcAbsPosition(pos);
		app.noStroke();
		app.fill(globalGScheme.panelTabBG);
		app.rect(pos.x, pos.y - tabHeight, width, tabHeight);
		app.fill(globalGScheme.panelTabFont);
		app.text(text, pos.x + 4, pos.y - globalGScheme.gpFontSize / 4);
		if(!tabOnly){
			app.fill(globalGScheme.panelBG);
			app.rect(pos.x, pos.y, width, height);
			Iterator<GComponent> iter = children.iterator();
			while(iter.hasNext()){
				iter.next().draw();
			}
		}
	}

	/**
	 * No object has the mouse focus so seek one and remember it
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
				if(x < 0) 
					x = 0;
				else if(x + width > app.getWidth()) 
					x = app.getWidth() - width;
				if(y - tabHeight < 0) 
					y = tabHeight;
				else {
					if(tabOnly)
						if(y > app.getHeight())	y = app.getHeight();
					else
						if(y + height > app.getHeight()) y = app.getHeight() - height;
				}
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
		if(ax >= p.x && ax <= p.x + width && ay >= p.y - tabHeight && ay <= p.y){
			System.out.println("Is Over "+this);
			return true;
		}
		else {
			return false;
		}
	}


	/**
	 * For GPanel set children
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
		Iterator<GComponent> iter = children.iterator();
		while(iter.hasNext()){
			iter.next().setVisible(visible);
		}
	}

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
