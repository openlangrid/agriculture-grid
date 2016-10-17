package org.pangaea.agrigrid.service.agriculture.dao.importer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateCategoryDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

import au.com.bytecode.opencsv.CSVReader;

public class TemplateParallelTextImporter {
	public static void main(String[] args) throws Exception{
		InputStream is = new FileInputStream("data/category/category-20110120.csv");
		try{
			System.out.println(
					new TemplateParallelTextImporter().importCategories(is)
					+ " categories imported."
					);
		} finally{
			is.close();
		}
	}

	public int importCategories(InputStream is) throws IOException{
		DaoFactory f = DaoFactory.createInstance();
		DaoContext c = f.getContext();
		c.beginTransaction();
		try{
			HibernateCategoryDao dao = DaoFactory.createInstance().getCategoryDao();
			dao.clear();
			int count = 0;
			CSVReader r = new CSVReader(new InputStreamReader(is, "UTF-8"));
			r.readNext();
			for(String[] values; (values = r.readNext()) != null;){
				if(values.length == 0) continue;
				if(values[0].length() == 0) continue;
				Category cat = new Category();
				cat.setCategoryId(values[0]);
				addText(cat, "ja", values, 2, 3, 4);
				addText(cat, "en", values, 6, 7, 8);
				addText(cat, "vi", values, 10, 11, 12);
				dao.addCategory(cat);
				System.out.println(cat);
				count++;
			}
			return count;
		} finally{
			c.commit();
		}
	}

	private static void addText(Category cat, String lang, String[] texts, int i1, int i2, int i3){
		StringBuilder b = new StringBuilder();
		if(i1 < texts.length){
			b.append(texts[i1]);
		}
		b.append(":");
		if(i2 < texts.length){
			b.append(texts[i2]);
		}
		b.append(":");
		if(i3 < texts.length){
			b.append(texts[i3]);
		}
		String s = b.toString();
		if(s.equals("::")) return;
		cat.getTexts().put(lang, s);
	}
}
