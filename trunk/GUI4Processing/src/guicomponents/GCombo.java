/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
	http://code.google.com/p/gui-for-processing/

  Copyright (c) 2008-09 Peter Lager

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package guicomponents;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Combo Box (drop down list) component.
 * 
 * @author Peter Lager
 *
 */
public class GCombo extends GComponent {

	protected static PImage imgArrow;

	protected int startRow;
	protected int maxRows = 5; 		// max height of list
	protected int nbrRowsToShow;	// number of rows to show

	protected GOptionGroup optGroup;
	protected GVertSlider slider;

	protected boolean expanded = false;

	/**
	 * Create the combo using the string array for the options
	 * the first option in the list is made the selected one.
	 * If you want another then use setSelected()
	 * 
	 * @param theApplet
	 * @param options
	 * @param maxRows
	 * @param x
	 * @param y
	 * @param width
	 */
	public GCombo(PApplet theApplet, String[] options, int maxRows, int x, int y, int width){
		super(theApplet, x, y);
		this.maxRows = PApplet.constrain(maxRows, 1, 25);
		comboCtorCore(width);
		createOptions(options);
		createSlider();
	}

	private void comboCtorCore(int width) {
		children = new HashSet<GComponent>();
		if(imgArrow == null)
			imgArrow = app.loadImage("combo0.png");
		this.width = width;
		this.height = localFont.size + 2 * PADV;
		opaque = true;
		border = 1;
		createEventHandler(app);
		registerAutos_DMPK(true, true, false, false);
	}

	/**
	 * Create the vertical slider for the drop down list
	 */
	private void createSlider(){
		slider = new GVertSlider(app, width - 10, height, 10, maxRows * height);
		slider.setBorder(1);
		slider.setVisible(false);
		slider.setLimits(0, 0, maxRows - 1);
		slider.addEventHandler(this, "processSliderMotion");
		add(slider);
	}

	/**
	 * Create initial options based on string array.
	 * 
	 * @param optTexts
	 */
	private void createOptions(String[] optTexts){
		optGroup = new GOptionGroup();
		GOption option;
		for(int i = 0; i < optTexts.length; i++){
			option = makeOption(optTexts[i]);
			if(option != null){
				optGroup.addOption(option);
				add(option);
			}
		}
		optGroup.setSelected(0);
		text = optGroup.selectedText();
		nbrRowsToShow = Math.min(optTexts.length, maxRows);
	}

	/**
	 * Add a new option for the text optText
	 * 
	 * @param optText
	 * @return
	 */
	public GOption makeOption(String optText){
		GOption opt = null;
		if(optText != null && !optText.equals("")){
			opt = new GOption(app, optText, 0, 0, width - 10);
			opt.addEventHandler(this, "processOptionSelection");
			opt.setVisible(false);
			opt.setOpaque(true);
			opt.setBorder(0);
		}
		return opt;
	}
	
