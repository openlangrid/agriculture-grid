package org.pangaea.agrigrid.service.agriculture.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.annotations.Type;

@Entity
@IdClass(ImageCaptionPK.class)
public class ImageCaption {
	public ImageCaption() {
	}

	public ImageCaption(long imageId, String language, String text) {
		this.imageId = imageId;
		this.language = language;
		this.text = text;
	}

	public long getImageId() {
		return imageId;
	}
	public void setImageId(long imageId) {
		this.imageId = imageId;
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
	private long imageId;
	
	@Id
	private String language;
	@Type(type="text")
	private String text;
}
