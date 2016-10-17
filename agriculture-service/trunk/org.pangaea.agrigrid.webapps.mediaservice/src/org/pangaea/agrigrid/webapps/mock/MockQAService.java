package org.pangaea.agrigrid.webapps.mock;

import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;

import org.pangaea.agrigrid.service.api.agriculture.Category;
import org.pangaea.agrigrid.service.api.agriculture.Order;
import org.pangaea.agrigrid.service.api.agriculture.qa.QAEntry;
import org.pangaea.agrigrid.service.api.agriculture.qa.QAService;

public class MockQAService
implements QAService{
	@Override
	public Category[] listAllCategories(String language)
			throws ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getCategoryNames(String categoryId, String[] languages)
			throws InvalidParameterException, ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QAEntry[] searchQAs(String text, String textLang,
			String matchingMethod, String[] categoryIds, Order[] orders)
			throws InvalidParameterException, ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QAEntry[] getQAs(String qaId, String[] languages)
			throws InvalidParameterException, ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSupportedLanguages() throws ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}
}
