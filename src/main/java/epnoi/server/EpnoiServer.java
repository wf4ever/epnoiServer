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
import epnoi.model.parameterization.ParametersModelWrapper;

/*
 import com.sun.grizzly.http.SelectorThread;
 import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
 */

public class EpnoiServer {
	private static final Logger logger = Logger.getLogger(EpnoiServer.class
			.getName());

	public static void main(String[] args) throws IOException {
		EpnoiLogger.setup();
		// final String baseUri = "http://localhost:9998/";
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages",
				"epnoi.server.services");

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
			parametersModel = ParametersModelWrapper.read(configFileURL
					.getPath());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Before we start the server we translate those properties that are
		// related to the
		// path where the epnoi server is deployed in order to have complete
		// routes

		logger.info("The modelPath is made absolute: intial value: "
				+ parametersModel.getModelPath());

		System.out
				.println(">"
						+ EpnoiServer.class.getResource(parametersModel
								.getModelPath()));

		String completeModelPath = EpnoiServer.class.getResource(
				parametersModel.getModelPath()).getPath();

		parametersModel.setModelPath(completeModelPath);
		logger.info("The modelPath is made absolute: absolute value: "
				+ parametersModel.getModelPath());

		logger.info("The index Path is made absolute: intial value: "
				+ parametersModel.getIndexPath());

		String indexPath = EpnoiServer.class.getResource(
				parametersModel.getIndexPath()).getPath();

		parametersModel.setIndexPath(indexPath);
		logger.info("The indexPath is made absolute: absolute value: "
				+ parametersModel.getIndexPath());
		logger.info("The graph Path is made absolute: intial value: "
				+ parametersModel.getGraphPath());

		String graphPath = EpnoiServer.class.getResource(
				parametersModel.getGraphPath()).getPath();

		parametersModel.setGraphPath(graphPath);
		logger.info("The graph path is made absolute: absolute value: "
				+ parametersModel.getGraphPath());

		return parametersModel;
	}
}
