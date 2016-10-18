package org.pangaea.agrigrid.service.agriculture.gui.img;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebComponent;
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
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateImageDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.ImageCaption;
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.common.LanguageChoice;
import org.pangaea.agrigrid.service.agriculture.gui.util.LanguageUtil;
import org.pangaea.agrigrid.service.agriculture.image.util.ImageUtil;

public class EditPage extends WebPage {
	public EditPage(long videoId) {
		this.editImageId = videoId;
		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		dc.beginTransaction();
		org.pangaea.agrigrid.service.agriculture.dao.entity.Image v = null;
		byte[] thumbnailObj;
		ArrayList<Category> selectedCList = new ArrayList<Category>();
		List<ImageCaption> captionList = new ArrayList<ImageCaption>() ;
		try {
			v = f.getImageDao().getImage(videoId);
			Blob b = v.getThumbnail();
			thumbnailObj = b.getBytes(1, (int)b.length());
			for(Category c : v.getCategories().values()){
				selectedCList.add(c);
			}
			for(ImageCaption vc : v.getCaptions().values()){
				captionList.add(vc);
			}
			dc.commit();
		} catch (SQLException e) {
			dc.rollback();
			e.printStackTrace();
			throw new RestartResponseException(new InternalErrorPage());
		}
		add(new FeedbackPanel("feedback"));
		form = new Form("form");
		form.setOutputMarkupId(true);
		add(form);
		
		form.add(new Label("fileName", fileName = v.getFileName()));
		form.add(imageField = new FileUploadField("video"));
		
		form.add(new Image("thumbnailImage", new ImageThumbnail(thumbnailObj)));

		form.add(copyright = new TextArea<String>("copyright", new Model<String>(v.getCopyright())));
		form.add(license = new TextArea<String>("license", new Model<String>(v.getLicense())));

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

		try {
			for(ImageCaption vc : captionList){				
				setCaptionComponents(captionRepeater, vc.getText(), new Language(vc.getLanguage()));
			}
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
				FileUpload imageFile = imageField.getFileUpload();
				
				Map<Language, String> captionMap = new LinkedHashMap<Language, String>();
				for(int i = 0; i < captionRepeater.size(); i++){
					WebMarkupContainer wmc = (WebMarkupContainer) captionRepeater.get(i);
					TextArea<String> ta = (TextArea<String>)wmc.get("caption");
					LanguageChoice lc = (LanguageChoice)wmc.get("captionLang");
					captionMap.put(lc.getModelObject(), ta.getModelObject());
				}

				DaoFactory f = DaoFactory.createInstance();
				DaoContext dc = f.getContext();
				HibernateImageDao imgd = f.getImageDao();
				dc.beginTransaction();
				try{
					if(imageFile != null){
						String[] fileNames = imageFile.getClientFileName().split("\\.");
						String format = fileNames[fileNames.length - 1];
						InputStream thumb = ImageUtil.resize(imageFile.getInputStream(), 100, format);
						imgd.update(editImageId, imageFile == null ? "" : imageFile.getClientFileName()
								, copyright.getModelObject(), license.getModelObject()
								, captionMap, category.getModelObject()
								, imageFile.getInputStream(), thumb);
					}else{
						imgd.update(editImageId, imageFile == null ? "" : imageFile.getClientFileName()
								, copyright.getModelObject(), license.getModelObject()
								, captionMap, category.getModelObject(), null, null);
					}
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
		form.add(new AjaxSubmitLink("addCaption"){
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> arg1) {
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
		}.setDefaultFormProcessing(false));
	}
	
	private void setCaptionComponents(RepeatingView rv, String caption, Language captionLang)
	throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException
	{
		WebMarkupContainer wmc = new WebMarkupContainer(rv.newChildId());
		rv.add(wmc);
		wmc.add(new TextArea<String>("caption", new Model<String>(caption)));
		wmc.add(new LanguageChoice(
				"captionLang", AgricultureAdminApplication.getApplication().getSupportedLanguage()
				, captionLang));
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
	
	
	private Form form;
	
	private FileUploadField imageField;
	private TextArea<String> copyright;
	private TextArea<String> license;
	private CheckBoxMultipleChoice<Category> category;
	private WebMarkupContainer captionWrapper;
	private RepeatingView captionRepeater;
	
	private String fileName;
	private long editImageId;
	
	private class ImageThumbnail extends DynamicImageResource{		
		public ImageThumbnail(byte[] image) {
			this.image = image;
		}
		
		@Override
		protected byte[] getImageData() {
			return image;
		}

		private byte[] image;
	}
}
