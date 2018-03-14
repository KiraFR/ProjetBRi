package bri;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partag�e en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = new Vector<Class<? extends Service>>();
	}
	private static List<Class<? extends Service>> servicesClasses;

// ajoute une classe de service apr�s contr�le de la norme BLTi
	@SuppressWarnings("unchecked")
	public static void addService(Class<?> c) throws NonConformityException {
		// v�rifier la conformit� par introspection
		if(!Service.class.isAssignableFrom(c))
			throw new NonConformityException("Classe envoy�e n'extend pas Service");
		if(Modifier.isAbstract(c.getModifiers()))
			throw new NonConformityException("Classe envoy�eest abstraite");
		if(!Modifier.isPublic(c.getModifiers()))
			throw new NonConformityException("Classe envoy�e n'est pas publique");
		try {
		if(!Modifier.isPublic(c.getConstructor(Socket.class).getModifiers()))
			throw new NonConformityException("Classe envoy�e ne contiens pas de constructeur public avec un param�tre de type Socket");
		
			if(!(c.getDeclaredConstructor(Socket.class).getExceptionTypes().length == 0))
				throw new NonConformityException("Le constructeur public avec un param�tre Socket renvoie des exceptions");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new NonConformityException("le constructeur n'est pas conforme.");
		}
		if(!hasPrivateFinalSocketField(c))
			throw new NonConformityException("Classe envoy�e ne contiens pas d'attribut Socket priv�");
		if(!hasToStringueMethod(c))
			throw new NonConformityException("Classe envoy�e n'a pas de m�thode : public String toStringue()");
				
		
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
	
// liste les activit�s pr�sentes
	public static String toStringue() {
		String result = "Activit�s pr�sentes :";
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
	
	public static void checkExists(String classeName) throws ServiceNotFoundException{
		for(Class <? extends Service> c : servicesClasses){
			if(c.getName().equals(classeName))
				return;
		}
		throw new bri.ServiceNotFoundException();
	}

	public static int getServiceIndex(String classeName) throws bri.ServiceNotFoundException{
		int index = 0;
		for(Class <? extends Service> c : servicesClasses){
			if (c.getName().equals(classeName))
				return index;
			++index;
		}
		throw new bri.ServiceNotFoundException();
	}
	
	public static void removeService(String classeName) throws ServiceNotFoundException {
		servicesClasses.remove(getServiceIndex(classeName));
		
	}

}
