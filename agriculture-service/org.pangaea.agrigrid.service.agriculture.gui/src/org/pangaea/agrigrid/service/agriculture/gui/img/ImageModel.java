package org.pangaea.agrigrid.service.agriculture.gui.img;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

public class ImageModel {
	public Collection<Category> getCategories() {
		return categories;
	}
	public void setCategories(Collection<Category> categories) {
		this.categories = categories;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Map<String, String> getCaptions() {
		return captions;
	}
	public void setCaptions(Map<String, String> captions) {
		this.captions = captions;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public byte[] getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long id;
	private Collection<Category> categories = new ArrayList<Category>();
	private String fileName;
	private Map<String, String> captions = new HashMap<String, String>();
	private Date updatedAt;
	private byte[] thumbnail;
}
