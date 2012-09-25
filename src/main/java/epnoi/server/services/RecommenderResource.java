package epnoi.server.services;

import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import epnoi.core.EpnoiCore;
import epnoi.server.services.responses.RecommenderService;

@Path("/recommender/")
@Produces(MediaType.APPLICATION_XML)
public class RecommenderResource {
	private final static Logger logger = Logger.getLogger(RecommenderResource.class
			.getName());
	

	@Context
	ServletContext context;

	private EpnoiCore epnoiCore = null;
	// -----------------------------------------------------------------
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public RecommenderService setRecommendationAsXML() {
		logger.info("The recommender resource has been requested in XML format");
		return new RecommenderService();
	}


}
