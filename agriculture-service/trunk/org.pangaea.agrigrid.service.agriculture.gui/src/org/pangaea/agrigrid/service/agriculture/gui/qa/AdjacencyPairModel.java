package org.pangaea.agrigrid.service.agriculture.gui.qa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

public class AdjacencyPairModel {
	public Collection<Category> getCategories() {
		return categories;
	}
	public long getId() {
		return id;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	
	public void setCategories(Collection<Category> categories) {
		this.categories = categories;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Map<String, String> getQuestion() {
		return question;
	}
	public void setQuestion(Map<String, String> question) {
		this.question = question;
	}
	public Map<Long, Map<String, String>> getAnswers() {
		return answers;
	}
	public void setAnswers(Map<Long, Map<String, String>> answers) {
		this.answers = answers;
	}

	private long id;
	private Collection<Category> categories = new ArrayList<Category>();

	private Map<String, String> question = new LinkedHashMap<String, String>();
	private Map<Long, Map<String, String>> answers = new LinkedHashMap<Long, Map<String, String>>();
	
	private Date updatedAt;
}
