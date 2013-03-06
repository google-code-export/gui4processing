package g4p_controls;

import java.util.ArrayList;

import processing.core.PApplet;

/**
 * This class will be used to safely close windows based on their actionOnClose action
 * outside the windows normal Processing event loop. <br>
 * This class has to be declared public so it can register the post event, but it should
 * not be used directly. <br>
 * To close a window use the GWinodw close() method
 *  
 * @author Peter Lager
 *
 */
public class GWindowCloser {

	
	private ArrayList<GWindow> toDisposeOf;
		
		GWindowCloser() {
			toDisposeOf = new ArrayList<GWindow>();
		}
		
		public void addWindow(GWindow gwindow){
			toDisposeOf.add(gwindow);
		}
		
		public void post(){
			// System.out.println("Window to dispose " + toDisposeOf.size());
			if(!toDisposeOf.isEmpty()){
				for(GWindow gwindow : toDisposeOf){
					PApplet wapp = gwindow.papplet;
					GWindowInfo winfo = G4P.windows.get(wapp);
					if(winfo != null){
						winfo.dispose();
						G4P.windows.remove(wapp);
						gwindow.dispose();
					}
				}
				toDisposeOf.clear();
			}
		}
	
	
}
