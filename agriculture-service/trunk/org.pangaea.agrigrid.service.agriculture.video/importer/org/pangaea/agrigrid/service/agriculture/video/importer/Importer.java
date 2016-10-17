package org.pangaea.agrigrid.service.agriculture.video.importer;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.Language;

import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateCategoryDao;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;

public class Importer {
	public static void main(String[] args) throws Exception {
		DaoFactory f = DaoFactory.createInstance();
		DaoContext c = f.getContext();
//		c.getSession().beginTransaction();
		try{
			c.getSession().beginTransaction();
			HibernateCategoryDao cd = f.getCategoryDao();
			HibernateVideoDao vd = f.getVideoDao();
			vd.clear();
			cd.clear();
			Language[] langs = new Language[]{en, ja};
			Map<String, Category> cats = new HashMap<String, Category>();
			for(String cat : categories){
				cats.put(
						cat
						, cd.addCategory(cat, langs, new String[]{cat, cat})
						);
			}
			c.getSession().getTransaction().commit();
			for(String[] row : data){
				
				c.getSession().beginTransaction();
				Video video = vd.addVideo(
						row[0], row[1], row[2], langs, new String[]{row[3], row[4]}
						, Importer.class.getResourceAsStream(row[0])
				);
				for(int i = 5; i < row.length; i++){
					video.getCategories().put(row[i], cats.get(row[i]));
				}
				for(String text : subtitlesEn.keySet()){
					Subtitle s = new Subtitle();
					s.setVideoId(video.getVideoId());
					s.setLanguage("en");
					s.setText(row[3] + ", " + text);
					s.setStartMillis(subtitlesEn.get(text).getFirst());
					video.getSubtitles().add(s);
				}
				for(String text : subtitlesJa.keySet()){
					Subtitle s = new Subtitle();
					s.setVideoId(video.getVideoId());
					s.setLanguage("ja");
					s.setText(row[4] + "、" + text);
					s.setStartMillis(subtitlesJa.get(text).getFirst());
					video.getSubtitles().add(s);
				}
				c.getSession().getTransaction().commit();
				
				c.getSession().beginTransaction();
				vd.setThumbnail(video.getVideoId(), StreamUtil.readAsBytes(
						Importer.class.getResourceAsStream("thumb_" + row[0])));
				c.getSession().getTransaction().commit();
				System.out.println("image added: " + video.getVideoId());
			}
		} finally{
//			c.getSession().getTransaction().commit();
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
	
	private static Map<String, Pair<Long, Long>> subtitlesEn = new LinkedHashMap<String, Pair<Long, Long>>();
	private static Map<String, Pair<Long, Long>> subtitlesJa = new LinkedHashMap<String, Pair<Long, Long>>();
	static {
		subtitlesEn.put("Test text to subtitels1", new Pair<Long, Long>(0L, 10000L));
		subtitlesEn.put("Test text to subtitels2", new Pair<Long, Long>(10001L, 20000L));
		subtitlesEn.put("Test text to subtitels3", new Pair<Long, Long>(20001L, 30000L));
		subtitlesJa.put("字幕のためのテストテキスト１", new Pair<Long, Long>(0L, 10000L));
		subtitlesJa.put("字幕のためのテストテキスト２", new Pair<Long, Long>(10001L, 20000L));
		subtitlesJa.put("字幕のためのテストテキスト３", new Pair<Long, Long>(20001L, 30000L));
	}
}
