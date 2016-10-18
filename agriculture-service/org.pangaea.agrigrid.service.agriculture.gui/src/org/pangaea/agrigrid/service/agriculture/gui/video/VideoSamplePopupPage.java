package org.pangaea.agrigrid.service.agriculture.gui.video;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequest;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.common.LanguageChoice;

public class VideoSamplePopupPage extends WebPage {
	public VideoSamplePopupPage(final Long id) {
		HttpServletRequest req = ((WebRequest)getRequest()).getHttpServletRequest();
		String host = "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
		final List<Language> supported = AgricultureAdminApplication.getApplication().getSupportedLanguage();
		currentLang = supported.get(0);
		add(new LanguageChoice("sourceLang", supported, currentLang));
		try {
			add(new LanguageChoice("targetLang", supported, new Language("en")));
		} catch (InvalidLanguageTagException e) {
			e.printStackTrace();
		}
		List<String> list = new ArrayList<String>();
		list.add("GoogleTranslate");
		list.add("NICTJServer");
		list.add("NICTCLWT");
		
		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		String fileName = "";
		try{
			dc.beginTransaction();
			HibernateVideoDao vd = f.getVideoDao();
			Video vm = vd.getVideo(id);
			fileName = vm.getFileName();
			dc.commit();
		}catch(Exception e){
			dc.rollback();
		}
		
		add(new DropDownChoice<String>("serviceIds", new Model<String>("google"), list));
		add(new HiddenField<String>("videoId", new Model<String>("" + id)));
		add(new HiddenField<String>("requestUrl", new Model<String>(host)));	
		add(new HiddenField<String>("videoUrl", new Model<String>(
				host + "/services/AgricultureVideoService/download/" + fileName + "?videoId=" + id
//				host + "/services/AgricultureVideoService/download/" + id + ":" + fileName
//				host + "/js/langrid.ogv"		
		)));
	}
	
	private Language currentLang;
}
