package epnoi.server.services;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import epnoi.core.EpnoiCore;
import epnoi.model.File;
import epnoi.model.Pack;
import epnoi.model.Provenance;
import epnoi.model.Recommendation;
import epnoi.model.User;
import epnoi.model.Workflow;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.ParametersModelWrapper;
import epnoi.server.EpnoiServer;

@Path("/recommendations/")
@Produces(MediaType.APPLICATION_XML)
public class RecommenderResourceOld {
	private final static Logger logger = Logger.getLogger(RecommenderResourceOld.class
			.getName());
	private String EPNOI_CORE_ATTRIBUTE = "EPNOI_CORE";
	private String XML_FORMAT = "xml";
	private String TEXTUAL_FORMAT = "txt";
	private ParametersModel parametersModel;

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
			parametersModel = this._readParameters();
			this.epnoiCore.init(parametersModel);
			this.context.setAttribute(EPNOI_CORE_ATTRIBUTE, epnoiCore);
			long afterTime = System.currentTimeMillis();
			System.out.println("It took " + (Long) (afterTime - time) / 1000.0
					+ "to load the model");
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
		// path where the epnoi server is deployed in order to have complete
		// routes
		
		logger.info("The modelPath is made absolute: intial value: "+parametersModel.getModelPath());
		


		System.out.println(">"+EpnoiServer.class.getResource(parametersModel
				.getModelPath()));

		String completeModelPath = EpnoiServer.class.getResource(
				parametersModel.getModelPath()).getPath();
	
		

		parametersModel.setModelPath(completeModelPath);
		logger.info("The modelPath is made absolute: absolute value: "+parametersModel.getModelPath());

		logger.info("The index Path is made absolute: intial value: "+parametersModel.getIndexPath());

		String indexPath = EpnoiServer.class.getResource(
				parametersModel.getIndexPath()).getPath();

		parametersModel.setIndexPath(indexPath);
		logger.info("The indexPath is made absolute: absolute value: "+parametersModel.getIndexPath());
		logger.info("The graph Path is made absolute: intial value: "+parametersModel.getGraphPath());

		String graphPath = EpnoiServer.class.getResource(
				parametersModel.getGraphPath()).getPath();
		
		
		
		parametersModel.setGraphPath(graphPath);
		logger.info("The graph path is made absolute: absolute value: "+parametersModel.getModelPath());

		

