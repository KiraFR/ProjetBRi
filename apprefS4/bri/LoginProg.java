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
		
		Element parent = (Element) doc.getElementsByTagName("programers").item(0);
		NodeList listNode = parent.getElementsByTagName("connection");
		System.out.println(listNode.getLength());
		for(int i = 0; i < listNode.getLength(); i++){
			Element child = (Element) listNode.item(i); 
			Element loginP =(Element) child.getChildNodes().item(1);
			Element pwdP =(Element) child.getChildNodes().item(3);
			System.out.println(loginP.getTextContent() + " " + pwdP.getTextContent());
			if(login.equals(child.getElementsByTagName("login").item(0).getTextContent())){
				if(password.equals(child.getElementsByTagName("password").item(0).getTextContent())){
					return true;
				}
			}
		}
		return false;
	}
}
