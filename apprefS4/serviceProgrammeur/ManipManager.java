package serviceProgrammeur;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.management.ServiceNotFoundException;

import bri.NonConformityException;
import bri.ServiceRegistry;

public class ManipManager {
	
	static URL url[] = new URL[1];
	static{
		
		try {
			url[0] = new URL("ftp://localhost:2121/classes/");
		} catch (MalformedURLException e) {
			System.err.println("Cannot find the URL : \"ftp://localhost:2121/classes/\"");
		}
		
	}
	
	
	
	public static void installService(String classeName) throws IOException, ClassNotFoundException, NonConformityException, ServiceAlreadyInstalledException{
		
		try{
			ServiceRegistry.checkExists(classeName);
			throw new ServiceAlreadyInstalledException();
		}
		catch(ServiceNotInstalledException e){}
			
		
		
		URLClassLoader urlcl = new URLClassLoader(url){
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if (classeName.equals(name))
                    return findClass(name);
                return super.loadClass(name);
            }
        };
		
		Class<?> c = urlcl.loadClass(classeName);
		System.out.println("classe chargée : " + c);
		urlcl.close();
		ServiceRegistry.addService(c);
		try {
			XmlHandler.installService(classeName, classeName);
		} catch (ServiceAlreadyInstalledException e) {
			System.err.println("le service " + classeName + " existe deja dans le fichier de configuration");
		}
	}

	public static void updateService(String classeName) throws ClassNotFoundException, IOException, NonConformityException, ServiceAlreadyInstalledException, ServiceNotInstalledException {
		
			uninstallService(classeName);
			installService(classeName);
			
			try {
				XmlHandler.uninstallService(classeName);
				XmlHandler.installService(classeName, classeName);
			} catch (ServiceNotInstalledException e) {
				System.err.println("Le service " + classeName + " n'a pas été trouvé dans le fichier de configuration");
			} catch (ServiceAlreadyInstalledException e) {
				System.err.println("Le service " + classeName + " est déjà installé dans le fichier de configuration");
			}
		
	}
	public static void enableService(String classeName) {
		try {
			XmlHandler.activateService(classeName);
		} catch (ServiceNotInstalledException e) {
			System.err.println("le service " + classeName + " n'a pas été trouvé dans le fichier de configuration");
		}
	}
	
	public static void uninstallService(String classeName) throws ServiceNotInstalledException {
		ServiceRegistry.checkExists(classeName);
		ServiceRegistry.removeService(classeName);
		try {
			XmlHandler.uninstallService(classeName);
		} catch (ServiceNotInstalledException e) {
			System.err.println("le service " + classeName + " n'as pas été trouvé dans le fichier de configuration");
		}
	}
	
	public static void disableService(String classeName) {
		try {
			XmlHandler.desactivateService(classeName);
		} catch (ServiceNotInstalledException e) {
			System.err.println("le service " + classeName + " n'a pas été trouvé dans le fichier de configuration");
		}
	}

	public static String printServices() {
		return ServiceRegistry.toStringue();
	}
}
