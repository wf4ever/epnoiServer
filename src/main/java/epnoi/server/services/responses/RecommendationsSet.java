package epnoi.server.services.responses;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement

//@XmlType(propOrder = { "recommendations"})
public class RecommendationsSet{
	
	
	ArrayList<Recommendation> recommendation;

	public RecommendationsSet() {
		this.recommendation = new ArrayList<Recommendation>();
	}

	public ArrayList<Recommendation> getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(ArrayList<Recommendation> recommendations) {
		this.recommendation = recommendations;
	}

}
