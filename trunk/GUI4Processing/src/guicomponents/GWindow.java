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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import processing.core.PApplet;

/**
 * Allows the user to specify one or more controls windows.
 * 
 * @author Peter Lager
 *
 */
public class GWindow extends Frame{

	public GCWinApplet embed;


	private int dWidth, dHeight;
	private int bkColor;
	private String winName;

	public GWindow(String name, int x, int y, int w, int h, int background) {
		super(name);
		winName = name;
		dWidth = w;
		dHeight = h;
		bkColor = background;

		embed = new GCWinApplet();
		embed.frame = this;
		embed.frame.setResizable(true);

		embed.resize(dWidth, dHeight);
		embed.setPreferredSize(new Dimension(dWidth, dHeight));
		embed.setMinimumSize(new Dimension(dWidth, dHeight));

		// add the PApplet to the Frame
		setLayout(new BorderLayout());
		add(embed, BorderLayout.CENTER);

		// ensures that the animation thread is started and
		// that other internal variables are properly set.
		embed.init();

		// add an exit on close listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				removeFromG4P();
				// close this frame
				//embed.dispose();
				dispose();
			}
		});

		pack();
		setLocation(x,y);
		setVisible(true);
		G4P.addControlWindow(this);
	}

	public void add(GComponent component){
		component.changeWindow(embed);
	}

	public void setLocation(int x, int y){
		super.setLocation(x,y);
	}
	
	
	
	private void removeFromG4P(){
		embed.noLoop();
		embed.unregisterPost(embed);
		embed.unregisterDraw(embed);
		G4P.removeControlWindow(this);
	}


	/**
	 * The PApplet embeded into a Frame
	 * 
	 * 
	 * @author Peter Lager
	 */
	public class GCWinApplet extends PApplet {

		public void setup() {
			size(dWidth, dHeight);
			registerPost(this);
			System.out.println(dWidth+" X " +dHeight);
			frameRate(15);
		}

		public void draw() {
//			System.out.println("DRAW "+winName); //+"   "+w+" X "+h);
			background(bkColor);
			strokeWeight(1);
			stroke(255,0,0);
			noFill();
			rect(0,0,dWidth-1,dHeight-1);
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