	/**
	 * Override the default event handler created with createEventHandler(Object obj)
	 * @param obj
	 * @param methodName
	 */
	public void addEventHandler(Object obj, String methodName){
		try{
			this.eventHandler = obj.getClass().getMethod(methodName, new Class[] { GCombo.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			if(G4P.messages){
				System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
				System.out.println("with a parameter of type GCombo");
			}
			eventHandlerObject = null;
		}
	}

	/**
	 * Create an event handler that will call a method handleComboEvents(GCombo combo)
	 * when the selected option has changed
	 * @param obj
	 */
	protected void createEventHandler(Object obj){
		try{
			this.eventHandler = obj.getClass().getMethod("handleComboEvents", new Class[] { GCombo.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			if(G4P.messages){
				System.out.println("You might want to add a method to handle \ncombo events the syntax is");
				System.out.println("void handleComboEvents(GCombo combo){\n   ...\n}\n\n");
			}
			eventHandlerObject = null;
		}
	}

	/**
	 * Handle vertical slider events by changing the number of the first
	 * one to be displayed.
	 * 
	 * @param slider
	 */
	public void processSliderMotion(GSlider slider){
		startRow = slider.getValue();
	}

	/**
	 * This method is called when an option is selected from the 
	 * drop down list.
	 * 
	 * @param sOpt selected option
	 * @param dOpt deselected option
	 */
	public void processOptionSelection(GOption sOpt, GOption dOpt){
		text = optGroup.selectedText();
		if(sOpt != dOpt)
			fireEvent();
		shrink();
		looseFocus(null);
	}

	/**
	 * Set the selected option by its index value. (Starts at 0)
	 * @param index
	 */
	public void setSelected(int index){
		optGroup.setSelected(index);
	}
	
	/**
	 * Set the selected option by its text value
	 * @param optText
	 */
	public void setSelected(String optText){
		optGroup.setSelected(optText);
	}

	/**
	 * Add an option to the end of the list
	 * 
	 * @param optText
	 * @return
	 */
	public boolean addOption(String optText){
		GOption option = makeOption(optText);
		boolean ok = optGroup.addOption(option);
		if(ok)
			add(option);
		return ok;
	}

	/**
	 * Add an option in the given position
	 * 
	 * @param pos
	 * @param optText
	 * @return
	 */
	public boolean addOption(int pos, String optText){
		GOption option = makeOption(optText);
		boolean ok = optGroup.addOption(pos, option);
		if(ok)
			add(option);
		return ok;
	}

	/**
	 * Remove an option based on its index value in the list
	 * 
	 * @param index
	 */
	public void removeOption(int index){
		GOption option = optGroup.removeOption(index);
		if(option != null)
			remove(option);
	}
	
	/**
	 * Remove a value based on its text value
	 * 
	 * @param optText
	 */
	public void removeOption(String optText){
		GOption option = optGroup.removeOption(optText);
		if(option != null)
			remove(option);
	}
	
	/**
	 * Determines whether the position ax, ay is over the expand arrow 
	 * or over the expanded combo box, depending on whether the box 
	 * is expanded or not.
	 * 
	 * @return true if mouse is over the appropriate part
	 */
	public boolean isOver(int ax, int ay){
		Point p = new Point(0,0);
		calcAbsPosition(p);
		int x1, x2, y1, y2;
		if(expanded){
			x1 = p.x;
			y1 = p.y;
			x2 = x1 + width;
			y2 = y1 + (nbrRowsToShow + 1)*height;
		}
		else {
			x1 = p.x + width - imgArrow.width - PADH;
			y1 = p.y + (height - imgArrow.height)/2 + PADV;
			x2 = x1 + imgArrow.width;
			y2 = y1 + imgArrow.height;
		}
		return (ax >= x1 && ax <= x2 && ay >= y1 && ay <= y2); 
	}

	/**
	 * Close the drop down list
	 */
	public void shrink(){
		expanded = false;
		for(int i = 0; i < optGroup.size(); i++)
			optGroup.get(i).visible = false;
	}

	/**
	 * Open the drop down list
	 */
	public void expand(){
		expanded = true;
		startRow = 0;
		slider.setLimits(0, 0, optGroup.size() - maxRows);
		takeFocus();
	}

	/**
	 * Is the drop down list visible
	 * @return
	 */
	public boolean isExpanded(){
		return expanded;
	}

	/**
	 * If we loose the focus to another GUI component
	 * that is not a child of this, then shrink the drop
	 * down list and release focus
	 */
	public void looseFocus(GComponent grabber){
		if(!children.contains(grabber)){
			shrink();
			focusIsWith = null;
		}
	}

	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		if(!visible) return;
		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && isOver(app.mouseX, app.mouseY))
				takeFocus();
			else if(focusIsWith == this && !isOver(app.mouseX, app.mouseY))
				looseFocus(null);
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == this && isOver(app.mouseX, app.mouseY)){
				if(expanded)
					shrink();
				else
					expand();
				mdx = mdy = Integer.MAX_VALUE;
			}
			break;
		}
	}

	/**
	 * Draw the combo box
	 */
	public void draw(){
		if(!visible) return;

		app.pushStyle();
		app.style(G4P.g4pStyle);
		Point pos = new Point(0,0);
		calcAbsPosition(pos);

		// Draw selected option area
		if(border == 0)
			app.noStroke();
		else {
			app.strokeWeight(border);
			app.stroke(localColor.txfBorder);
		}
		if(opaque)
			app.fill(localColor.txfBack);
		else
			app.noFill();
		app.rect(pos.x, pos.y, width, height);
		
		// Draw selected text
		app.noStroke();
		app.fill(localColor.txfFont);
		app.textFont(localFont, localFont.size);
		app.text(text, pos.x + PADH, pos.y -PADV +(height - localFont.size)/2, width - 16, height);

		// draw drop down list
		app.fill(app.color(255,255));
		app.image(imgArrow, pos.x + width - imgArrow.width - 1, pos.y + (height - imgArrow.height)/2);
		if(expanded == true){
			GOption opt;
			app.noStroke();
			app.fill(localColor.txfBack);
			app.rect(pos.x,pos.y+height,width,nbrRowsToShow*height);

			for(int i = 0; i < optGroup.size(); i++){
				opt = optGroup.get(i);
				if(i >= startRow && i < startRow + nbrRowsToShow){
					opt.visible = true;
					opt.y = height * (i - startRow + 1);
					opt.draw();
				}
				else {
					opt.visible = false;					
				}
			}
			// Draw box round list
			if(border != 0){
				app.strokeWeight(border);
				app.stroke(localColor.txfBorder);
				app.noFill();
				app.rect(pos.x,pos.y+height,width,nbrRowsToShow*height);
			}
			if(optGroup.size() > maxRows){
				slider.setVisible(true);
				slider.draw();
			}
		}
		app.popStyle();
	}

	public int selectedIndex(){
		return optGroup.selectedIndex();
	}

	public String selectedText(){
		return optGroup.selectedText();
	}

	public int deselectedIndex(){
		return optGroup.deselectedIndex();
	}

	public String deselectedText(){
		return optGroup.deselectedText();
	}

}
