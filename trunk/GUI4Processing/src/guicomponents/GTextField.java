/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
	http://code.google.com/p/gui-for-processing/

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
 * can also be pasted in. It supports text that is longer than the dispalayable
 * area
 * 
 * @author Peter Lager
 *
 */
public class GTextField extends GComponent {
	// Text has changed
	public final static int CHANGED = 0x00ff0001;
	// The enter key has been pressed
	public final static int ENTERED = 0x00ff0002;
	// The text has changed using the setText() method
	public final static int SET = 0x00ff0003;

	// The event type
	private int eventType = 0;

	// Measured in characters
	private int cursorPos = 0;
	private int visiblePortionStart = 0, visiblePortionEnd = 0;
	private int startSelect = -1, endSelect = -1;
	// Measured in pixels
	private float cursorXPos = 0, startSelectXPos = 0, endSelectXPos = 0;


	/**
	 * Creates a GTextField object
	 * @param theApplet
	 * @param text initial text to display
	 * @param x horizontal position relative to PApplet or PPanel
	 * @param y vertical position relative to PApplet or PPanel
	 * @param width width of text field
	 * @param height height of text field
	 * @param colors colour scheme to use
	 * @param font font to use
	 */
//	public GTextField(PApplet theApplet, String text, int x, int y, int width, int height, 
//			GCScheme colors, PFont font){
//		super(theApplet, x, y, colors, font);
//		textFieldCtorCore(text, width, height);
//	}
	

	/**
	 * Creates a GTextField object
	 * @param theApplet
	 * @param text initial text to display
	 * @param x horizontal position relative to PApplet or PPanel
	 * @param y vertical position relative to PApplet or PPanel
	 * @param width width of text field
	 * @param height height of text field
	 */
	public GTextField(PApplet theApplet, String text, int x, int y, int width, int height){
		super(theApplet, x, y);
		textFieldCtorCore(text, width, height);
	}
	
	/**
	 * Common code required by ctors
	 * @param text initial text to display
	 * @param width width of text field
	 * @param height height of text field
	 */

	private void textFieldCtorCore(String text, int width, int height) {
		this.width = Math.max(width, textWidth + PADH * 2);
		this.height = Math.max(height, localFont.size + PADV * 2);
		border = 1;
		// Set text AFTER the width of the textfield has been set
		setText(text);
		createEventHandler(app);
		registerAutos_DMPK(true, true, false, true);
	}

