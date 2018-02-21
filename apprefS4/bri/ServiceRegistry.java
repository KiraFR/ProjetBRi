package bri;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagée en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = new ArrayList<Class<? extends Service>>();
	}
	private static List<Class<? extends Service>> servicesClasses;

// ajoute une classe de service après contrôle de la norme BLTi
	@SuppressWarnings("unchecked")
	public static void addService(Class<?> c) throws Exception {
		// vérifier la conformité par introspection
		if(!(Service.class.isAssignableFrom(c)
				&& !(Modifier.isAbstract(c.getModifiers()))
				&& Modifier.isPublic(c.getModifiers())
				&& Modifier.isPublic(c.getConstructor(Socket.class).getModifiers())
				&& c.getConstructor(Socket.class).getExceptionTypes().length == 0
				&& hasPrivateFinalSocketField(c)
				&& hasToStringueMethod(c)
				))
			throw new Exception("Classe envoyée n'est pas un service conforme.");
		
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		
		servicesClasses.add((Class <? extends Service>) c);
		
	}
	
private static boolean hasToStringueMethod(Class<?> c) {
	for(Method m : c.getMethods()){
		if(Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()) && m.getReturnType() == String.class && m.getName() == "toStringue" && m.getExceptionTypes().length == 0)
			return true;
	}
		
	return false;
}

private static boolean hasPrivateFinalSocketField(Class<?> c) {
	for(Field f : c.getDeclaredFields()){
		if(Modifier.isFinal(f.getModifiers()) 
				&& Modifier.isPrivate(f.getModifiers()) 
				&& f.getType() == Socket.class
				)
			return true;
	}
		
	return false;
}

// renvoie la classe de service (numService -1)	
	public static Class<? extends Service> getServiceClass(int numService) {
		return  servicesClasses.get(numService-1);
	}
	
// liste les activités présentes
	public static String toStringue() {
		String result = "Activités présentes :";
		for(Class<?> c : servicesClasses){
			try {
				result += "##" + (servicesClasses.indexOf(c)+1) + " "  + c.getMethod("toStringue").invoke(null, null);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	

}
