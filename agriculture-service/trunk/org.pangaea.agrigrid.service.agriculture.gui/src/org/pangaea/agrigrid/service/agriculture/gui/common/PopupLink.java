package org.pangaea.agrigrid.service.agriculture.gui.common;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.model.IModel;

public abstract class PopupLink<T> extends Link<T> {
	public PopupLink(String linkId, IModel<T> model) {
		super(linkId, model);
		PopupSettings settings = new PopupSettings(PopupSettings.SCROLLBARS | PopupSettings.RESIZABLE);
//		{
//			@Override
//			public String getPopupJavaScript(){
//				String url = super.getPopupJavaScript();
//				url = url.replaceAll("', 'scrollbars=", label.concat(uniqueId)
//						+ "', 'scrollbars=");
//				return url;
//			}
//
//			private static final long serialVersionUID = 1L;
//		};
//		settings.setHeight(HEIGHT);
//		settings.setWidth(WIDTH);
//		settings.setTop(TOP);
//		settings.setLeft(LEFT);
		setPopupSettings(settings);
	}
	@Override
	public void onClick() {
		setResponsePage(getPopupPage());
	}
	
	protected abstract Page getPopupPage();
}
