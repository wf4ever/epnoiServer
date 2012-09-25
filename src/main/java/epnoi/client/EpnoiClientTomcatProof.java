package epnoi.client;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

import epnoi.model.RecommendationContext;
import epnoi.server.services.responses.RecommendationsSet;

public class EpnoiClientTomcatProof {
	public static void main(String[] args) {
		
		/*
		String candidateKeyword="contents:  ";
		candidateKeyword = candidateKeyword.toLowerCase();
		//candidateKeyword =candidateKeyword.replaceAll("[^a-zA-Z 0-9]+", "");
		System.out.println("------->" +candidateKeyword.matches("contents:\\s*"));
		System.exit(0);
		*/
		String testURI = "http://localhost:8081/epnoiServerWAR/rest/";

		
		
		
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(testURI);

		ClientResponse response = null;
		// Create one todo
		System.out.println("Context creation------------------------------");
		String userURI = "http://www.myexperiment.org/user.xml?id=2";
		// String userURI = "http://www.myexperiment.org/user.xml?id=43";
		System.out.println(service
				.path("recommender")
				.path("contexts")
				.path("recommendationContext")
				.queryParam("user", userURI)
				.queryParam("resource",
						"http://www.myexperiment.org/workflow.xml?id=16")
				.queryParam("resource",
						"http://www.myexperiment.org/workflow.xml?id=1583")
				.accept(MediaType.APPLICATION_XML).put(ClientResponse.class));
		System.out.println("Context retrieval------------------------------");
		System.out.println(service.path("recommender").path("contexts")
				.path("recommendationContext").queryParam("user", userURI)
				.accept(MediaType.APPLICATION_XML)
				.get(RecommendationContext.class));

		System.out
				.println("Recommendation retrieval------------------------------");
		System.out.println(service.path("recommender").path("recommendations")
				.path("contextualizedRecommendationsSet")
				.queryParam("user", userURI).accept(MediaType.APPLICATION_XML)
				.get(RecommendationsSet.class).getRecommendation());

	}

}
