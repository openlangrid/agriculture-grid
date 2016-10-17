package org.pangaea.agrigrid.service.agriculture.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.annotations.Type;

@Entity
@IdClass(SubtitlePK.class)
public class Subtitle {
	public long getVideoId() {
		return videoId;
	}
	public void setVideoId(long videoId) {
		this.videoId = videoId;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
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
	
	@Id
	private long videoId;
	@Id
	private String language;
	@Id
	private long startMillis;
	@Id
	private long endMillis;

	@Type(type="text")
	private String text;
}
