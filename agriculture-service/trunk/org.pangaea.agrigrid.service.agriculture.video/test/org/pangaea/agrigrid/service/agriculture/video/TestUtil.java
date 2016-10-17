package org.pangaea.agrigrid.service.agriculture.video;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.Language;

import org.hibernate.Session;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateCategoryDao;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;

public class TestUtil {
	public TestUtil() {
		if(session == null){
			session = factory.getContext().getSession();
		}
	}
	
	public long setTestData(
			String fileName, String copyright, String lisence
			, String captionEn, String captionJa)
	throws Exception
	{
		InputStream body = getClass().getResourceAsStream(fileName);
		Video video = vdDao.addVideo(
				fileName, copyright, lisence
				, new Language[]{en, ja}
				, new String[]{captionEn, captionJa}
				, body
			);
		// en
		Map<String, Pair<Long, Long>> subtitlesEn = new LinkedHashMap<String, Pair<Long, Long>>();
		subtitlesEn.put("Test text to subtitels 1.", new Pair<Long, Long>(1L, 2L));
		subtitlesEn.put("Test text to subtitels 2.", new Pair<Long, Long>(2L, 3L));
		subtitlesEn.put("Test text to subtitels 3.", new Pair<Long, Long>(3L, 4L));
		vdDao.addSubtitles(video.getVideoId(), en, subtitlesEn);

		// ja
		Map<String, Pair<Long, Long>> subtitlesJa = new LinkedHashMap<String, Pair<Long, Long>>();
		subtitlesJa.put("字幕のためのテストテキスト１", new Pair<Long, Long>(1L, 2L));
		subtitlesJa.put("字幕のためのテストテキスト２", new Pair<Long, Long>(2L, 3L));
		subtitlesJa.put("字幕のためのテストテキスト３", new Pair<Long, Long>(3L, 4L));
		vdDao.addSubtitles(video.getVideoId(), ja, subtitlesJa);
		
		return video.getVideoId();
	}
	
	public void deleteTestData(long videoId){
		vdDao.deleteVideo(videoId);
	}
	
	public HibernateVideoDao getVideoDao(){
		return vdDao;
	}
	
	public HibernateCategoryDao getCategoryDao(){
		return catDao;
	}
	
	public Session getSession(){
		return session;
	}
	
	private DaoFactory factory = DaoFactory.createInstance();
	private HibernateCategoryDao catDao = factory.getCategoryDao();
	private HibernateVideoDao vdDao = factory.getVideoDao();
	private Session session;
}
