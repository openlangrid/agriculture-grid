package org.pangaea.agrigrid.service.agriculture.gui.video;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.Language;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.StringResourceStream;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.common.LanguageChoice;
import org.pangaea.agrigrid.service.agriculture.gui.common.PopupLink;

/**
 * <#if locale="ja">
 * 映像一覧ページ
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author $Author: Masaaki Kamiya $
 * @version $Revision: 11603 $
 */
public class IndexPage extends WebPage {
	public IndexPage() {
		add(new FeedbackPanel("feedback"));
		add(new Link("topLink") {
			@Override
			public void onClick() {
				setResponsePage(new org.pangaea.agrigrid.service.agriculture.gui.IndexPage());
			}
		});
		add(new Link("addLink"){
			@Override
			public void onClick() {
				setResponsePage(new AddPage());
			}
		});
		
		final List<Language> supported = AgricultureAdminApplication.getApplication().getSupportedLanguage();
		catCurrentLang = supported.get(0);
		capCurrentLang = supported.get(0);
		subCurrentLang = supported.get(0);

		dataViewContainer = new WebMarkupContainer("listContainer");
		dataViewContainer.add(new Form("formCat"){{
			add(new LanguageChoice("displayLangCat", supported, catCurrentLang){{
				add(new AjaxFormComponentUpdatingBehavior("onchange"){
		            public void onUpdate(AjaxRequestTarget target){
		            	catCurrentLang = getModel().getObject();
		            	target.addComponent(dataViewContainer);
		            }
				});
			}});
		}});
		dataViewContainer.add(new Form("formCap"){{
			add(new LanguageChoice("displayLangCap", supported, capCurrentLang){{
				add(new AjaxFormComponentUpdatingBehavior("onchange"){
		            public void onUpdate(AjaxRequestTarget target){
		            	capCurrentLang = getModel().getObject();
		            	target.addComponent(dataViewContainer);
		            }
				});
			}});
		}});
		dataViewContainer.add(new Form("formSub"){{
			add(new LanguageChoice("displayLangSub", supported, subCurrentLang){{
				add(new AjaxFormComponentUpdatingBehavior("onchange"){
					public void onUpdate(AjaxRequestTarget target){
						subCurrentLang = getModel().getObject();
						target.addComponent(dataViewContainer);
					}
				});
			}});
		}});
		dataViewContainer.setOutputMarkupId(true);
		add(dataViewContainer);
		dataViewContainer.add(
				dataView = new DataView<VideoModel>("list", new VideoDataProvider()){
			@Override
			protected void populateItem(Item<VideoModel> item) {
				VideoModel vm = item.getModelObject();
				
				if(vm.getThumbnail() == null){
					Link link = new Link("thumbnailLink") {
						@Override
						public void onClick() {}
					};
					item.add(link);
					ResourceReference rr = new ResourceReference(this.getClass(), "no-image.jpg");
					link.add(new Image("thumbnail", rr));
				}else{
					PopupLink<Long> link = new PopupLink<Long>("thumbnailLink", new Model<Long>(vm.getId())) {
						@Override
						protected Page getPopupPage() {
							return new ThumbnailPopupPage(getModelObject());
						}
					};
					item.add(link);
					link.add(new Image("thumbnail", new VideoThumbnail(vm.getThumbnail())));
				}
				PopupLink<Long> fileLink = new PopupLink<Long>("fileLink", new Model<Long>(vm.getId())) {
					@Override
					protected Page getPopupPage() {
						return new VideoPopupPage(getModelObject());
					}
				};
				fileLink.getPopupSettings().setHeight(640).setWidth(700);
				item.add(fileLink);
				final String fileName= vm.getFileName();
				fileLink.add(new Label("fileName", vm.getFileName()));

				PopupLink<Long> fileSampleLink = new PopupLink<Long>("fileSampleLink", new Model<Long>(vm.getId())) {
					@Override
					protected Page getPopupPage() {
						return new VideoSamplePopupPage(getModelObject());
					}
				};
				fileSampleLink.getPopupSettings().setHeight(1024).setWidth(700);
				item.add(fileSampleLink);

				item.add(new Label("caption", vm.getCaptions().get(capCurrentLang.getCode())));
				StringBuilder categories = new StringBuilder();
				for(Category c : vm.getCategories()){
					if(categories.length() > 0){
						categories.append("<br/>");
					}
					categories.append(c.getCategoryId())
						.append("(").append(c.getTexts().get(catCurrentLang.getCode()))
						.append(")");
					
				}
				item.add(new Label("categories", categories.toString()).setEscapeModelStrings(false));
				
				StringBuilder subBuff = new StringBuilder();
				if(vm.getSubtitles().containsKey(subCurrentLang.getCode())){
					for(Subtitle sub : vm.getSubtitles().get(subCurrentLang.getCode())){
						
						subBuff.append(makeTimeString(sub.getStartMillis()));
						subBuff.append("~");
						subBuff.append(makeTimeString(sub.getEndMillis()));
						subBuff.append("<br/>");
						subBuff.append(sub.getText());
						subBuff.append("<br/><br/>");
					}
				}
				item.add(new Label("subtitles", subBuff.toString()).setEscapeModelStrings(false));
				
				LinkedHashMap<Pair<Long, Long>, String> subtitle = new LinkedHashMap<Pair<Long, Long>, String>();
				if(vm.getSubtitles().get(subCurrentLang.getCode()) != null){
					for(Subtitle s : vm.getSubtitles().get(subCurrentLang.getCode())){
						subtitle.put(new Pair<Long, Long>(s.getStartMillis(), s.getEndMillis()), s.getText());
					}
				}
				Link<LinkedHashMap<Pair<Long, Long>, String>> b = new Link<LinkedHashMap<Pair<Long, Long>, String>>(
						"download", new Model<LinkedHashMap<Pair<Long, Long>, String>>(subtitle))
				{
					@Override
					public void onClick() {
						StringBuilder sb = new StringBuilder();
						sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Subtitles>\n");
						for(Pair<Long, Long> s : getModelObject().keySet()) {
							sb.append("\t<Subtitle start=\"" + makeTimeString(s.getFirst()) + "\" end=\"" + makeTimeString(s.getSecond()) + "\" text=\"" + getModelObject().get(s) + "\" />");
							sb.append("\n");
						}
						sb.append("</Subtitles>\n");
						StringResourceStream stream = new StringResourceStream(
								sb.toString()
								, "application/octet-stream");
						stream.setCharset(Charset.forName("utf-8"));
						
						String name = fileName.split("\\.")[0] + "_cue-" + subCurrentLang.getCode() + ".xml";
						getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(stream, name));
					}
				};
				item.add(b.setVisible(subtitle.size() != 0));
				String update = SimpleDateFormat.getDateTimeInstance().format(vm.getUpdatedAt());
				item.add(new Label("updatedAt", update));
				Form form = new Form("form");
				final long id = vm.getId();
				form.add(new Button("delete"){
					@Override
					public void onSubmit() {
						DaoFactory f = DaoFactory.createInstance();
						DaoContext dc = f.getContext();
						dc.beginTransaction();
						HibernateVideoDao vd = f.getVideoDao();
						vd.deleteVideo(id);
						info("delete video : " + fileName);
						dc.commit();
					}
					@Override
			         protected String getOnClickScript(){
				            return "return confirm('Are you sure you want to delete the selected video?')";
			         }
				});
				form.add(new Button("edit"){
					@Override
					public void onSubmit() {
						setResponsePage(new EditPage(id));
					}
				});
		
				item.add(form);
			}
		});
		AjaxPagingNavigator top = new AjaxPagingNavigator("topNavigator", dataView);
		AjaxPagingNavigator under = new AjaxPagingNavigator("underNavigator", dataView);
		
