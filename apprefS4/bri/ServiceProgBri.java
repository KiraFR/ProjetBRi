package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

import serviceProgrammeur.ManipManager;

public class ServiceProgBri implements Runnable {

private Socket client;
PrintWriter out;
	
BufferedReader in;

	public ServiceProgBri(Socket socket) {
		client = socket;
	}

	private void askLogin() throws Exception{
		
		out.println("login : ");
		String login = in.readLine();
		out.println("password : ");
		String pw = in.readLine();
		if(!LoginProg.checkLogin(login, pw))
			throw new Exception("Bad login");
	}
	
	public void run() {
		try {
			
			in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			out = new PrintWriter (client.getOutputStream ( ), true);
			//askLogin();
			out.println("vous pouvez :##"
					+ "1 : installer un service##"
					+ "2 : activer un service##"
					+ "3 : d�sinstaller un service##"
					+ "4 : d�sactiver un service##"
					+ "5 : re-charger et mettre � jour un service##"
					+ "votre choix : ");
			int choix = Integer.parseInt(in.readLine());
			switch(choix){
			case 1:
				out.println("##nom de la classe de service � installer : ");
				ManipManager.installService(in.readLine());
				out.println("##service install�. Merci.");
				break;
			case 2:
				out.println("##nom de la classe de service � activer : ");
				ManipManager.enableService(in.readLine());
				out.println("##service activ�. Merci.");
				break;
			case 3:
				out.println("##nom de la classe de service � d�sinstaller : ");
				ManipManager.uninstallService(in.readLine());
				out.println("##service d�sinstall�. Merci.");
				break;
			case 4:
				out.println("##nom de la classe de service � d�sactiver : ");
				ManipManager.disableService(in.readLine());
				out.println("##service d�sactiv�. Merci.");
				break;
			case 5:
				out.println("##nom de la classe de service � mettre � jour : ");
				ManipManager.updateService(in.readLine());
				out.println("##service mis � jour. Merci.");
			
			}
				
			}
		catch (Exception e) {
			e.printStackTrace(System.err);
			
			
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
