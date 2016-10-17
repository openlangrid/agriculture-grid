package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class ImagePK implements Serializable{
	public ImagePK() {
		recalcHashCode();
	}
	public ImagePK(long imageId) {
		this.imageId = imageId;
		recalcHashCode();
	}
	public long getImageId() {
		return imageId;
	}
	public void setImageId(long imageId) {
		this.imageId = imageId;
		recalcHashCode();
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
		.hashCode();
	}

	private long imageId;
	private transient int hashCode; 

	private static final long serialVersionUID = 8951728198095167432L;
}
