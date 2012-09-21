package guicomponents;

import java.awt.Font;
import java.util.HashMap;

import processing.core.PApplet;

public class F4P {

	
	public static int globalColorScheme = FCScheme.CYAN_SCHEME;
	public static int globalAlpha = 255;
	public static Font globalFont = new Font("Dialog", Font.PLAIN, 11);

	static HashMap<PApplet, WindowInfo> windows = new HashMap<PApplet, WindowInfo>();
	
	
	static FAbstractControl.Z_Order zorder = new FAbstractControl.Z_Order();
	
	static void addControl(FAbstractControl control){
		PApplet app = control.getPApplet();
		WindowInfo winfo = windows.get(app);
		if(winfo == null){
			winfo = new WindowInfo(app);
			windows.put(app, winfo);
		}
		winfo.addControl(control);
	}
	
	/**
	 * Remove a control from the window. This is used in preparation 
	 * for disposing of a control.
	 * @param control
	 * @return true if control was remove else false
	 */
	static boolean removeControl(FAbstractControl control){
		PApplet app = control.getPApplet();
		WindowInfo winfo = windows.get(app);
		if(winfo != null){
			winfo = new WindowInfo(app);
			winfo.removeControl(control);
			return true;
		}
		return false;
	}
	
	/**
	 * Set the global colour scheme. This will change the local
	 * colour scheme for every control.
	 * @param cs colour scheme to use (0-15)
	 */
	static void setGlobalColorScheme(int cs){
		cs = Math.abs(cs) % 16; // Force into valid range
		if(globalColorScheme != cs){
			globalColorScheme = cs;
			for(WindowInfo winfo : windows.values())
				winfo.setGlobalColorScheme(globalColorScheme);
		}
	}

	/**
	 * Set the transparency of all controls. <br>
	 * If alpha is belwow GConstants.ALPHA_BLOCK then the control is no
	 * longer available., so will not respond to mouse and keyboard events.
	 * 
	 * @param alpha value in the range 0 (transparent) to 255 (opaque)
	 */
	static void setGlobalAlpha(int alpha){
		alpha = Math.abs(alpha) % 256; // Force into valid range
		if(globalAlpha != alpha){
			globalAlpha = alpha;
			for(WindowInfo winfo : windows.values())
				winfo.setGlobalAlpha(globalAlpha);
		}
	}
	
	
}
