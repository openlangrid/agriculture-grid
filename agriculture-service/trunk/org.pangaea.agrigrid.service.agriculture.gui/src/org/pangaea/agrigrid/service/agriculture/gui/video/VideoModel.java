package org.pangaea.agrigrid.service.agriculture.gui.video;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;

public class VideoModel {
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	public Collection<Category> getCategories() {
		return categories;
	}
	public void setCategories(Collection<Category> categories) {
		this.categories = categories;
	}
	public Map<String, String> getCaptions() {
		return captions;
	}
	public void setCaptions(Map<String, String> captions) {
		this.captions = captions;
	}
	public Map<String, List<Subtitle>> getSubtitles() {
		return subtitles;
	}
	public void setSubtitles(Map<String, List<Subtitle>> subtitles) {
		this.subtitles = subtitles;
	}

	private long id;
	private String fileName;
	private Date updatedAt;
	private byte[] thumbnail;
	private Collection<Category> categories = new ArrayList<Category>();
	private Map<String, String> captions = new HashMap<String, String>();
	private Map<String, List<Subtitle>> subtitles = new HashMap<String, List<Subtitle>>();
}
