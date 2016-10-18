package org.pangaea.agrigrid.service.agriculture.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.annotations.Type;

@Entity
@IdClass(VideoCaptionPK.class)
public class VideoCaption {
	public VideoCaption() {
	}

	public VideoCaption(long videoId, String language, String text) {
		this.videoId = videoId;
		this.language = language;
		this.text = text;
	}

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

	@Id
	private long videoId;
	
	@Id
	private String language;
	@Type(type="text")
	private String text;
}
