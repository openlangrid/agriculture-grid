package org.pangaea.agrigrid.service.agriculture.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.annotations.Type;

@Entity
@IdClass(AdjacencyPairSecondTurnPK.class)
public class AdjacencyPairSecondTurn {
	public AdjacencyPairSecondTurn() {
	}

	public AdjacencyPairSecondTurn(String language, String text){
		this.language = language;
		this.text = text;
	}

	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
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
	private long groupId;
	@Id
	private String language;
	@Type(type="text")
	private String text;
}
