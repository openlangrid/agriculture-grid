package org.pangaea.agrigrid.service.agriculture.image;

import jp.go.nict.langrid.commons.ws.LocalServiceContext;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import junit.framework.TestCase;

import org.pangaea.agrigrid.service.agriculture.qa.AgriCultureQAService;
import org.pangaea.agrigrid.service.api.agriculture.Order;
import org.pangaea.agrigrid.service.api.agriculture.qa.QAEntry;

/**
 * <#if locale="ja">
 * サービス実装のテスト
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 */
public class AgriCultureQAServiceTest extends TestCase {
	public void testSearchQAsWithFull() throws Exception {
		QAEntry[] entries = service.searchQAs(
				"Test", "en", MatchingMethod.PARTIAL.name()
				, new String[]{"siteseeing"}
				, new Order[]{new Order("qaId", "DESCENDANT")});
		assertNotNull(entries);
		for(QAEntry ie : entries){
			System.out.println(ie.getQaId());
		}
	}

	public void testSearchQAsWithCategory() throws Exception {
		QAEntry[] entries = service.searchQAs(
				"Test", "en", MatchingMethod.PARTIAL.name()
				, new String[]{"siteseeing"}
				, new Order[]{new Order("qaId", "DESCENDANT")});
		assertNotNull(entries);
		for(QAEntry ie : entries){
			System.out.println(ie.getQaId());
		}
	}

	public void testSearchQAs() throws Exception {
		QAEntry[] entries = service.searchQAs(
				"Test", "en", MatchingMethod.PARTIAL.name()
				, new String[]{}
				, new Order[]{new Order("qaId", "DESCENDANT")});
		assertNotNull(entries);
		for(QAEntry ie : entries){
			System.out.println(ie.getQaId());
		}
	}

	public void testSearchQAs_2() throws Exception {
		QAEntry[] entries = service.searchQAs(
				"Banner", "en", MatchingMethod.PARTIAL.name()
				, new String[]{}
				, new Order[]{new Order("qaId", "DESCENDANT")});
		assertNotNull(entries);
		for(QAEntry ie : entries){
			System.out.println(ie.getQaId() + ": " + ie.getLanguage() + ", " + ie.getQuestion());
		}
	}

	public void testGetQAs() throws Exception {
		QAEntry[] entries = service.searchQAs(
				"hello", "en", MatchingMethod.PARTIAL.name()
				, new String[]{}
				, new Order[]{new Order("qaId", "DESCENDANT")});
		assertNotNull(entries);
		QAEntry[] qas = service.getQAs(entries[0].getQaId(), new String[]{"en", "ja"});
		assertEquals(2, qas.length);
	}

	private AgriCultureQAService service = new AgriCultureQAService(new LocalServiceContext());
}
