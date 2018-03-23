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
		catch(bri.ServiceNotFoundException e){}
			
		
		
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
		
		/*
		 * install dans xml, il fait déjà le check si le service existe
		 * String serviceName = classeName.split(".")[1];
		 * XmlHandler.installService(serviceName, classeName);
		 * */
	}

	public static void updateService(String classeName) throws ClassNotFoundException, IOException, NonConformityException, ServiceAlreadyInstalledException, bri.ServiceNotFoundException {
		
		
		/*
		 * XmlHandler.installService(serviceName, classeName);
		 * XmlHandler.uninstallService(serviceName);
		 * */
		
			uninstallService(classeName);
			installService(classeName);
			
		
	}
	public static void enableService(String classeName) throws Exception{
		//Le nom de example.ServiceInversion est ServiceInversion (pour donner un example)
		String serviceName = classeName.split(".")[1];
		bri.XmlHandler.enableService(serviceName);
	}
	
	public static void uninstallService(String classeName) throws bri.ServiceNotFoundException{
		
		/*
		 * uninstallService check deja si le service existe
		 * 
		 * Le nom de example.ServiceInversion est ServiceInversion (pour donner un example)
		 * String serviceName = classeName.split(".")[1];
		 * XmlHandler.uninstallService(serviceName);
		 */
		
		ServiceRegistry.checkExists(classeName);
		ServiceRegistry.removeService(classeName);
	}
	
	public static void disableService(String classeName) throws Exception{
		//Le nom de example.ServiceInversion est ServiceInversion (pour donner un example)
		String serviceName = classeName.split(".")[1];
		bri.XmlHandler.disableService(serviceName);
	}

	public static String printServices() {
		return ServiceRegistry.toStringue();
	}
}
