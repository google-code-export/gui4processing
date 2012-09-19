package guicomponents;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PMatrix;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;

public class F4P {

	
	public static int globalColorScheme = FCScheme.CYAN_SCHEME;
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
	

}
