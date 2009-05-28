/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
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

import java.awt.event.MouseEvent;

import processing.core.PApplet;

/**
 * CLASS FOR INTERNAL USE ONLY
 * 
 * This class extends PApplet and provides a drawing surface for
 * the GWindow class.
 * 
 * @author Peter Lager
 */
public class GWinApplet extends PApplet implements GConstants {
	
	public GWindow owner;
	public int appWidth, appHeight;
	public int bkColor;

	public void setup() {
		size(appWidth, appHeight);
		registerPost(this);
		//frameRate(30);
	}

	public void pre(){
		if(owner.drawHandlerObject != null){
			try {
				owner.preHandlerMethod.invoke(owner.preHandlerObject, 
						new Object[] { owner.embed, owner.data });
			} catch (Exception e) {
				GMessenger.message(EXCP_IN_HANDLER, owner.preHandlerObject, 
						new Object[] {owner.preHandlerMethodName } );
			}
		}
	}
	
	public void draw() {
		background(bkColor);
		if(owner.drawHandlerObject != null){
			try {
				owner.drawHandlerMethod.invoke(owner.drawHandlerObject, new Object[] { this, owner.data });
			} catch (Exception e) {
				GMessenger.message(EXCP_IN_HANDLER, owner.drawHandlerObject, new Object[] {owner.drawHandlerMethodName } );
			}
		}
	}

	public void mouseEvent(MouseEvent event){
		if(owner.drawHandlerObject != null){
			try {
				owner.preHandlerMethod.invoke(owner.preHandlerObject, new Object[] { this, owner.data, event });
			} catch (Exception e) {
				GMessenger.message(EXCP_IN_HANDLER, owner.preHandlerObject, new Object[] {owner.preHandlerMethodName } );
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
