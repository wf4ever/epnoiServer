package epnoi.client;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

import epnoi.model.ActionsContext;
import epnoi.model.RecommendationContext;
import epnoi.server.services.responses.RecommendationsSet;

public class EpnoiClient {
	public static void main(String[] args) {
		
		/*
		String candidateKeyword="contents:  ";
		candidateKeyword = candidateKeyword.toLowerCase();
		//candidateKeyword =candidateKeyword.replaceAll("[^a-zA-Z 0-9]+", "");
		System.out.println("------->" +candidateKeyword.matches("contents:\\s*"));
		System.exit(0);
		*/
		//String testURI = "http://localhost:8081/epnoiServerWAR/rest/";

		
		String testURI = "http://localhost:8015/";
		
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(testURI);

		ClientResponse response = null;
		// Create one todo
		System.out.println("Context creation------------------------------");
		
		System.out.println("Recommendations context creation------------------------------");
		
		String userURI = "http://www.myexperiment.org/user.xml?id=2";
		// String userURI = "http://www.myexperiment.org/user.xml?id=43";
		System.out.println(service
				.path("recommender")
				.path("contexts")
				.path("recommendationContext")
				.queryParam("user", userURI)
				.queryParam("resource",
						"http://www.myexperiment.org/user.xml?id=43")
				.queryParam("resource",
						"http://www.myexperiment.org/workflow.xml?id=16")
				.queryParam("resource",
						"http://www.myexperiment.org/workflow.xml?id=1583")
				.accept(MediaType.APPLICATION_XML).put(ClientResponse.class));
		System.out.println("Actions context creation------------------------------");
		//" action> " + actionType + " item> " + item+ " timestamp> " + timestamp
		System.out.println(service
				.path("recommender")
				.path("contexts")
				.path("actionsContext")
				.queryParam("user", userURI)
				.queryParam("actionType",
						"viewed")
				.queryParam("item",
						"http://www.myexperiment.org/workflow.xml?id=16")
				.queryParam("timestamp",
						"whenever")
				.accept(MediaType.APPLICATION_XML).post(ClientResponse.class));
		
		System.out.println("Context retrieval------------------------------");
		System.out.println(service.path("recommender").path("contexts")
				.path("recommendationContext").queryParam("user", userURI)
				.accept(MediaType.APPLICATION_XML)
				.get(RecommendationContext.class));

		System.out
				.println("Context retrieval------------------------------");
		System.out
		.println("Context recommendation retrieval------------------------------");
		System.out.println(service.path("recommender").path("recommendations")
				.path("contextualizedRecommendationsSet")
				.queryParam("user", userURI).accept(MediaType.APPLICATION_XML)
				.get(RecommendationsSet.class).getRecommendation());
	
		
		System.out
		.println("Actions recomendations retrieval------------------------------");

		System.out.println(service.path("recommender").path("contexts")
				.path("actionsContext")
				.queryParam("user", userURI).accept(MediaType.APPLICATION_XML)
				.get(ActionsContext.class));
	}

}
