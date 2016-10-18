package org.pangaea.agrigrid.service.agriculture.video;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.ws.LocalServiceContext;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import junit.framework.TestCase;

import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;
import org.pangaea.agrigrid.service.api.agriculture.Order;
import org.pangaea.agrigrid.service.api.agriculture.video.VideoEntry;

/**
 * <#if locale="ja">
 * サービス実装のテスト
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author $Author: Masaaki Kamiya $
 * @version $Revision: 14474 $
 */
public class AgriCultureVideoServiceTest extends TestCase {
	public void testSearchVideoWithFull() throws Exception {
		VideoEntry[] entries = service.searchVideos(
				"Test", "en", MatchingMethod.PARTIAL.name()
				, new String[]{"siteseeing"}
				, new Order[]{new Order("VideoId", "DESCENDANT")});
		assertNotNull(entries);
		for(VideoEntry ie : entries){
			System.out.println(ie.getVideoId());
		}
	}
	
	public void testSearchVideoWithCategory() throws Exception {
		VideoEntry[] entries = service.searchVideos(
				"Test", "en", MatchingMethod.PARTIAL.name()
				, new String[]{"siteseeing"}
				, new Order[]{new Order("VideoId", "DESCENDANT")});
		assertNotNull(entries);
		for(VideoEntry ie : entries){
			System.out.println(ie.getVideoId());
		}
	}
	
	public void testSearchVideo() throws Exception {
		VideoEntry[] entries = service.searchVideos(
				"1", "en", MatchingMethod.PARTIAL.name()
				, new String[]{}
				, new Order[]{new Order("VideoId", "DESCENDANT")});
		assertNotNull(entries);
		for(VideoEntry ie : entries){
			System.out.println(ie.getVideoId());
		}
	}
	
	public void testSearchVideo_2() throws Exception {
		VideoEntry[] entries = service.searchVideos(
				"2", "en", MatchingMethod.PARTIAL.name()
				, new String[]{}
				, new Order[]{new Order("VideoId", "DESCENDANT")});
		assertNotNull(entries);
		for(VideoEntry ie : entries){
			System.out.println(ie.getVideoId() + ": " + ie.getFileName() + ", " + ie.getCaptions()[0].getText());
		}
	}
	
	public void testGetSubtitles() throws Exception {
//		Subtitle[] result = service.getSubtitles(String.valueOf(videoId), en.getCode());
//		assertNotNull(result);
//		for(Subtitle st : result){
//			System.out.println(st.getStartMillis() + " - " + st.getText());
//		}
	}

	/**
	 * <#if locale="ja">
	 * サムネイル関連のテスト
	 * @throws Exception テストが失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void testManageThumbnail() throws Exception {
		byte[] thumbnail = StreamUtil.readAsBytes(getClass().getResourceAsStream("infrastructure_ja.gif"));
		service.setThumbnail(String.valueOf(videoId), thumbnail);
		
		byte[] thumb = service.getThumbnail(String.valueOf(videoId));
		assertNotNull(thumb);
		assertEquals(thumbnail.length, thumb.length);
	}
	
	@Override
	protected void setUp() throws Exception {
		TestUtil tu = new TestUtil();
		tu.getSession().beginTransaction();
		videoId = tu.setTestData("infrastructure_ja.gif", "", "", "test Video", "テストビデオ");
		tu.getSession().getTransaction().commit();
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestUtil tu = new TestUtil();
		tu.getSession().beginTransaction();
		tu.deleteTestData(videoId);
		tu.getSession().getTransaction().commit();
	}
	
	private long videoId;
	private AgriCultureVideoService service = new AgriCultureVideoService(new LocalServiceContext());
}
