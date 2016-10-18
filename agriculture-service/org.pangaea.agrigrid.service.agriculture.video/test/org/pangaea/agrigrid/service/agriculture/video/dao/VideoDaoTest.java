package org.pangaea.agrigrid.service.agriculture.video.dao;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.language.Language;
import junit.framework.TestCase;

import org.hibernate.HibernateException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateCategoryDao;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.MatchingMethod;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;
import org.pangaea.agrigrid.service.agriculture.video.TestUtil;
import org.pangaea.agrigrid.service.agriculture.dao.Order;

/**
 * <#if locale="ja">
 * Daoのテスト
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 14307 $
 */
public class VideoDaoTest extends TestCase {
	public void testAdd() throws Exception{
		try{
			long videoId = tu.setTestData("infrastructure_ja.gif", "", "", "test Video", "テストビデオ");
			tu.deleteTestData(videoId);
		} catch(HibernateException e){
			if(e.getCause() instanceof SQLException){
				e.printStackTrace();
				throw ((SQLException)e.getCause()).getNextException();
			}
		}
	}

	public void testSearch() throws Exception {
		try{
			long videoId = tu.setTestData("infrastructure_ja.gif", "", "", "test Video", "テストビデオ");
			try{
				List<Video> result = tu.getVideoDao().searchVideos(
						"test", en, MatchingMethod.PARTIAL, new String[]{}, new Order[]{});
				assertEquals(1, result.size());
				assertEquals(videoId, result.get(0).getVideoId());
			} finally{
				tu.deleteTestData(videoId);
			}
		} catch(HibernateException e){
			if(e.getCause() instanceof SQLException){
				e.printStackTrace();
				throw ((SQLException)e.getCause()).getNextException();
			}
		}
	}

	public void testSearchBySubtitleEn() throws Exception {
		try{
			long videoId = tu.setTestData("infrastructure_ja.gif", "", "", "test Video", "テストビデオ");
			try{
				// valid
				List<Video> result = tu.getVideoDao().searchVideos(
						"subtitels", en, MatchingMethod.PARTIAL, new String[]{}, new Order[]{});
				assertEquals(1, result.size());
				assertEquals(videoId, result.get(0).getVideoId());
				
				// invalid
				result = tu.getVideoDao().searchVideos(
						"non text", en, MatchingMethod.PARTIAL, new String[]{}, new Order[]{});
				assertEquals(0, result.size());				
			} finally{
				tu.deleteTestData(videoId);
			}
		} catch(HibernateException e){
			if(e.getCause() instanceof SQLException){
				e.printStackTrace();
				throw ((SQLException)e.getCause()).getNextException();
			}
		}
	}

	public void testSearchBySubtitleJa() throws Exception {
		try{
			long videoId = tu.setTestData("infrastructure_ja.gif", "", "", "test Video", "テストビデオ");
			try{
				List<Video> result = tu.getVideoDao().searchVideos(
						"字幕", ja, MatchingMethod.PARTIAL, new String[]{}, new Order[]{});
				assertEquals(1, result.size());
				assertEquals(videoId, result.get(0).getVideoId());
				
				// invalid
				result = tu.getVideoDao().searchVideos(
						"該当無し", ja, MatchingMethod.PARTIAL, new String[]{}, new Order[]{});
				assertEquals(0, result.size());
			} finally{
				tu.deleteTestData(videoId);
			}
		} catch(HibernateException e){
			if(e.getCause() instanceof SQLException){
				e.printStackTrace();
				throw ((SQLException)e.getCause()).getNextException();
			}
		}
	}
	
	public void testSearchBySubtitleAndCaption() throws Exception {
		try{
			long videoId = tu.setTestData("infrastructure_ja.gif", "", "", "test Video1", "テストビデオ");
			try{
				// valid
				List<Video> result = tu.getVideoDao().searchVideos(
						"1", en, MatchingMethod.PARTIAL, new String[]{}, new Order[]{});
				assertEquals(1, result.size());
				assertEquals(videoId, result.get(0).getVideoId());
				
				// invalid
				result = tu.getVideoDao().searchVideos(
						"non text", en, MatchingMethod.PARTIAL, new String[]{}, new Order[]{});
				assertEquals(0, result.size());				
			} finally{
				tu.deleteTestData(videoId);
			}
		} catch(HibernateException e){
			if(e.getCause() instanceof SQLException){
				e.printStackTrace();
				throw ((SQLException)e.getCause()).getNextException();
			}
		}
	}

