package org.pangaea.agrigrid.service.agriculture.gui.video;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.commons.util.Trio;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.pages.InternalErrorPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.StringResourceStream;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;
import org.pangaea.agrigrid.service.agriculture.dao.entity.VideoCaption;
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.common.LanguageChoice;
import org.pangaea.agrigrid.service.agriculture.gui.util.LanguageUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EditPage extends WebPage {
	public EditPage(long videoId) {
		this.editVideoId = videoId;
		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		dc.beginTransaction();
		Video v = null;
		byte[] thumbnailObj;
		ArrayList<Category> selectedCList = new ArrayList<Category>();
		List<VideoCaption> captionList = new ArrayList<VideoCaption>() ;
		Set<Subtitle> subtitleSet = new LinkedHashSet<Subtitle>();
		try {
			v = f.getVideoDao().getVideo(videoId);
			Blob b = v.getThumbnail();
			thumbnailObj = b.getBytes(1, (int)b.length());
			for(Category c : v.getCategories().values()){
				selectedCList.add(c);
			}
			for(VideoCaption vc : v.getCaptions().values()){
				captionList.add(vc);
			}
			// sort
			Map<Language, Map<Long, Subtitle>> map = new LinkedHashMap<Language, Map<Long, Subtitle>>();
			for(Subtitle st : v.getSubtitles()) {
				Language l = new Language(st.getLanguage());
				Map<Long, Subtitle> subMap = new TreeMap<Long, Subtitle>();
				if(map.containsKey(l)){
					subMap = map.get(l);
				}
				subMap.put(st.getStartMillis(), st);
				map.put(l, subMap);
			}
			for(Map<Long, Subtitle> subMap : map.values()){
				subtitleSet.addAll(subMap.values());
			}
			dc.commit();
		} catch (SQLException e) {
			dc.rollback();
			e.printStackTrace();
			throw new RestartResponseException(new InternalErrorPage());
		} catch (InvalidLanguageTagException e) {
			dc.rollback();
			e.printStackTrace();
			throw new RestartResponseException(new InternalErrorPage());
		}
		add(new FeedbackPanel("feedback"));
		form = new Form("form");
		form.setMultiPart(true);
		form.setOutputMarkupId(true);
		add(form);
		
		form.add(new Label("fileName", fileName = v.getFileName()));
		form.add(video = new FileUploadField("video"));
		
		form.add(new Image("thumbnailImage", new VideoThumbnail(thumbnailObj)));
		form.add(thumbnail = new FileUploadField("thumbnail"));

		form.add(copyright = new TextArea<String>("copyright", new Model<String>(v.getCopyright())));
		form.add(license = new TextArea<String>("license", new Model<String>(v.getLicense())));

		dc = f.getContext();
		dc.beginTransaction();
		try{
			List<Category> cList = new ArrayList<Category>(new LinkedHashSet<Category>(
					DaoFactory.createInstance().getCategoryDao().listAllCategories()));
	
			category = new CheckBoxMultipleChoice<Category>(
					"category", new Model<ArrayList<Category>>(selectedCList)
					, new WildcardListModel<Category>(cList));
			
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

		try {
			for(VideoCaption vc : captionList){				
				setCaptionComponents(captionRepeater, vc.getText(), new Language(vc.getLanguage()));
			}
			setSubtitleComponents(subtitleRepeater, subtitleSet);
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
		
		form.add(new Button("edit"){
			@SuppressWarnings("unchecked")
			@Override
			public void onSubmit() {
				FileUpload swf = video.getFileUpload();
				FileUpload image = thumbnail.getFileUpload();
				
				Map<Language, String> captionMap = new LinkedHashMap<Language, String>();
				for(int i = 0; i < captionRepeater.size(); i++){
					WebMarkupContainer wmc = (WebMarkupContainer) captionRepeater.get(i);
					TextArea<String> ta = (TextArea<String>)wmc.get("caption");
					LanguageChoice lc = (LanguageChoice)wmc.get("captionLang");
					captionMap.put(lc.getModelObject(), ta.getModelObject());
				}
				Map<Language, Map<String, Pair<Long, Long>>> subtitles = new LinkedHashMap<Language, Map<String, Pair<Long, Long>>>();
				for(int i = 0; i < subtitleRepeater.size(); i++){
					Map<String, Pair<Long, Long>> subtitle = new LinkedHashMap<String, Pair<Long, Long>>();
					
					WebMarkupContainer wmc = (WebMarkupContainer) subtitleRepeater.get(i);
					Language l = ((LanguageChoice)wmc.get("subtitleLang")).getModelObject();
					FileUpload fu = ((FileUploadField)wmc.get("subtitleFile")).getConvertedInput();
					if(fu == null){
						subtitles.put(l, subtitle);
						continue;
					}
					
					try{
						Document menuDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
								fu.getInputStream());
						XPath xpath = XPathFactory.newInstance().newXPath();
						NodeList targetList = (NodeList) xpath.evaluate("//Subtitle", menuDoc, XPathConstants.NODESET);
						for(int j = 0; j < targetList.getLength(); j++){
							Node sn = targetList.item(j).getAttributes().getNamedItem("start");
							Long startSec = parseMilliSecond(sn.getTextContent());
							Node en = targetList.item(j).getAttributes().getNamedItem("end");
							Long endSec = parseMilliSecond(en.getTextContent());
							String subtitleText = targetList.item(j).getAttributes().getNamedItem("name").getTextContent();
							subtitle.put(subtitleText, new Pair<Long, Long>(startSec, endSec));
						}
					}catch(IOException e){
						e.printStackTrace();
						throw new RestartResponseException(new InternalErrorPage());
					} catch (SAXException e) {
						e.printStackTrace();
						throw new RestartResponseException(new InternalErrorPage());
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
						throw new RestartResponseException(new InternalErrorPage());
					} catch (XPathExpressionException e) {
						e.printStackTrace();
						throw new RestartResponseException(new InternalErrorPage());
					}
					
					subtitles.put(l, subtitle);
				}

				DaoFactory f = DaoFactory.createInstance();
				DaoContext dc = f.getContext();
				HibernateVideoDao vd = f.getVideoDao();
				dc.beginTransaction();
				try{
					vd.update(editVideoId, swf == null ? "" : swf.getClientFileName()
							, copyright.getModelObject(), license.getModelObject()
							, captionMap, category.getModelObject(), subtitles
							, swf == null ? null : swf.getInputStream()
							, image == null ? null : image.getInputStream());
					dc.commit();
				} catch (NumberFormatException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (IOException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (EntityNotFoundException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (InvalidLanguageTagException e) {
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
		form.add(new AjaxLink("addCaption"){
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					setCaptionComponents(captionRepeater, "", LanguageUtil.getLanguageList(getLocale()).get(0));
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
		});

		form.add(new AjaxLink("addSubtitle"){
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					WebMarkupContainer wmc = new WebMarkupContainer(subtitleRepeater.newChildId());
					subtitleRepeater.add(wmc);
					wmc.add(new FileUploadField("subtitleFile"));
					wmc.add(new LanguageChoice("subtitleLang"
							, AgricultureAdminApplication.getApplication().getSupportedLanguage()));
					wmc.add(new WebMarkupContainer("download").setVisible(false));
					wmc.add(new AjaxLink<WebMarkupContainer>("deleteSubtitle"
							, new Model<WebMarkupContainer>(wmc)) {
						@Override
						public void onClick(AjaxRequestTarget target) {
							subtitleRepeater.remove(getModelObject());
							target.addComponent(subtitleWrapper);
						}
					});
					target.addComponent(subtitleWrapper);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (Throwable e) {
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				}
			}
		});
	}
	
	private void setCaptionComponents(RepeatingView rv, String caption, Language captionLang)
	throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException
	{
		WebMarkupContainer wmc = new WebMarkupContainer(rv.newChildId());
		rv.add(wmc);
		wmc.add(new TextArea<String>("caption", new Model<String>(caption)));
		wmc.add(new LanguageChoice(
				"captionLang"
				, AgricultureAdminApplication.getApplication().getSupportedLanguage()
				, captionLang
		));
		wmc.add(new AjaxLink<WebMarkupContainer>("deleteCaption"
				, new Model<WebMarkupContainer>(wmc))
		{
			@Override
			public void onClick(AjaxRequestTarget target) {
				captionRepeater.remove(getModelObject());
				target.addComponent(captionWrapper);
			}
		});
	}
	
	private void setSubtitleComponents(RepeatingView rv, Set<Subtitle> subtitleSet)
	throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException
	{
		Map<Language, List<Pair<Long, String>>> map = new LinkedHashMap<Language, List<Pair<Long, String>>>();
		for(Subtitle s : subtitleSet) {
			Language l = new Language(s.getLanguage());
			List<Pair<Long, String>> list = new ArrayList<Pair<Long, String>>();
			if(map.containsKey(l)) {
				list = map.get(l);
			}
			list.add(new Pair<Long, String>(s.getStartMillis(), s.getText()));
			map.put(l, list);
		}
		// make subtitle data.
		for(final Language l : map.keySet()) {
			WebMarkupContainer wmc = new WebMarkupContainer(rv.newChildId());
			rv.add(wmc);
			wmc.add(new FileUploadField("subtitleFile"));
			wmc.add(new LanguageChoice(
					"subtitleLang", AgricultureAdminApplication.getApplication().getSupportedLanguage()
					, l));
			ArrayList<Pair<Long, String>> subtitle = (ArrayList<Pair<Long, String>>)map.get(l);
//			Link<ArrayList<Pair<Long, String>>> b = new Link<ArrayList<Pair<Long, String>>>(
////					"download", new Model<ArrayList<Subtitle>>())
//					"download", new Model<ArrayList<Pair<Long, String>>>(subtitle))
//			{
//				@Override
//				public void onClick() {
//					StringBuilder sb = new StringBuilder();
//					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Flv>\n");
//					for(Pair<Long, String> s : getModelObject()) {
//						sb.append("\t<CuePoint time=\"" + s.getFirst() + "\" name=\"" + s.getSecond() + "\" />");
//						sb.append("\n");
//					}
//					sb.append("</Flv>\n");
//					StringResourceStream stream = new StringResourceStream(
//							sb.toString()
//							, "application/octet-stream");
//					stream.setCharset(Charset.forName("utf-8"));
//					
//					String name = fileName.split("\\.")[0] + "_cue-" + l + ".xml";
//					getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(stream, name));
//				}
//			};
//			wmc.add(b);
			wmc.add(new AjaxLink<WebMarkupContainer>("deleteSubtitle"
					, new Model<WebMarkupContainer>(wmc)) {
				@Override
				public void onClick(AjaxRequestTarget target) {
					subtitleRepeater.remove(getModelObject());
					target.addComponent(subtitleWrapper);
				}
			});
		}
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
	
	private String fileName;
	private long editVideoId;
	
	private class VideoThumbnail extends DynamicImageResource{		
		public VideoThumbnail(byte[] image) {this.image = image;}
		@Override
		protected byte[] getImageData() {return image;}
		private byte[] image;
		private static final long serialVersionUID = 1L;
	}
}
