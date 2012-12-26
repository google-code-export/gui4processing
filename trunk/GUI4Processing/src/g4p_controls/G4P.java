/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2008-12 Peter Lager

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

package g4p_controls;


import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.filechooser.FileFilter;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * The core class for the global manipulation and execution of G4P. <br>
 * It also gives access to many of the constants used in this library.
 * 
 * @author Peter Lager
 *
 */
public class G4P implements GConstants, PConstants {

	static PApplet sketchApplet = null;

	/**
	 * return the pretty version of the library.
	 */
	public static String getPrettyVersion() {
		return "##library.prettyVersion##";
	}

	/**
	 * return the version of the library used by Processing
	 */
	public static String getVersion() {
		return "##library.version##";
	}

	static int globalColorScheme = GCScheme.BLUE_SCHEME;
	static int globalAlpha = 255;

	/**
	 * Java has cross platform support for 5 logical fonts so use one of these
	 * in preference to platform specific fonts or include them here.
	 * <ul>
	 * <li>Dialog </li>
	 * <li>DialogInpu </li>t
	 * <li>Monospaced </li>
	 * <li>Serif </li>
	 * <li>SansSerif </li>
	 * </ul>
	 */
	static Font globalFont = new Font("Dialog", Font.PLAIN, 12);
	static Font numericLabelFont = new Font("DialogInput", Font.BOLD, 12);

	// Store of info about windows and controls
	static HashMap<PApplet, GWindowInfo> windows = new HashMap<PApplet, GWindowInfo>();
	// Used to order controls
	static GAbstractControl.Z_Order zorder = new GAbstractControl.Z_Order();

	/* INTERNAL USE ONLY  Mouse over changer */
	static boolean cursorChangeEnabled = true;
	static int mouseOff = ARROW;

	static boolean showMessages = true;

	// Determines how position and size parameters are interpreted when
	// a control is created
	// Introduced V3.0
	static int control_mode = PApplet.CORNER;

	static LinkedList<G4Pstyle> styles = new LinkedList<G4Pstyle>();

	static File lastSelectFolder = null;
	static File lastInputFolder = null;
	static File lastOutputFolder = null;

	static JColorChooser chooser = null;
	static Color lastColor = Color.white; // White

	/**
	 * Used to register the main sketch window with G4P. This is ignored if any
	 * G4P controls or windows have already been created because the act of
	 * creating a control will do this for you. <br>
	 * 
	 * Some controls are created without passing a reference to the sketch applet
	 * but still need to know it. An example is the GColorChooser control which
	 * cannot be used until this method is called or some other G4P control has
	 * been created.
	 * 
	 * Also some other libraries such as PeasyCam change the transformation matrix.
	 * In which case either a G4P control should be created or this method called
	 * before creating a PeasyCam object.
	 * 
	 * @param app
	 */
	public static void registerSketch(PApplet app){
		if(sketchApplet == null) {
			sketchApplet = app;
			GWindowInfo winfo = windows.get(app);
			if(winfo == null){
				winfo = new GWindowInfo(app);
				windows.put(app, winfo);
			}			
		}
	}

	/**
	 * Set the global colour scheme. This will change the local
	 * colour scheme for every control.
	 * @param cs colour scheme to use (0-15)
	 */
	public static void setGlobalColorScheme(int cs){
		cs = Math.abs(cs) % 16; // Force into valid range
		if(globalColorScheme != cs){
			globalColorScheme = cs;
			for(GWindowInfo winfo : windows.values())
				winfo.setColorScheme(globalColorScheme);
		}
	}

	/**
	 * Set the colour scheme for all the controls drawn by the given 
	 * PApplet. This will override any previous colour scheme for 
	 * these controls.
	 * @param app
	 * @param cs
	 */
	public static void setWindowColorScheme(PApplet app, int cs){
		cs = Math.abs(cs) % 16; // Force into valid range
		GWindowInfo winfo = windows.get(app);
		if(winfo != null)
			winfo.setColorScheme(cs);
	}

	/**
	 * Set the colour scheme for all the controls drawn by the given 
	 * GWindow. This will override any previous colour scheme for 
	 * these controls.
	 * @param win
	 * @param cs
	 */
	public static void setWindowColorScheme(GWindow win, int cs){
		cs = Math.abs(cs) % 16; // Force into valid range
		GWindowInfo winfo = windows.get(win.papplet);
		if(winfo != null)
			winfo.setColorScheme(cs);
	}


