package bri;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlHandler {
	
	private static File xmlFile;
	private static DocumentBuilderFactory dbFactory;
	private static DocumentBuilder dBuilder;
	private static Document doc;
	private static Element root; // racine du xml
	private static Element con; // branche "connections" dans la racine
	private static Element services; // branche "services" dans la racine
	
	static {
		try {
			xmlFile = new File("config_xml.xml");
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			root = (Element) doc.getElementsByTagName("programers").item(0);
			con = (Element) root.getElementsByTagName("connections").item(0);
			services = (Element) root.getElementsByTagName("services").item(0);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static boolean checkLogin(String login, String password){
		NodeList listNode = con.getElementsByTagName("connection");
		if(listNode.getLength() == 0) {return false;}
		for(int i = 0; i < listNode.getLength(); i++){
			Element ch = (Element) listNode.item(i);
			if(login.equals(ch.getElementsByTagName("login").item(0).getTextContent())){
				if(password.equals(ch.getElementsByTagName("password").item(0).getTextContent())){
					return true;
				}
			}
		}
		return false;
	}
	
	
	public static void installService(String serviceName,String serviceClass) throws Exception {
		if(ServiceExist(serviceName)) throw new Exception("Le service est déjà installé");
		Element node = doc.createElement(serviceName);
		
		Element className = doc.createElement("class");
		className.appendChild(doc.createTextNode(serviceClass));
		Element boolPublic = doc.createElement("public");
		boolPublic.appendChild(doc.createTextNode("false"));
		
		node.appendChild(className);
		node.appendChild(boolPublic);

		services.appendChild(node);

		updatefile();
	}
	
	public static void uninstallService(String serviceName) throws Exception {
		if(!ServiceExist(serviceName)) throw new Exception("Vous voulez desinstaller un service qui n'existe pas.");
		services.removeChild(services.getElementsByTagName(serviceName).item(0));
		
		
		updatefile();
	}
	
	
	
	public static void enableService(String serviceName) throws Exception {
		if(!ServiceExist(serviceName)) throw new Exception("Vous voulez activer un service qui n'existe pas.");
		Element node = (Element) services.getElementsByTagName(serviceName).item(0);
		node.getElementsByTagName("public").item(0).setTextContent("true");;
		
		updatefile();
	}
	
	public static void disableService(String serviceName) throws Exception {
		if(!ServiceExist(serviceName)) throw new Exception("Vous voulez activer un service qui n'existe pas.");
		Element node = (Element) services.getElementsByTagName(serviceName).item(0);
		node.getElementsByTagName("public").item(0).setTextContent("false");
		
		updatefile();
	}
	
	private static boolean ServiceExist(String ServiceName) {
		NodeList listNode = root.getElementsByTagName("services").item(0).getChildNodes();
		if(listNode.getLength() == 0) {return false;}
		for(int i = 0; i < listNode.getLength(); i++){
			Node ch = listNode.item(i);
			if(ServiceName.equals(ch.getNodeName())){
				return true;
			}
		}
		return false;
	}

	private static void updatefile() throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(xmlFile);
		transformer.transform(source, result);
	}
}
