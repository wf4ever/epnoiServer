package epnoi.server.services;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import epnoi.model.Action;
import epnoi.model.ActionsContext;
import epnoi.model.ContextModel;
import epnoi.model.User;

@Path("/recommender/contexts/actions")
public class ActionsContextResource extends EpnoiService {

	public ActionsContextResource() {

		this.logger = Logger.getLogger(RecommendationContextResource.class
				.getName());

	}

	// ----------------------------------------------------------------------------------------

	@Path("actionsContext")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response updateRecommendationContext(
			@QueryParam("user") String userURI,
			@QueryParam("actionType") String actionType,
			@QueryParam("item") String item,
			@QueryParam("timestamp") String timestamp
			)
			 {
		logger.info("Handling the request POST with the following parameters: user> "
				+ userURI + " action> " + actionType + " item> " + item+ " timestamp> " + timestamp);
		this._initEpnoiCore();
		if (userURI != null) {
			User user = this.epnoiCore.getModel().getUserByURI(userURI);
			if (user != null) {

				Action action = new Action();
				action.setItemURI(item);
				action.setName(actionType);
				action.setTimestamp(timestamp);
				this.epnoiCore.getContextModel().addAction(user.getURI(), action);
				return Response.ok().build();

			}
		}
		return Response.noContent().build();
	}

	// ----------------------------------------------------------------------------------------

	@Path("actionsContext")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public ActionsContext getActionsContext(
			@QueryParam("user") String userURI){

		logger.info("Handling the request of recommendations with the following parameters: user> "
				+ userURI);

		// this._initEpnoiCore();
		// logger.info("Handling the request of recommendations with the following parameters: id> "
		// + id + " max> " + maxNumberOfRecommedations);
		this._initEpnoiCore();

		User user = this.epnoiCore.getModel().getUserByURI(userURI);
		if (user != null) {
			ContextModel contextModel = this.epnoiCore.getContextModel();
			ActionsContext userContext = contextModel.getActionsContext(userURI);
			if (userContext != null) {
				return userContext;
			}
		}

		throw new RuntimeException("GET: user with URI " + userURI
				+ " not found");
	}
}
