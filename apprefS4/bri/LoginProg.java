package bri;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LoginProg {
	
	public static boolean checkLogin(String login, String password){
		File xmlFile = new File("config_xml.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();
		
		NodeList listNode = doc.getElementsByTagName("programers");
		for(int i = 0; i < listNode.getLength(); i++){
			Element child =(Element) listNode.item(i);
			if(login.equals(child.getElementsByTagName("login").item(0))){
				if(password.equals(child.getElementsByTagName("password").item(0))){
					return true;
				}
			}
		}
		return false;
	}
}
