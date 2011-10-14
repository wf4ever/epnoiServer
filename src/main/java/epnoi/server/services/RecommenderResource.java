package epnoi.server.services;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import epnoi.core.EpnoiCore;
import epnoi.model.Recommendation;
import epnoi.model.Workflow;

@Path("/recommender/")
@Produces(MediaType.APPLICATION_XML)
public class RecommenderResource {
	private String EPNOI_CORE_ATTRIBUTE = "EPNOI_CORE";
	private String XML_FORMAT = "xml";
	private String TEXTUAL_FORMAT = "txt";

	@Context
	ServletContext context;

	private EpnoiCore epnoiCore = null;

	private void _initEpnoiCore() {
		this.epnoiCore = (EpnoiCore) this.context
				.getAttribute(EPNOI_CORE_ATTRIBUTE);
		if (this.epnoiCore == null) {
			System.out.println("Loading the model!");
			long time = System.currentTimeMillis();
			this.epnoiCore = new EpnoiCore();
			this.epnoiCore.init("/model.xml");
			this.context.setAttribute(EPNOI_CORE_ATTRIBUTE, epnoiCore);
			long afterTime = System.currentTimeMillis();
			System.out.println("It took " + (Long)(afterTime - time) / 1000.0
					+ "to load the model");
		}

	}

	@Path("/recommendations/user")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getRecommendationAsText(
			@DefaultValue("0") @QueryParam("id") Long id,
			@DefaultValue("none") @QueryParam("name") String name,
			@DefaultValue("0") @QueryParam("max_number") Integer maxNumberOfRecommedations) {
		_initEpnoiCore();
		System.out.println("............name>" + name + "id> " + id);
		ArrayList<Recommendation> recommendationsForUser = null;
		if (id != 0) {
			recommendationsForUser = this.epnoiCore.getRecommendationSpace()
					.getRecommendationsForUserID(id);
		} else {

			if (!name.equals("none")) {
				recommendationsForUser = this.epnoiCore
						.getRecommendationSpace()
						.getRecommendationsForUserName(name);

			}
		}

		if (recommendationsForUser.isEmpty()) {
			if (id != 0)
				return "Sorry, no recommendations for user " + id + "\n";
			else
				return "Sorry, no recommendations for user " + name + "\n";
		}
		String result = "";
		if (maxNumberOfRecommedations == 0) {

			for (Recommendation recommendation : recommendationsForUser) {
				Workflow workflow = epnoiCore.getModel().getWorkflowByID(
						recommendation.getItemID());
				result += "I recommend the item entitled "
						+ workflow.getTitle() + " located at "
						+ workflow.getURI() + "\n" + " with an strength of "
						+ recommendation.getStrength() + "\n"
						+ "----------------------------------------------- \n";

			}
		} else {

			Iterator<Recommendation> recommendationsIt = recommendationsForUser
					.iterator();
			int index = maxNumberOfRecommedations;
			while (recommendationsIt.hasNext() && index > 0) {
				Recommendation recommendation = recommendationsIt.next();
				result += "I recommend the item " + recommendation.getItemID()
						+ "with an strength of " + recommendation.getStrength()
						+ "\n";
				index--;
			}
		}
		return result;
	}

	@Path("/recommendations/user")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public ArrayList<Recommendation> getRecommendationAsXML(
			@QueryParam("id") Long id,
			@QueryParam("max_number") Integer maxNumberOfRecommedations) {

		_initEpnoiCore();
		/*
		 * System.out.println(">>ID>> "+ id); for (Recommendation
		 * recommendation:
		 * epnoiCore.getRecommendationSpace().getRecommendationsForUserID(id)){
		 * System.out.println("------------------------------------------");
		 * System.out.println("         (i)" + recommendation.getItemID());
		 * System.out.println("         (s)" + recommendation.getStrength()); }
		 */

		ArrayList<Recommendation> recommendationsForUser = this.epnoiCore
				.getRecommendationSpace().getRecommendationsForUserID(id);

		if (recommendationsForUser != null)
			return recommendationsForUser;
		return new ArrayList<Recommendation>();
	}

	@Path("/recommendations/user")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public String setRecommendationAsXML() {
		System.out.println("ENXTRA!!!");
		return "whatever";
	}
}
