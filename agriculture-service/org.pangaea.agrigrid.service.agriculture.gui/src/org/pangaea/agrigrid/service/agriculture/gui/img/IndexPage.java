package org.pangaea.agrigrid.service.agriculture.gui.img;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jp.go.nict.langrid.language.Language;

import org.apache.wicket.AttributeModifier;
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
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateImageDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.common.LanguageChoice;
import org.pangaea.agrigrid.service.agriculture.gui.common.PopupLink;

/**
 * <#if locale="ja">
 *  画像一覧ページ
 * <#elseif locale="en">
 * </#if>
 * 
 * @author Masaaki Kamiya
 * @author $Author: Masaaki Kamiya $
 * @version $Revision: 11606 $
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
		add(new Link("addLink") {
			@Override
			public void onClick() {
				setResponsePage(new AddPage());
			}
		});

		final List<Language> supported = AgricultureAdminApplication.getApplication().getSupportedLanguage();
		catCurrentLang = supported.get(0);
		capCurrentLang = supported.get(0);

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
		dataViewContainer.setOutputMarkupId(true);
		add(dataViewContainer);
		dataView = new DataView<ImageModel>("list", new ImageDataProvider()) {
			@Override
			protected void populateItem(Item<ImageModel> item) {
				ImageModel im = item.getModelObject();

				StringBuilder categories = new StringBuilder();
				for(Category c : im.getCategories()){
					if(categories.length() > 0){
						categories.append("<br/>");
					}
					categories.append(c.getCategoryId())
						.append("(").append(c.getTexts().get(catCurrentLang.getCode()))
						.append(")");
					
				}
				item.add(new Label("categories", categories.toString()).setEscapeModelStrings(false));

				if (im.getThumbnail() == null) {
					ResourceReference rr = new ResourceReference(
							this.getClass(), "no-image.jpg");
					item.add(new Image("thumbnail", rr));
				} else {
					item.add(new Image("thumbnail", new ImageThumbnail(im
							.getThumbnail())));
				}
				PopupLink<Long> fileLink = new PopupLink<Long>("fileLink",
						new Model<Long>(im.getId())) {
					@Override
					protected Page getPopupPage() {
						return new ImagePopupPage(getModelObject());
					}
				};
				item.add(fileLink);
				fileLink.add(new Label("fileName", im.getFileName()));

				String requestUrl = ((ServletWebRequest)getRequest()).getHttpServletRequest().getRequestURL().toString();
				String url = requestUrl.replace(((ServletWebRequest)getRequest()).getServletPath(), "");
				fileLink.add(new AttributeModifier("href", new Model<String>(
						url + "/services/AgricultureImageService/download/?imageId=" + im.getId())));

				item.add(new Label("caption", im.getCaptions().get(capCurrentLang.getCode())));
				String update = SimpleDateFormat.getDateTimeInstance().format(
						im.getUpdatedAt());
				item.add(new Label("updatedAt", update));
				Form form = new Form("form");
				final long id = im.getId();
				final String fileName = im.getFileName();
				form.add(new Button("delete") {
					@Override
					public void onSubmit() {
						DaoFactory f = DaoFactory.createInstance();
						DaoContext dc = f.getContext();
						dc.beginTransaction();
						HibernateImageDao vd = f.getImageDao();
						vd.deleteImage(id);
						info("delete image: " + fileName);
						dc.commit();
					}

					@Override
					protected String getOnClickScript() {
						return "return confirm('Are you sure you want to delete the selected video?')";
					}
				});
				form.add(new Button("edit") {
					@Override
					public void onSubmit() {
						setResponsePage(new EditPage(id));
					}
				});

				item.add(form);
			}
		};
		dataViewContainer.add(dataView);
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

	private Language catCurrentLang;
	private Language capCurrentLang;
	private MarkupContainer dataViewContainer;
	private DataView<ImageModel> dataView;
	private DropDownChoice<Integer> pagingCountSelecter;

	private class ImageThumbnail extends DynamicImageResource {
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
