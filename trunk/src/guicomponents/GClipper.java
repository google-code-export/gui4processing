package guicomponents;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class GClipper implements ClipboardOwner {
	/**
	 * Static clipboard so shared by all components
	 */
	private static Clipboard clipboard = null;
	
	private static GClipper gclipboard = null;

	private GClipper(){
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
				// THIS IS DUMB
			}
		}
	}

	/**
	 * Copy string to clipboard
	 * @param v
	 */
	public static void copy(String v){
		if(gclipboard == null)
			gclipboard = new GClipper();
		gclipboard.copyString(v);
	}
	
	public String paste(String v){
		if(gclipboard == null)
			gclipboard = new GClipper();
		return gclipboard.pasteString();
	}
	
	
	private void copyString(String v){
		if(clipboard == null)
			makeClipboardObject();
		StringSelection fieldContent = new StringSelection (v);
		clipboard.setContents (fieldContent, this);
	}
	
	/**
	 * Retrieve string data from clipboard. If the clipboard does not
	 * have a string then returns null
	 * @return
	 */
	public String pasteString(){
		// If there is no clipboard then there was nothing to paste
		if(clipboard == null){
			makeClipboardObject();
			return "";
		}

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
		// TODO Auto-generated method stub
		
	}

}