	/**
	 * Set the transparency of all controls. If the alpha level for a 
	 * control falls below G4P.ALPHA_BLOCK then it will no longer 
	 * respond to mouse and keyboard events.
	 * 
	 * @param alpha value in the range 0 (transparent) to 255 (opaque)
	 */
	public static void setGlobalAlpha(int alpha){
		alpha = Math.abs(alpha) % 256; // Force into valid range
		if(globalAlpha != alpha){
			globalAlpha = alpha;
			for(GWindowInfo winfo : windows.values())
				winfo.setAlpha(globalAlpha);
		}
	}

	/**
	 * Set the transparency level for all controls drawn by the given
	 * PApplet. If the alpha level for a control falls below 
	 * G4P.ALPHA_BLOCK then it will no longer respond to mouse
	 * and keyboard events.
	 * 
	 * @param app
	 * @param alpha value in the range 0 (transparent) to 255 (opaque)
	 */
	public static void setWindowAlpha(PApplet app, int alpha){
		alpha = Math.abs(alpha) % 256; // Force into valid range
		GWindowInfo winfo = windows.get(app);
		if(winfo != null)
			winfo.setAlpha(alpha);
	}

	/**
	 * Set the transparency level for all controls drawn by the given
	 * GWindow. If the alpha level for a control falls below 
	 * G4P.ALPHA_BLOCK then it will no longer respond to mouse
	 * and keyboard events.
	 * 
	 * @param app
	 * @param alpha value in the range 0 (transparent) to 255 (opaque)
	 */
	public static void setWindowAlpha(GWindow win, int alpha){
		alpha = Math.abs(alpha) % 256; // Force into valid range
		GWindowInfo winfo = windows.get(win.papplet);
		if(winfo != null)
			winfo.setAlpha(alpha);
	}

	/**
	 * Register a GWindow object.
	 * 
	 * @param window
	 */
	static void addWindow(GWindow window){
		PApplet app = window.papplet;
		// The first applet must be the sketchApplet
		if(G4P.sketchApplet == null)
			G4P.sketchApplet = app;
		GWindowInfo winfo = windows.get(app);
		if(winfo == null){
			winfo = new GWindowInfo(app);
			windows.put(app, winfo);
		}
	}

	/**
	 * Used internally to remove a window from the list of windows. Done when
	 * a window is to be disposed of.
	 * 
	 * @param window
	 */
	static void removeWindow(GWindow window){
		PApplet app = window.papplet;
		GWindowInfo winfo = windows.get(app);
		if(winfo != null){
			winfo.dispose();
			windows.remove(winfo);
		}
	}

