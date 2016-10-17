package org.pangaea.agrigrid.service.agriculture.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPair;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairFirstTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurnGroup;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

public class HibernateAdjacencyPairDao
extends AbstractHibernateDao<AdjacencyPair>{
	public HibernateAdjacencyPairDao(DaoContext context) {
		super(context, AdjacencyPair.class);
	}

	public long getTotalCount(){
		Session s = getContext().getSession();
		try {
			return ((Number)s.createCriteria(AdjacencyPair.class)
					.setProjection(Projections.rowCount())
					.uniqueResult()).longValue();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<AdjacencyPair> listAllAdjacencyPairs(){
		Session s = getContext().getSession();
		try {
			return s.createCriteria(AdjacencyPair.class).list();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<AdjacencyPair> listAdjacencyPairs(int first, int count){
		Session s = getContext().getSession();
		try {
			return (List<AdjacencyPair>)s.createCriteria(AdjacencyPair.class)
					.addOrder(Property.forName("updatedAt").desc())
					.addOrder(Property.forName("adjacencyPairId").desc())
					.setFirstResult(first)
					.setMaxResults(count)
					.list();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<AdjacencyPair> searchAdjacencyPairs(String text, String textLanguage,
			MatchingMethod matchingMethod, String[] categoryIds, Order[] orders) {
		Session s = getContext().getSession();
		try {
			StringBuilder query = new StringBuilder();
			query.append("select distinct aj from AdjacencyPair as aj LEFT JOIN FETCH aj.secondTurnGroup as stg where");
			for(int i = 0; i < categoryIds.length; i++){
				query.append(" :catId").append(i).append(" in elements(aj.categories) and");
			}
			query.append(" ( exists (select text from aj.firstTurns where language=:language and text like :text)");
			query.append(" or ");
			query.append(" exists (select text from stg.secondTurns where language=:language and text like :text)");
			query.append(" )");

			Query q = s.createQuery(query.toString());
			for(int i = 0; i < categoryIds.length; i++){
				q.setString("catId" + i, categoryIds[i]);
			}
			q.setString("language", textLanguage);
			q.setString("text", decorate(text, matchingMethod));
			return (List<AdjacencyPair>)q.list();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public AdjacencyPair addAdjacencyPair(List<AdjacencyPairFirstTurn> firstTurns
			, List<List<AdjacencyPairSecondTurn>> secondTurns
			, List<Category> categories
			){
		Session s = getContext().getSession();
		try{
			AdjacencyPair p = new AdjacencyPair();
			Calendar now = Calendar.getInstance();
			p.setCreatedAt(now);
			p.setUpdatedAt(now);
			s.persist(p);
			for(AdjacencyPairFirstTurn ft : firstTurns){
				ft.setAdjacencyPairId(p.getAdjacencyPairId());
				p.getFirstTurns().put(ft.getLanguage(), ft);
			}
			for(List<AdjacencyPairSecondTurn> sts : secondTurns){
				AdjacencyPairSecondTurnGroup g = new AdjacencyPairSecondTurnGroup();
				g.setAdjacencyPairId(p.getAdjacencyPairId());
				s.persist(g);
				for(AdjacencyPairSecondTurn st : sts){
					st.setGroupId(g.getGroupId());
					g.getSecondTurns().put(st.getLanguage(), st);
				}
				p.getSecondTurnGroup().add(g);
			}
			for(Category ct : categories){
				p.getCategories().put(ct.getCategoryId(), ct);
			}
			return p;
		} catch(HibernateException e){
			e.printStackTrace();
			throw e;
		}
	}
	
	public void update(Long adjacencyPairId
			, Map<String, String> firstTurns
			, Map<Long, Map<String, String>> secondTurns
			, Collection<Category> categories)
	throws EntityNotFoundException
	{
		Session s = getContext().getSession();
		try{
			AdjacencyPair ap = (AdjacencyPair)s.get(AdjacencyPair.class, adjacencyPairId);
			if(ap == null){
				throw new EntityNotFoundException(AdjacencyPair.class, adjacencyPairId);
			}
			ap.setUpdatedAt(Calendar.getInstance());
			s.persist(ap);
			ap.getCategories().clear();
			for(Category c : categories){
				ap.getCategories().put(c.getCategoryId(), c);
			}
			for(String l : firstTurns.keySet()){
				String q = firstTurns.get(l);
				if(ap.getFirstTurns().containsKey(l)){
					// edit question
					ap.getFirstTurns().get(l).setText(q);
				}else{
					// add question
					AdjacencyPairFirstTurn apft = new AdjacencyPairFirstTurn(l, q);
					apft.setAdjacencyPairId(ap.getAdjacencyPairId());
					ap.getFirstTurns().put(l, apft);
				}
			}
			// remove questions
			List<String> removeQList = new ArrayList<String>();
			for(String key : ap.getFirstTurns().keySet()){
				if( ! firstTurns.containsKey(key)){
					removeQList.add(key);
				}
			}
			for(String key : removeQList){
				ap.getFirstTurns().remove(key);
			}
			
			for(long groupId : secondTurns.keySet()) {
				Map<String, String> value = secondTurns.get(groupId);
				boolean isAddGroup = true;
				for(AdjacencyPairSecondTurnGroup group : ap.getSecondTurnGroup()) {
					if(group.getGroupId() == groupId) {
						// edit group
						isAddGroup = false;

						for(String l : value.keySet()){
							boolean isAdd = true;
							for(AdjacencyPairSecondTurn apst : group.getSecondTurns().values()){
								if(apst.getLanguage().equals(l)){
									// edit second turn
									apst.setText(value.get(l));
									isAdd = false;
									break;
								}
							}
							// add second turn
							if(isAdd){
								AdjacencyPairSecondTurn apst = new AdjacencyPairSecondTurn(l, value.get(l));
								apst.setGroupId(groupId);
								group.getSecondTurns().put(l, apst);
							}
							isAdd = true;
						}
						break;
					}
				}
				if(isAddGroup){
					// add group
					AdjacencyPairSecondTurnGroup apstg = new AdjacencyPairSecondTurnGroup();
					apstg.setAdjacencyPairId(ap.getAdjacencyPairId());
					s.persist(apstg);
					for(String l : value.keySet()){
						AdjacencyPairSecondTurn apst = new AdjacencyPairSecondTurn();
						apst.setGroupId(apstg.getGroupId());
						apst.setLanguage(l);
						apst.setText(value.get(l));
						apstg.getSecondTurns().put(l, apst);
					}
					ap.getSecondTurnGroup().add(apstg);
				}
				isAddGroup = true;
			}
			// remove second turn group
			List<AdjacencyPairSecondTurnGroup> removeGroupList = new ArrayList<AdjacencyPairSecondTurnGroup>();
			for(AdjacencyPairSecondTurnGroup group : ap.getSecondTurnGroup()) {
				if( ! secondTurns.containsKey(group.getGroupId())){
					removeGroupList.add(group);
				}
			}
			ap.getSecondTurnGroup().removeAll(removeGroupList);
			
			// remove second turns
			for(AdjacencyPairSecondTurnGroup group : ap.getSecondTurnGroup()) {
				Map<String, String> stMap = secondTurns.get(group.getGroupId());
				List<String> removeSecendTurnKeyList = new ArrayList<String>();
				for(String l : group.getSecondTurns().keySet()){
					if( ! stMap.containsKey(l)){
						removeSecendTurnKeyList.add(l);
					}
				}
				for(String l : removeSecendTurnKeyList){
					group.getSecondTurns().remove(l);
				}
			}
		} catch(HibernateException e){
			e.printStackTrace();
			throw e;
		}
	}

	public void delete(long adjacencyPairId){
		Session s = getContext().getSession();
		try{
			Object entity = s.get(AdjacencyPair.class, adjacencyPairId);
			if(entity != null){
				s.delete(entity);
			}
		} catch(HibernateException e){
			e.printStackTrace();
			throw e;
		}
	}

	public AdjacencyPair get(long adjacencyPairId)
	throws EntityNotFoundException{
		Session s = getContext().getSession();
		try{
			AdjacencyPair ret = (AdjacencyPair)s.get(AdjacencyPair.class, adjacencyPairId);
			if(ret == null){
				throw new EntityNotFoundException(AdjacencyPair.class, adjacencyPairId);
			}
			return ret;
		} catch(HibernateException e){
			e.printStackTrace();
			throw e;
		}
	}
}