	public void testSearchByCategory() throws Exception {
		try{
			HibernateCategoryDao catDao = tu.getCategoryDao();
			
			Category cat = catDao.addCategory("cat001"
					, new Language[]{en, ja}
					, new String[]{"Category 001", "カテゴリ001"}
					);
			HibernateVideoDao vdDao = tu.getVideoDao();
			
			Video Video = vdDao.addVideo(
					"infrastructure_ja.gif", "", ""
					, new Language[]{en, ja}, new String[]{"test Video", "テストイメージ"}
					, getClass().getResourceAsStream("infrastructure_ja.gif")
					);
			Video.getCategories().put(cat.getCategoryId(), cat);
			Video Video2 = vdDao.addVideo(
					"LangridProject_e.gif", "", ""
					, new Language[]{en, ja}, new String[]{"test Video2", "テストイメージ2"}
					, getClass().getResourceAsStream("LangridProject_e.gif")
					);
			try{
				List<Video> result = vdDao.searchVideos(
						"test", en, MatchingMethod.PARTIAL, new String[]{"cat001"}, new Order[]{});
				assertEquals(1, result.size());
				assertEquals(Video.getVideoId(), result.get(0).getVideoId());
			} finally{
				catDao.deleteCategory(cat.getCategoryId());
				vdDao.deleteVideo(Video.getVideoId());
				vdDao.deleteVideo(Video2.getVideoId());
			}
		} catch(HibernateException e){
			if(e.getCause() instanceof SQLException){
				e.printStackTrace();
				throw ((SQLException)e.getCause()).getNextException();
			}
		}
	}
	
	public void testGetSubtitles() throws Exception {
		try{
			long videoId = tu.setTestData("infrastructure_ja.gif", "", "", "test Video", "テストビデオ");
			try{
				Set<Subtitle> result = tu.getVideoDao().getSubtitles(videoId, en);
				assertEquals(3, result.size());
			} finally{
				tu.deleteTestData(videoId);
			}
		} catch(HibernateException e){
			if(e.getCause() instanceof SQLException){
				e.printStackTrace();
				throw ((SQLException)e.getCause()).getNextException();
			}
		}
	}
	
	public void testManageThumbnail() throws Exception {
		long videoId = tu.setTestData("infrastructure_ja.gif", "", "", "test Video", "テストビデオ");
		byte[] thumbnail = StreamUtil.readAsBytes(getClass().getResourceAsStream("infrastructure_ja.gif"));
		HibernateVideoDao vd = tu.getVideoDao();
		
		vd.setThumbnail(videoId, thumbnail);

		InputStream is = vd.getThumbnail(videoId).getBinaryStream();
		try {
			byte[] thumb = StreamUtil.readAsBytes(is);
			assertNotNull(thumb);
			assertEquals(thumbnail.length, thumb.length);
		} finally {
			if(is != null){
				is.close();
			}
			tu.deleteTestData(videoId);
		}
	}
	
	public void testGetLimitList(){
		HibernateVideoDao vd = tu.getVideoDao();
		List<Video> list = vd.listVideo(1, 2);
		assertNotNull(list);
		assertEquals(2, list.size());
	}
	
	public void testGetTotalCount(){
		HibernateVideoDao vd = tu.getVideoDao();
		long count = vd.getTotalCount();
		assertTrue(count != 0);
	}

	@Override
	protected void setUp() throws Exception {
		tu = new TestUtil();
		tu.getSession().beginTransaction();
	}

	@Override
	protected void tearDown() throws Exception {
		tu.getSession().getTransaction().commit();
	}

	private TestUtil tu;
}
