package org.pangaea.agrigrid.service.agriculture.dao;

public class DaoFactory {
	public static synchronized DaoFactory createInstance(){
		if(factory == null){
			factory = new DaoFactory();
		}
		return factory;
	}
	
	public HibernateCategoryDao getCategoryDao(){
		return new HibernateCategoryDao(context);
	}

	public HibernateImageDao getImageDao() {
		return new HibernateImageDao(context);
	}
	
	public HibernateVideoDao getVideoDao() {
		return new HibernateVideoDao(context);
	}
	
	public HibernateAdjacencyPairDao getAdjacencyPairDao(){
		return new HibernateAdjacencyPairDao(context);
	}
	
	public DaoContext getContext(){
		return context;
	}
	
	private DaoFactory() {}

	private static DaoFactory factory;
	private DaoContext context = new DaoContext();
}
