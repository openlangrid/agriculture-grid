package org.pangaea.agrigrid.service.agriculture.image.importer;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.util.HashMap;
import java.util.Map;

import jp.go.nict.langrid.language.Language;

import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateCategoryDao;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateImageDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Image;

public class Importer {
	public static void main(String[] args) throws Exception{
		DaoFactory f = DaoFactory.createInstance();
		DaoContext c = f.getContext();
		c.getSession().beginTransaction();
		try{
			HibernateCategoryDao catDao = f.getCategoryDao();
			HibernateImageDao imgDao = f.getImageDao();
			imgDao.clear();
			catDao.clear();
			Language[] langs = new Language[]{en, ja};
			Map<String, Category> cats = new HashMap<String, Category>();
			for(String cat : categories){
				cats.put(
						cat
						, catDao.addCategory(cat, langs, new String[]{cat, cat})
						);
			}
			for(String[] row : data){
				Image image = imgDao.addImage(
						row[0], row[1], row[2]
						, langs, new String[]{row[3], row[4]}
						, Importer.class.getResourceAsStream(row[0])
						);
				for(int i = 5; i < row.length; i++){
					image.getCategories().put(row[i], cats.get(row[i]));
				}
				System.out.println("image added: " + image.getImageId());
			}
		} finally{
			c.getSession().getTransaction().commit();
		}
	}

	private static String[] categories = {
		"cat001", "cat002", "cat003"
	};
	private static String[][] data = {
		{"infrastructure_ja.gif", "Copyright NICT Language Grid Project.", ""
			, "infrastructure of langrid", "言語グリッドの基盤"
			, "cat001"}
		, {"LangridProject_e.gif", "Copyright NICT Language Grid Project.", ""
			, "Langrid Banner", "言語グリッドのバナー"
			, "cat001", "cat002"}
		, {"newsletter_e.gif", "Copyright NICT Language Grid Project.", ""
			, "Langrid Newsletter Banner", "言語グリッドニュースレターのバナー"
			, "cat002", "cat003"}
		, {"playground_logo.jpg", "Copyright NICT Language Grid Project.", ""
			, "Langrid Playground Banner", "言語グリッドPlaygroundのバナー"
			, "cat003"}
		, {"playground.jpg", "Copyright NICT Language Grid Project.", ""
			, "the screen shot of the Langrid Playground", "言語グリッドPlaygroundのスクリーンショット"
			, "cat001", "cat002", "cat003"}
	};
}