		return parametersModel;
	}
	
	
	
	
	

	/**
	 * 
	 * @param id
	 * @param maxNumberOfRecommedations
	 * @return
	 */

	@Path("/user")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getRecommendationAsText(
			@DefaultValue("0") @QueryParam("id") Long id,
			@DefaultValue("none") @QueryParam("name") String name,
			@DefaultValue("0") @QueryParam("max") Integer maxNumberOfRecommedations) {
		System.out.println("TEXT............name>" + name + "id> " + id
				+ " max> " + maxNumberOfRecommedations);
		_initEpnoiCore();

		ArrayList<Recommendation> recommendationsForUser = null;
		if (id != 0) {
			recommendationsForUser = this.epnoiCore.getRecommendationSpace()
					.getRecommendationsForUserID(id);
		} else {

			if (!name.equals("none")) {
				// User identified by human readeable name
				recommendationsForUser = this.epnoiCore
						.getRecommendationSpace()
						.getRecommendationsForUserName(name);

			} else {
				// Caso que no hay manera de identificar al usuario
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

			for (Recommendation recommendation : _orderByStrength(recommendationsForUser)) {
				if (this.epnoiCore.getModel().isWorkflow(
						recommendation.getItemURI())) {
					Workflow workflow = epnoiCore.getModel().getWorkflowByID(
							recommendation.getItemID());

					result += "I recommend the workflow entitled "
							+ workflow.getTitle()
							+ " located at "
							+ workflow.getURI()
							+ "\n"
							+ " with an strength of "
							+ recommendation.getStrength()
							+ "\n"
							+ "----------------------------------------------- \n";
				}

			}
		} else {
			// We have received the maximum number of recommendations that we
			// should provide
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

	/**
	 * This f
	 * 
	 * @param id
	 * @param name
	 * @param maxNumberOfRecommedations
	 * @return
	 */

	@Path("/user/{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public ArrayList<epnoi.server.services.responses.Recommendation> getRecommendationAsXML(
			@DefaultValue("0") @PathParam("id") Long id,
			@DefaultValue("0") @QueryParam("max") Integer maxNumberOfRecommedations) {
		System.out.println("XML............id> " + id + " max> "
				+ maxNumberOfRecommedations);
		logger.info("Handling the request of recommendations whith the following parameters: id> "+id+" max> "+maxNumberOfRecommedations);
		_initEpnoiCore();
		ArrayList<Recommendation> recommendationsForUser = null;

		if (id != 0) {

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
				return _convertToResponse(filteredRecommendationsForUser);
			}

			return _convertToResponse(_orderByStrength(recommendationsForUser));

		}

		return new ArrayList<epnoi.server.services.responses.Recommendation>();
	}

	// -----------------------------------------------------------------

	@Path("/user/{id}/{type}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	
	public ArrayList<epnoi.server.services.responses.Recommendation> getRecommendationAsXMLFiltered(
			@DefaultValue("0") @PathParam("id") Long id,
			@DefaultValue("any") @PathParam("type") String type,
			@DefaultValue("0") @QueryParam("max") Integer maxNumberOfRecommedations) {
		System.out.println("XML............id> " + id + " max> "
				+ maxNumberOfRecommedations);
		logger.info("Handling the filtered request of recommendations whith the following parameters: id> "+id+" max> "+maxNumberOfRecommedations+ " type> "+type);

		_initEpnoiCore();
		ArrayList<Recommendation> recommendationsForUser = null;

		if (id != 0) {
			
			if (type.equals("workflow")){
				recommendationsForUser = this.epnoiCore
						.getRecommendationSpace().getRecommendationsForUserID(
								id, Provenance.ITEM_TYPE_WORKFLOW);
			}else if (type.equals("user")){
				recommendationsForUser = this.epnoiCore
						.getRecommendationSpace().getRecommendationsForUserID(
								id, Provenance.ITEM_TYPE_USER);
			} 
			else if (type.equals("file")){
				recommendationsForUser = this.epnoiCore
						.getRecommendationSpace().getRecommendationsForUserID(
								id, Provenance.ITEM_TYPE_FILE);
			}
			
			else if (type.equals("pack")){
				recommendationsForUser = this.epnoiCore
						.getInferredRecommendationSpace().getRecommendationsForUserID(
								id, Provenance.ITEM_TYPE_PACK);
			}
			else if (type.equals("any")){
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
				return _convertToResponse(filteredRecommendationsForUser);
			}

			return _convertToResponse(_orderByStrength(recommendationsForUser));

		}

		return new ArrayList<epnoi.server.services.responses.Recommendation>();
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
				recommendationResponse
						.setStrength(recommendation.getStrength());
				recommendationResponse.setExplanation(recommendation
						.getExplanation().getExplanation());
			} else if (recommendationProvenance.getParameterByName(
					Provenance.ITEM_TYPE).equals(Provenance.ITEM_TYPE_FILE)) {
				File file = epnoiCore.getModel().getFileByURI(
						recommendation.getItemURI());
				recommendationResponse.setTitle(file.getTitle());
				recommendationResponse.setResource(file.getResource());
				recommendationResponse
						.setStrength(recommendation.getStrength());
				recommendationResponse.setExplanation(recommendation
						.getExplanation().getExplanation());
			} else if (recommendationProvenance.getParameterByName(
					Provenance.ITEM_TYPE).equals(Provenance.ITEM_TYPE_PACK)) {
				Pack pack = epnoiCore.getModel().getPackByURI(
						recommendation.getItemURI());
				recommendationResponse.setTitle(pack.getTitle());
				recommendationResponse.setResource(pack.getResource());
				recommendationResponse
						.setStrength(recommendation.getStrength());
				recommendationResponse.setExplanation(recommendation
						.getExplanation().getExplanation());
			} else if (recommendationProvenance.getParameterByName(
					Provenance.ITEM_TYPE).equals(Provenance.ITEM_TYPE_USER)) {
				User user = epnoiCore.getModel().getUserByURI(
						recommendation.getItemURI());
				recommendationResponse.setTitle(user.getName());
				recommendationResponse.setResource(user.getResource());
				recommendationResponse
						.setStrength(recommendation.getStrength());
				recommendationResponse.setExplanation(recommendation
						.getExplanation().getExplanation());
			}
			recommendationResponses.add(recommendationResponse);

		}
		return recommendationResponses;
	}

	@Path("/recommendations/user")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public String setRecommendationAsXML() {
		System.out.println("ENXTRA!!!");
		return "whatever";
	}

	private ArrayList<Recommendation> _orderByStrength(
			ArrayList<Recommendation> recommendations) {
		ArrayList<Recommendation> orderedRecommendations = (ArrayList<Recommendation>) recommendations
				.clone();

		Collections.sort(orderedRecommendations);
		Collections.reverse(orderedRecommendations);
		return orderedRecommendations;

	}

}