	/**
	 * Used internally to register a control with its applet.
	 * @param control
	 */
	static void addControl(GAbstractControl control){
		PApplet app = control.getPApplet();
		// The first applet must be the sketchApplet
		if(G4P.sketchApplet == null)
			G4P.sketchApplet = app;
		GWindowInfo winfo = windows.get(app);
		if(winfo == null){
			winfo = new GWindowInfo(app);
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
	static boolean removeControl(GAbstractControl control){
		PApplet app = control.getPApplet();
		GWindowInfo winfo = windows.get(app);
		if(winfo != null){
			winfo.removeControl(control);
			return true;
		}
		return false;
	}

	/**
	 * Change the way position and size parameters are interpreted when a control is created. 
	 * or added to another control e.g. GPanel. <br>
	 * There are 3 modes. <br><pre>
	 * PApplet.CORNER	 (x, y, w, h) <br>
	 * PApplet.CORNERS	 (x0, y0, x1, y1) <br>
	 * PApplet.CENTER	 (cx, cy, w, h) </pre><br>
	 * 
	 * @param mode illegal values are ignored leaving the mode unchanged
	 */
	public static void setCtrlMode(int mode){
		switch(mode){
		case PApplet.CORNER:	// (x, y, w, h)
		case PApplet.CORNERS:	// (x0, y0, x1, y1)
		case PApplet.CENTER:	// (cx, cy, w, h)
			control_mode = mode;
		}
	}

	/**
	 * Get the control creation mode @see ctrlMode(int mode)
	 * @return
	 */
	public static int getCtrlMode(){
		return control_mode;
	}

	/**
	 * G4P has a range of support messages eg <br>if you create a GUI component 
	 * without an event handler or, <br>a slider where the visible size of the
	 * slider is less than the difference between min and max values.
	 * 
	 * This method allows the user to enable (default) or disable this option. If
	 * disable then it should be called before any GUI components are created.
	 * 
	 * @param enable
	 */
	public static void messagesEnabled(boolean enable){
		showMessages = enable;
	}

	/**
	 * Enables or disables cursor over component change. <br>
	 * 
	 * Calls to this method are ignored if no G4P controls have been created.
	 * 
	 * @param enable true to enable cursor change over components.
	 */
	public static void setMouseOverEnabled(boolean enable){
		cursorChangeEnabled = enable;
	}

	/**
	 * Inform G4P which cursor shapes will be used.
	 * Initial values are ARROW (off) and HAND (over)
	 * 
	 * @param cursorOff
	 * @param cursorOver
	 */
	public static void setCursorOff(int cursorOff){
		mouseOff = cursorOff;
	}

	/**
	 * Inform G4P which cursor to use for mouse over.
	 * 
	 * @param cursorOver
	 */
	public static int getCursorOff(){
		return mouseOff;
	}

	/**
	 * Save the current style on a stack. <br>
	 * There should be a matching popStyle otherwise the program it will
	 * cause a memory leakage.
	 */
	static void pushStyle(){
		G4Pstyle s = new G4Pstyle();
		s.ctrlMode = control_mode;
		s.showMessages = showMessages;
		// Now save the style for later
		styles.addLast(s);
	}

	/**
	 * Remove and restore the current style from the stack. <br>
	 * There should be a matching pushStyle otherwise the program will crash.
	 */
	static void popStyle(){
		G4Pstyle s = styles.removeLast();
		control_mode = s.ctrlMode;
		showMessages = s.showMessages;
	}

	/**
	 * This class represents the current style used by G4P. 
	 * It can be extended to add other attributes but these should be 
	 * included in the pushStyle and popStyle. 
	 * @author Peter
	 *
	 */
	static class G4Pstyle {
		int ctrlMode;
		boolean showMessages;
	}

	/**
	 * This will open a version of the Java Swing color chooser dialog. The dialog's
	 * UI is dependent on the OS and JVM implementation running. <br>
	 * 
	 * If you click on Cancel then it returns the last color previously selected.
	 * 
	 * @return the ARGB colour as a 32 bit integer (as used in Processing). 
	 */
	public static int selectColor(){
		Frame owner = (sketchApplet == null) ? null : sketchApplet.frame;
		if(chooser == null){
			chooser = new JColorChooser();
			AbstractColorChooserPanel[] oldPanels = chooser.getChooserPanels();
			// Do not assume what panels are present
			LinkedList<AbstractColorChooserPanel> panels = new LinkedList<AbstractColorChooserPanel>();	
			for(AbstractColorChooserPanel p : oldPanels){
				String displayName = p.getDisplayName().toLowerCase();
				if(displayName.equals("swatches"))
					panels.addLast(p);
				else if(displayName.equals("rgb"))
					panels.addFirst(p);
				else if(displayName.startsWith("hs"))
					panels.addFirst(p);
			}
			AbstractColorChooserPanel[] newPanels;
			newPanels = panels.toArray(new AbstractColorChooserPanel[panels.size()]);
			chooser.setChooserPanels(newPanels);
			ColorPreviewPanel pp = new ColorPreviewPanel(lastColor);
			chooser.getSelectionModel().addChangeListener(pp);
			chooser.setPreviewPanel(pp);
		}
		// Use the last color selected to start it off
		chooser.setColor(lastColor);
		JDialog dialog = JColorChooser.createDialog(owner,
				"Color picker", 
				true, 
				chooser, 
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						lastColor = chooser.getColor();
					}
				}, 
				null);
				dialog.setVisible(true);
		return lastColor.getRGB();
	}


	/**
	 * Select a folder from the local file system. <br>
	 * 
	 * 
	 * @param prompt the frame text for the chooser
	 * @param initSelection the initial file path to use
	 * @return
	 */
	public static File selectFolder(String prompt){
		return selectFolder(prompt, null);
	}

