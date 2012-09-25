
package epnoi.server.services.responses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name="recommender")

//@XmlType(propOrder = { "recommendations"})

	    

public class RecommenderService{
	

	String recommendationsSet="/recommender/recommendations/recommendationSet/user/{userId}{?max}";
	String filteredRecommendationsSet="/recommender/recommendations/recommendationSet/user/{userID}/{type}{?max}";
	String recommendationContext="/recommender/contexts/recommendationContext{?user,resource, keyword}";
	String contextualizedRecommendationsSet="/recommender/contexts/contextualizedRecommendationsSet{?user,type, max}";

	
	
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

	public String getRecommendationContext() {
		return recommendationContext;
	}

	public void setRecommendationContext(String recommendationContext) {
		this.recommendationContext = recommendationContext;
	}

	public String getContextualizedRecommendationsSet() {
		return contextualizedRecommendationsSet;
	}

	public void setContextualizedRecommendationsSet(
			String contextualizedRecommendationsSet) {
		this.contextualizedRecommendationsSet = contextualizedRecommendationsSet;
	}

}
