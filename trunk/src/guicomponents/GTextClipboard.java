/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
	http://code.google.com/p/gui4processing/
	
  Will not claim authorship for this as the code in this class has been 
  taken from a similar GUI library Interfascia ALPHA 002 -- 
  http://superstable.net/interfascia/  produced by Brenden Berg 
  
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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import processing.core.PApplet;

public class GTextClipboard extends GComponent {
	
	private static Clipboard clipboard = null;

	public GTextClipboard(PApplet theApplet, int x, int y, GColor colorScheme,
			GFont fontScheme) {
		super(theApplet, x, y, colorScheme, fontScheme);
		textClipboardCtorCore();
	}


	public GTextClipboard(PApplet theApplet, int x, int y) {
		super(theApplet, x, y);
		textClipboardCtorCore();
	}

	private void textClipboardCtorCore() {
		if(clipboard == null){
			SecurityManager security = System.getSecurityManager();
			if (security != null) {
				try {
					security.checkSystemClipboardAccess();
					clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				} catch (SecurityException e) {
					clipboard = new Clipboard("Interfascia Clipboard");
				}
			} else {
				try {
					clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				} catch (Exception e) {
					// THIS IS DUMB
				}
			}
		}
	}

	/**
	 * Copy string to clipboard
	 * @param v
	 */
	public void copy(String v){
		StringSelection fieldContent = new StringSelection (v);
		clipboard.setContents (fieldContent, this);
	}
	
	/**
	 * Retrieve string data from clipboard. If the clipboard does not
	 * have a string then returns null
	 * @return
	 */
	public String paste(){
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


	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		System.out.println ("Lost ownership");		
	}

}
