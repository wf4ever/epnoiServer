package epnoi.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.omg.stub.java.rmi._Remote_Stub;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

/*
 import com.sun.grizzly.http.SelectorThread;
 import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
 */

public class EpnoiServer {
	public static final String HOSTNAME_PROPERTY = "server.hostname";
	public static final String PORT_PROPERTY = "server.port";
	public static final String PATH_PROPERTY = "server.path";

	public static void main(String[] args) throws IOException {


		final String baseUri = "http://localhost:9998/";
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages",
				"epnoi.server.services");
		
		
		Properties initializationProperties = EpnoiServer._readProperties();
		
		String serverPath = "http://"+ initializationProperties.getProperty(HOSTNAME_PROPERTY)+":"+
		initializationProperties.getProperty(PORT_PROPERTY)+"/"+initializationProperties.getProperty(PATH_PROPERTY);

			
		System.out.println("Starting grizzly...");
		SelectorThread threadSelector = GrizzlyWebContainerFactory.create(
				serverPath, initParams);
		System.out
				.println(String
						.format("Jersey app started with WADL available at %sapplication.wadl\n” +“Try out %shelloworld\nHit enter to stop it...",
								baseUri, baseUri));
		System.in.read();
		threadSelector.stopEndpoint();
		System.exit(0);
	}
	
	public static Properties _readProperties() {
		Properties properties = new Properties();

		try {
			URL configFileURL = EpnoiServer.class.getResource("epnoi.xml");

			FileInputStream fileInputStream = new FileInputStream(new File(
					configFileURL.getPath()));
			properties.loadFromXML(fileInputStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(".... properties ...");
		properties.list(System.out);
		return properties;
	}
}
