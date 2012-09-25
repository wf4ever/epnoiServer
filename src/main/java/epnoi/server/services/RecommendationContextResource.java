package epnoi.server.services;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import epnoi.model.ContextModel;
import epnoi.model.RecommendationContext;
import epnoi.model.User;

///recommender/contexts/recommendationContext
@Path("/recommender/contexts")

public class RecommendationContextResource extends EpnoiService {

	public RecommendationContextResource() {

		this.logger = Logger.getLogger(RecommendationContextResource.class
				.getName());

	}

	// ----------------------------------------------------------------------------------------

	@Path("recommendationContext")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response createRecommendationContext(
			@QueryParam("user") String userURI,
			@QueryParam("keyword") List<String> keyword,
			@QueryParam("resource") List<String> resource) {
		logger.info("Handling the request PUT with the following parameters: user> "
				+ userURI + " keyword> " + keyword + " resource> " + resource);
		this._initEpnoiCore();
		if (userURI != null) {
			User user = this.epnoiCore.getModel().getUserByURI(userURI);
			if (user != null) {

				RecommendationContext userContext = new RecommendationContext();
				userContext.setUserURI(userURI);
				userContext.setKeyword(keyword);
				userContext.setResource(resource);

				this.epnoiCore.getContextModel().addContext(userURI,
						userContext);
				return Response.ok().build();

			}
		}
		return Response.noContent().build();
	}

	// ----------------------------------------------------------------------------------------

	@Path("recommendationContext")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public RecommendationContext getRecommendationContext(
			@QueryParam("user") String userURI,
			@QueryParam("keyword") List<String> keyword,
			@QueryParam("resource") List<String> resource) {

		logger.info("Handling the request of recommendations with the following parameters: user> "
				+ userURI);

		// this._initEpnoiCore();
		// logger.info("Handling the request of recommendations with the following parameters: id> "
		// + id + " max> " + maxNumberOfRecommedations);
		this._initEpnoiCore();

		User user = this.epnoiCore.getModel().getUserByURI(userURI);
		if (user != null) {
			ContextModel contextModel = this.epnoiCore.getContextModel();
			RecommendationContext userContext = contextModel
					.getUserContext(userURI);
			if (userContext != null) {
				return userContext;
			}
		}

		throw new RuntimeException("GET: user with URI " + userURI
				+ " not found");
	}

}
