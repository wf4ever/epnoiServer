package epnoi.server.services;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import epnoi.core.EpnoiCore;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.ParametersModelWrapper;
import epnoi.server.EpnoiServer;

@Path("/recommender/epnoiInformation")
public class EpnoiInformationResource {
	private String EPNOI_CORE_ATTRIBUTE = "EPNOI_CORE";
	@Context
	ServletContext context;

	private EpnoiCore epnoiCore = null;

	@GET
	@Produces("text/xml")
	public String getRecommendation() {
		this._initEpnoiCore();
		int numberOfRecommendations = epnoiCore.getRecommendationSpace()
				.getAllRecommendations().size();
		return "I have" + numberOfRecommendations + " recommendations \n";
	}

	private void _initEpnoiCore() {

		this.epnoiCore = (EpnoiCore) this.context
				.getAttribute(EPNOI_CORE_ATTRIBUTE);
		if (this.epnoiCore == null) {
			this.epnoiCore = new EpnoiCore();
			ParametersModel parametersModel = this._readParameters();
			this.epnoiCore.init(parametersModel);
			this.context.setAttribute(EPNOI_CORE_ATTRIBUTE, epnoiCore);

		}

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
