package guicomponents;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import processing.core.PApplet;

public class GTextField extends GTextClipboard {
	public final static int CHANGED = 0x00ff0001;
	public final static int ENTERED = 0x00ff0002;
	public final static int SET = 0x00ff0003;

	private int eventType = 0;

	// Measured in characters
	private int cursorPos = 0;
	private int visiblePortionStart = 0, visiblePortionEnd = 0;
	private int startSelect = -1, endSelect = -1;
	// Measured in pixels
	private float cursorXPos = 0, startSelectXPos = 0, endSelectXPos = 0;


	/**
	 * creates an empty IFTextField with the specified label, with specified position, and a default width of 100 pixels.
	 * @param argLabel the text field's label
	 * @param argX the text field's X location on the screen, relative to the PApplet.
	 * @param argY the text filed's Y location on the screen, relative 
	 * to the PApplet.
	 */

	public GTextField(PApplet theApplet, String text, int x, int y, int width, int height, GColor colorScheme, GFont fontScheme){
		super(theApplet, x, y, colorScheme, fontScheme);
		textFieldCtorCore(text, width, height);
	}

	public GTextField(PApplet theApplet, String text, int x, int y, int width, int height){
		super(theApplet, x, y);
		textFieldCtorCore(text, width, height);
	}

	private void textFieldCtorCore(String text, int width, int height) {
		this.width = Math.max(width, textWidth + PADH * 2);
		this.height = Math.max(height, localFont.gpFontSize + 8);
		// Set text after the width of the textfield has been set
		setText(text);
		createEventHandler(app);
		app.registerDraw(this);
		app.registerMouseEvent(this);
		app.registerKeyEvent(this);
	}

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
	 * 
	 */
	public void looseFocus(){
		System.out.println("TF   Focus released by "+this);
		startSelect = endSelect = -1;
		focusIsWith = null;
	}

	// FROM IFTEXTFIELD

