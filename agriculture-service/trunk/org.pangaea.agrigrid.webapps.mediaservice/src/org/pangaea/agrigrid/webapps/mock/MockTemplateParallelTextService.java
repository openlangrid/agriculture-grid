package org.pangaea.agrigrid.webapps.mock;

import jp.go.nict.langrid.service_1_2.AccessLimitExceededException;
import jp.go.nict.langrid.service_1_2.Category;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.NoAccessPermissionException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;
import jp.go.nict.langrid.service_1_2.UnsupportedMatchingMethodException;
import jp.go.nict.langrid.service_1_2.templateparalleltext.BoundChoiceParameter;
import jp.go.nict.langrid.service_1_2.templateparalleltext.BoundValueParameter;
import jp.go.nict.langrid.service_1_2.templateparalleltext.Template;
import jp.go.nict.langrid.service_1_2.templateparalleltext.TemplateParallelTextService;

public class MockTemplateParallelTextService implements TemplateParallelTextService{
	@Override
	public Category[] listTemplateCategories(String language)
	throws AccessLimitExceededException, InvalidParameterException,
			NoAccessPermissionException, NoValidEndpointsException,
			ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException,
			UnsupportedLanguageException {
		return new Category[]{
				new Category("cat001", "rice.japonica")	
				, new Category("cat002", "rice.indica")
				, new Category("cat003", "problem.bugs")
				, new Category("cat004", "problem.sick")
				, new Category("cat005", "water.rain")
				, new Category("cat006", "water.irrigation")
		};
	}

	@Override
	public String[] getCategoryNames(String categoryId, String[] languages)
			throws InvalidParameterException, ProcessFailedException {
		return null;
	}

	@Override
	public Template[] searchTemplates(String language, String text,
			String matchingMethod, String[] categoryIds)
	throws AccessLimitExceededException, InvalidParameterException,
			NoAccessPermissionException, NoValidEndpointsException,
			ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException,
			UnsupportedLanguageException, UnsupportedMatchingMethodException{
		return null;
	}

	@Override
	public Template[] getTemplatesByTemplateId(String language,
			String[] templateIds)
	throws AccessLimitExceededException,
			InvalidParameterException, NoAccessPermissionException,
			NoValidEndpointsException, ProcessFailedException,
			ServerBusyException, ServiceNotActiveException,
			ServiceNotFoundException, UnsupportedLanguageException {
		return null;
	}

	@Override
	public String generateSentence(String language, String templateId,
			BoundChoiceParameter[] boundChoiceParameters,
			BoundValueParameter[] boundValueParameters)
			throws AccessLimitExceededException, InvalidParameterException,
			NoAccessPermissionException, NoValidEndpointsException,
			ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException,
			UnsupportedLanguageException {
		return null;
	}
}
