package bri;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import serviceProgrammeur.ServiceNotInstalledException;
import serviceProgrammeur.XmlHandler;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagée en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = new Vector<Class<? extends Service>>();
		
	}
	private static List<Class<? extends Service>> servicesClasses;
	

	public static void init(){
		for(String s : XmlHandler.getServices()){
			System.out.println(s);
			try {
				
				addService(Class.forName(s));
			} catch (ClassNotFoundException e) {
				System.err.println("Classe " + s + "non présente");
			} catch (NonConformityException e) {
				System.err.println("Classe " + s + "non conforme : " + e.getMessage());
			}
		}
	}
	
// ajoute une classe de service après contrôle de la norme BLTi
	@SuppressWarnings("unchecked")
	public static void addService(Class<?> c) throws NonConformityException {
		// vérifier la conformité par introspection
		if(!Service.class.isAssignableFrom(c))
			throw new NonConformityException("Classe envoyée n'extend pas Service");
		if(Modifier.isAbstract(c.getModifiers()))
			throw new NonConformityException("Classe envoyéeest abstraite");
		if(!Modifier.isPublic(c.getModifiers()))
			throw new NonConformityException("Classe envoyée n'est pas publique");
		try {
		if(!Modifier.isPublic(c.getConstructor(Socket.class).getModifiers()))
			throw new NonConformityException("Classe envoyée ne contiens pas de constructeur public avec un paramètre de type Socket");
		
			if(!(c.getDeclaredConstructor(Socket.class).getExceptionTypes().length == 0))
				throw new NonConformityException("Le constructeur public avec un paramètre Socket renvoie des exceptions");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new NonConformityException("le constructeur n'est pas conforme.");
		}
		if(!hasPrivateFinalSocketField(c))
			throw new NonConformityException("Classe envoyée ne contiens pas d'attribut Socket privé");
		if(!hasToStringueMethod(c))
			throw new NonConformityException("Classe envoyée n'a pas de méthode : public String toStringue()");
				
		
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
	public static Class<? extends Service> getServiceClass(int numService) throws ServiceNotInstalledException, ServiceNotEnabledException {
		if(!XmlHandler.getServicesAccess(servicesClasses.get(numService-1).getName()))
			throw new ServiceNotEnabledException();
		return  servicesClasses.get(numService-1);
	}
	
// liste les activités présentes
	public static String toStringue() {
		String result = "Activités présentes :";
		for(Class<?> c : servicesClasses){
			try {
				
				result += "##" + (servicesClasses.indexOf(c)+1) + " "  + c.getMethod("toStringue").invoke(null, null) + " : ";
				if(XmlHandler.getServicesAccess(c.getName()))
					result += "activé";
				else
					result += "désactivé";
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceNotInstalledException e) {
				System.err.println("service " + c.getName() + "pas trouvé dans fichier de configuration mais présent dans la liste des services enregistrés");
			}
		}
		return result;
	}
	
	public static void checkExists(String classeName) throws ServiceNotInstalledException{
		for(Class <? extends Service> c : servicesClasses){
			if(c.getName().equals(classeName))
				return;
		}
		throw new ServiceNotInstalledException();
	}

	public static int getServiceIndex(String classeName) throws ServiceNotInstalledException{
		int index = 0;
		for(Class <? extends Service> c : servicesClasses){
			if (c.getName().equals(classeName))
				return index;
			++index;
		}
		throw new ServiceNotInstalledException();
	}
	
	public static void removeService(String classeName) throws ServiceNotInstalledException {
		servicesClasses.remove(getServiceIndex(classeName));
		
	}

}
