package org.pangaea.agrigrid.service.agriculture.dao;

import java.io.Serializable;

public class EntityNotFoundException extends Exception {
	public EntityNotFoundException(Class<?> entityClass, Serializable id){
		this.entityClass = entityClass;
		this.id = id;
	}

	public  Class<?> getEntityClass() {
		return entityClass;
	}

	public Serializable getId() {
		return id;
	}

	private Class<?> entityClass;
	private Serializable id;

	private static final long serialVersionUID = 6578980659328414608L;
}
