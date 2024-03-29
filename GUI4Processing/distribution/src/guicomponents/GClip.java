/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
	http://code.google.com/p/gui-for-processing/

	 Copyright (c) 2008-09 Peter Lager

  The actual code to create the clipbaord, copy and paste were 
  taken taken from a similar GUI library Interfascia ALPHA 002 -- 
  http://superstable.net/interfascia/  produced by Brenden Berg 
  The main change is to provide static copy and paste methods to 
  separate the clipboard logic from the component logic and provide
  global access.

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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/*
 * I wanted to implement copying and pasting to the clipboard using static
 * methods to simplify the sharing of a single clipboard over all classes.
 * The need to implement the ClipboardOwner interface requires an object so
 * this class creates an object the first time an attempt to copy or paste
 * is used.
 * 
 * All methods are private except copy() and paste() - lostOwnership()
 * has to be public because of the Clipboard owner interface.
 * 
 * @author Peter Lager
 *
 */

/**
 * This provides clipboard functionality for text and is currently only used by the 
 * GTextField class.
 * 
 * @author Peter Lager
 *
 */
public class GClip implements ClipboardOwner {

	/**
	 * Static reference to enforce singleton pattern
	 */
	private static GClip gclipboard = null;

	/**
	 * Class attribute to reference the programs clipboard
	 */
	private Clipboard clipboard = null;


	/**
	 * Copy a string to the clipboard
	 * @param chars
	 */
	public static void copy(String chars){
		if(gclipboard == null)
			gclipboard = new GClip();
		gclipboard.copyString(chars);
	}

	/**
	 * Get a string from the clipboard
	 * @return
	 */
	public static String paste(){
		if(gclipboard == null)
			gclipboard = new GClip();
		return gclipboard.pasteString();
	}

	private GClip(){
		if(clipboard == null){
			makeClipboardObject();
		}
	}

	private void makeClipboardObject(){
		SecurityManager security = System.getSecurityManager();
		if (security != null) {
			try {
				security.checkSystemClipboardAccess();
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			} catch (SecurityException e) {
				clipboard = new Clipboard("Application Clipboard");
			}
		} else {
			try {
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			} catch (Exception e) {
				// THIS IS DUMB - true but is there another way - I think not
			}
		}
	}

	/**
	 * Copy a string to the clipboard. If the Clipboard has not been created
	 * then create it.
	 * @return
	 */
	private void copyString(String chars){
		if(clipboard == null)
			makeClipboardObject();
		StringSelection fieldContent = new StringSelection (chars);
		clipboard.setContents (fieldContent, this);
	}

	/**
	 * Gets a string from the clipboard. If there is no Clipboard
	 * then create it.
	 * @return
	 */
	private String pasteString(){
		// If there is no clipboard then there is nothing to paste
		if(clipboard == null){
			makeClipboardObject();
			return "";
		}
		// We have a clipboard so get the string if we can
		Transferable clipboardContent = clipboard.getContents(this);

		if ((clipboardContent != null) &&
				(clipboardContent.isDataFlavorSupported(DataFlavor.stringFlavor))) {
			try {
				String tempString;
				tempString = (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
				return tempString;
			}
			catch (Exception e) {
				e.printStackTrace ();
			}
		}
		return "";
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

}