		pagingCountSelecter = new DropDownChoice<Integer>(
				"selecter", new Model<Integer>(), new WildcardListModel<Integer>())
		{
			@Override
			protected CharSequence getDefaultChoice(Object selected) {
				return null;
			}
		};
		
		List<Integer> list = new ArrayList<Integer>();
		list.add(10);
		list.add(30);
		list.add(50);
		list.add(100);
		pagingCountSelecter.setChoices(list);
		pagingCountSelecter.setModelObject(30);
		
		add(pagingCountSelecter);
		pagingCountSelecter.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				dataView.setItemsPerPage(pagingCountSelecter.getConvertedInput());
				target.addComponent(dataViewContainer);
			}
		});

		dataView.setItemsPerPage(pagingCountSelecter.getModelObject());
		dataViewContainer.add(top);
		dataViewContainer.add(under);
	}
	
	private String makeTimeString(long millitime){
		long time = millitime / 1000;
		long sec = time % 60;
		long min = time / 60;
		long hour = min / 60;
		
		StringBuilder sb = new StringBuilder();
		if(hour != 0){
			if(hour < 10){
				sb.append("0");
			}
			sb.append(hour);
			sb.append(":");
		}
		if(min < 10){
			sb.append("0");
		}
		sb.append(min);
		sb.append(":");
		if(sec < 10){
			sb.append("0");
		}
		sb.append(sec);
		return sb.toString();
	}
	
	private Language catCurrentLang;
	private Language capCurrentLang;
	private Language subCurrentLang;
	private MarkupContainer dataViewContainer;
	private DataView<VideoModel> dataView;
	private DropDownChoice<Integer> pagingCountSelecter;
	
	private class VideoThumbnail extends DynamicImageResource{		
		public VideoThumbnail(byte[] image) {
			this.image = image;
		}
		
		@Override
		protected byte[] getImageData() {
			return image;
		}

		private byte[] image;
	}
}
