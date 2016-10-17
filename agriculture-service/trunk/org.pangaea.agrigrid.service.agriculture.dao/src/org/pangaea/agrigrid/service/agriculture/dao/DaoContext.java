package org.pangaea.agrigrid.service.agriculture.dao;

import java.io.IOException;
import java.net.MalformedURLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPair;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairFirstTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurnGroup;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Image;
import org.pangaea.agrigrid.service.agriculture.dao.entity.ImageCaption;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;
import org.pangaea.agrigrid.service.agriculture.dao.entity.VideoCaption;

public class DaoContext {
	public void beginTransaction() {
		getSession().beginTransaction();
	}
	
	public void rollback() {
		Session s = getSession();
		if(s.isOpen() && s.getTransaction().isActive()){
			s.getTransaction().rollback();
		}
	}

	public void commit() {
		Session s = getSession();
		if(s.getTransaction().isActive()){
			s.getTransaction().commit();
		}
	}
	
	public Session getSession(){
		synchronized(DaoContext.class){
			if(factory == null){
				try {
					init("media_");
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch(HibernateException e){
					e.printStackTrace();
					throw e;
				}
			}
			
			return factory.getCurrentSession();
		}
	}

	private static void init(final String tablePrefix) throws MalformedURLException{
		AnnotationConfiguration config = new AnnotationConfiguration();
		// add entitys.
		config.addAnnotatedClass(Image.class)
			.addAnnotatedClass(ImageCaption.class)
			.addAnnotatedClass(Video.class)
			.addAnnotatedClass(VideoCaption.class)
			.addAnnotatedClass(Subtitle.class)
			.addAnnotatedClass(Category.class)
			.addAnnotatedClass(AdjacencyPair.class)
			.addAnnotatedClass(AdjacencyPairFirstTurn.class)
			.addAnnotatedClass(AdjacencyPairSecondTurnGroup.class)
			.addAnnotatedClass(AdjacencyPairSecondTurn.class)
			.setNamingStrategy(new DefaultNamingStrategy() {
				@Override
				public String tableName(String tableName) {
					return tablePrefix + super.tableName(tableName);
				}
				@Override
				public String collectionTableName(String ownerEntity,
						String ownerEntityTable, String associatedEntity,
						String associatedEntityTable, String propertyName) {
					return tablePrefix + super.collectionTableName(ownerEntity, ownerEntityTable,
							associatedEntity, associatedEntityTable, propertyName);
				}
				@Override
				public String logicalCollectionTableName(String tableName,
						String ownerEntityTable, String associatedEntityTable,
						String propertyName) {
					return tablePrefix + super.logicalCollectionTableName(tableName, ownerEntityTable,
							associatedEntityTable, propertyName);
				}
				@Override
				public String classToTableName(String className) {
					return tablePrefix + super.classToTableName(className);
				}
				private static final long serialVersionUID = 4641732674379658045L;
			});
		
		config.configure(configFile);
		factory = config.buildSessionFactory();
	}
	
	private static String configFile = "hibernate.cfg.xml";
	private static SessionFactory factory;
}
