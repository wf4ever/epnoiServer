package epnoi.server.services.responses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "recommendation")
public class Recommendation {

	String title;
	Float strength;
	String resource;
	String usedTechnique;
	String explanation;
	String technique;
	String itemType;


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

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	
	@Override
	public String toString() {
		return "R [" + this.strength + " Item ((URI) " + this.resource +"(Technique) "+this.usedTechnique+" ]";
	}

}