	/**
	 * Override the default event handler created with createEventHandler(Object obj)
	 * @param obj
	 * @param methodName
	 */
	public void addEventHandler(Object obj, String methodName){
		try{
			this.eventHandler = obj.getClass().getMethod(methodName, new Class[] { GTextField.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
			System.out.println("with a parameter of type GTextField");
			eventHandlerObject = null;
		}
	}

	/**
	 * Create an event handler that will call a method handleTextFieldEvents(GTextField tfield)
	 * when text is changed or entered
	 * @param obj
	 */
	protected void createEventHandler(Object obj){
		try{
			this.eventHandler = obj.getClass().getMethod("handleTextFieldEvents", new Class[] { GTextField.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			eventHandlerObject = null;
			System.out.println("You might want to add a method to handle \ntext field events the syntax is");
			System.out.println("void handleTextFieldEvents(GTextField tfield){\n   ...\n}\n\n");
		}
	}	

	/**
	 * When the textfield looses focus it also looses any text selection.
	 */
	protected void looseFocus(){
		startSelect = endSelect = -1;
		focusIsWith = null;
	}

	/**
	 * This can be used to detect the type of event
	 * @return the eventType
	 */
	public int getEventType() {
		return eventType;
	}

	/**
	 * adds a character to the immediate right of the insertion point or replaces the selected 
	 * group of characters. This method is called by <pre>public void MouseEvent</pre> if a unicode 
	 * character is entered via the keyboard.
	 * @param c the character to be added
	 */
	protected void appendToRightOfCursor(char c) {
		appendToRightOfCursor("" + c);
	}


	/**
	 * adds a string to the immediate right of the insertion point or replaces the selected group
	 * of characters.
	 * @param s the string to be added
	 */
	protected void appendToRightOfCursor(String s) {
		String t1, t2;
		if(startSelect != endSelect) {
			int start = Math.min(startSelect, endSelect);
			int end = Math.max(startSelect, endSelect);
			t1 = text.substring(0, start);
			t2 = text.substring(end);
			startSelect = endSelect = cursorPos = start;
		} else {
			t1 = text.substring(0, cursorPos);
			t2 = text.substring(cursorPos);
		}

		text = t1 + s + t2;
		cursorPos += s.length();

		// Adjust the start and end positions of the visible portion of the string
		if(app.textWidth(text) < width - 12) {
			visiblePortionStart = 0;
			visiblePortionEnd = text.length();
		} else {
			if(cursorPos == text.length()) {
				visiblePortionEnd = cursorPos;
				adjustVisiblePortionStart();
			} else {
				if(cursorPos >= visiblePortionEnd)
					centerCursor();
				else {
					adjustVisiblePortionEnd();
				}
			}
		}
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
		if(startSelect != endSelect) {
			deleteSubstring(startSelect, endSelect);
		} else if(cursorPos > 0){
			deleteSubstring(cursorPos - 1, cursorPos);
		}
	}

	protected void deleteChar() {
		if(startSelect != endSelect) {
			deleteSubstring(startSelect, endSelect);
		} else if(cursorPos < text.length()){
			deleteSubstring(cursorPos, cursorPos + 1);
		}
	}

	protected void deleteSubstring(int startString, int endString) {
		int start = Math.min(startString, endString);
		int end = Math.max(startString, endString);
		text = text.substring(0, start) + text.substring(end);
		cursorPos = start;

		if(app.textWidth(text) < width - 12) {
			visiblePortionStart = 0;
			visiblePortionEnd = text.length();
		} else {
			if(cursorPos == text.length()) {
				visiblePortionEnd = cursorPos;
				adjustVisiblePortionStart();
			} else {
				if(cursorPos <= visiblePortionStart) {
					centerCursor();
				} else {
					adjustVisiblePortionEnd();
				}
			}
		}
		startSelect = endSelect = cursorPos;

		eventType = CHANGED;
		fireEvent();
	}

	/**
	 * Copy string to clipboard
	 * @param start
	 * @param end
	 */
	protected void copySubstring(int start, int end) {
		int s = Math.min(start, end);
		int e = Math.max(start, end);
		GClip.copy(text.substring(s, e));
	}

	/**
	 * 	calculate the pixel positions for the start and end
	 * of selected text.
	 */
	private void updateXPos() {
		int tempStart = startSelect;
		int tempEnd = endSelect;
		cursorXPos = app.textWidth(text.substring(visiblePortionStart, cursorPos));
		if(startSelect != endSelect){
			if(endSelect < startSelect) {
				tempStart = endSelect;
				tempEnd = startSelect;
			}
			if(tempStart < visiblePortionStart)
				startSelectXPos = 0;
			else
				startSelectXPos = app.textWidth(text.substring(visiblePortionStart, tempStart));

			if(tempEnd > visiblePortionEnd)
				endSelectXPos = width - 12;
			else
				endSelectXPos = app.textWidth(text.substring(visiblePortionStart, tempEnd));
		}
	}

	private void adjustVisiblePortionStart() {
		if(app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd)) < width - 12) {
			while (app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd)) < width - 12) {
				if(visiblePortionStart == 0)
					break;
				else
					visiblePortionStart--;
			}
		} else {
			while (app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd)) > width - 12) {
				visiblePortionStart++;
			}
		}
	}

	private void adjustVisiblePortionEnd() {
		if(app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd)) < width - 12) {
			while (app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd)) < width - 12) {
				if(visiblePortionEnd == text.length())
					break;
				else
					visiblePortionEnd++;
			}
		} else {
			while (app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd)) > width - 12) {
				visiblePortionEnd--;
			}
		}
	}

	private void centerCursor() {
		visiblePortionStart = visiblePortionEnd = cursorPos;

		while (app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd)) < width - 12) {
			if(visiblePortionStart != 0)
				visiblePortionStart--;

			if(visiblePortionEnd != text.length())
				visiblePortionEnd++;

			if(visiblePortionEnd == text.length() && visiblePortionStart == 0)
				break;
		}
	}

	/**
	 * given the X position of the mouse in relation to the X
	 * position of the text field, findClosestGap(int x) will
	 * return the index of the closest letter boundary in the 
	 * letterWidths array.
	 * If the second parameter is true then the visible portion
	 * of the string is constrained to the to the text field size.
	 * Set to false to allow scrolling of the string when selecting text. 
	 * @param x
	 * @param constrain 
	 * @return
	 */
	private int findClosestGap(int x, boolean constrain) {
		float prev = 0, cur;
		if(constrain){
			if(x < 0) {
				return visiblePortionStart;
			} else if(x > width) {
				return visiblePortionEnd;
			}
		}
		for (int i = visiblePortionStart; i < text.length(); i++) {
			cur = app.textWidth(text.substring(visiblePortionStart, i));
			if(cur > x) {
				if(cur - x < x - prev)
					return i;
				else
					return Math.max(0, i - 1);
			}
			prev = cur;
		}
		// Don't know what else to return
		return text.length();
	}

	/**
	 * sets the contents of the text box and displays the
	 * specified string in the text box widget.
	 * @param val the string to become the text field's contents
	 */
	public void setText(String newValue) {

		text = newValue;
		cursorPos = text.length();
		startSelect = endSelect = -1;

		visiblePortionStart = 0;
		visiblePortionEnd = text.length();
		app.textFont(localFont);
		if(app.textWidth(text) > width - 12) {
			adjustVisiblePortionEnd();
		}
		eventType = SET;
		fireEvent();
	}

	/**
	 * Mouse event handler - the focus cannot be lost by anything
	 * we do here - it has to be taken away when the mouse is pressed
	 * somewhere else.
	 * 
	 * @param e the MouseEvent to handle
	 */
	public void mouseEvent(MouseEvent e) {
		Point p = new Point(0,0);
		calcAbsPosition(p);

		switch(e.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(isOver(app.mouseX, app.mouseY)){
				if(focusIsWith != this)
					this.takeFocus();
				startSelect = endSelect = cursorPos = findClosestGap(e.getX() - p.x, true);
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == this){
				startSelect = endSelect = cursorPos = findClosestGap(e.getX() - p.x, true);
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			// Nothing to do
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this /*&& app.millis() % 200 > 100*/){
				int oldEndSelect = endSelect;
				endSelect = cursorPos = findClosestGap(e.getX() - p.x, false);
				if(endSelect < oldEndSelect) {  // dragging left
					while(endSelect < visiblePortionStart && visiblePortionStart > 0){
						visiblePortionStart--;
					}
					adjustVisiblePortionEnd();
				}
				else if(endSelect > oldEndSelect) {  // dragging right
					while(endSelect > visiblePortionEnd && visiblePortionEnd < text.length()) {
						visiblePortionEnd++;
					}
					adjustVisiblePortionStart();
				}
			}
		} // end of switch
		updateXPos();
	}

	/**
	 * receives KeyEvents forwarded to it by the GUIController
	 * if the current instance is currently in focus.
	 * @param e the KeyEvent to be handled
	 */
	public void keyEvent(KeyEvent e) {
		if(focusIsWith == this){
			int shortcutMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			boolean shiftDown = ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK);

			if(e.getID() == KeyEvent.KEY_PRESSED) {
				if(e.getKeyCode() == KeyEvent.VK_END) {
					if(shiftDown) { // Select to end of text
						endSelect = text.length();
						cursorPos = endSelect;
					} else { // Move cursor to end of text
						startSelect = endSelect = cursorPos = text.length();
					}
					visiblePortionEnd = cursorPos;
					if(cursorPos > visiblePortionEnd)
						visiblePortionEnd = cursorPos;
					adjustVisiblePortionStart();
				} 
				else if(e.getKeyCode() == KeyEvent.VK_HOME) {
					if(shiftDown) { // select to start of text
						startSelect = 0;
						cursorPos = startSelect;
					} else { //Move cursor to start of text
						startSelect = endSelect = cursorPos = visiblePortionStart = 0;
					}
					if(cursorPos < visiblePortionStart)
						visiblePortionStart = cursorPos;
					adjustVisiblePortionEnd();
				} 
				else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
					if(shiftDown) {	// selecting text to left
						if(cursorPos > 0) {
							cursorPos--;
							endSelect = cursorPos;
						}
					} else { // moving cursor left
						if(cursorPos > 0){
							cursorPos--;
							startSelect = endSelect = cursorPos;
						}
					}
					centerCursor();
				} 
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if(shiftDown) { // selecting text to right
						if(cursorPos < text.length()) {
							cursorPos++;
							endSelect = cursorPos;
						}
					} else { // moving cursor right
						if(cursorPos < text.length()){
							cursorPos++;
							startSelect = endSelect = cursorPos;
						}
					}
					centerCursor();
				} 
				else if(e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteChar();
				}
				else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					eventType = ENTERED;
					fireEvent();
				}
				else{
					if((e.getModifiers() & shortcutMask) == shortcutMask) {
						switch (e.getKeyCode()) {
						case KeyEvent.VK_C:
							if(startSelect != endSelect) {
								copySubstring(startSelect, endSelect);
							}
							break;
						case KeyEvent.VK_V:
							appendToRightOfCursor(GClip.paste());
							break;
						case KeyEvent.VK_X:
							if(startSelect != endSelect) {
								copySubstring(startSelect, endSelect);
								deleteSubstring(startSelect, endSelect);
							}
							break;
						case KeyEvent.VK_A:
							startSelect = 0;
							endSelect = text.length();
							break;
						}
					} 
				}
			}
		
			else if(e.getID() == KeyEvent.KEY_TYPED) {
				if((e.getModifiers() & shortcutMask) == shortcutMask) {
				}
				else if(e.getKeyChar() == '\b') {
					backspaceChar();
				} 
				else if(e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
					if(validUnicode(e.getKeyChar()))
						appendToRightOfCursor(e.getKeyChar());
				}
			}
			updateXPos();
		}
	}

	/**
	 * draws the text field, contents, selection, and cursor
	 * to the screen.
	 */
	public void draw () {
		if(visible){
			app.pushStyle();
			Point pos = new Point(0,0);
			calcAbsPosition(pos);

			// Draw the surrounding box
			app.strokeWeight(border);
			app.stroke(localColor.txfBorder);
			app.fill(localColor.txfBack);
			app.rect(pos.x, pos.y, width, height);
			app.noStroke();

			// Draw the selection rectangle
			if(startSelect != endSelect) {
				//app.fill(0xffa0a0ff);
				app.fill(localColor.txfSelBack);
				app.rect(pos.x + startSelectXPos + 4, pos.y + PADV, endSelectXPos - startSelectXPos + 1, height - 2 * PADV + 1);
			}

			// Draw the string (using fixed offset = 4)
			app.fill(localColor.txfFont);
			app.text (text.substring(visiblePortionStart, visiblePortionEnd), pos.x + 4, 
					pos.y - 1 + (height - localFont.size)/2 , width - 8, height);

			// Draw the insertion point (it blinks!)
			if(focusIsWith == this	&& ((app.millis() % 1000) > 500)
					&& (cursorPos >= visiblePortionStart)
					&& cursorPos <= visiblePortionEnd) {
				app.stroke(64);
				app.line(pos.x + (int) cursorXPos + 4, pos.y + PADV, pos.x + (int) cursorXPos + 4, pos.y + height - 2 * PADV);
			}
			app.popStyle();
		}
	}

	/**
	 * Returns true if b has a valid unicode value
	 * 
	 * @param b
	 * @return
	 */
	public static boolean validUnicode(char b)
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

	public int getVisiblePortionStart()
	{
		return visiblePortionStart;
	}

	public void setVisiblePortionStart(int VisiblePortionStart)
	{
		visiblePortionStart = VisiblePortionStart;
	}

	public int getVisiblePortionEnd()
	{
		return visiblePortionEnd;
	}

	public void setVisiblePortionEnd(int VisiblePortionEnd)
	{
		visiblePortionEnd = VisiblePortionEnd;
	}

	public int getStartSelect()
	{
		return startSelect;
	}

	public void setStartSelect(int StartSelect)
	{
		startSelect = StartSelect;
	}

	public int getEndSelect()
	{
		return endSelect;
	}

	public void setEndSelect(int EndSelect)
	{
		endSelect = EndSelect;
	}

	public int getCursorPosition()
	{
		return cursorPos;
	}

	public void setCursorPosition(int CursorPos)
	{
		cursorPos = CursorPos;
	}
}
