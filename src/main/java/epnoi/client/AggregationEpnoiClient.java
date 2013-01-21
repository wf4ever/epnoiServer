package epnoi.client;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import epnoi.model.ActionsContext;
import epnoi.model.RecommendationContext;
import epnoi.server.services.responses.RecommendationsSet;

public class AggregationEpnoiClient {
	public static void main(String[] args) {
		
		
		
		String testURI = "http://localhost:8015/";
		
		ClientConfig config = new DefaultClientConfig();
		config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		Client client = Client.create(config);
		
		WebResource service = client.resource(testURI);

		ClientResponse response = null;
		// Create one todo
		String userURI = "http://www.myexperiment.org/user.xml?id=1";
		String packURI = "http://www.myexperiment.org/pack.xml?id=122";
		
		System.out.println("Getting recommendations");
	//	System.out.println("-> "+service.path("recommender/recommendations/recommendationsSet/user/2")
	//	.accept(MediaType.APPLICATION_JSON).get(Track.class));
	
			 
	
		
		System.out
		.println("Aggregation based recommendation ------------------------------");
		System.out.println(service.path("recommender").path("recommendations")
				.path("aggregationRecommendationsSet")
				.queryParam("user", userURI).queryParam("pack", packURI).accept(MediaType.APPLICATION_XML)
				.get(RecommendationsSet.class).getRecommendation());
	
		
		}

}
