package org.pangaea.agrigrid.service.agriculture.gui.common;

import java.util.List;
import java.util.Locale;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.WildcardListModel;
import org.pangaea.agrigrid.service.agriculture.gui.util.LanguageUtil;

public class LanguageChoice extends DropDownChoice<Language> {
	public LanguageChoice(String componentId)
	throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException{
		this(componentId, LanguageUtil.getLanguageList(locale));
	}

	public LanguageChoice(String componentId, Language language)
	throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException{
		this(componentId, LanguageUtil.getLanguageList(locale), language);
	}

	public LanguageChoice(String componentId, List<Language> languages){
		super(componentId, new Model<Language>(),
				new WildcardListModel<Language>());
		setChoices(languages);
		setChoiceRenderer(new LanguageChoiceRenderer());
	}

	public LanguageChoice(String componentId, List<Language> languages, Language language){
		this(componentId, languages);
		setDefaultModelObject(language);
	}
	
	@Override
	protected Object getNoSelectionValue() {
		return 1;
	}

	public class LanguageChoiceRenderer implements IChoiceRenderer<Language> {
		public Object getDisplayValue(Language object) {
			if (object == null) {
				return "";
			}
			return wrapForHTMLView(object.getLocalizedName(locale));
		}
	
		public String getIdValue(Language object, int index) {
			if (object == null) {
				return "";
			}
			return object.getCode();
		}
	}

	private String wrapForHTMLView(String code) {
		return code.split(";")[0];
	}
	
	private static Locale locale = Locale.ENGLISH;
}
