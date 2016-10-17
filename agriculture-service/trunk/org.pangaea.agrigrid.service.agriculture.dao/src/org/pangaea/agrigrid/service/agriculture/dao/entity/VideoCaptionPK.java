package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class VideoCaptionPK implements Serializable{
	public VideoCaptionPK() {
		recalcHashCode();
	}
	public VideoCaptionPK(long videoId, String language) {
		this.videoId = videoId;
		this.language = language;
		recalcHashCode();
	}
	public long getVideoId() {
		return videoId;
	}
	public void setVideoId(long videoId) {
		this.videoId = videoId;
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
		.append(videoId)
		.append(language)
		.hashCode();
	}

	private long videoId;
	private String language;
	private transient int hashCode; 

	private static final long serialVersionUID = -1907871165619638832L;
}
