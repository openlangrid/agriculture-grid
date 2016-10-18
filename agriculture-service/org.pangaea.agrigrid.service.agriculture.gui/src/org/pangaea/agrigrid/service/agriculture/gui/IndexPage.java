package org.pangaea.agrigrid.service.agriculture.gui;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * <#if locale="ja">
 * トップページ
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author $Author: Masaaki Kamiya $
 * @version $Revision: 11603 $
 */
public class IndexPage extends WebPage {
	public IndexPage() {
		add(new FeedbackPanel("feedback"));
		add(new Link("imageLink") {
			@Override
			public void onClick() {
				setResponsePage(new org.pangaea.agrigrid.service.agriculture.gui.img.IndexPage());
			}
		});
		add(new Link("videoLink") {
			@Override
			public void onClick() {
				setResponsePage(new org.pangaea.agrigrid.service.agriculture.gui.video.IndexPage());
			}
		});
		add(new Link("adjacencyPairLink") {
			@Override
			public void onClick() {
				setResponsePage(new org.pangaea.agrigrid.service.agriculture.gui.qa.IndexPage());
			}
		});
	}
}
