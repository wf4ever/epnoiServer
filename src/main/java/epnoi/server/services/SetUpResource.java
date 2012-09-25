package epnoi.server.services;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import epnoi.core.EpnoiCore;
import epnoi.model.Provenance;
import epnoi.model.Rating;
import epnoi.model.Recommendation;
import epnoi.model.User;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.ParametersModelReader;
import epnoi.server.EpnoiServer;

@Path("/recommender/setup")
public class SetUpResource {
	private final static Logger logger = Logger.getLogger(SetUpResource.class
			.getName());
	private String EPNOI_CORE_ATTRIBUTE = "EPNOI_CORE";
	@Context
	ServletContext context;

	private EpnoiCore epnoiCore = null;
	private ParametersModel parametersModel;
	// -----------------------------------------------------------------
	@GET
	@Produces("text/xml")
	public String getRecommendation() {
		this._initEpnoiCore();
		int numberOfRecommendations = epnoiCore.getRecommendationSpace()
				.getAllRecommendations().size();
		return "I have" + numberOfRecommendations + " recommendations \n";
	}
	// -----------------------------------------------------------------
	@Path("/wakeup")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getRecommendationAsText() {
		logger.info("Invoking the /wakeup operation");
		String response = "";
		long time = System.currentTimeMillis();

		try {
			this.parametersModel = this._readParameters();
			this._initEpnoiCore();
		} catch (Exception e) {
			return "Something went wrong in the recommender initialization :( /n"
					+ e.getMessage();
		}
		long afterTime = System.currentTimeMillis();
		String serverPath = "http://" + parametersModel.getHostname() + ":"
				+ parametersModel.getPort() + "/" + parametersModel.getPath();
		response += "The Recommender Service has been initialized!  \n";
		response += "It is available at: " + serverPath + " \n";
		response += "------------------------------------------------ \n";
		response += "------------------------------------------------ \n";
		response += "It took " + (Long) (afterTime - time) / 1000.0
				+ "to initialize the recommender system \n";

		response += "# of recommendations "
				+ epnoiCore.getRecommendationSpace().getAllRecommendations()
						.size() + "\n";
		response += "# of inferred recommendations "
				+ epnoiCore.getInferredRecommendationSpace()
						.getAllRecommendations().size() + "\n";

		response += "# of users> " + epnoiCore.getModel().getUsers().size()
				+ "\n";
		response += "# of workflows> "
				+ epnoiCore.getModel().getWorkflows().size() + "\n";
		response += "# of files> " + epnoiCore.getModel().getFiles().size()
				+ "\n";

		ArrayList<String> differentRaters = new ArrayList<String>();
		for (Rating rating : epnoiCore.getModel().getRatings()) {
			if (!differentRaters.contains(rating.getOwnerURI())) {
				differentRaters.add(rating.getOwnerURI());
			}
		}

		response += "# of users with at least one rating "
				+ differentRaters.size() + "\n";

		int numberOfFavouritedWorkflows = 0;
		ArrayList<String> differentUploaders = new ArrayList<String>();
		ArrayList<String> differentFavouriters = new ArrayList<String>();
		for (User user : epnoiCore.getModel().getUsers()) {
			if (user.getWorkflows().size() > 0) {
				differentUploaders.add(user.getURI());
			}
			if (user.getFavouritedWorkflows().size() > 0) {
				differentFavouriters.add(user.getURI());
				numberOfFavouritedWorkflows += user.getFavouritedWorkflows()
						.size();
			}

		}
		response += "# of users that have uploaded a workflow "
				+ differentUploaders.size() + "\n";
		response += "# of users with at least one favourite workflow "
				+ differentFavouriters.size() + "\n";
		response += "# of favourited workflows (they may be repeated) "
				+ numberOfFavouritedWorkflows + "\n";

		ArrayList<String> differentFavouritersAndRaters = new ArrayList<String>();
		for (String userURI : differentFavouriters) {

			if (differentRaters.contains(userURI)) {
				differentFavouritersAndRaters.add(userURI);
			}
		}
		response += "# of users with at least one favourite and rating "
				+ differentFavouritersAndRaters.size() + "\n";

		ArrayList<String> differentRecommendedUsers = new ArrayList<String>();
		ArrayList<Long> differentRatedWorkflow = new ArrayList<Long>();
		int contentBased = 0;
		int collaborativeBased = 0;
		int socialbased = 0;

		for (Recommendation recommendation : epnoiCore.getRecommendationSpace()
				.getAllRecommendations()) {
			if (!differentRecommendedUsers
					.contains(recommendation.getUserURI())) {
				differentRecommendedUsers.add(recommendation.getUserURI());
			}
			if (!differentRatedWorkflow.contains(recommendation.getItemID())) {
				differentRatedWorkflow.add(recommendation.getItemID());
			}

			if (recommendation.getProvenance()
					.getParameterByName(Provenance.TECHNIQUE)
					.equals(Provenance.TECHNIQUE_COLLABORATIVE)) {
				collaborativeBased++;
			} else {

				if (recommendation.getProvenance()
						.getParameterByName(Provenance.TECHNIQUE)
						.equals(Provenance.TECHNIQUE_SOCIAL)) {
					socialbased++;
				} else {
					contentBased++;
				}

			}
		}

		response += "# of users that have received a recommendation "
				+ differentRecommendedUsers.size() + "\n";
		response += "# of items that have been recommended "
				+ differentRatedWorkflow.size() + "\n";

		response += "# of recommendations by collaborative filtering algorithm "
				+ collaborativeBased + "\n";
		response += "# of recommendations by content based algorithm "
				+ contentBased + "\n";
		response += "# of recommendations by social network algorithm "
				+ socialbased + "\n";

		int numberOfFavouritedFiles = 0;
		ArrayList<String> differentFilesUploaders = new ArrayList<String>();
		ArrayList<String> differentFilesFavouriters = new ArrayList<String>();
		for (User user : epnoiCore.getModel().getUsers()) {
			if (user.getFiles().size() > 0) {
				differentFilesUploaders.add(user.getURI());
			}
			if (user.getFavouritedFiles().size() > 0) {
				differentFilesFavouriters.add(user.getURI());
				numberOfFavouritedFiles += user.getFavouritedFiles().size();
			}

		}
		response += "# of users that have uploaded a file "
				+ differentFilesUploaders.size() + "\n";
		response += "# of users with at least one favourite file "
				+ differentFilesFavouriters.size() + "\n";
		response += "# of favourited files  (they may be repeated) "
				+ numberOfFavouritedFiles + "\n";

		int ratingsForFiles = 0;
		int ratingsForWorkflows = 0;
		for (Rating rating : epnoiCore.getModel().getRatings()) {
			if (rating.getType().equals(Rating.FILE_RATING)) {
				ratingsForFiles++;
			}
			if (rating.getType().equals(Rating.WORKFLOW_RATING)) {
				ratingsForWorkflows++;
			}
		}

		response += "# of file ratings " + ratingsForFiles + "\n";
		response += "# of workflow ratings " + ratingsForWorkflows + "\n";

		int numberOfUsersWithTags = 0;
		float averageNumberOfTags = 0;
		int numberOfTags = 0;
		for (User user : epnoiCore.getModel().getUsers()) {
			if (user.getTagApplied().size() > 0) {

				numberOfUsersWithTags++;
				numberOfTags += user.getTagApplied().size();
			}
		}
		response += "# of tags " + numberOfTags + "\n";
		response += "# of users with at least one tag " + numberOfUsersWithTags
				+ "\n";
		response += "# of the average tag per user " + ((float) numberOfTags)
				/ ((float) epnoiCore.getModel().getUsers().size()) + "\n";
		response += "# of the average tag per user that has tags "
				+ ((float) numberOfTags) / ((float) numberOfUsersWithTags)
				+ "\n";

		response += "# of packs " + epnoiCore.getModel().getPacks().size()
				+ "\n";

		this.logger.info(response);
		return response;

	}

