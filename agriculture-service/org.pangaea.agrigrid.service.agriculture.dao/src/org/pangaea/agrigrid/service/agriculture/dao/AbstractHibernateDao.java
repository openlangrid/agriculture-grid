package org.pangaea.agrigrid.service.agriculture.dao;

import org.hibernate.Session;

public abstract class AbstractHibernateDao<T> {
	public AbstractHibernateDao(DaoContext context, Class<T> clazz){
		this.context = context;
		this.clazz = clazz;
	}

	public void clear(){
		Session s = context.getSession();
		for(Object o : s.createCriteria(clazz).list()){
			s.delete(o);
		} 
	}

	protected DaoContext getContext(){
		return context;
	}

	protected String decorate(String text, MatchingMethod matchingMethod){
		if (matchingMethod.equals(MatchingMethod.COMPLETE)) {
			return text;
		} else if (matchingMethod.equals(MatchingMethod.PARTIAL)) {
			return "%" + text + "%";
		} else if (matchingMethod.equals(MatchingMethod.SUFFIX)) {
			return "%" + text;
		} else if (matchingMethod.equals(MatchingMethod.PREFIX)) {
			return text + "%";
		} else{
			return text;
		}
	}

	private DaoContext context;
	private Class<T> clazz;
}
