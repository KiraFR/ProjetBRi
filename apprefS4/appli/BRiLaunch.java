package appli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.util.Scanner;

import javax.swing.plaf.synth.SynthSpinnerUI;

import bri.NonConformityException;
import bri.ServeurBRi;
import bri.ServiceProgBri;
import bri.ServiceRegistry;
import serviceProgrammeur.ManipManager;
import serviceProgrammeur.ServiceAlreadyInstalledException;
import bri.ServiceAmaBri;

public class BRiLaunch {
	private final static int PORT_PROG = 3000;
	private final static int PORT_AMA = 3030;
	
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));
		
		ServiceRegistry.init();
		
		System.out.println("Bienvenue dans votre gestionnaire dynamique d'activité BRi");
		System.out.println("Pour ajouter une activité, celle-ci doit être présente sur votre serveur ftp");
		System.out.println("A tout instant, en tapant le nom de la classe, vous pouvez l'intégrer");
		System.out.println("Les clients se connectent au serveur 3000 pour lancer une activité");
		
		new Thread(new ServeurBRi(PORT_PROG, ServiceProgBri.class)).start();
		new Thread(new ServeurBRi(PORT_AMA, ServiceAmaBri.class)).start();
		
		while(true){
			
				try {
					ManipManager.installService(clavier.readLine());
				} catch (ClassNotFoundException e) {
					System.err.println("classe non présente");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NonConformityException e) {
					System.err.println("classe non conforme");
				} catch (ServiceAlreadyInstalledException e) {
					System.err.println("service deja installé");
				}
		}
	}
}
