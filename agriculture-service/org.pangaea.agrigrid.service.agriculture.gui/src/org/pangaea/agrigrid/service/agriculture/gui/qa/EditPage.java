package org.pangaea.agrigrid.service.agriculture.gui.qa;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.pages.InternalErrorPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.WildcardListModel;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateAdjacencyPairDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPair;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurnGroup;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

/**
 * <#if locale="ja">
 * Q&A編集ページ
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author $Author: Masaaki Kamiya $
 * @version $Revision: 14538 $
 */
public class EditPage extends WebPage implements AjaxEventChain {
	public EditPage(long qaId) {
		this.editQaId = qaId;
		add(new FeedbackPanel("feedback"));
		form = new Form("form");
		form.setOutputMarkupId(true);
		add(form);
		secondTurnRewriteContainer = new WebMarkupContainer("secondTurnRewriteContainer");
		secondTurnRewriteContainer.setOutputMarkupId(true);
		form.add(secondTurnRewriteContainer);
		groupRepeater = new RepeatingView("secondTurnGroupRepeater");
		secondTurnRewriteContainer.add(groupRepeater);

		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		dc.beginTransaction();
		AdjacencyPair ap = null;
		ArrayList<Category> selectedCList = new ArrayList<Category>();
		Map<Long, Map<String,AdjacencyPairSecondTurn>> turnMap = new LinkedHashMap<Long, Map<String, AdjacencyPairSecondTurn>>();
		try {
			ap = f.getAdjacencyPairDao().get(qaId);
			for(Category c : ap.getCategories().values()){
				selectedCList.add(c);
			}
			for(AdjacencyPairSecondTurnGroup g : ap.getSecondTurnGroup()){
				turnMap.put(g.getGroupId(), g.getSecondTurns());
			}
		
			Map<Language, String> q = new LinkedHashMap<Language, String>();
			for(String key : ap.getFirstTurns().keySet()){
				q.put(new Language(key), ap.getFirstTurns().get(key).getText());
			}
			form.add(questionPanel = new QuestionFormPanel("questionForm", q));
			for(Long groupId : turnMap.keySet()){
				List<AdjacencyPairSecondTurn> list = new ArrayList<AdjacencyPairSecondTurn>();
				for(AdjacencyPairSecondTurn apst : turnMap.get(groupId).values()){
					list.add(apst);
				}
				
				WebMarkupContainer secondTurnGroupContainer = new WebMarkupContainer(groupRepeater.newChildId());
				groupRepeater.add(secondTurnGroupContainer);
				RepeatingView rv = new RepeatingView("secondTurnRepeater");
				secondTurnGroupContainer.add(rv);
				WebMarkupContainer secondTurnContainer = new WebMarkupContainer(rv.newChildId());
				AnswerFormPanel afp = new AnswerFormPanel("secondTurn", groupId, list, this);
				secondTurnContainer.add(afp);
				rv.add(secondTurnContainer);
			}
			dc.commit();
		} catch (IllegalArgumentException e) {
			dc.rollback();
			e.printStackTrace();
			throw new RestartResponseException(new InternalErrorPage());
		} catch (IllegalAccessException e) {
			dc.rollback();
			e.printStackTrace();
			throw new RestartResponseException(new InternalErrorPage());
		} catch (InvalidLanguageTagException e) {
			dc.rollback();
			e.printStackTrace();
			throw new RestartResponseException(new InternalErrorPage());
		} catch (EntityNotFoundException e) {
			dc.rollback();
			e.printStackTrace();
			throw new RestartResponseException(new InternalErrorPage());
		}
		
		f = DaoFactory.createInstance();
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
		
		form.add(new Button("edit"){
			@SuppressWarnings("unchecked")
			@Override
			public void onSubmit() {
				DaoFactory f = DaoFactory.createInstance();
				DaoContext dc = f.getContext();
				HibernateAdjacencyPairDao dao= f.getAdjacencyPairDao();
				dc.beginTransaction();
				try {
					dao.update(editQaId, questionPanel.getValue(), getSecondTurnValue(), category.getModelObject());
					dc.commit();
					setResponsePage(new IndexPage());
				} catch (EntityNotFoundException e) {
					dc.rollback();
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				}
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
							"secondTurn", (long)Math.random()
							, new ArrayList<AdjacencyPairSecondTurn>(), (AjaxEventChain)form.getPage()));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
				} catch (InvalidLanguageTagException e) {
					e.printStackTrace();
					throw new RestartResponseException(new InternalErrorPage());
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
	
	private Map<Long, Map<String, String>> getSecondTurnValue(){
		Map<Long, Map<String, String>> map = new LinkedHashMap<Long, Map<String, String>>();
		for(int i = 0; i < groupRepeater.size(); i++){
			WebMarkupContainer wmc = (WebMarkupContainer) groupRepeater.get(i);
			RepeatingView rv = (RepeatingView)wmc.get("secondTurnRepeater");
			for(int j = 0; j < rv.size(); j++){
				WebMarkupContainer wmc2 = (WebMarkupContainer) rv.get(j);
				AnswerFormPanel answerPanel = (AnswerFormPanel)wmc2.get("secondTurn");
				if(map.containsKey(answerPanel.getGroupId())){
					map.get(answerPanel.getGroupId()).putAll(answerPanel.getValue());
				}else{
					map.put(answerPanel.getGroupId(), answerPanel.getValue());
				}
			}
		}
		return map;
	}
	

	private Form form;
	private long editQaId;

	private CheckBoxMultipleChoice<Category> category;
	private QuestionFormPanel questionPanel;
	private WebMarkupContainer secondTurnRewriteContainer;
	private RepeatingView groupRepeater;
}
