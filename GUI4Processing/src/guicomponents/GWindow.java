/*
  Part of the GUI for Processing library 
  	http://gui-for-processing.lagers.org.uk
	http://code.google.com/p/gui-for-processing/

  Copyright (c) 2008-09 Peter Lager

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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import processing.core.PApplet;

/**
 * Allows the user to specify one or more controls windows.
 * 
 * @author Peter Lager
 *
 */
@SuppressWarnings("serial")
public class GWindow extends Frame implements GConstants {

	protected PApplet app;
	protected GCWinApplet embed;

	protected String winName;

	protected GWinData data;
	
	protected int exitBehaviour = CLOSE_ON_EXIT;
	
	
	/**
	 * Remember what we have registered for.
	 */
	protected boolean regDraw = false;
	protected boolean regMouse = false;
	protected boolean regPre = false;
	protected boolean regKey = false;

	/** The object to handle the pre event */
	protected Object preHandlerObject = null;
	/** The method in preHandlerObject to execute */
	protected Method preHandlerMethod = null;
	/** the name of the method to handle the event */ 
	protected String preHandlerMethodName;

	/** The object to handle the event */
	protected Object drawHandlerObject = null;
	/** The method in drawHandlerObject to execute */
	protected Method drawHandlerMethod = null;
	/** the name of the method to handle the event */ 
	protected String drawHandlerMethodName;

