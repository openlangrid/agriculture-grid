package org.pangaea.agrigrid.service.agriculture.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Image;
import org.pangaea.agrigrid.service.agriculture.dao.entity.ImageCaption;

public class HibernateImageDao
extends AbstractHibernateDao<Image>{
	public HibernateImageDao(DaoContext context) {
		super(context, Image.class);
	}

	@SuppressWarnings("unchecked")
	public List<Image> listAllImages(){
		Session s = getContext().getSession();
		try {
			return s.createCriteria(Image.class).list();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Image> listImage(int first, int count){
		Session s = getContext().getSession();
		try {
			Query q = s.createQuery("from Image as img order by updatedAt desc, imageId desc");
			q.setFirstResult(first);
			q.setMaxResults(count);
			return q.list();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Image> searchImages(String text, Language textLanguage,
			MatchingMethod matchingMethod, String[] categoryIds, Order[] orders) {
		Session s = getContext().getSession();
		try {
			StringBuilder query = new StringBuilder();
			query.append("from Image as img where");
			for(int i = 0; i < categoryIds.length; i++){
				query.append(" :catId").append(i).append(" in elements(categories) and");
			}
			query.append(" (select text from img.captions where language=:language) like :caption");
			Query q = s.createQuery(query.toString());
			for(int i = 0; i < categoryIds.length; i++){
				q.setString("catId" + i, categoryIds[i]);
			}
			q.setString("language", textLanguage.getCode());
			q.setString("caption", decorate(text, matchingMethod));
			List<Image> result = q.list();
			return result;
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public long getTotalCount(){
		Session s = getContext().getSession();
		try {
			Query q = s.createQuery("select count(imageid) from Image");
			return (Long) q.uniqueResult();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public Image getImage(long imageId){
		return (Image) getContext().getSession().get(Image.class, imageId);
	}

	public Blob getImageBinary(long imageId)
	throws EntityNotFoundException{
		Image i = (Image) getContext().getSession().get(Image.class, imageId);
		if(i != null){
			return i.getFile();
		} else{
			throw new EntityNotFoundException(Image.class, imageId);
		}
	}
	
	public Blob getThumbnail(long imageId) throws EntityNotFoundException {
		Image i = (Image) getContext().getSession().get(Image.class, imageId);
		if(i != null){
			return i.getThumbnail();
		} else{
			throw new EntityNotFoundException(Image.class, imageId);
		}
	}

	public void setThumbnail(long imageId, byte[] thumnail) throws EntityNotFoundException{
		Session s = getContext().getSession();
		Image i = (Image) s.get(Image.class, imageId);
		if(i != null){
			i.setThumbnail(Hibernate.createBlob(thumnail));
			s.update(i);
		} else{
			throw new EntityNotFoundException(Image.class, imageId);
		}
	}
	
	public Image addImage(String filename, String copyright, String license
			, Language[] languages, String[] captions, InputStream imageBody)
	throws IOException{
		Session s = getContext().getSession();
		Image image = new Image();
		image.setFileName(filename);
		image.setCopyright(copyright);
		image.setLicense(license);
		image.setFile(Hibernate.createBlob(imageBody));
		Calendar now = Calendar.getInstance();
		image.setCreatedAt(now);
		image.setUpdatedAt(now);
		s.save(image);
		for(int i = 0; i < languages.length; i++){
			String l = languages[i].getCode();
			image.getCaptions().put(
					l
					, new ImageCaption(image.getImageId(), l, captions[i])
					);
		}
		return image;
	}

	public void deleteImage(long imageId) {
		Session s = getContext().getSession();
		Object img = s.get(Image.class, imageId);
		if(img != null){
			s.delete(img);
		}
	}
	
	/**
	 * <#if locale="ja">
	 * 画像を編集する
	 * 
	 * @param imageId
	 * @param fileName
	 * @param copyright
	 * @param license
	 * @param captionMap
	 * @param categories
	 * @param imageBody
	 * @param thumbnail
	 * @throws IOException
	 * @throws EntityNotFoundException
	 * @throws InvalidLanguageTagException
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void update(long imageId, String fileName, String copyright, String license
			, Map<Language, String> captionMap, Collection<Category> categories
			, InputStream imageBody, InputStream thumbnail)
	throws IOException, EntityNotFoundException, InvalidLanguageTagException
	{
		Session s = getContext().getSession();
		Image i = (Image) s.get(Image.class, imageId);
		if(i != null){
			if(imageBody != null){
				i.setFile(Hibernate.createBlob(imageBody));
				i.setFileName(fileName); 
			}
			if(thumbnail != null){
				i.setThumbnail(Hibernate.createBlob(thumbnail));
			}
			i.setLicense(license);
			i.setCopyright(copyright);
			i.getCategories().clear();
			for(Category c : categories){
				i.getCategories().put(c.getCategoryId(), c);
			}

			// update and remove caption
			List<Language> removeCateList = new ArrayList<Language>();
			for(String lang : i.getCaptions().keySet()){
				Language l = new Language(lang);
				if(captionMap.containsKey(l)){
					// edit
					i.getCaptions().get(lang).setText(captionMap.get(l));
				}else{
					removeCateList.add(l);
				}
			}
			for(Language l : removeCateList){
				// remove
				i.getCaptions().remove(l.getCode());
			}
			// add caption
			List<ImageCaption> addCapList = new ArrayList<ImageCaption>();
			for(Language l : captionMap.keySet()){
				if( ! i.getCaptions().containsKey(l.getCode())) {
					ImageCaption vc = new ImageCaption();
					vc.setImageId(i.getImageId());
					vc.setText(captionMap.get(l));
					vc.setLanguage(l.getCode());
					addCapList.add(vc);
				}
			}
			for(ImageCaption vc : addCapList){
				i.getCaptions().put(vc.getLanguage(), vc);
			}
			i.setUpdatedAt(Calendar.getInstance());
			s.update(i);
		} else{
			throw new EntityNotFoundException(Image.class, imageId);
		}
	}
}
