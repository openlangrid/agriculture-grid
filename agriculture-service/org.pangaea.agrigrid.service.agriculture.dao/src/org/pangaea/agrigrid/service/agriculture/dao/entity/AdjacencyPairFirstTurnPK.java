package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class AdjacencyPairFirstTurnPK implements Serializable{
	public AdjacencyPairFirstTurnPK() {
		recalcHashCode();
	}
	public AdjacencyPairFirstTurnPK(long adjacencyPairId, String language) {
		this.adjacencyPairId = adjacencyPairId;
		this.language = language;
		recalcHashCode();
	}

	@Override
	public boolean equals(Object value){
		return EqualsBuilder.reflectionEquals(this, value);
	}
	@Override
	public int hashCode(){
		return hashCode;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
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

	private void recalcHashCode(){
		hashCode = new HashCodeBuilder()
		.append(adjacencyPairId)
		.append(language)
		.hashCode();
	}

	private long adjacencyPairId;
	private String language;
	private transient int hashCode; 
	private static final long serialVersionUID = 8480926778075520976L;
}
