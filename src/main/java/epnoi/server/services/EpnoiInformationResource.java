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
		return "I have" + numberOfRecommendations +" recommendations \n";
	}

	
	
	private void _initEpnoiCore() {
	
		this.epnoiCore = (EpnoiCore) this.context
				.getAttribute(EPNOI_CORE_ATTRIBUTE);
		if (this.epnoiCore == null) {
			this.epnoiCore = new EpnoiCore();
			Properties initializationProperties = _readProperties();
			this.epnoiCore.init(initializationProperties);
			this.context.setAttribute(EPNOI_CORE_ATTRIBUTE, epnoiCore);

		}
	
		
	}
	
	private Properties _readProperties() {
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
