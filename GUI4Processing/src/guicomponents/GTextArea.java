/*
  Part of the GUI for Processing library
  http://gui4processing.lagers.org.uk
  http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2008-09 Peter Lager

  The string handling and clipboard logic has been taken from a similar
  GUI library Interfascia ALPHA 002 -- http://superstable.net/interfascia/
  produced by Brenden Berg
  This code had to be modified to correct some logic errors in selecting text
  using the mouse and the shift+cursor keys. The biggest change is in the
  usage of startSelect and endSelect variables. In the original code if either
  had the value -1 then no text was selected. In this class if they have the same
  value i.e. startSelect == endSelect then no text is selected. Also both will
  equal the cursorPos UNLESS we are selecting text. This has simplified the code.

  Other modifications have been made in the way it handles events, draws itself
  and focus handling to fit in with my library which supports floating panels.

  Final modifications made by Brendan Nichols, March 15, 2010 to support multi-line text.

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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import processing.core.PApplet;

/**
 * The text field component.
 *
 * Enables user text input at runtime. Text can be selected using the mouse
 * or keyboard shortcuts and then copied or cut to the clipboard. Text
 * can also be pasted in. It supports text that is longer than the displayable
 * area
 *
 * @author Brendan Nichols  & Peter Lager
 *
 */
public class GTextArea extends GComponent {

	//Define all the private variables needed to keep track of view window and selection and etc.
	private int cursorPos = 0; 						//stores the cursor's position in the 1D string
	private int startSelect = -1, endSelect = -1; 	//stores where the selection start/end are
	public int startX = 0, startY = 0, endY = 0; 	//stores where to begin showing text and end showing text
	private float leading;							//space between lines

	private boolean drawSepLines = true; 			//whether or not to draw separating lines between each 
	// line, really only needed in my application

	public String toString(){
		return ("Cursor pos " +cursorPos + "   sX " + startX+"   sY "+startY+"   eY "+ endY);
	}
	
	/**
	 * Creates a GTextField object
	 * @param theApplet
	 * @param text initial text to display
	 * @param x horizontal position relative to PApplet or PPanel
	 * @param y vertical position relative to PApplet or PPanel
	 * @param width width of text field
	 * @param height height of text field
	 */
	public GTextArea(PApplet theApplet, String text, int x, int y, int width, int height){
		super(theApplet, x, y); 					//applet set as super something?
		textFieldCtorCore(text, width, height); 	//call the function below to create textbox?
	}

	/**
	 * Common code required by constructors
	 * @param text initial text to display
	 * @param width width of text field
	 * @param height height of text field
	 */
	private void textFieldCtorCore(String text, int width, int height) {
		this.width = Math.max(width, textWidth + PADH * 2); 						//set to either the spec. width or the width of the box plus horz. padding
		this.height = Math.max(height, localFont.getFont().getSize() + PADV * 2); 	//set to either the spec. height or the height of font plus vert. padding
		border = 1;
		leading = (int)(1.5f * localFont.getFont().getSize()); 						//default line leading is 1.5 times the font height
		endY = (int)Math.floor(this.height / leading); 								//calculate the number of lines that can be displayed fully
		// Set text AFTER the width of the textfield has been set
		setText(text); 		
		createEventHandler(winApp, "handleTextFieldEvents", new Class[]{ GTextArea.class }); //create the event handler for user to code
		registerAutos_DMPK(true, true, false, true);
		winApp.textFont(localFont, localFont.getFont().getSize());
	}

