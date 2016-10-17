package org.pangaea.agrigrid.service.agriculture.gui.video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.pages.InternalErrorPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.WildcardListModel;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.common.LanguageChoice;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <#if locale="ja">
 * 映像登録ページ
 * TODO Caption, subtitleの項目の削除機能作成.項目の追加､削除時に入力が消えないようにする.バリデート
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author $Author: Masaaki Kamiya $
 * @version $Revision: 11601 $
 */
public class AddPage extends WebPage {
	public AddPage() {
		add(new FeedbackPanel("feedback"));
		form = new Form("form");
		form.setMultiPart(true);
		form.setOutputMarkupId(true);
		add(form);
		form.add(video = new FileUploadField("video"));
		form.add(thumbnail = new FileUploadField("thumbnail"));

		form.add(copyright = new TextArea<String>("copyright", new Model<String>()));
		form.add(license = new TextArea<String>("license", new Model<String>()));

		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		dc.beginTransaction();
		try{
			List<Category> cList = new ArrayList<Category>(new LinkedHashSet<Category>(
					DaoFactory.createInstance().getCategoryDao().listAllCategories()));
	
			category = new CheckBoxMultipleChoice<Category>(
					"category", new Model<ArrayList<Category>>(), new WildcardListModel<Category>(cList));
			
			category.setChoiceRenderer(new IChoiceRenderer<Category>() {
						@Override
						public Object getDisplayValue(Category object) {
							return object.getCategoryId() + "(" + object.getTexts().get("en") + ")";
						}
						@Override
						public String getIdValue(Category object, int index) {
							return object.getCategoryId();
						}
					});
			form.add(category);
		} finally{
			dc.commit();
		}
		
		captionWrapper = new WebMarkupContainer("captionWrapper");
		captionWrapper.setOutputMarkupId(true);
		form.add(captionWrapper);
		captionRepeater = new RepeatingView("captionRepeater");
		captionWrapper.add(captionRepeater);

		
		subtitleWrapper = new WebMarkupContainer("subtitleWrapper");
		subtitleWrapper.setOutputMarkupId(true);
		form.add(subtitleWrapper);
		subtitleRepeater = new RepeatingView("subtitleRepeater");
		subtitleWrapper.add(subtitleRepeater);
		
		form.add(new Button("add"){
			@SuppressWarnings("unchecked")
			@Override
			public void onSubmit() {
				DaoFactory f = DaoFactory.createInstance();
				DaoContext dc = f.getContext();
				HibernateVideoDao vd = f.getVideoDao();
				long videoId;
				dc.beginTransaction();
				try {
					// add video
					List<String> captionList = new ArrayList<String>();
					List<Language> captionLangList = new ArrayList<Language>();
					for(int i = 0; i < captionRepeater.size(); i++){
						WebMarkupContainer wmc = (WebMarkupContainer) captionRepeater.get(i);
						captionList.add(((TextArea<String>)wmc.get("caption")).getModelObject());
						captionLangList.add(((LanguageChoice)wmc.get("captionLang")).getModelObject());
					}
					Video v = vd.addVideo(video.getFileUpload().getClientFileName()
							, copyright.getModelObject(), license.getModelObject()
							, captionLangList.toArray(new Language[]{})
							, captionList.toArray(new String[]{})
							, video.getFileUpload().getInputStream());
					
					for(Category c : category.getModelObject()){
						v.getCategories().put(c.getCategoryId(), c);
					}
					videoId = v.getVideoId();
					dc.commit();
				} catch (IOException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				}
					
				dc.beginTransaction();
				try {
					// set thumbnail
					vd.setThumbnail(videoId, thumbnail.getFileUpload().getBytes());
					dc.commit();
				} catch (IOException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (EntityNotFoundException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				}
				
				dc.beginTransaction();
				try {
					// add subtitles
					for(int i = 0; i < subtitleRepeater.size(); i++){
						WebMarkupContainer wmc = (WebMarkupContainer) subtitleRepeater.get(i);

						Map<String, Pair<Long, Long>> subtitle = new LinkedHashMap<String, Pair<Long, Long>>();
						FileUploadField fuf = ((FileUploadField)wmc.get("subtitleFile"));

						Document menuDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
								fuf.getConvertedInput().getInputStream());
						XPath xpath = XPathFactory.newInstance().newXPath();
						NodeList targetList = (NodeList) xpath.evaluate("//Subtitle", menuDoc, XPathConstants.NODESET);
						for(int j = 0; j < targetList.getLength(); j++) {
							Node sn = targetList.item(j).getAttributes().getNamedItem("start");
							long startSec = parseMilliSecond(sn.getTextContent());
							Node en = targetList.item(j).getAttributes().getNamedItem("end");
							long endSec = parseMilliSecond(en.getTextContent());
							String subtitleText = targetList.item(j).getAttributes().getNamedItem("name").getTextContent();
							subtitle.put(subtitleText, new Pair<Long, Long>(startSec, endSec));
						}
						vd.addSubtitles(videoId
								, ((LanguageChoice)wmc.get("subtitleLang")).getModelObject(), subtitle);
					}
					dc.commit();
				} catch (IOException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (SAXException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (ParserConfigurationException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (XPathExpressionException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				}
				
				setResponsePage(new IndexPage());
			}
		});
		form.add(new Link("cancel"){
			@Override
			public void onClick() {
				setResponsePage(new IndexPage());
			}
		});
		form.add(new AjaxSubmitLink("addCaption", form) {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> arg1) {
				try {
					setCaptionComponents(captionRepeater);
					target.addComponent(captionWrapper);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (Throwable e) {
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				}
			}
		}.setDefaultFormProcessing(false));

		form.add(new AjaxSubmitLink("addSubtitle", form){
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> arg1) {
				try {
					setSubtitleComponents(subtitleRepeater);
					target.addComponent(subtitleWrapper);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (Throwable e) {
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				}
			}
		}.setDefaultFormProcessing(false));
	}
	
	private void setCaptionComponents(RepeatingView rv)
	throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException
	{
		final WebMarkupContainer wmc = new WebMarkupContainer(rv.newChildId());
		rv.add(wmc);
		wmc.add(new TextArea<String>("caption", new Model<String>()));
		wmc.add(new LanguageChoice(
				"captionLang", AgricultureAdminApplication.getApplication().getSupportedLanguage()
				));
		wmc.add(new AjaxSubmitLink("deleteCaption") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				captionRepeater.remove(wmc);
				target.addComponent(captionWrapper);
			}
		}.setDefaultFormProcessing(false));
	}
	
	private void setSubtitleComponents(RepeatingView rv)
	throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException
	{
		final WebMarkupContainer wmc = new WebMarkupContainer(rv.newChildId());
		rv.add(wmc);
		wmc.add(new FileUploadField("subtitleFile"));
		wmc.add(new LanguageChoice(
				"subtitleLang", AgricultureAdminApplication.getApplication().getSupportedLanguage()
				));
		wmc.add(new AjaxSubmitLink("deleteSubtitle") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				subtitleRepeater.remove(wmc);
				target.addComponent(subtitleWrapper);
			}
		}.setDefaultFormProcessing(false));
	}
	
	private long parseMilliSecond(String time){
		String[] times = time.split(":");
		Long sec = 0L;
		int count = times.length - 1;
		sec += Long.parseLong(times[count--]);
		sec += Long.parseLong(times[count--]) * 60;
		if(count == 0){
			sec += Long.parseLong(times[count]) * 3600;
		}
		return sec * 1000;
	}
	
	private Form form;
	
	private FileUploadField video;
	private FileUploadField thumbnail;
	private TextArea<String> copyright;
	private TextArea<String> license;
	private CheckBoxMultipleChoice<Category> category;
	private RepeatingView captionRepeater;
	private RepeatingView subtitleRepeater;
	private WebMarkupContainer captionWrapper;
	private WebMarkupContainer subtitleWrapper;
	
}