	/**
	 * Select a folder from the local file system.
	 * 
	 * @param prompt the frame text for the chooser
	 * @param initSelection the initial file path to use
	 * @return the folder selected
	 */
	public static File selectFolder(String prompt, File initSelection){
		if(initSelection == null)
			initSelection = lastSelectFolder;
		File selectedFile = null;
		Frame frame = (sketchApplet == null) ? null : sketchApplet.frame;
		if (PApplet.platform == MACOSX && PApplet.useNativeSelect != false) {
			FileDialog fileDialog =
				new FileDialog(frame, prompt, FileDialog.LOAD);
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
			fileDialog.setVisible(true);
			System.setProperty("apple.awt.fileDialogForDirectories", "false");
			String filename = fileDialog.getFile();
			if (filename != null) {
				selectedFile = new File(fileDialog.getDirectory(), fileDialog.getFile());
			}
		} else {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle(prompt);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (initSelection != null) {
				fileChooser.setSelectedFile(initSelection);
			}

			int result = fileChooser.showOpenDialog(frame);
			if (result == JFileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
			}
		}
		if(selectedFile != null)
			lastSelectFolder = selectedFile;
		return selectedFile;
	}

	/**
	 * Select a file for input from the local file system. <br>
	 * 
	 * 
	 * @param prompt the frame text for the chooser
	 * @return the file selected or null
	 */
	public static File selectInput(String prompt){
		return selectInput(prompt, null, null, null);
	}

	/**
	 * Select a file for input from the local file system. <br>
	 * 
	 * 
	 * @param prompt the frame text for the chooser
	 * @param initSelection the initial file path to use
	 * @return the file selected or null
	 */
	public static File selectInput(String prompt, File initSelection){
		return selectInput(prompt, initSelection, null, null);
	}

	/**
	 * Select a file for input from the local file system. <br>
	 * 
	 * This version allows the dialog window to filter the output based on file extensions.
	 * This is not available on all platforms, if not then it is ignored. <br>
	 * 
	 * It is definitely available on Linux systems because it uses the standard swing
	 * JFileFinder component.
	 * 
	 * @param prompt the frame text for the chooser
	 * @param initSelection the initial file path to use
	 * @param types a comma separated list of file extensions e.g. 
	 * @param typeDesc simple textual description of the file types e.g. "Image files"
	 * @return the file selected or null
	 */
	public static File selectInput(String prompt, File initSelection, String types, String typeDesc){
		return selectImpl(prompt, initSelection, FileDialog.LOAD, types, typeDesc);
	}

	/**
	 * Select a file for output from the local file system. <br>
	 * 
	 * @param prompt the frame text for the chooser
	 * @param initSelection the initial file path to use
	 * @return the file selected or null
	 */
	public static File selectOutput(String prompt){
		return selectOutput(prompt, null, null, null);
	}

	/**
	 * Select a file for output from the local file system. <br>
	 * 
	 * @param prompt the frame text for the chooser
	 * @param initSelection the initial file path to use
	 * @return the file selected or null
	 */
	public static File selectOutput(String prompt, File initSelection){
		return selectOutput(prompt, initSelection, null, null);
	}

	/**
	 * Select a file for output from the local file system. <br>
	 * 
	 * This version allows the dialog window to filter the output based on file extensions.
	 * This is not available on all platforms, if not then it is ignored. <br>
	 * 
	 * It is definitely available on Linux systems because it uses the standard swing
	 * JFileFinder component.
	 * 
	 * @param prompt the frame text for the chooser
	 * @param initSelection the initial file path to use
	 * @param types a comma separated list of file extensions e.g. "png,jpf,tiff"
	 * @param typeDesc simple textual description of the file types e.g. "Image files"
	 * @return the file selected or null
	 */
	public static File selectOutput(String prompt, File initSelection, String types, String typeDesc){
		return selectImpl(prompt, initSelection, FileDialog.SAVE, types, typeDesc);
	}

