package org.pangaea.agrigrid.service.agriculture.gui.qa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.language.Language;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.WildcardListModel;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.common.LanguageChoice;

/**
 * <#if locale="ja">
 *  一覧ページ
 * <#elseif locale="en">
 * </#if>
 * 
 * @author Masaaki Kamiya
 * @author $Author: Masaaki Kamiya $
 * @version $Revision: 14550 $
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
		turmCurrentLang = supported.get(0);
		relatedTurmCurrentLang = supported.get(0);

		dataViewContainer = new WebMarkupContainer("listContainer");
		dataViewContainer.setOutputMarkupId(true);
		add(dataViewContainer);

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
		dataViewContainer.add(new Form("formTurm"){{
			add(new LanguageChoice("displayLangTurm", supported, turmCurrentLang){{
				add(new AjaxFormComponentUpdatingBehavior("onchange"){
		            public void onUpdate(AjaxRequestTarget target){
		            	turmCurrentLang = getModel().getObject();
		            	target.addComponent(dataViewContainer);
		            }
				});
			}});
		}});
		dataViewContainer.add(new Form("formRelatedTurm"){{
			add(new LanguageChoice("displayLangRelatedTurm", supported, relatedTurmCurrentLang){{
				add(new AjaxFormComponentUpdatingBehavior("onchange"){
					public void onUpdate(AjaxRequestTarget target){
						relatedTurmCurrentLang = getModel().getObject();
						target.addComponent(dataViewContainer);
					}
				});
			}});
		}});
		
		dataView = new DataView<AdjacencyPairModel>("list", new AdjacencyPairDataProvider()) {
			@Override
			protected void populateItem(Item<AdjacencyPairModel> item) {
				AdjacencyPairModel apm = item.getModelObject();

				StringBuilder categories = new StringBuilder();
				for(Category c : apm.getCategories()){
					if(categories.length() > 0){
						categories.append("<br/>");
					}
					categories.append(c.getCategoryId())
						.append("(").append(c.getTexts().get(catCurrentLang.getCode()))
						.append(")");
					
				}
				item.add(new Label("categories", categories.toString().equals("") ? "-" : categories.toString()).setEscapeModelStrings(false));
				String question = apm.getQuestion().get(turmCurrentLang.getCode());
				item.add(new Label("turm", question != null && ! question.equals("") ? question : "-").setEscapeModelStrings(false));
				
				RepeatingView rv = new RepeatingView("answerContainer");
				int i = 1;
				for(Map<String, String> map : apm.getAnswers().values()){
					WebMarkupContainer answerContainer = new WebMarkupContainer(rv.newChildId());
					String answer = map.get(relatedTurmCurrentLang.getCode());
					answerContainer.add(
							new Label("numbering", (1 < i ? "<br/>" : "") + (i++ + ".&nbsp;"))
								.setEscapeModelStrings(false)
								.setVisible(1 < apm.getAnswers().size()));
					answerContainer.add(new Label("answer", answer != null && ! answer.equals("") ? answer : "-").setEscapeModelStrings(false));
					rv.add(answerContainer);
				}
				item.add(rv);

				String update = SimpleDateFormat.getDateTimeInstance().format(
						apm.getUpdatedAt());
				item.add(new Label("updatedAt", update));
				Form form = new Form("form");
				final long id = apm.getId();
				form.add(new Button("delete") {
					@Override
					public void onSubmit() {
						DaoFactory f = DaoFactory.createInstance();
						DaoContext dc = f.getContext();
						dc.beginTransaction();
						f.getAdjacencyPairDao().delete(id);
						info("delete Q&A: " + id);
						dc.commit();
					}

					@Override
					protected String getOnClickScript() {
						return "return confirm('Are you sure you want to delete the selected Q&A?')";
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
	private Language turmCurrentLang;
	private Language relatedTurmCurrentLang;

	private MarkupContainer dataViewContainer;
	private DataView<AdjacencyPairModel> dataView;
	private DropDownChoice<Integer> pagingCountSelecter;
}
