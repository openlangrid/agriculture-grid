package org.pangaea.agrigrid.webapps.mock;

import jp.go.nict.langrid.service_1_2.Category;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.compositionassistance.CompositionAssistanceService;
import jp.go.nict.langrid.service_1_2.compositionassistance.Template;
import jp.go.nict.langrid.service_1_2.templateparalleltext.BoundChoiceParameter;
import jp.go.nict.langrid.service_1_2.templateparalleltext.BoundValueParameter;

public class MockCompositionAssistanceService
implements CompositionAssistanceService{
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
	public Template[] listRootTemplates(String language, String[] categoryIds)
			throws InvalidParameterException, ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Template[] getChildTemplates(String parentId, String langauge,
			String[] categoryIds) throws InvalidParameterException,
			ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Template[] listRootAndChildTemplates(String language,
			String[] categoryIds) throws InvalidParameterException,
			ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Template[] getChildTemplatesOfParents(String[] parentIds,
			String langauge, String[] categoryIds)
			throws InvalidParameterException, ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateSentense(String language, String templateId,
			BoundChoiceParameter[] boundChoiceParameters,
			BoundValueParameter[] boundValueParameters)
			throws InvalidParameterException, ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
