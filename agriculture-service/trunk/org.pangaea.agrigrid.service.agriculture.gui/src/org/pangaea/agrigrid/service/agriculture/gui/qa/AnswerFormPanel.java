package org.pangaea.agrigrid.service.agriculture.gui.qa;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.pages.InternalErrorPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurn;
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.common.LanguageChoice;

public class AnswerFormPanel extends Panel {
	private static final long serialVersionUID = 1L;
	public AnswerFormPanel(
			String panelId, long groupId, List<AdjacencyPairSecondTurn> list, AjaxEventChain chain)
	throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException
	{
		super(panelId);
		this.chain = chain;
		this.groupId = groupId;
		rewriteWrapper = new WebMarkupContainer("answerRewriteContainer");
		rewriteWrapper.setOutputMarkupId(true);
		add(rewriteWrapper);
		rv = new RepeatingView("answerRepeater");
		rewriteWrapper.add(rv);
		for(AdjacencyPairSecondTurn apst : list){
			AnswerFormContainer afc = new AnswerFormContainer(
					rv.newChildId(), new Language(apst.getLanguage()), apst.getText());
			afc.toggleEnable(true);
			rv.add(afc);
		}
		rv.add(new AnswerFormContainer(rv.newChildId(), null, ""));
	}
	
	public Long getGroupId(){
		return groupId;
	}
	
	public Map<String, String> getValue() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for(int i = 0; i < rv.size(); i++){
			AnswerFormContainer afc = (AnswerFormContainer) rv.get(i);
			Pair<Language, String> value = afc.getValue();
			if(value == null){
				continue;
			}
			map.put(value.getFirst().getCode(), value.getSecond());
		}
		return map;
	}
	
	public void displayComponents(boolean isOnly){
		for(int i = 0; i < rv.size(); i++){
			AnswerFormContainer afc = (AnswerFormContainer) rv.get(i);
			afc.toggleEnable(true, isOnly);
		}
	}
	
	private void deleteOwn(AjaxRequestTarget target){
		chain.delete(target, this);
	}
	
	private class AnswerFormContainer extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;
		public AnswerFormContainer(String panelId, Language lang, String answer)
		throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException
		{
			super(panelId);
			final List<Language> supported = AgricultureAdminApplication.getApplication().getSupportedLanguage();
			if(lang == null){
				add(lc = new LanguageChoice("answerLang", supported){
					protected Object getNoSelectionValue() {return NO_SELECTION_VALUE;};
				});
			}else{
				add(lc = new LanguageChoice("answerLang", supported, lang));
			}
//			lc.setHasNoSelection(true);
			lc.add(new AjaxFormSubmitBehavior("onchange"){
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					if(delete.isVisible()){
						return;
					}
					toggleEnable(true);
					try {
						AnswerFormContainer afc = new AnswerFormContainer(rv.newChildId(), null, "");
						rv.add(afc);
						afc.toggleEnable(false);
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
					target.addComponent(rewriteWrapper);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					
				}
			});
			add(ta = new TextArea<String>("answer", new Model<String>(answer)));
			add(delete = new AjaxButton("deleteAnswerLink") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					if(1 < rv.size()) {
						rv.remove(getParent());
						
						if(rv.size() == 1) {
							deleteOwn(target);
						} else {
							target.addComponent(rewriteWrapper);
						}
					}
				}
			});
			ta.setOutputMarkupPlaceholderTag(true);
			delete.setOutputMarkupPlaceholderTag(true);
			toggleEnable(false);
		}
		
		public Pair<Language, String> getValue(){
			if(lc.getModelObject() != null && ta.getModelObject() != null){
				return new Pair<Language, String>(lc.getModelObject(), ta.getModelObject());
			}
			return null;
		}
		
		public void toggleEnable(boolean isEnable){
			toggleEnable(isEnable, false);
		}
		
		public void toggleEnable(boolean isEnable, boolean isOnly){
			ta.setVisible(isEnable);
			delete.setVisible(isEnable == ! isOnly);
		}
		private AjaxButton delete;
		private LanguageChoice lc;
		private TextArea<String> ta;
	}
	
	private long groupId;
	private AjaxEventChain chain;
	private RepeatingView rv;
	private WebMarkupContainer rewriteWrapper;
}
