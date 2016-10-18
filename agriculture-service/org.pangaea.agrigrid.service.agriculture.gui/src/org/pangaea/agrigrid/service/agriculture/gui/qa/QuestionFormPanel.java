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
import org.pangaea.agrigrid.service.agriculture.gui.AgricultureAdminApplication;
import org.pangaea.agrigrid.service.agriculture.gui.common.LanguageChoice;

public class QuestionFormPanel extends Panel {
	public QuestionFormPanel(String panelId, Map<Language, String> questionMap)
	throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException
	{
		super(panelId);
		rewriteWrapper = new WebMarkupContainer("questionRewriteContainer");
		rewriteWrapper.setOutputMarkupId(true);
		add(rewriteWrapper);
		rv = new RepeatingView("questionRepeater");
		rewriteWrapper.add(rv);
		for(Language lang : questionMap.keySet()){
			QuestionFormContainer qfc = new QuestionFormContainer(rv.newChildId(), lang, questionMap.get(lang));
			qfc.toggleEnable(true);
			rv.add(qfc);
		}
		rv.add(new QuestionFormContainer(rv.newChildId(), null, ""));
	}

	public Map<String, String> getValue() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for(int i = 0; i < rv.size(); i++){
			QuestionFormContainer wmc = (QuestionFormContainer) rv.get(i);
			Pair<Language, String> value = wmc.getValue();
			if(value == null){
				continue;
			}
			map.put(value.getFirst().getCode(), value.getSecond());
		}
		return map;
	}

	private class QuestionFormContainer extends WebMarkupContainer {
		public QuestionFormContainer(String panelId, Language lang, String question)
		throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException
		{
			super(panelId);
			final List<Language> supported = AgricultureAdminApplication.getApplication().getSupportedLanguage();
			if(lang == null){
				add(lc = new LanguageChoice("questionLang", supported){
					@Override
					protected Object getNoSelectionValue() {return NO_SELECTION_VALUE;}
				});
			}else{
				add(lc = new LanguageChoice("questionLang", supported, lang));
			}
			lc.add(new AjaxFormSubmitBehavior("onchange"){
				@Override
				protected void onError(AjaxRequestTarget target) {}
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					if(delete.isVisible()){
						return;
					}
					toggleEnable(true);
					try {
						QuestionFormContainer afc = new QuestionFormContainer(rv.newChildId(), null, "");
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
			});
			add(ta = new TextArea<String>("question", new Model<String>(question)));
			add(delete = new AjaxButton("deleteQuestionLink") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					if(1 < rv.size()){
						rv.remove(getParent());
						target.addComponent(rewriteWrapper);
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
			ta.setVisible(isEnable);
			delete.setVisible(isEnable);
		}
		private AjaxButton delete;
		private LanguageChoice lc;
		private TextArea<String> ta;
	}

	private RepeatingView rv;
	private WebMarkupContainer rewriteWrapper;
}
