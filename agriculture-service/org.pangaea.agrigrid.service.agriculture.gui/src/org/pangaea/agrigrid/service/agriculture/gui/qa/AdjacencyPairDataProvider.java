package org.pangaea.agrigrid.service.agriculture.gui.qa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateAdjacencyPairDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPair;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairFirstTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurnGroup;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

public class AdjacencyPairDataProvider extends SortableDataProvider<AdjacencyPairModel> {
	@Override
	public Iterator<? extends AdjacencyPairModel> iterator(int first, int count) {
		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		dc.beginTransaction();
		List<AdjacencyPairModel> list = new ArrayList<AdjacencyPairModel>();
		HibernateAdjacencyPairDao dao = DaoFactory.createInstance().getAdjacencyPairDao();
		for(AdjacencyPair ap : dao.listAdjacencyPairs(first, count)) {
			AdjacencyPairModel apm = new AdjacencyPairModel();

			List<Category> cats = new ArrayList<Category>(ap.getCategories().values());
			Collections.sort(cats, new Comparator<Category>() {
				@Override
				public int compare(Category arg0, Category arg1) {
					return arg0.getCategoryId().compareTo(arg1.getCategoryId());
				}
			});
			apm.getCategories().addAll(cats);
			apm.setUpdatedAt(ap.getUpdatedAt().getTime());
			apm.setId(ap.getAdjacencyPairId());
			
			Map<Long, Map<String, String>> secondTurns = new LinkedHashMap<Long, Map<String, String>>();
			for(AdjacencyPairSecondTurnGroup g : ap.getSecondTurnGroup()){
				Map<String, String> secondTurn = new LinkedHashMap<String, String>();
				for(AdjacencyPairSecondTurn apst : g.getSecondTurns().values()){
					secondTurn.put(apst.getLanguage(), apst.getText());
				}
				secondTurns.put(g.getGroupId(), secondTurn);
				
			}
			apm.setAnswers(secondTurns);
			
			Map<String, String> firstTurn = new LinkedHashMap<String, String>();
			for(AdjacencyPairFirstTurn apft : ap.getFirstTurns().values()){
				firstTurn.put(apft.getLanguage(), apft.getText());
			}
			apm.setQuestion(firstTurn);
			
			list.add(apm);
		}
		dc.commit();
		return list.iterator();
	}
	
	private AdjacencyPairModel makeRelatedAdjacencyPairModel(AdjacencyPair ap){
		AdjacencyPairModel apm = new AdjacencyPairModel();
		apm.setId(ap.getAdjacencyPairId());
//		apm.setUpdatedAt(ap.getUpdatedAt().getTime());
		
		List<Category> cats = new ArrayList<Category>(ap.getCategories().values());
		Collections.sort(cats, new Comparator<Category>() {
			@Override
			public int compare(Category arg0, Category arg1) {
				return arg0.getCategoryId().compareTo(arg1.getCategoryId());
			}
		});
		apm.getCategories().addAll(cats);
//		for(AdjacencyPairTurm apt : ap.getTurms()){
//			apm.getTurms().put(apt.getLanguage(), apt.getturm());
//		}
		return apm;
	}
	
	@Override
	public IModel<AdjacencyPairModel> model(AdjacencyPairModel i) {
		return new AdjacencyPairDetachableModel(i);
	}
	
	@Override
	public int size() {
		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		dc.beginTransaction();
		try{
			return (int)f.getAdjacencyPairDao().getTotalCount();
		}finally{
			dc.commit();
		}
	}
	
	private class AdjacencyPairDetachableModel extends LoadableDetachableModel<AdjacencyPairModel> {
		public AdjacencyPairDetachableModel(AdjacencyPairModel v) {
			setObject(v);
		}
		@Override
		protected AdjacencyPairModel load() {
			return getObject();
		}
	}
}
