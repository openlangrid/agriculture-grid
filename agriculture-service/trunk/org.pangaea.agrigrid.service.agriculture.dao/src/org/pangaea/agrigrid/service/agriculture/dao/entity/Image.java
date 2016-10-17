package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.sql.Blob;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

@Entity
@Table(name="images")
public class Image {
	public long getImageId() {
		return imageId;
	}

	public void setImageId(long imageId) {
		this.imageId = imageId;
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

	public Map<String, ImageCaption> getCaptions() {
		return captions;
	}

	public Blob getFile() {
		return file;
	}

	public void setFile(Blob file) {
		this.file = file;
	}

	public Blob getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Blob thumbnail) {
		this.thumbnail = thumbnail;
	}

	@Id
	@GeneratedValue
	private long imageId;
	private String fileName;

	@Type(type = "text")
	private String copyright;

	@Type(type = "text")
	private String license;

	@ManyToMany
	@MapKey(name="categoryId")
	@JoinTable(
			name="ImageCategory",
			joinColumns=@JoinColumn(name="imageId"),
			inverseJoinColumns=@JoinColumn(name="categoryId")
	)
	private Map<String, Category> categories = new HashMap<String, Category>();

	@OneToMany(cascade=CascadeType.ALL, mappedBy="imageId")
	@MapKey(name="language")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private Map<String, ImageCaption> captions = new TreeMap<String, ImageCaption>();

	@Lob
	private Blob file;
	
	@Lob
	private Blob thumbnail;
	private Calendar createdAt;
	private Calendar updatedAt;
}
