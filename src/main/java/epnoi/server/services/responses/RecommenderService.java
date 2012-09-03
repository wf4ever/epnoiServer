
package epnoi.server.services.responses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name="recommender")

//@XmlType(propOrder = { "recommendations"})

	    

public class RecommenderService{
	

	String recommendationsSet="/recommender/recommendations/recommendationSet/user/{userId}{?max}";
	String filteredRecommendationsSet="/recommender/recommendations/recommendationSet/user/{userId}/{itemType}{?max}";

	
	
	// @XmlElement(name = "title", namespace ="http://loquesea")
	public String getRecommendationsSet() {
		return recommendationsSet;
	}

	public void setRecommendationsSet(String recommendationSet) {
		this.recommendationsSet = recommendationSet;
	}

	public String getFilteredRecommendationsSet() {
		return filteredRecommendationsSet;
	}

	public void setFilteredRecommendationsSet(String filteredRecommendationSet) {
		this.filteredRecommendationsSet = filteredRecommendationSet;
	}

}
