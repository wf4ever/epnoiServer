package epnoi.server.services;

import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import epnoi.core.EpnoiCore;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.ParametersModelReader;
import epnoi.server.EpnoiServer;

public abstract class EpnoiService {

	protected Logger logger = null;

	private String EPNOI_CORE_ATTRIBUTE = "EPNOI_CORE";

	protected ParametersModel parametersModel;
	@Context
	protected ServletContext context;
	@Context
	protected UriInfo uriInfo;

	protected EpnoiCore epnoiCore = null;

	// ----------------------------------------------------------------------------------------

	protected void _initEpnoiCore() {
		this.epnoiCore = (EpnoiCore) this.context
				.getAttribute(EPNOI_CORE_ATTRIBUTE);
		if (this.epnoiCore == null) {
			System.out.println("Loading the model!");
			long time = System.currentTimeMillis();
			this.epnoiCore = new EpnoiCore();
			parametersModel = this._readParameters();
			this.epnoiCore.init(parametersModel);
			this.context.setAttribute(EPNOI_CORE_ATTRIBUTE, epnoiCore);
			long afterTime = System.currentTimeMillis();
			System.out.println("It took " + (Long) (afterTime - time) / 1000.0
					+ "to load the model");
		}

	}

	// ----------------------------------------------------------------------------------------

	public ParametersModel _readParameters() {
		ParametersModel parametersModel = null;

		try {
			URL configFileURL = EpnoiServer.class.getResource("epnoi.xml");
			parametersModel = ParametersModelReader.read(configFileURL
					.getPath());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Before we start the server we translate those properties that are
		// related to the
		// path where the epnoi server is deployed in order to have complete
		// routes
		parametersModel.resolveToAbsolutePaths(EpnoiServer.class);

		return parametersModel;
	}

}
