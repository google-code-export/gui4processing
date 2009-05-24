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

/**
 * CLASS FOR INTERNAL USE ONLY
 * 
 * @author Peter Lager
 *
 */
public class GMessenger implements GConstants {

	public static void message(Integer id, Object obj, Object[] info){
		// Display G4P messages if required
		if(G4P.messages){
			switch(id){
			case MISSING:
				missingEventHandler(obj, info);
				break;
			case NONEXISTANT:
				nonexistantEventHandler(obj, info);
				break;
			case ADD_DUPLICATE:
				System.out.println("Component " + obj + " has already been regitered!");
				break;
			case USER_COL_SCHEME:
				System.out.println("USER DEFINED colour schema active");
				break;
			case DISABLE_AUTO_DRAW:
				System.out.println("You have disabled autoDraw so you have to use");
				System.out.println("G4P.draw() when you want to display the GUI" );
				System.out.println("this is not action is not reversible." );
				break;
			}
		}
		// Display all runtime errors
		switch(id){
		case EXCP_IN_HANDLER:
			eventHandlerFailed(obj, info);
			break;
		
		}
	}
	
	/**
	 * 
	 * @param handler
	 * @param info
	 */
	private static void eventHandlerFailed(Object handler, Object[] info) {
		String className = handler.getClass().getSimpleName();
		String methodName = (String) info[0];
		StringBuilder output = new StringBuilder();

		output.append("#######  EXCEPTION IN EVENT HANDLER  #######\n");
		output.append("An exception occured during execution of the\n");
		output.append("eventhandler. Examine your code\n");
		output.append("  Class: "+className+"   Method: "+methodName+"\n");
		System.out.println(output);
	}

	/**
	 * 
	 * @param obj1 the object generating the method
	 * @param obj2 the method name
	 * @param obj3 parameter types (Class[])
	 */
	@SuppressWarnings("unchecked")
	private static void missingEventHandler(Object caller, Object[] info) {
		String className = caller.getClass().getSimpleName();
		String methodName = (String) info[0];
		String pname;
		StringBuilder output = new StringBuilder();
		
		output.append("You might want to add a method to handle " + className + " events syntax is\n");
		output.append("void " + methodName + "(");
		if(info != null && info.length > 1){
			Class[] parameters = (Class[])(info[1]);
			if(parameters == null)
				parameters = new Class[0];
			for(int i = 0; i < parameters.length; i++){
				pname = (parameters[i]).getSimpleName();
				output.append(pname + " " + pname.substring(1).toLowerCase());
				if(parameters.length > 1)
					output.append(i);
				if(i < parameters.length - 1)
					output.append(", ");
			}
		}
		output.append(") { /* code */ }\n");
		System.out.println(output.toString());
	}

	/**
	 * 
	 * @param obj1 the object generating the method
	 * @param obj2 the method name
	 * @param obj3 parameter types (Class[])
	 */
	@SuppressWarnings("unchecked")
	private static void nonexistantEventHandler(Object handler, Object[] info) {
		String className = handler.getClass().getSimpleName();
		String methodName = (String) info[0];
		String pname;
		StringBuilder output = new StringBuilder();
		
		output.append("The "+className+" class does not have this method \n");
		output.append("\tvoid " + methodName + "(");
		if(info != null && info.length > 1){
			Class[] parameters = (Class[])(info[1]);
			if(parameters == null)
				parameters = new Class[0];
			for(int i = 0; i < parameters.length; i++){
				pname = (parameters[i]).getSimpleName();
				output.append(pname + " " + pname.substring(1).toLowerCase());
				if(parameters.length > 1)
					output.append(i);
				if(i < parameters.length - 1)
					output.append(", ");
			}
		}
		output.append(") { /* code */ }\n");
		System.out.println(output.toString());
	}


}
