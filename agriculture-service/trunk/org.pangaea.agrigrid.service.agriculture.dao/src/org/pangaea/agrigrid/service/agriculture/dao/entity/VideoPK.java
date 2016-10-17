package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class VideoPK implements Serializable{
	public VideoPK() {
		recalcHashCode();
	}
	public VideoPK(long videoId) {
		this.videoId = videoId;
		recalcHashCode();
	}
	public long getVideoId() {
		return videoId;
	}
	public void setVideoId(long videoId) {
		this.videoId = videoId;
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
		.append(videoId)
		.hashCode();
	}

	private long videoId;
	private transient int hashCode; 

	private static final long serialVersionUID = -5104143322308562794L;
}
