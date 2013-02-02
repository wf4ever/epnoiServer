package epnoi.server.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import epnoi.model.File;
import epnoi.model.Pack;
import epnoi.model.Provenance;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.model.User;
import epnoi.model.Workflow;
import epnoi.recommeders.OnTheFlyRecommender;
import epnoi.server.services.responses.RecommendationsSet;

@Path("/recommender/recommendations/aggregationRecommendationsSet")
public class AggregationRecommendationsSetResource extends EpnoiService {

	// -----------------------------------------------------------------

	public AggregationRecommendationsSetResource() {
		this.logger = Logger.getLogger(RecommendationsSetResource.class
				.getName());
	}

	// -----------------------------------------------------------------

	@Path("")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public RecommendationsSet getRecommendationAsXMLFiltered(
			@DefaultValue("none") @QueryParam("user") String userURI,
			@DefaultValue("none") @QueryParam("pack") String packURI,
			@DefaultValue("any") @QueryParam("type") String type,
			@DefaultValue("0") @QueryParam("max") Integer maxNumberOfRecommedations) {

		logger.info("Handling the request of  recommendations for an aggregation whith the following parameters: user> "
				+ userURI
				+ " pack>"
				+ packURI
				+ " max> "
				+ maxNumberOfRecommedations + " type> " + type);

		_initEpnoiCore();

		HashMap<String, Object> parameters = new HashMap<String, Object>();

		parameters.put(OnTheFlyRecommender.USER_URI_PARAMETER, userURI);
		parameters.put(OnTheFlyRecommender.PACK_URI_PARAMETER, packURI);

		if (!userURI.equals("none") && !packURI.equals("none")) {
			RecommendationSpace recommendationSpace = this.epnoiCore
					.getOnTheFlyRecommendationSpace(parameters);
			
			ArrayList<Recommendation> recommendationsForUser = null;

			if (type.equals("workflows")) {
				recommendationsForUser = recommendationSpace
						.getRecommendationsForUserURI(packURI,
								Provenance.ITEM_TYPE_WORKFLOW);
			} else if (type.equals("users")) {
				recommendationsForUser = recommendationSpace
						.getRecommendationsForUserURI(packURI,
								Provenance.ITEM_TYPE_USER);
			} else if (type.equals("files")) {
				recommendationsForUser = recommendationSpace
						.getRecommendationsForUserURI(packURI,
								Provenance.ITEM_TYPE_FILE);
			}

			else if (type.equals("packs")) {
				recommendationsForUser = recommendationSpace
						.getRecommendationsForUserURI(packURI,
								Provenance.ITEM_TYPE_PACK);
			} else if (type.equals("any")) {
				recommendationsForUser = recommendationSpace
						.getRecommendationsForUserURI(packURI);
			}

		
			if (recommendationsForUser != null) {
				if (maxNumberOfRecommedations != 0) {
					ArrayList<Recommendation> filteredRecommendationsForUser = new ArrayList<Recommendation>();
					Iterator<Recommendation> recommendationsIt = _orderByStrength(
							recommendationsForUser).iterator();
					int i = 0;
					while (recommendationsIt.hasNext()
							&& i < maxNumberOfRecommedations) {

						filteredRecommendationsForUser.add(i,
								recommendationsIt.next());
						i++;
					}
					RecommendationsSet recommendationSet = new RecommendationsSet();
					recommendationSet
							.setRecommendation(_convertToResponse(filteredRecommendationsForUser));
					return recommendationSet;

				}
				RecommendationsSet recommendationSet = new RecommendationsSet();
				recommendationSet
						.setRecommendation(_convertToResponse(_orderByStrength(recommendationsForUser)));
				return recommendationSet;
			}
		}

		return new RecommendationsSet();
	}

	// -----------------------------------------------------------------

	ArrayList<epnoi.server.services.responses.Recommendation> _convertToResponse(
			ArrayList<Recommendation> recommendations) {
		ArrayList<epnoi.server.services.responses.Recommendation> recommendationResponses = new ArrayList<epnoi.server.services.responses.Recommendation>();
		for (Recommendation recommendation : recommendations) {
			epnoi.server.services.responses.Recommendation recommendationResponse = new epnoi.server.services.responses.Recommendation();
			recommendationResponse.setStrength(recommendation.getStrength());
			Provenance recommendationProvenance = recommendation
					.getProvenance();
			if (recommendationProvenance.getParameterByName(
					Provenance.ITEM_TYPE).equals(Provenance.ITEM_TYPE_WORKFLOW)) {
				Workflow workflow = epnoiCore.getModel().getWorkflowByURI(
						recommendation.getItemURI());
				recommendationResponse.setTitle(workflow.getTitle());
				recommendationResponse.setResource(workflow.getResource());

				recommendationResponse.setExplanation(recommendation
						.getExplanation().getExplanation());

			} else if (recommendationProvenance.getParameterByName(
					Provenance.ITEM_TYPE).equals(Provenance.ITEM_TYPE_FILE)) {
				File file = epnoiCore.getModel().getFileByURI(
						recommendation.getItemURI());
				recommendationResponse.setTitle(file.getTitle());
				recommendationResponse.setResource(file.getResource());

				recommendationResponse.setExplanation(recommendation
						.getExplanation().getExplanation());
			} else if (recommendationProvenance.getParameterByName(
					Provenance.ITEM_TYPE).equals(Provenance.ITEM_TYPE_PACK)) {
				Pack pack = epnoiCore.getModel().getPackByURI(
						recommendation.getItemURI());
				recommendationResponse.setTitle(pack.getTitle());
				recommendationResponse.setResource(pack.getResource());

				recommendationResponse.setExplanation(recommendation
						.getExplanation().getExplanation());
			} else if (recommendationProvenance.getParameterByName(
					Provenance.ITEM_TYPE).equals(Provenance.ITEM_TYPE_USER)) {
				User user = epnoiCore.getModel().getUserByURI(
						recommendation.getItemURI());
				recommendationResponse.setTitle(user.getName());
				recommendationResponse.setResource(user.getResource());

				recommendationResponse.setExplanation(recommendation
						.getExplanation().getExplanation());
			}

			recommendationResponse.setStrength(recommendation.getStrength());
			recommendationResponse.setUsedTechnique(recommendation
					.getProvenance().getParameterByName(Provenance.TECHNIQUE));

			recommendationResponse.setItemType(recommendation.getProvenance()
					.getParameterByName(Provenance.ITEM_TYPE));
			recommendationResponses.add(recommendationResponse);

		}
		return recommendationResponses;
	}

	// -----------------------------------------------------------------

	private ArrayList<Recommendation> _orderByStrength(
			ArrayList<Recommendation> recommendations) {
		ArrayList<Recommendation> orderedRecommendations = (ArrayList<Recommendation>) recommendations
				.clone();

		Collections.sort(orderedRecommendations);
		Collections.reverse(orderedRecommendations);
		return orderedRecommendations;

	}

}
