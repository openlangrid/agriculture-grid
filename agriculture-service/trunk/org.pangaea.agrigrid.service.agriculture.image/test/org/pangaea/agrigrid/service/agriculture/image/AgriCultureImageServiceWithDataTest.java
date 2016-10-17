package org.pangaea.agrigrid.service.agriculture.image;

import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.commons.ws.LocalServiceContext;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import junit.framework.TestCase;

import org.pangaea.agrigrid.service.api.agriculture.Caption;
import org.pangaea.agrigrid.service.api.agriculture.Category;
import org.pangaea.agrigrid.service.api.agriculture.Order;
import org.pangaea.agrigrid.service.api.agriculture.image.ImageEntry;

/**
 * <#if locale="ja">
 * サービス実装のテスト
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 */
public class AgriCultureImageServiceWithDataTest extends TestCase {
	public void testSearchImageWithFull() throws Exception {
		ImageEntry[] entries = service.searchImages(
				"", "ja", MatchingMethod.PARTIAL.name()
				, new String[]{"aec001001000", "aec001003000"}
				, new Order[]{new Order("imageId", "DESCENDANT")});
		assertNotNull(entries);
		assertEquals(3, entries.length);
		for(ImageEntry ie : entries){
			System.out.println(String.format(
					"id:%s, categories[%s], captions[%s]"
					, ie.getImageId()
					, StringUtil.join(ArrayUtil.collect(
							ie.getCategories(), new Transformer<Category, String>(){
								public String transform(Category value) throws TransformationException {
									return value.getCategoryId();
								}
							}), ",")
					, StringUtil.join(ArrayUtil.collect(
							ie.getCaptions(), new Transformer<Caption, String>(){
								public String transform(Caption value) throws TransformationException {
									return "\"" + value.getLanguage() + "\"->\"" + value.getText() + "\"";
								}
							}), ",")
					));
		}
	}

	private AgriCultureImageService service = new AgriCultureImageService(new LocalServiceContext());
}
