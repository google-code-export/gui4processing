package guicomponents;

import java.util.HashMap;

public class GMessenger implements GConstants {

	private static HashMap<Integer, Integer> messCounter = new HashMap<Integer, Integer>();
	private static int count;

	public static void message(Integer id, Object caller, Object[] info){
		if(G4P.messages){
			Integer c = messCounter.get(id);
			if(c == null){
				messCounter.put(id, new Integer(1));
			}
			else {
				messCounter.put(id, new Integer(count + 1));
			}
			switch(id){
			case MISSING:
				missingEventHandler(caller, info);
			}
		}
	}
	
	/**
	 * 
	 * @param obj1 the object generating the method
	 * @param obj2 the method name
	 * @param obj3 parameters
	 */
	private static void missingEventHandler(Object caller, Object[] info) {
		String className = caller.getClass().getSimpleName();
		String methodName = (String) info[0];
		String pname;
		StringBuilder output = new StringBuilder();
		
		output.append("You might want to add a method to handle " + className + " events syntax is\n");
		output.append("void " + methodName + "(");
		if(info != null && info.length > 1){
			Class[] parameters = (Class[])(info[1]);
			for(int i = 0; i < parameters.length; i++){
				pname = (parameters[i]).getSimpleName();
				output.append(pname + " " + pname.substring(1).toLowerCase());
				if(parameters.length > 1)
					output.append(i);
				if(i < parameters.length - 1)
					output.append(", ");
			}
		}
		output.append(") { // code }\n");
		System.out.println(output.toString());
	}


}
