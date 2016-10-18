package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.sql.Blob;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

@Entity
@Table(name="videos")
public class Video {
	public long getVideoId() {
		return videoId;
	}

	public void setVideoId(long videoId) {
		this.videoId = videoId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public Calendar getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Calendar createdAt) {
		this.createdAt = createdAt;
	}

	public Calendar getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Calendar updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Map<String, Category> getCategories() {
		return categories;
	}

	public Map<String, VideoCaption> getCaptions() {
		return captions;
	}

	public Blob getFile() {
		return file;
	}

	public void setFile(Blob file) {
		this.file = file;
	}
	
	public Set<Subtitle> getSubtitles() {
		return subtitles;
	}

	public Blob getThumbnail() {
		return thumbnail;
	}
	
	public void setThumbnail(Blob thumbnail) {
		this.thumbnail = thumbnail;
	}

	@Id
	@GeneratedValue
	private long videoId;
	private String fileName;

	@Type(type = "text")
	private String copyright;

	@Type(type = "text")
	private String license;

	@ManyToMany
	@MapKey(name="categoryId")
	@JoinTable(
			name="VideoCategory",
			joinColumns=@JoinColumn(name="videoId"),
			inverseJoinColumns=@JoinColumn(name="categoryId")
	)
	private Map<String, Category> categories = new HashMap<String, Category>();

	@OneToMany(cascade=CascadeType.ALL, mappedBy="videoId")
	@MapKey(name="language")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private Map<String, VideoCaption> captions = new TreeMap<String, VideoCaption>();

	@OneToMany(cascade=CascadeType.ALL, mappedBy="videoId")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("startMillis ASC")
	private Set<Subtitle> subtitles = new LinkedHashSet<Subtitle>();
	
	@Lob
	private Blob file;
	@Lob
	private Blob thumbnail;
	
	private Calendar createdAt;
	private Calendar updatedAt;
}
