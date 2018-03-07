package bri;


import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

import examples.ServiceInversion;


class ServiceAmaBri implements Runnable {
	
	private Socket client;
	
	public ServiceAmaBri(Socket socket) {
		client = socket;
	}

	public void run() {
		try {BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			out.println(ServiceRegistry.toStringue() + "##Tapez le numéro de service désiré : ");
			int choix = Integer.parseInt(in.readLine());
			Class<? extends Service> service = (Class <? extends Service>) ServiceRegistry.getServiceClass(choix);
			Service serv;
			System.out.println(service);
			// instancier le service numéro "choix" en lui passant la socket "client"
			serv = service.getDeclaredConstructor(Socket.class).newInstance(client);
			System.out.println(serv);
			// invoquer run() pour cette instance ou la lancer dans un thread à part 
			Thread n = new Thread(serv);
			n.start();
			while(n.isAlive()){Thread.sleep(1000);}
			}
		catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InterruptedException e) {
			//Fin du service
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
