package guicomponents;

import java.util.HashMap;

public class GMessageManager implements GConstants {

	private static HashMap<Integer, Integer> messCounter = new HashMap<Integer, Integer>();
	private static int count;

	public static void message(Integer id, Object obj1, Object obj2, Object obj3){
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
				missingEventHandler(obj1, obj2, obj3);
			}
		}
	}
	
	/**
	 * 
	 * @param obj1 the object generating the method
	 * @param obj2 the method name
	 * @param obj3 parameters
	 */
	private static void missingEventHandler(Object obj1, Object obj2, Object obj3) {
		System.out.print("The class " + obj1.getClass().getSimpleName());
		System.out.println(" does not have a method called " + (String)obj2);
		System.out.println("with a parameter(s) of type " + (String)obj3);
	}


}
