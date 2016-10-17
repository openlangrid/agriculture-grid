package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class SubtitlePK implements Serializable{
	public SubtitlePK() {
		recalcHashCode();
	}
	public SubtitlePK(long videoId, String language, long startMillis, long endMillis) {
		this.videoId = videoId;
		this.startMillis = startMillis;
		this.endMillis = endMillis;
		this.language = language;
		recalcHashCode();
	}
	public int getHashCode() {
		return hashCode;
	}
	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
	public long getVideoId() {
		return videoId;
	}
	public void setVideoId(long videoId) {
		this.videoId = videoId;
	}
	public long getStartMillis() {
		return startMillis;
	}
	public void setStartMillis(long startMillis) {
		this.startMillis = startMillis;
	}
	public long getEndMillis() {
		return endMillis;
	}
	public void setEndMillis(long endMillis) {
		this.endMillis = endMillis;
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
		.append(startMillis)
		.append(endMillis)
		.append(language)
		.hashCode();
	}

	private long videoId;
	private long startMillis;
	private long endMillis;
	private String language;
	private transient int hashCode; 
	
	private static final long serialVersionUID = 2824730952060479720L;	
}
