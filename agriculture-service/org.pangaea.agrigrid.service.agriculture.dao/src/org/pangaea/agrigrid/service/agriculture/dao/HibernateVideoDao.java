package org.pangaea.agrigrid.service.agriculture.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;
import org.pangaea.agrigrid.service.agriculture.dao.entity.VideoCaption;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

public class HibernateVideoDao {
	public HibernateVideoDao(DaoContext context) {
		this.context = context;
	}

	public void clear(){
		Session s = context.getSession();
		for(Object o : s.createCriteria(Video.class).list()){
			s.delete(o);
		} 
	}

	@SuppressWarnings("unchecked")
	public List<Video> searchVideos(String text, Language textLanguage,
			MatchingMethod matchingMethod, String[] categoryIds, Order[] orders) {
		Session s = context.getSession();
		try {
			StringBuilder query = new StringBuilder();
			query.append("from Video as vd where");
			for(int i = 0; i < categoryIds.length; i++){
				query.append(" :catId").append(i).append(" in elements(categories) and");
			}

			query.append(" ((select count(videoId) from vd.subtitles where language=:language and text like :caption) > 0 or");
			query.append(" ((select text from vd.captions where language=:language) like :caption))");
			
//			query.append(" (select text from vd.captions where language=:language) like :caption");
			Query q = s.createQuery(query.toString());
			for(int i = 0; i < categoryIds.length; i++){
				q.setString("catId" + i, categoryIds[i]);
			}
			q.setString("language", textLanguage.getCode());
			q.setString("caption", decorate(text, matchingMethod));
			List<Video> result = q.list();
			return result;
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Video> listVideo(int first, int count){
		Session s = context.getSession();
		try {
			Query q = s.createQuery("from Video as vd order by updatedAt desc");
			q.setFirstResult(first);
			q.setMaxResults(count);
			return q.list();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public long getTotalCount(){
		Session s = context.getSession();
		try {
			Query q = s.createQuery("select count(videoid) from Video");
			return (Long) q.uniqueResult();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public Video getVideo(long videoId) {
		return (Video) context.getSession().get(Video.class, videoId);
	}
	
	public Blob getVideoBinary(long videoId)
	throws EntityNotFoundException
	{
		Video v = (Video) context.getSession().get(Video.class, videoId);
		if(v != null){
			return v.getFile();
		} else{
			throw new EntityNotFoundException(Video.class, videoId);
		}
	}

	public Blob getThumbnail(long videoId)
	throws EntityNotFoundException{
		Video v = (Video) context.getSession().get(Video.class, videoId);
		if(v != null){
			return v.getThumbnail();
		} else{
			throw new EntityNotFoundException(Video.class, videoId);
		}
	}
	
	public Set<Subtitle> getSubtitles(long videoId, Language language)
	throws EntityNotFoundException
	{
		Session s = context.getSession();
		Video vd = (Video)s.get(Video.class, videoId);
		if(vd != null){
			Set<Subtitle> set = new LinkedHashSet<Subtitle>();
			for(Subtitle st : vd.getSubtitles()){
				if(st.getLanguage().equals(language.getCode())){
					set.add(st);
				}
			}
			return set;
		} else {
			throw new EntityNotFoundException(Video.class, videoId);
		}
	}

	public void addSubtitles(long videoId, Language language, Map<String, Pair<Long, Long>> subtitles){
		Session s = context.getSession();
		Video vd = (Video)s.get(Video.class, videoId);
		if(vd != null){
			for(String text : subtitles.keySet()){
				Subtitle st = new Subtitle();
				st.setVideoId(vd.getVideoId());
				st.setText(text);
				st.setLanguage(language.getCode());
				st.setStartMillis(subtitles.get(text).getFirst());
				st.setEndMillis(subtitles.get(text).getSecond());
				vd.getSubtitles().add(st);
			}
		}
	}
	
	public Video addVideo(String filename, String copyright, String license
			, Language[] languages, String[] captions, InputStream videoBody)
	throws IOException{
		Session s = context.getSession();
		Video vd = new Video();
		vd.setFileName(filename);
		vd.setCopyright(copyright);
		vd.setLicense(license);
		vd.setFile(Hibernate.createBlob(videoBody));
		Calendar now = Calendar.getInstance();
		vd.setCreatedAt(now);
		vd.setUpdatedAt(now);
		s.save(vd);
		
		for(int i = 0; i < languages.length; i++){
			String l = languages[i].getCode();
			vd.getCaptions().put(
					l
					, new VideoCaption(vd.getVideoId(), l, captions[i])
					);
		}
		return vd;
	}
	
	public void setThumbnail(long videoId, byte[] thumbnail)
	throws EntityNotFoundException, IOException
	{
		Session s = context.getSession();
		Video v = (Video) s.get(Video.class, videoId);
		if(v != null){
			v.setThumbnail(Hibernate.createBlob(thumbnail));
			s.update(v);
		} else{
			throw new EntityNotFoundException(Video.class, videoId);
		}
	}
	
	/**
	 * <#if locale="ja">
	 * 映像を編集する
	 * 
	 * @param videoId
	 * @param fileName
	 * @param copyright
	 * @param license
	 * @param captionMap
	 * @param categories
	 * @param subtitleMap
	 * @param videoBody
	 * @param thumbnail
	 * @throws IOException
	 * @throws EntityNotFoundException
	 * @throws InvalidLanguageTagException
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void update(long videoId, String fileName, String copyright, String license
			, Map<Language, String> captionMap, Collection<Category> categories
			, Map<Language, Map<String, Pair<Long, Long>>> subtitleMap
			, InputStream videoBody, InputStream thumbnail)
	throws IOException, EntityNotFoundException, InvalidLanguageTagException
	{
		Session s = context.getSession();
		Video v = (Video) s.get(Video.class, videoId);
		if(v != null){
			if(videoBody != null){
				v.setFile(Hibernate.createBlob(videoBody));
				v.setFileName(fileName); 
			}
			if(thumbnail != null){
				v.setThumbnail(Hibernate.createBlob(thumbnail));
			}
			v.setLicense(license);
			v.setCopyright(copyright);
			
			// remove category
			List<Category> removeList = new ArrayList<Category>();
			for(Category c : v.getCategories().values()){
				if( ! categories.contains(c)){
					removeList.add(c);
				}
			}
			for(Category c : removeList){
				v.getCategories().remove(c);
			}
			// add category
			List<Category> addList = new ArrayList<Category>();
			for(Category c : categories){
				if( ! v.getCategories().containsKey(c.getCategoryId())){
					addList.add(c);
				}
			}
			for(Category c : addList){
				v.getCategories().put(c.getCategoryId(), c);
			}
			// update and remove caption
			List<Language> removeCateList = new ArrayList<Language>();
			for(String lang : v.getCaptions().keySet()){
				Language l = new Language(lang);
				if(captionMap.containsKey(l)){
					// edit
					v.getCaptions().get(lang).setText(captionMap.get(l));
				}else{
					removeCateList.add(l);
				}
			}
			for(Language l : removeCateList){
				// remove
				v.getCaptions().remove(l.getCode());
			}
			// add caption
			List<VideoCaption> addCapList = new ArrayList<VideoCaption>();
			for(Language l : captionMap.keySet()){
				if( ! v.getCaptions().containsKey(l.getCode())) {
					VideoCaption vc = new VideoCaption();
					vc.setVideoId(v.getVideoId());
					vc.setText(captionMap.get(l));
					vc.setLanguage(l.getCode());
					addCapList.add(vc);
				}
			}
			for(VideoCaption vc : addCapList){
				v.getCaptions().put(vc.getLanguage(), vc);
			}

			List<Language> langList = new ArrayList<Language>();
			for(Language l : subtitleMap.keySet()){
				langList.add(l);
			}
			removeSubtitleByLang(v, langList);
			for(Language l : subtitleMap.keySet()){
				modifySubtitle(v, l, subtitleMap.get(l));
				addSubtitleRow(v, l, subtitleMap.get(l));
			}
			
			s.update(v);
		} else{
			throw new EntityNotFoundException(Video.class, videoId);
		}
	}

	public void deleteVideo(long videoId) {
		Session s = context.getSession();
		Object vd = s.get(Video.class, videoId);
		if(vd != null){
			s.delete(vd);
		}
	}
	
	/**
	 * <#if locale="ja">
	 * 字幕を追加する
	 * @param v
	 * @param lang 字幕の言語
	 * @param newSubtitle
	 * <#elseif locale="en">
	 * </#if>
	 */
	private void addSubtitleRow(Video v, Language lang, Map<String, Pair<Long, Long>> newSubtitle){
		List<Subtitle> addSubList = new ArrayList<Subtitle>();
		boolean isAdd = true;
		for(String text : newSubtitle.keySet()){
			Long start = newSubtitle.get(text).getFirst();
			Long end = newSubtitle.get(text).getSecond();
			for(Subtitle sub : v.getSubtitles()){
				if(sub.getLanguage().equals(lang.getCode())){
					if(sub.getStartMillis() == start && sub.getEndMillis() == end){
						isAdd = false;
						continue;
					}
				}
			}
			if(isAdd){
				Subtitle s = new Subtitle();
				s.setText(text);
				s.setLanguage(lang.getCode());
				s.setStartMillis(start);
				s.setEndMillis(end);
				s.setVideoId(v.getVideoId());
				addSubList.add(s);
			}
			isAdd = true;
		}
		v.getSubtitles().addAll(addSubList);
	}
	
	/**
	 * <#if locale="ja">
	 * 字幕を編集する
	 * @param v
	 * @param lang 字幕の言語
	 * @param newSubtitle
	 * <#elseif locale="en">
	 * </#if>
	 */
	private void modifySubtitle(Video v, Language lang, Map<String, Pair<Long, Long>> newSubtitle){
		if(newSubtitle.size() == 0){
			return;
		}
		List<Subtitle> removeSubList = new ArrayList<Subtitle>();
		boolean isRemove = true;
		for(Subtitle sub : v.getSubtitles()){
			if(sub.getLanguage().equals(lang.getCode())){
				for(String text : newSubtitle.keySet()){
					Long start = newSubtitle.get(text).getFirst();
					Long end = newSubtitle.get(text).getSecond();
					if(sub.getStartMillis() == start && sub.getEndMillis() == end) {
						sub.setText(text);
						isRemove = false;
						continue;
					}
				}
				if(isRemove){
					removeSubList.add(sub);
				}
				isRemove = true;
			}
		}
		v.getSubtitles().removeAll(removeSubList);
	}
	
	/**
	 * <#if locale="ja">
	 * 字幕の対応言語で削除する
	 * @param v
	 * @param list 編集後の対応言語リスト
	 * <#elseif locale="en">
	 * </#if>
	 */
	private void removeSubtitleByLang(Video v, List<Language> list){
		Set<String> subLangs = new HashSet<String>();
		for(Subtitle sub : v.getSubtitles()){
			if( ! subLangs.contains(sub.getLanguage())){
				subLangs.add(sub.getLanguage());
			}
		}
		
		Set<String> removeLangs = new HashSet<String>();
		boolean isRemove = true;
		for(String lang : subLangs){
			for(Language l : list){
				if(lang.equals(l.getCode())){
					isRemove = false;
					continue;
				}
			}
			if(isRemove){
				removeLangs.add(lang);
			}
			isRemove = true;
		}
		
		List<Subtitle> removeSubList = new ArrayList<Subtitle>();
		for(Subtitle sub : v.getSubtitles()){
			if(removeLangs.contains(sub.getLanguage())){
				removeSubList.add(sub);
			}
		}
		if(removeSubList.size() != 0){
			v.getSubtitles().removeAll(removeSubList);
		}
	}
	
	private String decorate(String text, MatchingMethod matchingMethod){
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
}
