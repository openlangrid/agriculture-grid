package org.pangaea.agrigrid.service.agriculture.gui.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import jp.go.nict.langrid.language.ISO639_1LanguageTags;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

public class LanguageUtil {
	public static List<Language> getLanguageList(Locale locale)
	throws IllegalArgumentException, IllegalAccessException, InvalidLanguageTagException
	{
		List<Language> list = new ArrayList<Language>();
		Map<String, Language> map = new TreeMap<String, Language>();
		for (Field f : ISO639_1LanguageTags.class.getDeclaredFields()) {
			if (f.getAnnotation(Deprecated.class) != null) {
				continue;
			}
			Language l = (Language) f.get(null);
			if (l.getLocalizedName(locale).equals("")) {
				continue;
			}
			map.put(l.getLocalizedName(locale), l);
		}
		for (Language sorted : map.values()) {
			list.add(sorted);
		}
		return list;
	}
}
