package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;

@Entity
@Table(name="adjacencypairs")
public class AdjacencyPair {
	public AdjacencyPair() {
	}
	public long getAdjacencyPairId() {
		return adjacencyPairId;
	}
	public void setAdjacencyPairId(long adjacencyPairId) {
		this.adjacencyPairId = adjacencyPairId;
	}
	public Map<String, AdjacencyPairFirstTurn> getFirstTurns() {
		return firstTurns;
	}
	public List<AdjacencyPairSecondTurnGroup> getSecondTurnGroup() {
		return secondTurnGroup;
	}
	public Map<String, Category> getCategories() {
		return categories;
	}
	public void setCategories(Map<String, Category> categories) {
		this.categories = categories;
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

	@Id
	@GeneratedValue
	private long adjacencyPairId;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="adjacencyPairId")
	@MapKey(name="language")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private Map<String, AdjacencyPairFirstTurn> firstTurns
			= new HashMap<String, AdjacencyPairFirstTurn>();

	@OneToMany(cascade=CascadeType.ALL, mappedBy="adjacencyPairId")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<AdjacencyPairSecondTurnGroup> secondTurnGroup
			= new ArrayList<AdjacencyPairSecondTurnGroup>();

	@ManyToMany
	@MapKey(name="categoryId")
	@JoinTable(
		name="AdjacencyPairCategory",
		joinColumns=@JoinColumn(name="adjacencyPairId"),
		inverseJoinColumns=@JoinColumn(name="categoryId")
	)
	private Map<String, Category> categories = new HashMap<String, Category>();

	@Temporal(TemporalType.TIMESTAMP)
	private Calendar createdAt;
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar updatedAt;
}
