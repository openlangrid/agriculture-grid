package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class ImageCaptionPK implements Serializable{
	public ImageCaptionPK() {
		recalcHashCode();
	}
	public ImageCaptionPK(long imageId, String language) {
		this.imageId = imageId;
		this.language = language;
		recalcHashCode();
	}
	public long getImageId() {
		return imageId;
	}
	public void setImageId(long imageId) {
		this.imageId = imageId;
		recalcHashCode();
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	@Override
	public boolean equals(Object value){
		return EqualsBuilder.reflectionEquals(this, value);
	}
	@Override
	public int hashCode(){
		return hashCode;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	private void recalcHashCode(){
		hashCode = new HashCodeBuilder()
		.append(imageId)
		.append(language)
		.hashCode();
	}

	private long imageId;
	private String language;
	private transient int hashCode; 

	private static final long serialVersionUID = 7359806372534334450L;
}
