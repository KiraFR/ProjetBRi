package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

import javax.management.ServiceNotFoundException;

import serviceProgrammeur.ManipManager;
import serviceProgrammeur.ServiceAlreadyInstalledException;
import serviceProgrammeur.ServiceNotInstalledException;

public class ServiceProgBri implements Runnable {

private Socket client;
PrintWriter out;
	
BufferedReader in;

	public ServiceProgBri(Socket socket) {
		client = socket;
	}

	private String askLogin() throws WrongLoginException, IOException{
		
		out.println("login : ");
		String login = in.readLine();
		out.println("password : ");
		String pw = in.readLine();
		//TODO : fix checkLogin
		/*if(!LoginProg.checkLogin(login, pw))
			throw new WrongLoginException();*/
		return login;
	}
	
	public void run() {
		
			try{
			in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			out = new PrintWriter (client.getOutputStream ( ), true);
			String name = askLogin();
				out.println("Bienvenue " + name + "##vous pouvez :##"
						+ "1 : installer un service##"
						+ "2 : activer un service##"
						+ "3 : d�sinstaller un service##"
						+ "4 : d�sactiver un service##"
						+ "5 : re-charger et mettre � jour un service##"
						+ "6 : afficher tous les services install�s##"
						+ "votre choix : ");
				int choix = Integer.parseInt(in.readLine());
				switch(choix){
					case 1:
						out.println("nom de la classe de service � installer : ");
						ManipManager.installService(in.readLine());
						out.println("service install�. Merci.");
						break;
					case 2:
						out.println("nom de la classe de service � activer : ");
						ManipManager.enableService(in.readLine());
						out.println("service activ�. Merci.");
						break;
					case 3:
						out.println("nom de la classe de service � d�sinstaller : ");
						ManipManager.uninstallService(in.readLine());
						out.println("service d�sinstall�. Merci.");
						break;
					case 4:
						out.println("nom de la classe de service � d�sactiver : ");
						ManipManager.disableService(in.readLine());
						out.println("service d�sactiv�. Merci.");
						break;
					case 5:
						out.println("nom de la classe de service � mettre � jour : ");
						ManipManager.updateService(in.readLine());
						out.println("service mis � jour. Merci.");
						break;
					case 6:
						out.println(ManipManager.printServices());
						break;
					
					}
				}
				
				
				catch(ClassNotFoundException e){
					out.println("classe inconnue.");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NonConformityException e) {
					out.println("Ce service n'est pas conforme : " + e);
					e.printStackTrace();
				} catch (ServiceAlreadyInstalledException e) {
					out.println("Ce service est d�j� install�. Essayez une mise � jour � la place");
					e.printStackTrace();
				} catch (bri.ServiceNotFoundException e) {
					out.println("Le service n'a pas �t� trouv�.");
					e.printStackTrace();
				} catch (WrongLoginException e) {
					out.println("Mauvais login");
				}
			
			
		
		try {
			client.close();
			System.out.println("fermeture de connexion");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void finalize() throws Throwable {
		 client.close(); 
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();		
	}

}
