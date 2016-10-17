package org.pangaea.agrigrid.service.agriculture.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.annotations.Type;

@Entity
@IdClass(AdjacencyPairFirstTurnPK.class)
public class AdjacencyPairFirstTurn {
	public AdjacencyPairFirstTurn() {
	}

	public AdjacencyPairFirstTurn(String language, String text){
		this.language = language;
		this.text = text;
	}

	public long getAdjacencyPairId() {
		return adjacencyPairId;
	}

	public void setAdjacencyPairId(long adjacencyPairId) {
		this.adjacencyPairId = adjacencyPairId;
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
	private long adjacencyPairId;
	@Id
	private String language;
	@Type(type="text")
	private String text;
}
