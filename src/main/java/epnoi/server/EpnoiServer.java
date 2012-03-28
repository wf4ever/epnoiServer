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

import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.ParametersModelWrapper;
import epnoi.server.services.RecommenderResource;

/*
 import com.sun.grizzly.http.SelectorThread;
 import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
 */

public class EpnoiServer {
	/*
	 * public static final String HOSTNAME_PROPERTY = "server.hostname"; public
	 * static final String PORT_PROPERTY = "server.port"; public static final
	 * String PATH_PROPERTY = "server.path"; public static final String
	 * MODEL_PATH_PROPERTY = "model.path"; public static final String
	 * INDEX_PATH_PROPERTY = "index.path";
	 */
	public static void main(String[] args) throws IOException {

		final String baseUri = "http://localhost:9998/";
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages",
				"epnoi.server.services");

		ParametersModel parametersModel = null;

		parametersModel = EpnoiServer._readParameters();

		String serverPath = "http://" + parametersModel.getHostname() + ":"
				+ parametersModel.getPort() + "/" + parametersModel.getPath();

		System.out.println("Starting grizzly...");
		SelectorThread threadSelector = GrizzlyWebContainerFactory.create(
				serverPath, initParams);
		
		System.out.println(
				"Epnoi server started with WADL available at " + serverPath
						+ "application.wadl\n");
						
		System.in.read();
		threadSelector.stopEndpoint();
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
		// path where the erponoi server is deployed in order to have complete
		// routes
		System.out.println("modelPath after--------->"
				+ parametersModel.getModelPath());

		System.out.println(EpnoiServer.class.getResource(parametersModel
				.getModelPath()));

		String completeModelPath = EpnoiServer.class.getResource(
				parametersModel.getModelPath()).getPath();
		System.out.println("modelPath before--------->" + completeModelPath);
		parametersModel.setModelPath(completeModelPath);

		String indexPath = EpnoiServer.class.getResource(
				parametersModel.getIndexPath()).getPath();

		parametersModel.setIndexPath(indexPath);
		System.out.println(".... properties ...");

		return parametersModel;
	}
}
