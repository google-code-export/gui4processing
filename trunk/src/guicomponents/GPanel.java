package guicomponents;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import processing.core.*;



/**
 * Core component
 * 
 * @author Peter Lager
 *
 */
public class GPanel extends GComponent implements PConstants {

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
	private ArrayList children = new ArrayList();

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
		tabHeight = gscheme.gpFontSize + 4;
		childrenPremitted = true;
		app.registerDraw(this);
		app.registerMouseEvent(this);				
	}

	public void draw(){
		app.noStroke();
		app.fill(gscheme.panelTabBG);
		app.rect(x, y - tabHeight, width, tabHeight);
		app.fill(gscheme.panelTabFont);
		app.text(text, x + 4, y - gscheme.gpFontSize / 4);
		if(!tabOnly){
			app.fill(gscheme.panelBG);
			app.rect(x,y,width,height);
		}
	}

	/**
	 * Determines whether the position ax, ay is over this component
	 * @return
	 */
	public boolean isOver(int ax, int ay){
		Point pos = new Point(0,0);
		calcAbsPosition(pos);
		if(ax >= x && ax <= x + width && ay >= y - tabHeight && ay <= y)
			return true;
		else
			return false;
	}

	public void mouseEvent(MouseEvent event){
		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(gcWithFocus == null && isOver(app.mouseX, app.mouseY)){
				gcWithFocus = this;
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			gcWithFocus = null;
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(isOver(app.mouseX, app.mouseY)){
				tabOnly = !tabOnly;
				if(!tabOnly && y + height > app.getHeight())
					y = app.getHeight() - height;
			}
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(gcWithFocus == this){
				x += (app.mouseX - app.pmouseX);
				y += (app.mouseY - app.pmouseY);
				if(x < 0) 
					x = 0;
				else if(x + width > app.getWidth()) 
					x = app.getWidth() - width;
				if(y - tabHeight < 0) 
					y = tabHeight;
				else {
					if(tabOnly){
						if(y > app.getHeight())
							y = app.getHeight();
					}
					else
					{
						if(y + height > app.getHeight()) 
							y = app.getHeight() - height;					
					}
				}
				System.out.println(gcWithFocus + " " + x + " " + y);
			}
			break;
		case MouseEvent.MOUSE_MOVED:
			break;
		}
		System.out.println(gcWithFocus);
	}

} // end of class
