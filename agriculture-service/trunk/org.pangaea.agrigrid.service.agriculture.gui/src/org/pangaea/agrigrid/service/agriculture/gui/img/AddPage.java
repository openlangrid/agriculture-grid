package org.pangaea.agrigrid.service.agriculture.gui.img;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.imageio.ImageIO;

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
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.pages.InternalErrorPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.WildcardListModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateImageDao;
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.common.LanguageChoice;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Image;
import org.pangaea.agrigrid.service.agriculture.image.util.ImageUtil;

/**
 * <#if locale="ja">
 * 画像登録ページ
 * TODO Caption, subtitleの項目の削除機能作成.項目の追加､削除時に入力が消えないようにする.バリデート
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author $Author: Masaaki Kamiya $
 * @version $Revision: 11606 $
 */
public class AddPage extends WebPage {
	public AddPage() {
		add(new FeedbackPanel("feedback"));
		form = new Form("form");
		form.setMultiPart(true);
		add(form);
		form.add(imageField = new FileUploadField("image"));

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
	//		category.setSuffix("&nbsp; ");
			form.add(category);
		} finally{
			dc.commit();
		}
		
		captionWrapper = new WebMarkupContainer("captionWrapper");
		captionWrapper.setOutputMarkupId(true);
		form.add(captionWrapper);
		captionRepeater = new RepeatingView("captionRepeater");
		captionWrapper.add(captionRepeater);
				
		form.add(new Button("add"){
			@SuppressWarnings("unchecked")
			@Override
			public void onSubmit() {
				DaoFactory f = DaoFactory.createInstance();
				DaoContext dc = f.getContext();
				HibernateImageDao imgd = f.getImageDao();
				long imageId;
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
					
					Image i = imgd.addImage(imageField.getFileUpload().getClientFileName()
							, copyright.getModelObject()
							, license.getModelObject()
							, captionLangList.toArray(new Language[]{})
							, captionList.toArray(new String[]{})
							, imageField.getFileUpload().getInputStream());
					
					for(Category c : category.getModelObject()){
						i.getCategories().put(c.getCategoryId(), c);
					}
					imageId = i.getImageId();
					dc.commit();
				} catch (IOException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				}
					
				dc.beginTransaction();
				try {
					// set thumbnail
					FileUpload thumbFile = imageField.getFileUpload();
					String[] fileNames = thumbFile.getClientFileName().split("\\.");
					String format = fileNames[fileNames.length - 1];

					BufferedImage thumb = ImageIO.read(ImageUtil.resize(
							thumbFile.getInputStream(), thumbWidth, format));
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
			        BufferedOutputStream os = new BufferedOutputStream(bos);
			        try{
			        	thumb.flush();
			        	ImageIO.write(thumb, format, os);
			        	os.flush();
			        }finally{
			        	os.close();
			        }
			        byte[] thumbnail = bos.toByteArray();
					
					imgd.setThumbnail(imageId, thumbnail);
					
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

	}
	
	private void setCaptionComponents(RepeatingView rv)
	throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException
	{
		WebMarkupContainer wmc = new WebMarkupContainer(rv.newChildId());
		rv.add(wmc);
		wmc.add(new TextArea<String>("caption", new Model<String>()));
		wmc.add(new LanguageChoice(
				"captionLang", AgricultureAdminApplication.getApplication().getSupportedLanguage()
				));
		wmc.add(new AjaxLink<WebMarkupContainer>("deleteCaption"
				, new Model<WebMarkupContainer>(wmc)) {
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
	private RepeatingView subtitleRepeater;
	
	private final int thumbWidth = 100;
}