	/**
	 * Set the font, size and leading for the textfield. <br>
	 * This will not alter the size of the textfield so use with caution
	 * if the font is too tall it may not be possible to see anything
	 * ADDED: font leading input which will change the spacing between lines
	 * @param fontname
	 * @param fontSize
	 * @param fontLeading
	 */
	public void setFont(String fontname, int fontSize, float fontLeading){
		localFont = GFont.getFont(winApp, fontname, fontSize); 	//possible change in font size
		leading = Math.max(fontSize, fontLeading); 				//update the line leading
		endY = (int) Math.floor(this.height / fontLeading); 	//change the number of lines that can be displayed fully

		setText(text);
		winApp.textFont(localFont, localFont.getFont().getSize());
	}

	
	/**
	 * When the textfield loses focus it also loses any text selection.
	 */
	protected void looseFocus(GComponent toThis){ //spelling, but don't know if I can change
		startSelect = endSelect = -1; //reset text selection to nothing
		focusIsWith = null; //reset focus to nothing
	}

	/**
	 * adds a character to the immediate right of the insertion point or replaces the selected
	 * group of characters. This method is called by <pre>public void MouseEvent</pre> if a unicode
	 * character is entered via the keyboard.
	 * @param c the character to be added
	 */
	private void appendToRightOfCursor(char c) {
		appendToRightOfCursor("" + c); //just converts a character to string and passes to function below
	}

	/**
	 * adds a string to the immediate right of the insertion point or replaces the selected group
	 * of characters.
	 * @param s the string to be added
	 */
	private void appendToRightOfCursor(String s) {
		String t1, t2;
		if(startSelect != endSelect) { //if bunch of text is selected
			int start = Math.min(startSelect, endSelect); //make sure start < end
			int end = Math.max(startSelect, endSelect); //make sure end > start
			t1 = text.substring(0, start); //take text up until start of selection
			t2 = text.substring(end); //take text past end of selection
			startSelect = endSelect = cursorPos = start; //put cursor at start of added text, then later we add the length of text
		} else {
			t1 = text.substring(0, cursorPos); //take text up until cursor position
			t2 = text.substring(cursorPos); //take text past cursor position
		}

		text = t1 + s + t2; //appended text
		cursorPos += s.length(); //set cursor position to end of appended text
		startSelect = endSelect = cursorPos; //reset any selection but put start and end select at cursor

		// refresh view to include cursor

		eventType = CHANGED;
		fireEvent();
	}

	/**
	 * deletes either the character directly to the left of the insertion point
	 * or the selected group of characters. It automatically handles cases where
	 * there is no character to the left of the insertion point (when the
	 * insertion point is at the beginning of the string). It is called by
	 * <pre>public void keyEvent</pre> when the delete key is pressed.
	 */
	protected void backspaceChar() {
		if(startSelect != endSelect) {//if a bunch of text is selected
			deleteSubstring(startSelect, endSelect); //delete substring function with range of selected text
		} else if(cursorPos > 0){//no selection and can't be at the beginning of the text
			deleteSubstring(cursorPos - 1, cursorPos);//delete one character to the left of cursor
		}
	}

	private void deleteChar() {
		if(startSelect != endSelect) { //if a bunch of text is selected
			deleteSubstring(startSelect, endSelect); //delete substring function with range of selected text
		} else if(cursorPos < text.length()){ //no selection and can't be at the end of the text
			deleteSubstring(cursorPos, cursorPos + 1); //delete one character to right of cursor
		}
	}

	private void deleteSubstring(int startString, int endString) {
		int start = Math.min(startString, endString); //make sure start < end
		int end = Math.max(startString, endString); //make sure end > start
		text = text.substring(0, start) + text.substring(end); //replace text with subsets of surrounding text

		startSelect = endSelect = cursorPos = start; //reset cursor position to start of string
		//refresh view to include cursor

		eventType = CHANGED;
		fireEvent();
	}

	/**
	 * Copy string to clipboard
	 * @param start
	 * @param end
	 */
	private void copySubstring(int start, int end) {
		int s = Math.min(start, end); //make sure start is less than end
		int e = Math.max(start, end); //make sure end is greater than start
		GClip.copy(text.substring(s, e)); //copy selected text to clipboard
	}

