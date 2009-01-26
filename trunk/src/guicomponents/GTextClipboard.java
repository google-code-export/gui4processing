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

	public void copy(String v){
		
		StringSelection fieldContent = new StringSelection (v);
		clipboard.setContents (fieldContent, this);
	}
	
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
