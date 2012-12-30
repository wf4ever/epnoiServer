package epnoi.server;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

import epnoi.logging.EpnoiLogger;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.ParametersModelReader;

public class EpnoiServer {
	private static final Logger logger = Logger.getLogger(EpnoiServer.class
			.getName());

	public static void main(String[] args) throws IOException {
		// The first step is to set up the logger
		EpnoiLogger.setup();

		// final String baseUri = "http://localhost:9998/";
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages",
				"epnoi.server.services, epnoi.server.services.json");
		initParams.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
		ParametersModel parametersModel = null;

		parametersModel = EpnoiServer._readParameters();

		String serverPath = "http://" + parametersModel.getHostname() + ":"
				+ parametersModel.getPort() + "/" + parametersModel.getPath();

		logger.info("Starting grizzly...");

		SelectorThread threadSelector = null;
		try {
			threadSelector = GrizzlyWebContainerFactory.create(serverPath,
					initParams);
		} catch (Exception e) {

		}
		logger.info("Epnoi server started with WADL available at " + serverPath
				+ "application.wadl");

		System.in.read();
		if (threadSelector != null) {

			threadSelector.stopEndpoint();
		}
		logger.info("The Recommender Service has been shut down propperly");
		System.exit(0);

	}

	public static ParametersModel _readParameters() {
		ParametersModel parametersModel = null;

		try {
			URL configFileURL = EpnoiServer.class.getResource("epnoi.xml");
			parametersModel = ParametersModelReader.read(configFileURL
					.getPath());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		parametersModel.resolveToAbsolutePaths(EpnoiServer.class);

		return parametersModel;
	}
}
