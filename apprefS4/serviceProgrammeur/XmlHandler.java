package serviceProgrammeur;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
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
	
	
	public static void installService(String serviceName,String serviceClass) throws ServiceAlreadyInstalledException  {
		if(ServiceExist(serviceName)) throw new ServiceAlreadyInstalledException();
		Element node = doc.createElement("service");
		
		Element name = doc.createElement("name");
		name.appendChild(doc.createTextNode(serviceName));
		Element className = doc.createElement("class");
		className.appendChild(doc.createTextNode(serviceClass));
		Element boolPublic = doc.createElement("public");
		boolPublic.appendChild(doc.createTextNode("true"));

		node.appendChild(name);
		node.appendChild(className);
		node.appendChild(boolPublic);

		services.appendChild(node);

		updatefile();
	}
	
	public static void uninstallService(String serviceName) throws ServiceNotInstalledException {
		if(!ServiceExist(serviceName)) throw new ServiceNotInstalledException();
		NodeList service = services.getElementsByTagName("service");
		
		for(int i = 0;i < service.getLength();i++){
			Element node = (Element) service.item(i);
			
			if(serviceName.equals(node.getElementsByTagName("name").item(0).getTextContent())) {
				services.removeChild(node);
			}
		}
		
		updatefile();
	}
	
	
	public static boolean getServicesAccess(String serviceName) throws ServiceNotInstalledException {
		if(!ServiceExist(serviceName)) throw new ServiceNotInstalledException();
		NodeList service = services.getElementsByTagName("service");
		
		for(int i = 0;i < service.getLength();i++) {
			Element node = (Element) service.item(i);
			if(serviceName.equals(node.getElementsByTagName("name").item(0).getTextContent())) {
				return Boolean.parseBoolean(node.getElementsByTagName("public").item(0).getTextContent());
			}
		}
		return false;
	}
	
	public static void activateService(String serviceName) throws ServiceNotInstalledException {
		if(!ServiceExist(serviceName)) throw new ServiceNotInstalledException();
		NodeList service = services.getElementsByTagName("service");
		for(int i = 0;i < service.getLength();i++) {
			Element node = (Element) service.item(i);
			if(serviceName.equals(node.getElementsByTagName("name").item(0).getTextContent())) {
				node.getElementsByTagName("public").item(0).setTextContent("true");
			}
		}
		updatefile();
	}
	
	public static void desactivateService(String serviceName) throws ServiceNotInstalledException {
		if(!ServiceExist(serviceName)) throw new ServiceNotInstalledException();
		NodeList service = services.getElementsByTagName("service");
		
		for(int i = 0;i < service.getLength();i++) {
			Element node = (Element) service.item(i);
			if(serviceName.equals(node.getElementsByTagName("name").item(0).getTextContent())) {
				node.getElementsByTagName("public").item(0).setTextContent("false");
			}
		}
		updatefile();
	}
	
	public static ArrayList<String> getServices() {
		NodeList listNode = services.getElementsByTagName("service");
		if(listNode.getLength() == 0) {return null;}
		ArrayList<String> list = new ArrayList<>();
		for(int i = 0; i < listNode.getLength(); i++){
			Element ch = (Element) listNode.item(i);
			Node nodeClass = ch.getElementsByTagName("class").item(0);
				list.add(nodeClass.getTextContent());
			
		}
		return list;
	}
	
	private static boolean ServiceExist(String ServiceName) {
		NodeList listNode = services.getElementsByTagName("service");
		if(listNode.getLength() == 0) {return false;}
		for(int i = 0; i < listNode.getLength(); i++){
			Element ch = (Element) listNode.item(i);
			Element name = (Element) ch.getElementsByTagName("name").item(0);
			if(ServiceName.equals(name.getTextContent())){
				return true;
			}
		}
		return false;
	}
	
	private static void updatefile() {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}