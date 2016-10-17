package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class AdjacencyPairSecondTurnPK implements Serializable{
	public AdjacencyPairSecondTurnPK() {
		recalcHashCode();
	}
	public AdjacencyPairSecondTurnPK(long adjacencyPairId, long groupId, String language) {
		this.groupId = groupId;
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

	private void recalcHashCode(){
		hashCode = new HashCodeBuilder()
			.append(groupId)
			.append(language)
			.hashCode();
	}

	private long groupId;
	private String language;
	private transient int hashCode; 
	private static final long serialVersionUID = 8480926778075520976L;
}
