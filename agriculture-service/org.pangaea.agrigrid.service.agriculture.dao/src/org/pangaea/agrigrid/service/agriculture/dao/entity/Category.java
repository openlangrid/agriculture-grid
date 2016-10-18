package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.CollectionOfElements;

@Entity
public class Category implements Serializable{
	public Category(){
	}

	public Category(String categoryId){
		this.categoryId = categoryId;
	}

	@Override
	public boolean equals(Object value){
		return EqualsBuilder.reflectionEquals(this, value);
	}

	@Override
	public int hashCode(){
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Map<String, String> getTexts() {
		return texts;
	}

	@Id
	private String categoryId;

	@CollectionOfElements(fetch=FetchType.EAGER)
	private Map<String, String> texts = new TreeMap<String, String>();

	private static final long serialVersionUID = -3814832757634980610L;
}
