/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
	http://gui4processing.googlecode.com/svn/trunk/

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

import java.util.ArrayList;

import processing.core.PApplet;

/**
 * This is used to group options together to provide single-selection
 * from 2 or more GOption buttons.
 * 
 * @author Peter Lager
 *
 */
public class GOptionGroup {

	protected GOption selected = null;
	protected GOption deselected = null;

	protected ArrayList<GOption> options = new ArrayList<GOption>();;

	public GOptionGroup(){
	}

	/**
	 * This class does not need a reference to the applet class
	 * @deprecated
	 * @param theApplet
	 */
	public GOptionGroup(PApplet theApplet){
	}

	public ArrayList<GOption> getOptions(){
		return options;
	}

	public GOption get(int index){
		return options.get(index);
	}

	public boolean addOption(GOption option){
		if(option != null){
			options.add(option);
			option.setGroup(this);
			return true;
		}
		else
			return false;
	}

	public boolean addOption(int pos, GOption option){
		if(option != null && pos >= 0 && pos <= options.size()){
			options.add(pos, option);
			option.setGroup(this);
			return true;			
		}
		return false;
	}

	public GOption removeOption(GOption option){
		options.remove(option);
		return option;
	}

	public GOption removeOption(int index){
		GOption option = null;
		if(index >= 0 && index < options.size()){
			option = options.remove(index);
		}
		return option;
	}

	public GOption removeOption(String optText){
		System.out.println("REMOVE");
		GOption option = null;
		int i = options.size() - 1;
		while(i >= 0){
			if(options.get(i).getText().compareToIgnoreCase(optText) == 0){
				option = options.get(i);
				break;
			}
			i--;
		}
		if(option != null)
			options.remove(option);
		return option;
	}

	/**
	 * Make this option the selected one
	 * 
	 * @param option
	 */
	public void setSelected(GOption option){
		deselected = selected;
		selected = option;
	}

	/**
	 * If index is in range make this one selected
	 *  
	 * @param index
	 */
	public void setSelected(int index){
		if(index >= 0 && index < options.size()){
			deselected = selected;
			options.get(index).setSelected(true);
		}
	}

	public void setSelected(String optText){
		int i = options.size();
		while(i-- >= 0){
			if(options.get(i).getText().compareToIgnoreCase(optText) == 0)
				break;
		}
		if(i > 0)
			setSelected(options.get(i));
	}

	public GOption selectedOption(){
		return selected;
	}

	public GOption deselectedOption(){
		return deselected;
	}

	public int selectedIndex(){
		return options.indexOf(selected);
	}

	public int deselectedIndex(){
		return options.indexOf(deselected);
	}

	public String selectedText(){
		return selected.text;
	}

	public String deselectedText(){
		return deselected.text;
	}

	public int size(){
		return options.size();
	}

}
