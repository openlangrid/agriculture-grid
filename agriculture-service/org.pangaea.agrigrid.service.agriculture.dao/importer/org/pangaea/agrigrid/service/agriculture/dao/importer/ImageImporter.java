package org.pangaea.agrigrid.service.agriculture.dao.importer;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.vi;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import jp.go.nict.langrid.language.Language;

import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateImageDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Image;

import au.com.bytecode.opencsv.CSVReader;

public class ImageImporter {
	public static void main(String[] args) throws Exception{
		InputStream is = ImageImporter.class.getResourceAsStream("images-20110131.csv");
		try{
/*
			new ImageImporter().clearImages();
			System.out.println("images cleared.");
/*/
			System.out.println(
					new ImageImporter().importImages(is)
					+ " images imported."
					);
//*/
		} finally{
			is.close();
		}
	}

	public void clearImages() throws IOException, EntityNotFoundException{
		DaoFactory f = DaoFactory.createInstance();
		DaoContext c = f.getContext();
		c.beginTransaction();
		try{
			HibernateImageDao dao = f.getImageDao();
			dao.clear();
		} finally{
			c.commit();
		}
	}

	public int importImages(InputStream is) throws IOException, EntityNotFoundException{
		Map<Long, String> imageMap = new LinkedHashMap<Long, String>();
		int count = 0;
		DaoFactory f = DaoFactory.createInstance();
		DaoContext c = f.getContext();
		c.beginTransaction();
		try{
			HibernateImageDao dao = f.getImageDao();
			dao.clear();
			CSVReader r = new CSVReader(new InputStreamReader(is, "UTF-8"));
			r.readNext();
			for(String[] values; (values = r.readNext()) != null;){
				if(values.length == 0) continue;
				if(values[0].length() == 0) continue;

				InputStream fis = ImageImporter.class.getResourceAsStream("./images/" + values[0]);
				
				// add image
				Image i = dao.addImage(values[0], values[1], values[2], new Language[]{ja, en, vi}
					, new String[]{values[4], values[5], values[6]}, fis);
				// set category
				Map<String, Category> cat= i.getCategories();
				for(String categoryId : values[3].split(",")){
					Category category = new Category();
					category.setCategoryId(categoryId);
					cat.put(categoryId, category);
				}

				imageMap.put(i.getImageId(), values[0]);
				
				System.out.println(i);
				count++;
			}
		} finally{
			c.commit();
		}

		// set thumbnail
		c = f.getContext();
		c.beginTransaction();
		try{
			HibernateImageDao dao = f.getImageDao();
			for(Long id : imageMap.keySet()){
				String[] fileNames = imageMap.get(id).split("\\.");
				String format = fileNames[fileNames.length - 1];
				InputStream fis = ImageImporter.class.getResourceAsStream("./images/" + imageMap.get(id));
				BufferedImage thumb = ImageIO.read(ImageUtil.resize(
						fis, thumbWidth, format));
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				BufferedOutputStream os = new BufferedOutputStream(bos);
				try{
					thumb.flush();
					ImageIO.write(thumb, format, os);
					os.flush();
				}finally{
					os.close();
				}
				byte[] thumbnail = bos.toByteArray();
				dao.setThumbnail(id, thumbnail);
			}
		}finally{
			c.commit();
		}
		return count;
	}
	
	private static int thumbWidth = 100;
}
