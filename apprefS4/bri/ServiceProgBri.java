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

	private void askLogin() throws IOException{
		
		out.println("login : ");
		String login = in.readLine();
		out.println("password : ");
		String pw = in.readLine();
		//TODO : LoginProg.checkLogin
		
	}
	
	public void run() {
		try {
			
			in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			out = new PrintWriter (client.getOutputStream ( ), true);
			askLogin();
			out.println("vous pouvez :##1 : charger une classe de service## votre choix : ");
			int choix = Integer.parseInt(in.readLine());
			switch(choix){
			case 1:
				out.println("##nom de la classe de service à charger : ");
				ManipManager.enableService(in.readLine());
				break;
			}
			}
		catch (IOException e) {
			e.printStackTrace(System.err);
		}

		try {client.close();} catch (IOException e2) {}
	}
	
	protected void finalize() throws Throwable {
		 client.close(); 
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();		
	}

}