	/**
	 * The implementation of the select input and output methods.
	 * @param prompt
	 * @param initSelection
	 * @param mode
	 * @param types
	 * @param typeDesc
	 * @return the selected file or null
	 */
	private static File selectImpl(String prompt, File initSelection, int mode, String types, String typeDesc) {
		// If no initial selection made then use last selection	
		if(initSelection == null){
			if(mode == FileDialog.SAVE)
				initSelection = lastInputFolder;
			else
				initSelection = lastOutputFolder;
		}
		// Assume that a file will not be selected
		File selectedFile = null;
		// Get the owner
		Frame owner = (sketchApplet == null) ? null : sketchApplet.frame;
		// Create a file filter
		if (PApplet.useNativeSelect) {
			FileDialog dialog = new FileDialog(owner, prompt, mode);
			if (initSelection != null) {
				dialog.setDirectory(initSelection.getParent());
				dialog.setFile(initSelection.getName());
			}
			FilenameFilter filter = null;
			if(types != null && types.length() > 0){
				filter = new FilenameChooserFilter(types);
				dialog.setFilenameFilter(filter);
			}
			dialog.setVisible(true);
			String directory = dialog.getDirectory();
			String filename = dialog.getFile();
			if (filename != null) {
				selectedFile = new File(directory, filename);
			}
		} else {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle(prompt);
			FileFilter filter = null;
			if(types != null && types.length() > 0){
				filter = new FileChooserFilter(types, typeDesc);
				chooser.setFileFilter(filter);
			}
			if (initSelection != null) {
				chooser.setSelectedFile(initSelection);
			}
			int result = JFileChooser.ERROR_OPTION;
			if (mode == FileDialog.SAVE) {
				result = chooser.showSaveDialog(owner);
			} else if (mode == FileDialog.LOAD) {
				result = chooser.showOpenDialog(owner);
			}
			if (result == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
			}
		}
		// If a file has been selected then update the last?????Folder
		if(selectedFile != null){
			if(mode == FileDialog.SAVE)
				lastInputFolder = selectedFile;
			else
				lastOutputFolder = selectedFile;
		}
		return selectedFile;
	}
	/*
	 
		Component parentComponent
		    The first argument to each showXxxDialog method is always the parent component, which must be a 
		    Frame, a component inside a Frame, or null. If you specify a Frame or Dialog, then the Dialog 
		    will appear over the center of the Frame and follow the focus behavior of that Frame. If you 
		    specify a component inside a Frame, then the Dialog will appear over the center of that component 
		    and will follow the focus behavior of that component's Frame. If you specify null, then the look 
		    and feel will pick an appropriate position for the dialog — generally the center of the screen — and 
		    the Dialog will not necessarily follow the focus behavior of any visible Frame or Dialog.
		
		    The JOptionPane constructors do not include this argument. Instead, you specify the parent frame 
		    when you create the JDialog that contains the JOptionPane, and you use the JDialog 
		    setLocationRelativeTo method to set the dialog position.
		Object message
		    This required argument specifies what the dialog should display in its main area. Generally, you 
		    specify a string, which results in the dialog displaying a label with the specified text. You can 
		    split the message over several lines by putting newline (\n) characters inside the message string. 
		    For example:
		
		    "Complete the sentence:\n \"Green eggs and...\""
		
		String title
		    The title of the dialog.
		int optionType
		    Specifies the set of buttons that appear at the bottom of the dialog. Choose from one of the 
		    following standard sets: DEFAULT_OPTION, YES_NO_OPTION, YES_NO_CANCEL_OPTION, OK_CANCEL_OPTION.
		int messageType
		    This argument determines the icon displayed in the dialog. Choose from one of the following 
		    values: PLAIN_MESSAGE (no icon), ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE, QUESTION_MESSAGE.
		Icon icon
		    The icon to display in the dialog.
		Object[] options
		    Generally used to specify the string displayed by each button at the bottom of the dialog. See 
		    Customizing Button Text in a Standard Dialog for more information. Can also be used to specify 
		    icons to be displayed by the buttons or non-button components to be added to the button row.
		Object initialValue
		    Specifies the default value to be selected.
		
		You can either let the option pane display its default icon or specify the icon using the message 
		type or icon argument. By default, an option pane created with showMessageDialog displays the 
		information icon, one created with showConfirmDialog or showInputDialog displays the question 
		icon, and one created with a JOptionPane constructor displays no icon. To specify that the dialog 
		display a standard icon or no icon, specify the message type corresponding to the icon you desire. 
		To specify a custom icon, use the icon argument. The icon argument takes precedence over the 
		message type; as long as the icon argument has a non-null value, the dialog displays the 
		specified icon.
	 */
	
	public static int showMessage(){
		JOptionPane xxx;
		return 0;
	}
}
