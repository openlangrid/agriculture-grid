package org.pangaea.agrigrid.service.agriculture.dao;

import java.io.IOException;
import java.util.List;

import jp.go.nict.langrid.language.Language;

import org.hibernate.Session;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

public class HibernateCategoryDao {
	public HibernateCategoryDao(DaoContext context) {
		this.context = context;
	}

	public void clear(){
		Session s = context.getSession();
		for(Object o : s.createCriteria(Category.class).list()){
			s.delete(o);
		} 
	}

	@SuppressWarnings("unchecked")
	public List<Category> listAllCategories() {
		return context.getSession().createCriteria(Category.class).list();
	}

	public Category addCategory(String categoryId, Language[] languages, String texts[])
	throws IOException{
		if(languages.length != texts.length){
			throw new IllegalArgumentException("numbers of languages and texts elements must be same.");
		}
		Session s = context.getSession();
		Category cat = new Category();
		cat.setCategoryId(categoryId);
		for(int i = 0; i < languages.length; i++){
			cat.getTexts().put(languages[i].getCode(), texts[i]);
		}
		s.save(cat);
		return cat;
	}

	public void addCategory(Category cat){
		context.getSession().save(cat);
	}

	public void deleteCategory(String categoryId) {
		Session s = context.getSession();
		Object entity = s.get(Category.class, categoryId);
		if(entity != null){
			s.delete(entity);
		}
	}

	public Category getCategory(String categoryId)
	throws EntityNotFoundException{
		Category cat = (Category)context.getSession().get(Category.class, categoryId);
		if(cat != null){
			return cat;
		} else{
			throw new EntityNotFoundException(Category.class, categoryId);
		}
	}

	private DaoContext context;
}