	/**
	 * Returns the x and y position of the cursor (measured in characters)
	 */
	private Point cursorPosition(int pos){
		String preCursor = text.substring(0, pos); //take text up until the cursor position
		int cursorPosY = (preCursor.length() - preCursor.replaceAll("\n", "").length()); //quick code for counting occurrence of "\n"'s;
		int cursorPosX = preCursor.length() - preCursor.lastIndexOf("\n") - 1; //number of characters to the cursor on this line
		return new Point(cursorPosX, cursorPosY);
	}

	/**
	 * Returns the x and y position of the cursor relative to upper corner of textbox (measured in pixels)
	 */
	private Point cursorPixPosition(int pos){
		String preCursor = text.substring(0, pos); //take text up until the cursor position
		String thisline = preCursor.substring(Math.max(preCursor.lastIndexOf("\n")+1, 0), preCursor.length()); //text of the current line

		int cursorPixX = (int)winApp.textWidth(thisline.substring(startX, thisline.length())); //pixel width of characters on this line
		int cursorPixY = (int) ((preCursor.length() - preCursor.replaceAll("\n", "").length() - startY) * leading); //pixel position is integer line number times line height
		return new Point(cursorPixX, cursorPixY);
	}

	/**
	 * Converts an x, y position(characters) of the cursor to its position in the 1D text
	 */
	private int cursorPos1D(Point c){
		String[] splittext = text.split("\n"); //split the text into lines

		int sumLinesBefore = 0; //will add up to the number of characters in previous lines
		for(int i = 0; i < c.y; i++){
			sumLinesBefore += splittext[i].length() + 1; //sum all characters in the line plus the \n character which got dropped in split
		}
		return sumLinesBefore + c.x; //add the characters in current line to all ones in previous lines
	}

	/**
	 * Converts an x, y position(pixels) relative to corner of textbox to an x,y cursor index
	 */
	private Point cursorPos2D(int x, int y){
		x -= 4; y -= 4; //subtract the padding from the cursor position
		String[] splittext = text.split("\n"); //split the text into lines
		int cursorY = (int) ((int) y / leading + startY); //take nearest integer multiple of line height and add to starting line number

		//if the click was out of the range, return the current cursor pos.
		if(cursorY >= splittext.length){
			return cursorPosition(cursorPos);
		} 
		//return 0,0 if outside the lowest range.
		if(cursorY < 0){
			return new Point(0,0);
		} 

		float prev = 0, cur; //measures of line widths
		for(int i = startX; i < splittext[cursorY].length(); i++){ //loop from visible start to end of line
			cur = winApp.textWidth(splittext[cursorY].substring(startX, i)); //set current width of line
			if(cur > x){ //if current width exceeds the location of the mouse
				if(cur - x < x - prev){ //decide whether it's closer to stay or go back one character
					return new Point (i, cursorY);
				} else{
					return new Point (Math.max(0, i-1), cursorY);
				}
			}
			prev = cur;
		}
		return new Point(splittext[cursorY].length(), cursorY); //if the x was out of bounds, return the end of the line
	}

	/**
	 * Returns the string of text that the text box can display
	 */
	public String viewText() {
		showCursor();
		String[] splittext = PApplet.split(text, "\n"); //split the text into its separate lines
		String[] cuttext = new String[endY - startY]; //start a new array that will hold just the visible lines

		for(int i = startY; i < endY; i++){ //take from startY line to endY line
			if(i < splittext.length){
				float twidth = winApp.textWidth(splittext[i].substring(Math.min(startX, splittext[i].length())));
				int charWidth = splittext[i].length()-1; //initialize for the loop, which will determine how many characters can fit on one line
				while(twidth > width - 8){
					float w = winApp.textWidth(splittext[i].substring(charWidth, charWidth+1));
					charWidth--;
					twidth -= w;
				}
				charWidth++;
				// take the viewable text on the current line
				cuttext[i-startY] = splittext[i].substring(Math.min(startX, splittext[i].length()), 
						Math.min(charWidth, splittext[i].length()));
			}
			else {
				cuttext[i-startY] = "";
			}
		}
		// Join lines together again with line breaks
		String jointext = PApplet.join(cuttext, "\n"); 
		return jointext;
	}

