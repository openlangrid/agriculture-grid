package org.pangaea.agrigrid.service.agriculture.image;

import jp.go.nict.langrid.commons.ws.LocalServiceContext;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import junit.framework.TestCase;

import org.pangaea.agrigrid.service.api.agriculture.Order;
import org.pangaea.agrigrid.service.api.agriculture.image.ImageEntry;

/**
 * <#if locale="ja">
 * サービス実装のテスト
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author $Author: Masaaki Kamiya $
 * @version $Revision: 14232 $
 */
public class AgriCultureImageServiceTest extends TestCase {
	public void testSearchImageWithFull() throws Exception {
		ImageEntry[] entries = service.searchImages(
				"Test", "en", MatchingMethod.PARTIAL.name()
				, new String[]{"siteseeing"}
				, new Order[]{new Order("imageId", "DESCENDANT")});
		assertNotNull(entries);
		for(ImageEntry ie : entries){
			System.out.println(ie.getImageId());
		}
	}

	public void testSearchImageWithCategory() throws Exception {
		ImageEntry[] entries = service.searchImages(
				"Test", "en", MatchingMethod.PARTIAL.name()
				, new String[]{"siteseeing"}
				, new Order[]{new Order("imageId", "DESCENDANT")});
		assertNotNull(entries);
		for(ImageEntry ie : entries){
			System.out.println(ie.getImageId());
		}
	}

	public void testSearchImage() throws Exception {
		ImageEntry[] entries = service.searchImages(
				"Test", "en", MatchingMethod.PARTIAL.name()
				, new String[]{}
				, new Order[]{new Order("imageId", "DESCENDANT")});
		assertNotNull(entries);
		for(ImageEntry ie : entries){
			System.out.println(ie.getImageId());
		}
	}

	public void testSearchImage_2() throws Exception {
		ImageEntry[] entries = service.searchImages(
				"Banner", "en", MatchingMethod.PARTIAL.name()
				, new String[]{}
				, new Order[]{new Order("imageId", "DESCENDANT")});
		assertNotNull(entries);
		for(ImageEntry ie : entries){
			System.out.println(ie.getImageId() + ": " + ie.getFileName() + ", " + ie.getCaptions()[0].getText());
		}
	}

	private AgriCultureImageService service = new AgriCultureImageService(new LocalServiceContext());
}
