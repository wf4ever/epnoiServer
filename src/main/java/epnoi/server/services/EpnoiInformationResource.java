package epnoi.server.services;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import epnoi.core.EpnoiCore;

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
			this.epnoiCore.init("/model.xml");
			this.context.setAttribute(EPNOI_CORE_ATTRIBUTE, epnoiCore);

		}
	}

}