	/**
	 * Places the cursor within the textbox view.
	 * Do as little moving as possible to keep it in.
	 */

	private void showCursor(){
		// cursor went out to the left move the text until inside
		if(cursorPosition(cursorPos).x < startX){
			while (cursorPosition(cursorPos).x < startX){
				startX --;
			}
		}
		//cursor went out to the right move the text until inside
		if(cursorPixPosition(cursorPos).x > width - 8){
			while (cursorPixPosition(cursorPos).x > width - 8){
				startX ++;
			}
		}
		// cursor went out at the top move the text until inside
		if(cursorPosition(cursorPos).y < startY){ 	
System.out.println(tag+" showCursor decY:  > "+this + " "+ cursorPosition(cursorPos).y);			
			while (cursorPosition(cursorPos).y < startY){ 
				startY --;
				endY --;
			}
System.out.println(tag+ "showCursor decY:  > "+this+ " "+ cursorPosition(cursorPos).y);			
		}
		// cursor went out past the bottom move the text until inside
		if(cursorPosition(cursorPos).y > endY - 2){ 		
System.out.println(tag+" showCursor incY:  > "+this+ " "+ cursorPosition(cursorPos).y);			
			while (cursorPosition(cursorPos).y > endY - 2){
				startY ++;
				endY ++;
			}
System.out.println(tag+"  showCursor incY:  < "+this+ " "+ cursorPosition(cursorPos).y);			
		}
	}

	/**
	 * Sets the contents of the text box and displays the
	 * specified string in the text box widget. <br>
	 * Fires SET event.
	 * @param val the string to become the text field's contents
	 */
	public void setText(String newValue) {
		winApp.textFont(localFont, localFont.getFont().getSize()); //set the font used
		text = newValue; //change the text to whatever the user specified

		//reset the selection and cursor positions
		startSelect = endSelect = -1;
		cursorPos = 0;
		
		eventType = SET;
		fireEvent();
	}

	public void setStartX(int newx) {
		startX = newx;
	}

