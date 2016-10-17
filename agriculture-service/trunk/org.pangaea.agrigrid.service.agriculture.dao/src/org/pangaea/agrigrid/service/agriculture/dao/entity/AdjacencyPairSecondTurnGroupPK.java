package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class AdjacencyPairSecondTurnGroupPK implements Serializable{
	public AdjacencyPairSecondTurnGroupPK() {
		recalcHashCode();
	}
	public AdjacencyPairSecondTurnGroupPK(long adjacencyPairId, long groupId) {
		this.adjacencyPairId = adjacencyPairId;
		this.groupId = groupId;
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
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	private void recalcHashCode(){
		hashCode = new HashCodeBuilder()
			.append(adjacencyPairId)
			.append(groupId)
			.hashCode();
	}

	private long adjacencyPairId;
	private long groupId;
	private transient int hashCode; 
	private static final long serialVersionUID = 8480926778075520976L;
}
