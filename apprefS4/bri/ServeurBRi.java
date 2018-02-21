package bri;


import java.io.*;
import java.net.*;


public class ServeurBRi implements Runnable {
	private ServerSocket listen_socket;
	private TypeUser type;
	
	// Cree un serveur TCP - objet de la classe ServerSocket
	public ServeurBRi(int port, TypeUser t) {
		try {
			listen_socket = new ServerSocket(port);
			type = t;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// Le serveur ecoute et accepte les connections.
	// pour chaque connection, il cree un ServiceInversion, 
	// qui va la traiter.
	public void run() {
		try {
			//TODO : on veut que ServiceProgrammeur & ServiceAmateur extend ServiceBri, et on veut ici définir quel ServiceBri sera lancé par le serveur selon le TypeUser
			while(true){
				new ServiceBRi(listen_socket.accept()).start();
				System.out.println("nouvelle connexion");
			}
		}
		catch (IOException e) { 
			try {this.listen_socket.close();} catch (IOException e1) {}
			System.err.println("Pb sur le port d'écoute :"+e);
		}
	}

	 // restituer les ressources --> finalize
	protected void finalize() throws Throwable {
		try {this.listen_socket.close();} catch (IOException e1) {}
	}

	// lancement du serveur
	public void lancer() {
		(new Thread(this)).start();		
	}
}
