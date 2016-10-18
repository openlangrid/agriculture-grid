package org.pangaea.agrigrid.service.agriculture.gui.video;

import java.util.List;

import jp.go.nict.langrid.language.Language;
import junit.framework.TestCase;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.util.LanguageUtil;

public class AddPageTest extends TestCase {
	public void testPage() throws Exception {
		wt.startPage(new AddPage());
		wt.assertRenderedPage(AddPage.class);
	}
	
	public void testAjaxComponent() throws Exception {
		wt.startPage(new AddPage());
		
		RepeatingView crv = (RepeatingView)wt.getComponentFromLastRenderedPage(
				"form:captionWrapper:captionRepeater");
		assertEquals(0, crv.size());
		
		wt.clickLink("form:addCaption", true);
		assertEquals(1, crv.size());
		
		RepeatingView srv = (RepeatingView)wt.getComponentFromLastRenderedPage(
				"form:subtitleWrapper:subtitleRepeater");
		assertEquals(0, srv.size());
		wt.clickLink("form:addSubtitle", true);
		assertEquals(1, srv.size());
	}
	
	public void testCancel() throws Exception {
		wt.startPage(new AddPage());
		wt.clickLink("form:cancel");
		wt.assertRenderedPage(IndexPage.class);
	}
	
	public void testAddSubmitWithSWF() throws Exception {
		wt.startPage(new AddPage());
		wt.clickLink("form:addCaption", true);
		wt.clickLink("form:addCaption", true);
		wt.clickLink("form:addSubtitle", true);
		wt.clickLink("form:addSubtitle", true);
		String path = AddPageTest.class.getResource("").getPath();
		wt.debugComponentTrees();
		
		FormTester ft = wt.newFormTester("form");
		ft.setValue("copyright", "test copyright");
		ft.setValue("license", "test license");

		ft.setFile("video", new File(path + "/langrid.flv"), "");
		ft.setFile("thumbnail", new File(path + "/test_thumbnail.jpg"), "");
		
		int[] selecteds = new int[]{1};
		ft.selectMultiple("category", selecteds, true);
		
		List<Language> list = LanguageUtil.getLanguageList(
				wt.getComponentFromLastRenderedPage("form").getLocale());
		int enIndex =list.indexOf(new Language("en"));
		int jaIndex = list.indexOf(new Language("ja"));

		ft.select("captionRepeater:1:captionLang", enIndex);
		ft.setValue("captionRepeater:1:caption", "flv file test caption");
		
		ft.select("captionRepeater:2:captionLang", jaIndex);
		ft.setValue("captionRepeater:2:caption", "flvファイルのテストキャプション");
		
		ft.select("subtitleRepeater:1:subtitleLang", enIndex);
		ft.setFile("subtitleRepeater:1:subtitleFile", new File(path + "/langridcue-en.xml"), "");
		
		ft.select("subtitleRepeater:2:subtitleLang", jaIndex);
		ft.setFile("subtitleRepeater:2:subtitleFile", new File(path + "/langridcue-ja.xml"), "");
		
		ft.submit("add");
		
		wt.assertNoErrorMessage();
		wt.assertRenderedPage(IndexPage.class);
	}

	
	@Override
	protected void setUp() throws Exception {
		wt = new WicketTester(new AgricultureAdminApplication());
	}
	
	private WicketTester wt;
}
