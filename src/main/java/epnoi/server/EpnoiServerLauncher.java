package epnoi.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

/*
 import com.sun.grizzly.http.SelectorThread;
 import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
 */

public class EpnoiServerLauncher {

	public static void main(String[] args) throws IOException {

		final String baseUri = "http://localhost:9998/";
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages",
				"epnoi.server.services");
		

		System.out.println("Starting grizzly...");
		SelectorThread threadSelector = GrizzlyWebContainerFactory.create(
				baseUri, initParams);
		System.out
				.println(String
						.format("Jersey app started with WADL available at %sapplication.wadl\n� +�Try out %shelloworld\nHit enter to stop it...",
								baseUri, baseUri));
		System.in.read();
		threadSelector.stopEndpoint();
		System.exit(0);
	}
}