	/** The object to handle the event */
	protected Object mouseHandlerObject = null;
	/** The method in drawHandlerObject to execute */
	protected Method mouseHandlerMethod = null;
	/** the name of the method to handle the event */ 
	protected String mouseHandlerMethodName;

	
	public GWindow(PApplet theApplet, String name, int x, int y, int w, int h, int background) {
		super(name);
		app = theApplet;
		winName = name;
		
		embed = new GCWinApplet();
		embed.frame = this;
		embed.frame.setResizable(true);

		embed.appWidth = w;
		embed.appHeight = h;
		embed.bkColor = background;


		embed.resize(embed.appWidth, embed.appHeight);
		embed.setPreferredSize(new Dimension(embed.appWidth, embed.appHeight));
		embed.setMinimumSize(new Dimension(embed.appWidth, embed.appHeight));

		// add the PApplet to the Frame
		setLayout(new BorderLayout());
		add(embed, BorderLayout.CENTER);

		// ensures that the animation thread is started and
		// that other internal variables are properly set.
		embed.init();

		// add an exit on close listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				switch(exitBehaviour){
				case CLOSE_ON_EXIT:
					removeFromG4P();
					// close this frame
					dispose();
					break;
				case SHUTDOWN_ON_EXIT:
					System.exit(0);
					break;
					}
			}
		});

		pack();
		setLocation(x,y);
		setVisible(true);
		try{
		setAlwaysOnTop(true);
		} catch (Exception e){
			e.printStackTrace();
		}
		// At least get a blank screen
		embed.registerDraw(embed);
		regDraw = true;
		
		// Make sure G4P knows about this window
		G4P.addControlWindow(this);
	}

	/**
	 * Attempt to add the 'draw' handler method. 
	 * The default event handler is a method that returns void and has two
	 * parameters Papplet and GWinData
	 * 
	 * @param obj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 */
	public void addDrawHandler(Object obj, String methodName){
		try{
			drawHandlerObject = obj;
			drawHandlerMethodName = methodName;
			drawHandlerMethod = obj.getClass().getMethod(methodName, new Class[] {GCWinApplet.class, GWinData.class } );
		} catch (Exception e) {
			GMessenger.message(NONEXISTANT, this, new Object[] {methodName, new Class[] { this.getClass() } } );
			drawHandlerObject = null;
			drawHandlerMethodName = "";
		}
	}

	/**
	 * Attempt to add the 'pre' handler method. 
	 * The default event handler is a method that returns void and has two
	 * parameters Papplet and GWinData
	 * 
	 * @param obj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 */
	public void addPreHandler(Object obj, String methodName){
		try{
			preHandlerObject = obj;
			preHandlerMethodName = methodName;
			preHandlerMethod = obj.getClass().getMethod(methodName, new Class[] {GCWinApplet.class, GWinData.class } );
		} catch (Exception e) {
			GMessenger.message(NONEXISTANT, this, new Object[] {methodName, new Class[] { this.getClass() } } );
			preHandlerObject = null;
			preHandlerMethodName = "";
		}
	}

	/**
	 * Attempt to add the 'mouse' handler method. 
	 * The default event handler is a method that returns void and has two
	 * parameters Papplet and GWinData
	 * 
	 * @param obj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 */
	public void addMouseHandler(Object obj, String methodName){
		try{
			preHandlerObject = obj;
			preHandlerMethodName = methodName;
			preHandlerMethod = obj.getClass().getMethod(methodName, 
					new Class[] {GCWinApplet.class, GWinData.class, MouseEvent.class } );
			embed.registerMouseEvent(embed);
			regMouse = true;
		} catch (Exception e) {
			GMessenger.message(NONEXISTANT, this, new Object[] {methodName, new Class[] { this.getClass() } } );
			preHandlerObject = null;
			preHandlerMethodName = "";
		}
	}

	public void add(GComponent component){
		component.changeWindow(embed);
	}

	public void addData(GWinData data){
		this.data = data;
		this.data.owner = this;
	}
	
	public void setLocation(int x, int y){
		super.setLocation(x,y);
	}
	
	private void removeFromG4P(){
		embed.noLoop();
		embed.unregisterPost(embed);
		if(regDraw)
			embed.unregisterDraw(embed);
		if(regPre)
			embed.unregisterPre(embed);
		if(regMouse)
			embed.unregisterMouseEvent(embed);
		regDraw = regPre = regMouse = false;
		G4P.removeControlWindow(this);
	}

	
	
	/**
	 * @return the exitBehaviour
	 */
	public int getExitBehaviour() {
		return exitBehaviour;
	}

	/**
	 * CLOSE_ON_EXIT  - closes/hides the window
	 * SHUTDOWN_ON_EXIT  - ends the application if the window is closed
	 * 
	 * @param exitBehaviour the exitBehaviour to set
	 */
	public void setExitBehaviour(int exitBehaviour) {
		this.exitBehaviour = exitBehaviour;
	}

	/**
	 * 
	 * This class extends PApplet and provides a drawing surface for
	 * the GWindo
	 * The PApplet embedded into a Frame
	 * 
	 * 
	 * @author Peter Lager
	 */
	public class GCWinApplet extends PApplet {

		public int appWidth, appHeight;
		public int bkColor;

		public void setup() {
			size(appWidth, appHeight);
			registerPost(this);
			//frameRate(30);
		}

		public void pre(){
			if(drawHandlerObject != null){
				try {
					preHandlerMethod.invoke(preHandlerObject, new Object[] { embed, data });
				} catch (Exception e) {
					GMessenger.message(EXCP_IN_HANDLER, preHandlerObject, new Object[] {preHandlerMethodName } );
				}
			}
		}
		
		public void draw() {
			pushMatrix();
			app.hint(DISABLE_DEPTH_TEST);
			background(bkColor);
			if(drawHandlerObject != null){
				try {
					drawHandlerMethod.invoke(drawHandlerObject, new Object[] { embed, data });
				} catch (Exception e) {
					GMessenger.message(EXCP_IN_HANDLER, drawHandlerObject, new Object[] {drawHandlerMethodName } );
				}
			}
			app.hint(ENABLE_DEPTH_TEST);
			popMatrix();
		}

		public void mouseEvent(MouseEvent event){
			if(drawHandlerObject != null){
				try {
					preHandlerMethod.invoke(preHandlerObject, new Object[] { embed, data, event });
				} catch (Exception e) {
					GMessenger.message(EXCP_IN_HANDLER, preHandlerObject, new Object[] {preHandlerMethodName } );
				}
			}
		}
	
		public void post(){
			if(isVisible() && G4P.cursorChangeEnabled){
				if(GComponent.cursorIsOver != null)
					cursor(G4P.mouseOver);
				else
					cursor(G4P.mouseOff);
			}
		}
	}
}