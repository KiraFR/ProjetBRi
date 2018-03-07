package serviceProgrammeur;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

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
	
	
	
	public static void installService(String classeName){
		
		try {
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
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public static void updateService(String readLine) {
	
	}
	public static void enableService(String classeName){
		
	}
	
	public static void uninstallService(String classeName){
		
	}
	
	public static void disableService(String classeName){
		
	}
}