	/**
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
	 * adds a string to the immediate right of the insertion point or replaces the selected group of characters.
	 * @param s the string to be added
	 */
	protected void appendToRightOfCursor(String s) {
		String t1, t2;
		if (startSelect != -1 && endSelect != -1) {
			int start = Math.min(startSelect, endSelect);
			int end = Math.max(startSelect, endSelect);
			if (start >= end || start < 0 || end > text.length()) {
				System.out.println("Brendan needs to check array bounds.");
				return;
			}

			t1 = text.substring(0, start);
			t2 = text.substring(end);
			cursorPos = start;
			startSelect = endSelect = -1;
		} else {
			t1 = text.substring(0, cursorPos);
			t2 = text.substring(cursorPos);
		}

		text = t1 + s + t2;
		cursorPos += s.length();

		// Adjust the start and end positions of the visible portion of the string
		if (app.textWidth(text) < width - 12) {
			visiblePortionStart = 0;
			visiblePortionEnd = text.length();
		} else {
			if (cursorPos == text.length()) {
				visiblePortionEnd = cursorPos;
				adjustVisiblePortionStart();
			} else {
				if (cursorPos >= visiblePortionEnd)
					centerCursor();
				else {
					//visiblePortionEnd = visiblePortionStart;
					adjustVisiblePortionEnd();
				}
				//while (app.textWidth(contents.substring(visiblePortionStart, visiblePortionEnd)) < getWidth() - 12)
				//	visiblePortionEnd++;
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
		if (startSelect != -1 && endSelect != -1) {
			deleteSubstring(startSelect, endSelect);
		} else if (cursorPos > 0){
			deleteSubstring(cursorPos - 1, cursorPos);
		}
	}

	protected void deleteChar() {
		if (startSelect != -1 && endSelect != -1) {
			deleteSubstring(startSelect, endSelect);
		} else if (cursorPos < text.length()){
			deleteSubstring(cursorPos, cursorPos + 1);
		}
	}

	protected void deleteSubstring(int startString, int endString) {
		int start = Math.min(startString, endString);
		int end = Math.max(startString, endString);
		if (start >= end || start < 0 || end > text.length()) {
			System.out.println("Brendan needs to check array bounds.");
			return;
		}

		text = text.substring(0, start) + text.substring(end);
		cursorPos = start;

		if (app.textWidth(text) < width - 12) {
			visiblePortionStart = 0;
			visiblePortionEnd = text.length();
		} else {
			if (cursorPos == text.length()) {
				visiblePortionEnd = cursorPos;
				adjustVisiblePortionStart();
			} else {
				if (cursorPos <= visiblePortionStart) {
					centerCursor();
				} else {
					adjustVisiblePortionEnd();
				}
			}
		}

		startSelect = endSelect = -1;

		eventType = CHANGED;
		fireEvent();
	}

	protected void copySubstring(int start, int end) {
		int s = Math.min(start, end);
		int e = Math.max(start, end);
		copy(text.substring(s, e));
	}


	// ***** UNTIL GRAPHICS SETTINGS ARE STORED IN A QUEUE, MAKE SURE	   *****
	// ***** TO ALWAYS CALL THESE FUNCTIONS INSIDE THE INTERFASCIA DEFAULT *****
	// ***** GRAPHICS STATE. I'M NOT TOUCHING THE GRAPHICS STATE HERE.     *****

	private void updateXPos() {
		cursorXPos = app.textWidth(text.substring(visiblePortionStart, cursorPos));
		if (startSelect != -1 && endSelect != -1) {
			int tempStart, tempEnd;
			if (endSelect < startSelect) {
				tempStart = endSelect;
				tempEnd = startSelect;
			} else {
				tempStart = startSelect;
				tempEnd = endSelect;
			}

			if (tempStart < visiblePortionStart)
				startSelectXPos = 0;
			else
				startSelectXPos = app.textWidth(text.substring(visiblePortionStart, tempStart));

			if (tempEnd > visiblePortionEnd)
				endSelectXPos = width - 4;
			else
				endSelectXPos = app.textWidth(text.substring(visiblePortionStart, tempEnd));
		}
	}

	private void adjustVisiblePortionStart() {
		if (app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd)) < width - 12) {
			while (app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd)) < width - 12) {
				if (visiblePortionStart == 0)
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
		// Temporarily correcting for an erroneus precondition. Looking for the real issue
		visiblePortionEnd = Math.min(visiblePortionEnd, text.length()); 
		if (app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd)) < width - 12) {
			while (app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd)) < width - 12) {
				if (visiblePortionEnd == text.length())
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
			if (visiblePortionStart != 0)
				visiblePortionStart--;

			if (visiblePortionEnd != text.length())
				visiblePortionEnd++;

			if (visiblePortionEnd == text.length() && visiblePortionStart == 0)
				break;
		}
	}

	/**
	 * given the X position of the mouse in relation to the X
	 * position of the text field, findClosestGap(int x) will
	 * return the index of the closest letter boundary in the 
	 * letterWidths array.
	 */
	private int findClosestGap(int x) {
		float prev = 0, cur;
		if (x < 0) {
			return visiblePortionStart;
		} else if (x > width) {
			return visiblePortionEnd;
		}
		for (int i = visiblePortionStart; i < visiblePortionEnd; i++) {
			cur = app.textWidth(text.substring(visiblePortionStart, i));
			if (cur > x) {
				if (cur - x < x - prev)
					return i;
				else
					return i - 1;
			}
			prev = cur;
		}

		// Don't know what else to return
		return text.length();
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


	/**
	 * @param text the text to set
	 */
	public void setText(String text, int align) {
		setText(text);
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

		if (app.textWidth(text) > width - 12) {
			adjustVisiblePortionEnd();

		}

		eventType = SET;
		fireEvent();
	}

	/**
	 * implemented to conform to Processing's mouse event handler
	 * requirements. You shouldn't call this method directly, as
	 * Processing will forward mouse events to this object directly.
	 * mouseEvent() handles mouse clicks, drags, and releases sent
	 * from the parent PApplet. 
	 * @param e the MouseEvent to handle
	 */
	public void mouseEvent(MouseEvent e) {
		Point p = new Point(0,0);
		calcAbsPosition(p);

		switch(e.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith == null && isOver(app.mouseX, app.mouseY)){
				this.takeFocus();
//				focusIsWith = this;
				endSelect = -1;
				startSelect = cursorPos = findClosestGap(e.getX() - p.x);
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == null && isOver(app.mouseX, app.mouseY)){
				endSelect = -1;
				startSelect = cursorPos = findClosestGap(e.getX() - p.x);
				this.takeFocus();
//				focusIsWith = this;
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			if (focusIsWith == this && endSelect == startSelect) {
				startSelect = -1;
				endSelect = -1;
//				focusIsWith = null;
			}
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this)
				endSelect = cursorPos = findClosestGap(e.getX() - p.x);
		}
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

			if (e.getID() == KeyEvent.KEY_PRESSED) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (shiftDown) {
						if (startSelect == -1)
							startSelect = cursorPos;
						endSelect = cursorPos = visiblePortionEnd = text.length();
					} else {
						// Shift isn't down
						startSelect = endSelect = -1;
						cursorPos = visiblePortionEnd = text.length();
					}
					//visiblePortionStart = visiblePortionEnd;
					adjustVisiblePortionStart();
				} 
				else if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (shiftDown) {
						if (endSelect == -1)
							endSelect = cursorPos;
						startSelect = cursorPos = visiblePortionStart = 0;
					} else {
						// Shift isn't down
						startSelect = endSelect = -1;
						cursorPos = visiblePortionStart = 0;
					}
					//visiblePortionEnd = visiblePortionStart;
					adjustVisiblePortionEnd();
				} 
				else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (shiftDown) {
						if (cursorPos > 0) {
							if (startSelect != -1 && endSelect != -1) {
								startSelect--;
								cursorPos--;
							} else {
								endSelect = cursorPos;
								cursorPos--;
								startSelect = cursorPos;
							}
						}
					} else {
						if (startSelect != -1 && endSelect != -1) {
							cursorPos = Math.min(startSelect, endSelect);
							startSelect = endSelect = -1;
						} else if (cursorPos > 0) {
							cursorPos--;
						}
					}
					centerCursor();
				} 
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (shiftDown) {
						if (cursorPos < text.length()) {
							if (startSelect != -1 && endSelect != -1) {
								endSelect++;
								cursorPos++;
							} else {
								startSelect = cursorPos;
								cursorPos++;
								endSelect = cursorPos;
							}
						}
					} else {
						if (startSelect != -1 && endSelect != -1) {
							cursorPos = Math.max(startSelect, endSelect);
							startSelect = endSelect = -1;
						} else if (cursorPos < text.length()) {
							cursorPos++;
						}
					}
					centerCursor();
				} 
				else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteChar();
				}
				else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					eventType = ENTERED;
					fireEvent();
				}
				else{
					if ((e.getModifiers() & shortcutMask) == shortcutMask) {
						switch (e.getKeyCode()) {
						case KeyEvent.VK_C:
							if (startSelect != -1 && endSelect != -1) {
								copySubstring(startSelect, endSelect);
							}
							break;
						case KeyEvent.VK_V:
							appendToRightOfCursor(paste());
							break;
						case KeyEvent.VK_X:
							if (startSelect != -1 && endSelect != -1) {
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
			else if (e.getID() == KeyEvent.KEY_TYPED) {
				if ((e.getModifiers() & shortcutMask) == shortcutMask) {
				}
				else if (e.getKeyChar() == '\b') {
					backspaceChar();
				} 
				else if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
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
			Point pos = new Point(0,0);
			calcAbsPosition(pos);

			// Draw the surrounding box
			app.stroke(0);
			app.fill(255);
			app.rect(pos.x, pos.y, width, height);
			app.noStroke();

			// Compute the left offset for the start of text
			// ***** MOVE THIS TO SOMEWHERE THAT DOESN'T GET CALLED 50 MILLION TIMES PER SECOND ******
			float offset;
			if (cursorPos == text.length() && app.textWidth(text) > width - 8)
				offset = (width - 4) - app.textWidth(text.substring(visiblePortionStart, visiblePortionEnd));
			else
				offset = 4;

			// Draw the selection rectangle
			if (startSelect != -1 && endSelect != -1) {
				app.fill(0xffa0a0ff);
				app.rect(pos.x + startSelectXPos + offset, pos.y + 3, endSelectXPos - startSelectXPos + 1, 15);
			}

			// Draw the string
			app.fill(0);
			app.text (text.substring(visiblePortionStart, visiblePortionEnd), pos.x + offset, pos.y + 5, width - 8, height - 6);
			// Draw the insertion point (it blinks!)
			if (focusIsWith == this && (startSelect == -1 || endSelect == -1) 
					&& ((app.millis() % 1000) > 500)
					&& (cursorPos >= visiblePortionStart)
					&& cursorPos <= visiblePortionEnd) {
				app.stroke(64);
				app.line(pos.x + (int) cursorXPos + offset, pos.y + 3, pos.x + (int) cursorXPos + offset, pos.y + height - 3);
			}
		}
	}


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



}
