package epnoi.server.services;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import epnoi.core.EpnoiCore;
import epnoi.model.File;
import epnoi.model.Pack;
import epnoi.model.Provenance;
import epnoi.model.Recommendation;
import epnoi.model.User;
import epnoi.model.Workflow;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.ParametersModelReader;
import epnoi.server.EpnoiServer;
import epnoi.server.services.responses.RecommendationsSet;

@Path("/recommender/recommendations/recommendationsSet/")
@Produces(MediaType.APPLICATION_XML)
public class RecommendationsSetResource extends EpnoiService {
	private final static Logger logger = Logger
			.getLogger(RecommendationsSetResource.class.getName());

	@Context
	ServletContext context;

	@Path("/user/{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public RecommendationsSet getRecommendationAsXML(
			@DefaultValue("0") @PathParam("id") Long id,
			@DefaultValue("0") @QueryParam("max") Integer maxNumberOfRecommedations) {
		System.out.println("XML............id> " + id + " max> "
				+ maxNumberOfRecommedations);
		logger.info("Handling the request of recommendations whith the following parameters: id> "
				+ id + " max> " + maxNumberOfRecommedations);
		_initEpnoiCore();
		ArrayList<Recommendation> recommendationsForUser = null;

		if (id != 0) {
			System.out.println("-->" + this.epnoiCore);

			System.out.println("-->" + this.epnoiCore.getRecommendationSpace());
			recommendationsForUser = this.epnoiCore.getRecommendationSpace()
					.getRecommendationsForUserID(id);
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

		return new RecommendationsSet();
	}

	// -----------------------------------------------------------------

	@Path("/user/{id}/{type}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public RecommendationsSet getRecommendationAsXMLFiltered(
			@DefaultValue("0") @PathParam("id") Long id,
			@DefaultValue("any") @PathParam("type") String type,
			@DefaultValue("0") @QueryParam("max") Integer maxNumberOfRecommedations) {
		System.out.println("XML............id> " + id + " max> "
				+ maxNumberOfRecommedations);
		logger.info("Handling the filtered request of recommendations whith the following parameters: id> "
				+ id + " max> " + maxNumberOfRecommedations + " type> " + type);

		_initEpnoiCore();
		ArrayList<Recommendation> recommendationsForUser = null;

		if (id != 0) {

			if (type.equals("workflows")) {
				recommendationsForUser = this.epnoiCore
						.getRecommendationSpace().getRecommendationsForUserID(
								id, Provenance.ITEM_TYPE_WORKFLOW);
			} else if (type.equals("users")) {
				recommendationsForUser = this.epnoiCore
						.getRecommendationSpace().getRecommendationsForUserID(
								id, Provenance.ITEM_TYPE_USER);
			} else if (type.equals("files")) {
				recommendationsForUser = this.epnoiCore
						.getRecommendationSpace().getRecommendationsForUserID(
								id, Provenance.ITEM_TYPE_FILE);
			}

			else if (type.equals("packs")) {
				recommendationsForUser = this.epnoiCore
						.getInferredRecommendationSpace()
						.getRecommendationsForUserID(id,
								Provenance.ITEM_TYPE_PACK);
			} else if (type.equals("any")) {
				recommendationsForUser = this.epnoiCore
						.getRecommendationSpace().getRecommendationsForUserID(
								id);
			}
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

		return new RecommendationsSet();
	}

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

			} else if (recommendationProvenance.getParameterByName(
					Provenance.ITEM_TYPE).equals(Provenance.ITEM_TYPE_EXTERNAL_RESOURCE)) {
				//recommendationResponse.setTitle(user.getName());
				//recommendationResponse.setResource(user.getResource());

				recommendationResponse.setExplanation(recommendation
						.getExplanation().getExplanation());
				
				recommendationResponse.setResource(recommendation.getItemURI());
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

	private ArrayList<Recommendation> _orderByStrength(
			ArrayList<Recommendation> recommendations) {
		ArrayList<Recommendation> orderedRecommendations = (ArrayList<Recommendation>) recommendations
				.clone();

		Collections.sort(orderedRecommendations);
		Collections.reverse(orderedRecommendations);
		return orderedRecommendations;

	}
	
	@Path("/user/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RecommendationsSet getRecommendationAsJSON(){
		return new RecommendationsSet();
	}

}
