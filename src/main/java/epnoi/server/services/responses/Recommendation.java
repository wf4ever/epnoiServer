package epnoi.server.services.responses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "recommendation")
public class Recommendation {

	String title;
	Float strength;
	String resource;
	String usedTechnique;
	String explanation;

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Float getStrength() {
		return strength;
	}

	public void setStrength(Float strength) {
		this.strength = strength;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getUsedTechnique() {
		return usedTechnique;
	}

	public void setUsedTechnique(String usedTechnique) {
		this.usedTechnique = usedTechnique;
	}

}
