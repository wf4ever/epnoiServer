package epnoi.server.services;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

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
import epnoi.model.Recommendation;
import epnoi.model.Tagging;
import epnoi.model.Workflow;
import epnoi.server.EpnoiServer;

@Path("/recommendations/")
@Produces(MediaType.APPLICATION_XML)
public class RecommenderResource {
	private String EPNOI_CORE_ATTRIBUTE = "EPNOI_CORE";
	private String XML_FORMAT = "xml";
	private String TEXTUAL_FORMAT = "txt";
	private Properties initializationProperties = null;

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
Properties initializationProperties = this._readProperties();
			this.epnoiCore.init(initializationProperties);
			this.context.setAttribute(EPNOI_CORE_ATTRIBUTE, epnoiCore);
			long afterTime = System.currentTimeMillis();
			System.out.println("It took " + (Long) (afterTime - time) / 1000.0
					+ "to load the model");
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

	/**
	 * 
	 * @param id
	 * @param maxNumberOfRecommedations
	 * @return
	 */

	@Path("/recommendation/user")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getRecommendationAsText(
			@DefaultValue("0") @QueryParam("id") Long id,
			@DefaultValue("none") @QueryParam("name") String name,
			@DefaultValue("0") @QueryParam("max_number") Integer maxNumberOfRecommedations) {
		System.out.println("TEXT............name>" + name + "id> " + id
				+ " max_number> " + maxNumberOfRecommedations);
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

			for (Recommendation recommendation : recommendationsForUser) {
				Workflow workflow = epnoiCore.getModel().getWorkflowByID(
						recommendation.getItemID());

				System.out.println(">>> " + workflow);
				System.out.println("int>>>" + recommendation.getItemID());
				System.out.println("technique>>>"
						+ recommendation.getProvenance().getParameters());
				result += "I recommend the item entitled "
						+ workflow.getTitle() + " located at "
						+ workflow.getURI() + "\n" + " with an strength of "
						+ recommendation.getStrength() + "\n"
						+ "----------------------------------------------- \n";

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

	/*
	 * @Path("/recommendation/user")
	 * 
	 * @GET
	 * 
	 * @Produces(MediaType.TEXT_PLAIN) public String getRecommendationAsText(
	 * 
	 * @DefaultValue("0") @QueryParam("id") Long id,
	 * 
	 * @DefaultValue("none") @QueryParam("name") String name,
	 * 
	 * @DefaultValue("0") @QueryParam("max_number") Integer
	 * maxNumberOfRecommedations) { System.out.println("TEXT............name>" +
	 * name + "id> " + id+ " max_number> " + maxNumberOfRecommedations);
	 * _initEpnoiCore();
	 * 
	 * ArrayList<Recommendation> recommendationsForUser = null; if (id != 0) {
	 * recommendationsForUser = this.epnoiCore.getRecommendationSpace()
	 * .getRecommendationsForUserID(id); } else {
	 * 
	 * if (!name.equals("none")) { // User identified by human readeable name
	 * recommendationsForUser = this.epnoiCore .getRecommendationSpace()
	 * .getRecommendationsForUserName(name);
	 * 
	 * } else { // Caso que no hay manera de identificar al usuario } }
	 * 
	 * if (recommendationsForUser.isEmpty()) { if (id != 0) return
	 * "Sorry, no recommendations for user " + id + "\n"; else return
	 * "Sorry, no recommendations for user " + name + "\n"; }
	 * 
	 * String result = ""; if (maxNumberOfRecommedations == 0) {
	 * 
	 * for (Recommendation recommendation : recommendationsForUser) { Workflow
	 * workflow = epnoiCore.getModel().getWorkflowByID(
	 * recommendation.getItemID());
	 * 
	 * System.out.println(">>> "+workflow); System.out.println("int>>>"+
	 * recommendation.getItemID()); System.out.println("technique>>>"+
	 * recommendation.getProvenance().getParameters()); result +=
	 * "I recommend the item entitled " + workflow.getTitle() + " located at " +
	 * workflow.getURI() + "\n" + " with an strength of " +
	 * recommendation.getStrength() + "\n" +
	 * "----------------------------------------------- \n";
	 * 
	 * } } else { //We have received the maximum number of recommendations that
	 * we should provide Iterator<Recommendation> recommendationsIt =
	 * recommendationsForUser .iterator(); int index =
	 * maxNumberOfRecommedations; while (recommendationsIt.hasNext() && index >
	 * 0) { Recommendation recommendation = recommendationsIt.next(); result +=
	 * "I recommend the item " + recommendation.getItemID() +
	 * "with an strength of " + recommendation.getStrength() + "\n"; index--; }
	 * } return result; }
	 */

	/**
	 * This f
	 * 
	 * @param id
	 * @param name
	 * @param maxNumberOfRecommedations
	 * @return
	 */
	/*
	 * @Path("/recommendations/user")
	 * 
	 * @GET
	 * 
	 * @Produces(MediaType.APPLICATION_XML) public ArrayList<Recommendation>
	 * getRecommendationAsXML(
	 * 
	 * @DefaultValue("0") @QueryParam("id") Long id,
	 * 
	 * @DefaultValue("none") @QueryParam("name") String name,
	 * 
	 * @DefaultValue("0") @QueryParam("max_number") Integer
	 * maxNumberOfRecommedations) { System.out.println("XML............id> " +
	 * id + " max_number> " + maxNumberOfRecommedations); _initEpnoiCore();
	 * ArrayList<Recommendation> recommendationsForUser = null;
	 * 
	 * if (id != 0) {
	 * 
	 * recommendationsForUser = this.epnoiCore.getRecommendationSpace()
	 * .getRecommendationsForUserID(id); } else { if (!name.equals("none")) {
	 * recommendationsForUser = this.epnoiCore .getRecommendationSpace()
	 * .getRecommendationsForUserName(name); }
	 * 
	 * }
	 * 
	 * if (recommendationsForUser != null) { if (maxNumberOfRecommedations != 0)
	 * { ArrayList<Recommendation> filteredRecommendationsForUser = new
	 * ArrayList<Recommendation>(); Iterator<Recommendation> recommendationsIt =
	 * _orderByStrength( recommendationsForUser).iterator(); int i = 0; while
	 * (recommendationsIt.hasNext() && i < maxNumberOfRecommedations) {
	 * 
	 * filteredRecommendationsForUser.add(i, recommendationsIt.next()); i++; }
	 * return filteredRecommendationsForUser; } else { return
	 * _orderByStrength(recommendationsForUser); } }
	 * 
	 * return new ArrayList<Recommendation>(); }
	 */
	@Path("/recommendation/user/{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public ArrayList<Recommendation> getRecommendationAsXML(
			@DefaultValue("0") @PathParam("id") Long id,
			@DefaultValue("0") @QueryParam("max_number") Integer maxNumberOfRecommedations) {
		System.out.println("XML............id> " + id + " max_number> "
				+ maxNumberOfRecommedations);
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
				return filteredRecommendationsForUser;
			} else {
				Properties properties = new Properties();
				try {
					URL configFileURL = EpnoiServer.class
							.getResource("epnoi.xml");

					FileInputStream fis = new FileInputStream(new File(
							configFileURL.getPath()));
					properties.loadFromXML(fis);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println(".... properties ...");
				properties.list(System.out);

				return _orderByStrength(recommendationsForUser);
			}
		}

		return new ArrayList<Recommendation>();
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
