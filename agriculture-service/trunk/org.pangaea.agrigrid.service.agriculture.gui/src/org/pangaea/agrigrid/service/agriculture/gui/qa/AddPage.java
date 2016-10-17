package org.pangaea.agrigrid.service.agriculture.gui.qa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.WildcardListModel;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateAdjacencyPairDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairFirstTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

/**
 * <#if locale="ja">
 * Q&A登録ページ
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author $Author: Masaaki Kamiya $
 * @version $Revision: 14538 $
 */
public class AddPage extends WebPage implements AjaxEventChain {
	public AddPage() {
		add(new FeedbackPanel("feedback"));
		form = new Form("form");
		add(form);

		secondTurnRewriteContainer = new WebMarkupContainer("secondTurnRewriteContainer");
		secondTurnRewriteContainer.setOutputMarkupId(true);
		form.add(secondTurnRewriteContainer);

		groupRepeater = new RepeatingView("secondTurnGroupRepeater");
		secondTurnRewriteContainer.add(groupRepeater);

		WebMarkupContainer secondTurnGroupContainer = new WebMarkupContainer(groupRepeater.newChildId());
		groupRepeater.add(secondTurnGroupContainer);
		
		RepeatingView rv = new RepeatingView("secondTurnRepeater");
		secondTurnGroupContainer.add(rv);
		
		try {
			form.add(questionPanel = new QuestionFormPanel("questionForm", new HashMap<Language, String>()));
			WebMarkupContainer secondTurnContainer = new WebMarkupContainer(rv.newChildId());
			AnswerFormPanel afp = new AnswerFormPanel("secondTurn", 0, new ArrayList<AdjacencyPairSecondTurn>(), this);
			secondTurnContainer.add(afp);
			rv.add(secondTurnContainer);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvalidLanguageTagException e) {
			e.printStackTrace();
		}
		
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
		
		form.add(new Button("add"){
			@SuppressWarnings("unchecked")
			@Override
			public void onSubmit() {
				DaoFactory f = DaoFactory.createInstance();
				DaoContext dc = f.getContext();
				HibernateAdjacencyPairDao dao= f.getAdjacencyPairDao();
				dc.beginTransaction();
				List<Category> cList = new ArrayList<Category>();
				for(Category c : category.getModelObject()){
					cList.add(c);
				}
				List<AdjacencyPairFirstTurn> firstTurns = new ArrayList<AdjacencyPairFirstTurn>();
				Map<String, String> firstTurnValue = questionPanel.getValue();
				for(String l : firstTurnValue.keySet()){
					firstTurns.add(new AdjacencyPairFirstTurn(l, firstTurnValue.get(l)));
				}
					
				List<List<AdjacencyPairSecondTurn>> secondTurns = new ArrayList<List<AdjacencyPairSecondTurn>>();
				for(Map<String, String> map : getSecondTurnValue()){
					List<AdjacencyPairSecondTurn> list = new ArrayList<AdjacencyPairSecondTurn>();
					for(String l : map.keySet()){
						list.add(new AdjacencyPairSecondTurn(l, map.get(l)));
					}
					secondTurns.add(list);
				}
				
				dao.addAdjacencyPair(firstTurns, secondTurns, cList);
				dc.commit();
				setResponsePage(new IndexPage());
			}
		});
		
		form.add(new Link("cancel"){
			@Override
			public void onClick() {
				setResponsePage(new IndexPage());
			}
		});
		
		form.add(new AjaxButton("addAnswerGroupLink"){
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				WebMarkupContainer secondTurnGroupContainer = new WebMarkupContainer(groupRepeater.newChildId());
				groupRepeater.add(secondTurnGroupContainer);
				
				RepeatingView rv = new RepeatingView("secondTurnRepeater");
				secondTurnGroupContainer.add(rv);
				WebMarkupContainer secondTurnContainer = new WebMarkupContainer(rv.newChildId());
				try {
					secondTurnContainer.add(new AnswerFormPanel(
							"secondTurn", 0, new ArrayList<AdjacencyPairSecondTurn>(), (AjaxEventChain)form.getPage()));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvalidLanguageTagException e) {
					e.printStackTrace();
				}
				rv.add(secondTurnContainer);
				target.addComponent(secondTurnRewriteContainer);
			}
		});
	}
	
	@Override
	public void delete(AjaxRequestTarget target, MarkupContainer eventBody) {
		WebMarkupContainer removeBody = null;
		for(int i = 0; i < groupRepeater.size(); i++){
			removeBody = (WebMarkupContainer) groupRepeater.get(i);
			if(removeBody.contains(eventBody, true)){
				break;
			}
		}
		groupRepeater.remove(removeBody);
		target.addComponent(secondTurnRewriteContainer);
	}
	
	private List<Map<String, String>> getSecondTurnValue(){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for(int i = 0; i < groupRepeater.size(); i++){
			WebMarkupContainer wmc = (WebMarkupContainer) groupRepeater.get(i);
			RepeatingView rv = (RepeatingView)wmc.get("secondTurnRepeater");
			for(int j = 0; j < rv.size(); j++){
				WebMarkupContainer wmc2 = (WebMarkupContainer) rv.get(j);
				AnswerFormPanel answerPanel = (AnswerFormPanel)wmc2.get("secondTurn");
				list.add(answerPanel.getValue());
			}
		}
		return list;
	}
	
	private Form form;
	
	private QuestionFormPanel questionPanel;
	
	private WebMarkupContainer secondTurnRewriteContainer;
	private RepeatingView groupRepeater;
	private CheckBoxMultipleChoice<Category> category;
	
}
