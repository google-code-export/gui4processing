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

package guicomponents;

import java.util.LinkedList;

/**
 * Allows TABBING between text controls.
 * A tab manager allows the user to use the TAB key to move from one text control
 * (GTextField or GTextArea) to the another. This is useful when creating a 'form'
 * made from several text controls. <br>
 * The tab order is decised by the order the text controls are added to the tab 
 * manager. The TAB key move the focus forwards and SHIFT+TAB moves it backwards.<br>
 * At least 2 controls must be added to the control.
 * 
 * @author Peter Lager
 *
 */
public class GTabManager {
	
	private LinkedList<GEditableTextControl> controls;
	
	public GTabManager(){
		controls = new LinkedList<GEditableTextControl>();
	}
	
	public boolean addControls(GEditableTextControl... ctrls){
		boolean result = false;
		for(GEditableTextControl control : ctrls)
			result |= addControl(control);
		return result;
	}
	
	public boolean addControl(GEditableTextControl control){
		if(!controls.contains(control)){
			control.tabManager = this;
			controls.addLast(control);
			return true;
		}
		return false;
	}
	
	public boolean removeControl(GEditableTextControl control){
		int index = controls.lastIndexOf(control);
		if(index > 0){
			control.tabManager = null;
			controls.remove(index);
			return true;
		}
		return false;
	}
	
	public boolean nextControl(GEditableTextControl control){
		int index = controls.lastIndexOf(control);
		if(controls.size() > 1 && index >= 0 && index < controls.size() - 1){
			index++;
			//control = controls.get(index);
			GAbstractControl.controlToTakeFocus = controls.get(index);;
			return true;
		}
		return false;
	}

	public boolean prevControl(GEditableTextControl control){
		int index = controls.lastIndexOf(control);
		if(controls.size() > 1 && index > 0){
			index--;
			//control = controls.get(index);
			GAbstractControl.controlToTakeFocus = controls.get(index);
			return true;
		}
		return false;
	}

	
}