	private void _initEpnoiCore() {
		String response = "";
		this.epnoiCore = (EpnoiCore) this.context
				.getAttribute(EPNOI_CORE_ATTRIBUTE);
		if (this.epnoiCore == null) {

			this.epnoiCore = new EpnoiCore();
			ParametersModel parametersModel = this._readParameters();
			this.epnoiCore.init(parametersModel);
			this.context.setAttribute(EPNOI_CORE_ATTRIBUTE, epnoiCore);

		}
	}

	public static ParametersModel _readParameters() {
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

		logger.info("The modelPath is made absolute: intial value: "
				+ parametersModel.getModelPath());

		System.out
				.println(">"
						+ EpnoiServer.class.getResource(parametersModel
								.getModelPath()));

		String completeModelPath = EpnoiServer.class.getResource(
				parametersModel.getModelPath()).getPath();

		parametersModel.setModelPath(completeModelPath);
		logger.info("The modelPath is made absolute: absolute value: "
				+ parametersModel.getModelPath());

		logger.info("The index Path is made absolute: intial value: "
				+ parametersModel.getIndexPath());

		String indexPath = EpnoiServer.class.getResource(
				parametersModel.getIndexPath()).getPath();

		parametersModel.setIndexPath(indexPath);
		logger.info("The indexPath is made absolute: absolute value: "
				+ parametersModel.getIndexPath());
		logger.info("The graph Path is made absolute: intial value: "
				+ parametersModel.getGraphPath());

		String graphPath = EpnoiServer.class.getResource(
				parametersModel.getGraphPath()).getPath();

		parametersModel.setGraphPath(graphPath);
		logger.info("The graph path is made absolute: absolute value: "
				+ parametersModel.getModelPath());

		return parametersModel;
	}

}
