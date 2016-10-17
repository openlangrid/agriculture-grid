package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

@Entity
public class AdjacencyPairSecondTurnGroup {
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public long getAdjacencyPairId() {
		return adjacencyPairId;
	}
	public void setAdjacencyPairId(long adjacencyPairId) {
		this.adjacencyPairId = adjacencyPairId;
	}
	public Map<String, AdjacencyPairSecondTurn> getSecondTurns() {
		return secondTurns;
	}
	
	@Id
	@GeneratedValue
	private long groupId;
	private long adjacencyPairId;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="groupId")
	@MapKey(name="language")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private Map<String, AdjacencyPairSecondTurn> secondTurns
		= new HashMap<String, AdjacencyPairSecondTurn>();
}
