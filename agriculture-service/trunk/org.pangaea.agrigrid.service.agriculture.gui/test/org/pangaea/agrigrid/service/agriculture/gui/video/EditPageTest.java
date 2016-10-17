package org.pangaea.agrigrid.service.agriculture.gui.video;

import java.util.List;

import jp.go.nict.langrid.language.Language;
import junit.framework.TestCase;

import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.util.LanguageUtil;

public class EditPageTest extends TestCase {
	public void testPage() throws Exception {
		wt.startPage(new EditPage(editVideoId));
		wt.assertRenderedPage(EditPage.class);
		wt.debugComponentTrees();
	}
	
	public void testAjaxComponent() throws Exception {
		wt.startPage(new EditPage(editVideoId));
		
		RepeatingView crv = (RepeatingView)wt.getComponentFromLastRenderedPage("form:captionRepeater");
		int size = crv.size();
		assertTrue(0 < crv.size());
		wt.clickLink("form:addCaption", true);
		assertEquals(size + 1, crv.size());

		RepeatingView srv = (RepeatingView)wt.getComponentFromLastRenderedPage("form:subtitleRepeater");
		size = srv.size();
		wt.clickLink("form:addSubtitle", true);
		assertEquals(size + 1, srv.size());
	}
	
	public void testCancel() throws Exception {
		wt.startPage(new EditPage(editVideoId));
		wt.clickLink("form:cancel");
		wt.assertRenderedPage(IndexPage.class);
	}
	
	public void testEditSubmitWithSWF() throws Exception {
		wt.startPage(new EditPage(editVideoId));

		String path = EditPageTest.class.getResource("").getPath();
		wt.debugComponentTrees();
		
		FormTester ft = wt.newFormTester("form");
		ft.setValue("copyright", "test copyright(edit)");
		ft.setValue("license", "test license(edit)");
		
		ft.setFile("video", new File(path + "/black.swf"), "");
		ft.setFile("thumbnail", new File(path + "/test_thumbnail.jpg"), "");
		
		int[] selecteds = new int[]{1};
		ft.selectMultiple("category", selecteds, true);
		
		List<Language> list = LanguageUtil.getLanguageList(
				wt.getComponentFromLastRenderedPage("form").getLocale());
		int enIndex =list.indexOf(new Language("en"));
		int jaIndex = list.indexOf(new Language("ja"));
		
		ft.select("captionRepeater:2:captionLang", enIndex);
		ft.setValue("captionRepeater:2:caption", "swf file test caption(edit)");
		
		ft.select("captionRepeater:1:captionLang", jaIndex);
		ft.setValue("captionRepeater:1:caption", "SWFファイルのテストキャプション(編集済み)");
		
		ft.select("subtitleRepeater:2:subtitleLang", enIndex);
		ft.setFile("subtitleRepeater:2:subtitleFile", new File(path + "/testSubtitle_en.csv"), "");
		
		ft.select("subtitleRepeater:1:subtitleLang", jaIndex);
		ft.setFile("subtitleRepeater:1:subtitleFile", new File(path + "/testSubtitle_ja.csv"), "");
		
		ft.submit("edit");
		
		wt.assertNoErrorMessage();
		wt.assertRenderedPage(IndexPage.class);
	}

	
	@Override
	protected void setUp() throws Exception {
		wt = new WicketTester(new AgricultureAdminApplication());
	}
	
	private WicketTester wt;
	private int editVideoId = 65;
}
