package org.pangaea.agrigrid.service.agriculture.gui;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.vi;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.util.LanguageUtil;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

public class AgricultureAdminApplication extends WebApplication{
	@Override
	public Class<? extends Page> getHomePage() {
		return IndexPage.class;
	}

	public static AgricultureAdminApplication getApplication(){
		return (AgricultureAdminApplication)Application.get();
	}

	public List<Language> getSupportedLanguage(){
		if(supportedLanguages == null){
			String value = getServletContext().getInitParameter("supportedLanguages");
			if(value != null){
				try{
					supportedLanguages = LanguageUtil.decodeLanguageArray(value);
				} catch(InvalidLanguageTagException e){
					Logger.getAnonymousLogger().log(Level.WARNING, "invalid language tag.", e);
				}
			}
			if(supportedLanguages == null){
				supportedLanguages = new Language[]{ja, en, vi};
			}
		}
		return Arrays.asList(supportedLanguages);
	}

	private static Language[] supportedLanguages;
}