	public void setStartY(int newy) {
System.out.println("setStartY:  > "+this);
		int diff = endY - startY; 						//the difference in the start and end
		startY = newy; 									//change the start
		cursorPos = cursorPos1D(new Point(0, startY)); 	//put the cursor at the start of the new line so showCursor doesn't move the view back
		endY = startY + diff; 							//update the end accordingly
System.out.println("setStartY:  < "+this);
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public int getEndY() {
		return endY;
	}

	public void setDrawLines(boolean drawlines){
		drawSepLines = drawlines;
	}

	/**
	 * Mouse event handler - the focus cannot be lost by anything
	 * we do here - it has to be taken away when the mouse is pressed
	 * somewhere else.
	 *
	 * @param e the MouseEvent to handle
	 */

	public void mouseEvent(MouseEvent e) {
		if(!visible || !enabled) return; //don't do anything if invisible or disabled

		boolean mouseOver = isOver(winApp.mouseX, winApp.mouseY); //sets whether or not the mouse is even over the textbox
		if(mouseOver || focusIsWith == this) //if the mouse is over or the textbox has focus
			cursorIsOver = this; //set that the cursor is over the textbox
		else if(cursorIsOver == this) //if the cursor is over the box
			cursorIsOver = null; //set the cursor over to nothing

		Point p = new Point(0,0); //set the top-left point?
		calcAbsPosition(p); //find its absolute position in the parent window?

		switch(e.getID()){//select for mouse event
		case MouseEvent.MOUSE_PRESSED:
			if(isOver(winApp.mouseX, winApp.mouseY)){ //if actually over textbox
				if(focusIsWith != this){ //if focus is not on box
					this.takeFocus(); //give focus to the box
				}
//System.out.println(tag+ " > "+ this);
				startSelect = endSelect = cursorPos = cursorPos1D(cursorPos2D(e.getX() - p.x, e.getY() - p.y));//set the cursor position to the nearest location to where the user clicked
//System.out.println(tag+ " > "+ this);
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			// Nothing to do
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){ //if text has focus
				endSelect = cursorPos = cursorPos1D(cursorPos2D(e.getX() - p.x, e.getY() - p.y)); //put the selection end at the closest gap
			}
		}
	}

	/**
	 * receives KeyEvents forwarded to it by the GUIController
	 * if the current instance is currently in focus.
	 * @param e the KeyEvent to be handled
	 */
	public void keyEvent(KeyEvent e) {
		if(!enabled) return;
//		PApplet.println("Show text " + startX + " " + startY + " " + endY);
		if(focusIsWith == this){ //if the textbox has focus
			winApp.textFont(localFont, localFont.getFont().getSize()); //set the font so we can get the widths of everything

			int shortcutMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); //init some java toolbox or something?
			boolean shiftDown = ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK); //simplify SHIFT check?

			if(e.getID() == KeyEvent.KEY_PRESSED) {//if a key is pressed
				if(e.getKeyCode() == KeyEvent.VK_END) {
					if(shiftDown) { // Select to end of text
						if(text.indexOf("\n", cursorPos) != -1){
							cursorPos = text.indexOf("\n", cursorPos);
						} else{
							cursorPos = text.length();
						}
						endSelect = cursorPos;
					} else { // Move cursor to end of text
						if(text.indexOf("\n", cursorPos) != -1){
							cursorPos = text.indexOf("\n", cursorPos);
						} else{
							cursorPos = text.length();
						}
						startSelect = endSelect = cursorPos;
					}
				}
				else if(e.getKeyCode() == KeyEvent.VK_HOME) {
					if(shiftDown) { // select to start of text
						if(text.lastIndexOf("\n", cursorPos) != cursorPos){
							cursorPos = text.lastIndexOf("\n", cursorPos) + 1;
						} else{
							cursorPos = text.lastIndexOf("\n", cursorPos-1) + 1;
						}
						startSelect = cursorPos;
					} else { //Move cursor to start of text
						if(text.lastIndexOf("\n", cursorPos) != cursorPos){
							cursorPos = text.lastIndexOf("\n", cursorPos) + 1;
						} else{
							cursorPos = text.lastIndexOf("\n", cursorPos-1) + 1;
						}
						startSelect = endSelect = cursorPos;
					}
				}
				else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
					if(shiftDown) { // selecting text to left
						if(cursorPos > 0) { //don't go past the beginning
							cursorPos --;
							endSelect = cursorPos;
						}
					} else { // moving cursor left
						if(cursorPos > 0){ //don't go past the beginning
							cursorPos --;
						}
						startSelect = endSelect = cursorPos;
					}
				}
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if(shiftDown) { // selecting text to right
						if(cursorPos < text.length()) { //don't go past the end
							cursorPos ++;
							endSelect = cursorPos;
						}
					} else { // moving cursor right
						if(cursorPos < text.length()){ //don't go past the end
							cursorPos ++;
						}
						startSelect = endSelect = cursorPos;
					}
				}
				else if(e.getKeyCode() == KeyEvent.VK_UP) {
					String[] splittext = text.split("\n"); //to get the total number of lines
					if(shiftDown) { // selecting text upward
						if(cursorPosition(cursorPos).y > 0){//don't go past beginning
							cursorPos = cursorPos1D(new Point(Math.min(cursorPosition(cursorPos).x,splittext[cursorPosition(cursorPos).y - 1].length()), cursorPosition(cursorPos).y - 1));
							startSelect = cursorPos;
						}
					} else { // moving cursor upward
						if(cursorPosition(cursorPos).y > 0){//don't go past beginning
							cursorPos = cursorPos1D(new Point(Math.min(cursorPosition(cursorPos).x,splittext[cursorPosition(cursorPos).y - 1].length()), cursorPosition(cursorPos).y - 1));
						}
						startSelect = endSelect = cursorPos;
					}
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					String[] splittext = text.split("\n"); //to get the total number of lines
					if(shiftDown) { // selecting text downward
						if(cursorPosition(cursorPos).y < splittext.length - 1){//don't go past end
							cursorPos = cursorPos1D(new Point(Math.min(cursorPosition(cursorPos).x,splittext[cursorPosition(cursorPos).y + 1].length()), cursorPosition(cursorPos).y + 1));
							endSelect = cursorPos;
						}
					} else { // moving cursor upward
						if(cursorPosition(cursorPos).y < splittext.length - 1){//don't go past end
							cursorPos = cursorPos1D(new Point(Math.min(cursorPosition(cursorPos).x,splittext[cursorPosition(cursorPos).y + 1].length()), cursorPosition(cursorPos).y + 1));
						}
						startSelect = endSelect = cursorPos;
					}
				}
				else if(e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteChar(); //delete the character to the right
				}
				else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					appendToRightOfCursor("\n"); //add linefeed to right of cursor
					eventType = ENTERED;
					fireEvent();
				}
				else{
					if((e.getModifiers() & shortcutMask) == shortcutMask) {//shortcut handling
						switch (e.getKeyCode()) {
						case KeyEvent.VK_C:
							if(startSelect != endSelect) { //only copy if there is selected text
								copySubstring(startSelect, endSelect); //copy text code
							}
							break;
						case KeyEvent.VK_V:
							appendToRightOfCursor(GClip.paste()); //paste copied code at cursor position
							break;
						case KeyEvent.VK_X:
							if(startSelect != endSelect) { //only cut if there is selected text
								copySubstring(startSelect, endSelect); //copy selection
								deleteSubstring(startSelect, endSelect); //delete selection
							}
							break;
						case KeyEvent.VK_A:
							//set selection limits to beginning and end of text
							startSelect = 0;
							endSelect = text.length();
							break;
						}
					}
				}
			}
			else if(e.getID() == KeyEvent.KEY_TYPED) { //key entered was typed rather than a special code?
				if((e.getModifiers() & shortcutMask) == shortcutMask) {} //don't do anything for shortcuts?
				else if(e.getKeyChar() == '\b') {
					backspaceChar(); //call backspace function
				}
				else if(e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) { //if entered character is not undefined (i.e. it is defined)
					if(validUnicode(e.getKeyChar())) //if valid unicode character
						appendToRightOfCursor(e.getKeyChar()); //add typed character at cursor
				}
			}
		}
	}

	/**
	 * draws the text field, contents, selection, and cursor
	 * to the screen.
	 */
	public void draw () {
		if(!visible) return; // don't draw if visible is false

		winApp.pushStyle(); //guessing this is like pushmatrix but for style
		winApp.style(G4P.g4pStyle); //set the style

		winApp.textFont(localFont, localFont.getFont().getSize()); //set the font

		// Get the absolute coordinates of box.
		Point pos = new Point(0,0);
		calcAbsPosition(pos);

		// Draw the surrounding box
		if(border > 0){
			winApp.strokeWeight(border);
			winApp.stroke(localColor.txfBorder);
		}
		else {
			winApp.noStroke();
		}
		winApp.fill(localColor.txfBack);
		winApp.rect(pos.x, pos.y, width, height);

		//draw separating lines if specified
		if(drawSepLines){
			winApp.stroke(winApp.blendColor(localColor.txfBorder, winApp.color(100), ADD));
			winApp.strokeWeight(1);
			for(int i = 1; i <= endY - startY; i++){
				winApp.line(pos.x + 4, pos.y + i * leading, pos.x + width - 4, pos.y + i*leading);
			}
		}

		winApp.noStroke();
		// Draw the selection rectangles
		if(startSelect != endSelect){ //if something is selected
			winApp.fill(localColor.txfSelBack);

			for(int i = Math.min(startSelect, endSelect); i < Math.max(startSelect, endSelect); i++){
				if(cursorPosition(i).x >= startX && cursorPixPosition(i).x < (width - 8) &&
						cursorPosition(i).y >= startY && cursorPosition(i).y < endY - 1){
					winApp.rect(pos.x + 4 + cursorPixPosition(i).x, pos.y + 2 + cursorPixPosition(i).y,
							Math.min(winApp.textWidth(text.substring(i, i+1)),
							width - 8 - cursorPixPosition(i).x),localFont.getFont().getSize()+2);
				}
			}
		}

		// Draw the string
		winApp.fill(localColor.txfFont);
		winApp.textLeading(leading); //set the leading
		winApp.text (viewText(), pos.x + 4, pos.y , width - 8, height - 4);

		// Draw the insertion point (it blinks!)
		if(focusIsWith == this && (winApp.millis() % 1000) > 500) {
			Point cursorPix = cursorPixPosition(cursorPos);

			winApp.noFill();
			winApp.stroke(localColor.txfBorder);
			winApp.strokeWeight(2);
			winApp.line(pos.x + 4 + cursorPix.x, pos.y + cursorPix.y + 2, 
					pos.x + 4 + cursorPix.x, 
					pos.y + cursorPix.y + localFont.getFont().getSize()+2);
			winApp.fill(localColor.txfFont);
		}
		winApp.popStyle(); //pop the push we did earlier
	}

	/**
	 * Returns true if b has a valid unicode value
	 *
	 * @param b
	 * @return
	 */
	private static boolean validUnicode(char b)
	{
		int c = (int)b;
		return (
				(c >= 0x0020 && c <= 0x007E) ||
				(c >= 0x00A1 && c <= 0x017F) ||
				(c == 0x018F) ||
				(c == 0x0192) ||
				(c >= 0x01A0 && c <= 0x01A1) ||
				(c >= 0x01AF && c <= 0x01B0) ||
				(c >= 0x01D0 && c <= 0x01DC) ||
				(c >= 0x01FA && c <= 0x01FF) ||
				(c >= 0x0218 && c <= 0x021B) ||
				(c >= 0x0250 && c <= 0x02A8) ||
				(c >= 0x02B0 && c <= 0x02E9) ||
				(c >= 0x0300 && c <= 0x0345) ||
				(c >= 0x0374 && c <= 0x0375) ||
				(c == 0x037A) ||
				(c == 0x037E) ||
				(c >= 0x0384 && c <= 0x038A) ||
				(c >= 0x038E && c <= 0x03A1) ||
				(c >= 0x03A3 && c <= 0x03CE) ||
				(c >= 0x03D0 && c <= 0x03D6) ||
				(c >= 0x03DA) ||
				(c >= 0x03DC) ||
				(c >= 0x03DE) ||
				(c >= 0x03E0) ||
				(c >= 0x03E2 && c <= 0x03F3) ||
				(c >= 0x0401 && c <= 0x044F) ||
				(c >= 0x0451 && c <= 0x045C) ||
				(c >= 0x045E && c <= 0x0486) ||
				(c >= 0x0490 && c <= 0x04C4) ||
				(c >= 0x04C7 && c <= 0x04C9) ||
				(c >= 0x04CB && c <= 0x04CC) ||
				(c >= 0x04D0 && c <= 0x04EB) ||
				(c >= 0x04EE && c <= 0x04F5) ||
				(c >= 0x04F8 && c <= 0x04F9) ||
				(c >= 0x0591 && c <= 0x05A1) ||
				(c >= 0x05A3 && c <= 0x05C4) ||
				(c >= 0x05D0 && c <= 0x05EA) ||
				(c >= 0x05F0 && c <= 0x05F4) ||
				(c >= 0x060C) ||
				(c >= 0x061B) ||
				(c >= 0x061F) ||
				(c >= 0x0621 && c <= 0x063A) ||
				(c >= 0x0640 && c <= 0x0655) ||
				(c >= 0x0660 && c <= 0x06EE) ||
				(c >= 0x06F0 && c <= 0x06FE) ||
				(c >= 0x0901 && c <= 0x0939) ||
				(c >= 0x093C && c <= 0x094D) ||
				(c >= 0x0950 && c <= 0x0954) ||
				(c >= 0x0958 && c <= 0x0970) ||
				(c >= 0x0E01 && c <= 0x0E3A) ||
				(c >= 0x1E80 && c <= 0x1E85) ||
				(c >= 0x1EA0 && c <= 0x1EF9) ||
				(c >= 0x2000 && c <= 0x202E) ||
				(c >= 0x2030 && c <= 0x2046) ||
				(c == 0x2070) ||
				(c >= 0x2074 && c <= 0x208E) ||
				(c == 0x2091) ||
				(c >= 0x20A0 && c <= 0x20AC) ||
				(c >= 0x2100 && c <= 0x2138) ||
				(c >= 0x2153 && c <= 0x2182) ||
				(c >= 0x2190 && c <= 0x21EA) ||
				(c >= 0x2190 && c <= 0x21EA) ||
				(c >= 0x2000 && c <= 0x22F1) ||
				(c == 0x2302) ||
				(c >= 0x2320 && c <= 0x2321) ||
				(c >= 0x2460 && c <= 0x2469) ||
				(c == 0x2500) ||
				(c == 0x2502) ||
				(c == 0x250C) ||
				(c == 0x2510) ||
				(c == 0x2514) ||
				(c == 0x2518) ||
				(c == 0x251C) ||
				(c == 0x2524) ||
				(c == 0x252C) ||
				(c == 0x2534) ||
				(c == 0x253C) ||
				(c >= 0x2550 && c <= 0x256C) ||
				(c == 0x2580) ||
				(c == 0x2584) ||
				(c == 0x2588) ||
				(c == 0x258C) ||
				(c >= 0x2590 && c <= 0x2593) ||
				(c == 0x25A0) ||
				(c >= 0x25AA && c <= 0x25AC) ||
				(c == 0x25B2) ||
				(c == 0x25BA) ||
				(c == 0x25BC) ||
				(c == 0x25C4) ||
				(c == 0x25C6) ||
				(c >= 0x25CA && c <= 0x25CC) ||
				(c == 0x25CF) ||
				(c >= 0x25D7 && c <= 0x25D9) ||
				(c == 0x25E6) ||
				(c == 0x2605) ||
				(c == 0x260E) ||
				(c == 0x261B) ||
				(c == 0x261E) ||
				(c >= 0x263A && c <= 0x263C) ||
				(c == 0x2640) ||
				(c == 0x2642) ||
				(c == 0x2660) ||
				(c == 0x2663) ||
				(c == 0x2665) ||
				(c == 0x2666) ||
				(c == 0x266A) ||
				(c == 0x266B) ||
				(c >= 0x2701 && c <= 0x2709) ||
				(c >= 0x270C && c <= 0x2727) ||
				(c >= 0x2729 && c <= 0x274B) ||
				(c == 0x274D) ||
				(c >= 0x274F && c <= 0x2752) ||
				(c == 0x2756) ||
				(c >= 0x2758 && c <= 0x275E) ||
				(c >= 0x2761 && c <= 0x2767) ||
				(c >= 0x2776 && c <= 0x2794) ||
				(c >= 0x2798 && c <= 0x27BE) ||
				(c >= 0xF001 && c <= 0xF002) ||
				(c >= 0xF021 && c <= 0xF0FF) ||
				(c >= 0xF601 && c <= 0xF605) ||
				(c >= 0xF610 && c <= 0xF616) ||
				(c >= 0xF800 && c <= 0xF807) ||
				(c >= 0xF80A && c <= 0xF80B) ||
				(c >= 0xF80E && c <= 0xF811) ||
				(c >= 0xF814 && c <= 0xF815) ||
				(c >= 0xF81F && c <= 0xF820) ||
				(c >= 0xF81F && c <= 0xF820) ||
				(c == 0xF833));
	}


} //end class bracket
