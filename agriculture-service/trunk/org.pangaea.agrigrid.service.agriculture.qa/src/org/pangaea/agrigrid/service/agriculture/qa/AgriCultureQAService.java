package org.pangaea.agrigrid.service.agriculture.qa;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.vi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.go.nict.langrid.commons.beanutils.Converter;
import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.service_1_2.util.validator.LanguageValidator;
import jp.go.nict.langrid.service_1_2.util.validator.MatchingMethodValidator;
import jp.go.nict.langrid.service_1_2.util.validator.StringValidator;
import jp.go.nict.langrid.wrapper.ws_1_2.AbstractLanguageService;

import org.hibernate.Session;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateAdjacencyPairDao;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateCategoryDao;
import org.pangaea.agrigrid.service.agriculture.dao.OrderDirection;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPair;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurnGroup;
import org.pangaea.agrigrid.service.api.agriculture.Category;
import org.pangaea.agrigrid.service.api.agriculture.Order;
import org.pangaea.agrigrid.service.api.agriculture.qa.QAEntry;
import org.pangaea.agrigrid.service.api.agriculture.qa.QAService;

/**
 * <#if locale="ja">
 * QAサービスの実装
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 14525 $
 */
public class AgriCultureQAService extends AbstractLanguageService
implements QAService {
	public AgriCultureQAService(ServiceContext serviceContext){
		super(serviceContext);
		init();
	}

	public AgriCultureQAService() {
		init();
	}

	@Override
	public QAEntry[] searchQAs(String text, String textLanguage,
			String matchingMethod, String[] categoryIds, Order[] orders)
			throws InvalidParameterException, ProcessFailedException {
		checkStartupException();
		Language l = new LanguageValidator("language", textLanguage)
			.notNull().trim().notEmpty().getUniqueLanguage(
					getSupportedLanguageCollection());
		String txt = new StringValidator("text", text)
			.notNull().trim().getValue();
		MatchingMethod mm = new MatchingMethodValidator(
				"matchingMethod", matchingMethod
				).notNull().trim().notEmpty().getMatchingMethod(matchingMethods);
		if(categoryIds == null){
			categoryIds = new String[]{};
		}
		if(orders == null){
			orders = new Order[]{};
		}
		
		processStart();
		try{
			acquireSemaphore();
			try{
				return doSearchQAs(txt, l, mm, categoryIds, orders);
			} finally{
				releaseSemaphore();
			}
		} finally{
			processEnd();
		}
	}

	@Override
	public QAEntry[] getQAs(String qaId, String[] languages)
			throws InvalidParameterException, ProcessFailedException {
		checkStartupException();
		String[] l = null;
		try{
			l = ArrayUtil.collect(languages, String.class, new Transformer<String, String>() {
				public String transform(String value) throws TransformationException {
					try{
						return new LanguageValidator("language", value)
							.notNull().trim().notEmpty().getUniqueLanguage(
								getSupportedLanguageCollection()).getCode();
					} catch(InvalidParameterException e){
						throw new TransformationException(e);
					}
				};
			});
		} catch(TransformationException e){
			if(e.getCause() instanceof InvalidParameterException){
				throw (InvalidParameterException)e.getCause();
			}
		}
		
		processStart();
		try{
			acquireSemaphore();
			try{
				return doGetQAs(qaId, l);
			} finally{
				releaseSemaphore();
			}
		} finally{
			processEnd();
		}
	}

	@Override
	public String[] getSupportedLanguages() throws ProcessFailedException {
		try {
			return super.getSupportedLanguages();
		} catch (NoValidEndpointsException e) {
			throw new ProcessFailedException(e);
		} catch (ServerBusyException e) {
			throw new ProcessFailedException(e);
		} catch (ServiceNotActiveException e) {
			throw new ProcessFailedException(e);
		} catch (ServiceNotFoundException e) {
			throw new ProcessFailedException(e);
		}
	}

	@Override
	public String[] getCategoryNames(String categoryId, String[] languages)
	throws InvalidParameterException, ProcessFailedException {
		DaoFactory factory = DaoFactory.createInstance();
		HibernateCategoryDao dao = factory.getCategoryDao();
		Session s = factory.getContext().getSession();
		s.beginTransaction();
		try {
			org.pangaea.agrigrid.service.agriculture.dao.entity.Category category
					= dao.getCategory(categoryId);
			String[] ret = new String[languages.length];
			for(int i = 0; i < languages.length; i++){
				ret[i] = category.getTexts().get(languages[i]);
			}
			s.getTransaction().commit();
			return ret;
		} catch(EntityNotFoundException e){
			s.getTransaction().rollback();
			throw new ProcessFailedException(e);
		} catch(RuntimeException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		}
	}
	
	@Override
	public Category[] listAllCategories(String language)
	throws ProcessFailedException {
		return doListAllCategories(language);
	}
	
//	@Override
//	public void setThumbnail(String imageId, byte[] body)
//	throws InvalidParameterException, ProcessFailedException 
//	{
//		DaoFactory factory = DaoFactory.createInstance();
//		HibernateImageDao dao = factory.getImageDao();
//		Session s = factory.getContext().getSession();
//		s.beginTransaction();
//		try {
//			dao.setThumbnail(Long.parseLong(imageId), body);
//			s.getTransaction().commit();
//		} catch(RuntimeException e) {
//			s.getTransaction().rollback();
//			e.printStackTrace();
//			throw new ProcessFailedException(e);
//		} catch (EntityNotFoundException e) {
//			s.getTransaction().rollback();
//			e.printStackTrace();
//			throw new ProcessFailedException(e);
//		}
//	}

	private void init(){
		setSupportedLanguageCollection(Arrays.asList(ja, en, vi));
		matchingMethods.add(MatchingMethod.COMPLETE);
		matchingMethods.add(MatchingMethod.PARTIAL);
		matchingMethods.add(MatchingMethod.PREFIX);
		matchingMethods.add(MatchingMethod.SUFFIX);
	}

	private QAEntry[] doGetQAs(String qaId, String[] languages)
	throws ProcessFailedException{
		DaoFactory factory = DaoFactory.createInstance();
		HibernateAdjacencyPairDao dao = factory.getAdjacencyPairDao();
		Session s = factory.getContext().getSession();
		s.beginTransaction();
		try {
			AdjacencyPair p = dao.get(Long.parseLong(qaId));
			QAEntry[] ret = new QAEntry[languages.length];
			int i = 0;
			for(String lang : languages){
				ret[i++] = new QAEntry(
						Long.toString(p.getAdjacencyPairId())
						, lang
						, p.getFirstTurns().get(lang).getText()
						, convertSecondTurnGroup(p.getSecondTurnGroup(), lang)
						, convertCategory(p.getCategories().values(), lang)
						, p.getCreatedAt()
						, p.getUpdatedAt()
						);
			}
			s.getTransaction().commit();
			return ret;
		} catch(EntityNotFoundException e){
			s.getTransaction().rollback();
			throw new ProcessFailedException(e);
		} catch(RuntimeException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		}
	}

	private QAEntry[] doSearchQAs(String text, Language textLanguage,
			MatchingMethod matchingMethod, String[] categoryIds, Order[] orders)
	throws ProcessFailedException{
		DaoFactory factory = DaoFactory.createInstance();
		HibernateAdjacencyPairDao dao = factory.getAdjacencyPairDao();
		Session s = factory.getContext().getSession();
		s.beginTransaction();
		try {
			org.pangaea.agrigrid.service.agriculture.dao.Order[] o
				= new org.pangaea.agrigrid.service.agriculture.dao.Order[orders.length];
			for(int i = 0; i < orders.length; i++){
				String fieldName = orders[i].getFieldName();
				if(fieldName.equals("qaId")){
					fieldName = "adjacencyPairId";
				}
				o[i] = new org.pangaea.agrigrid.service.agriculture.dao.Order(
						fieldName, OrderDirection.valueOf(orders[i].getDirection().toUpperCase())
						);
			}
			List<AdjacencyPair> list = dao.searchAdjacencyPairs(
					text, textLanguage.getCode()
					, converter.convert(
							matchingMethod
							, org.pangaea.agrigrid.service.agriculture.dao.MatchingMethod.class)
					, categoryIds
					, o);
			List<QAEntry> result = new ArrayList<QAEntry>();
			String lang = textLanguage.getCode();
			for(AdjacencyPair p : list) {
				result.add(new QAEntry(
						Long.toString(p.getAdjacencyPairId())
						, lang
						, p.getFirstTurns().get(lang).getText()
						, convertSecondTurnGroup(p.getSecondTurnGroup(), lang)
						, convertCategory(p.getCategories().values(), lang)
						, p.getCreatedAt()
						, p.getUpdatedAt()
				));
			}
			s.getTransaction().commit();
			return result.toArray(new QAEntry[]{});
		} catch(RuntimeException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		}
	}

	private Category[] doListAllCategories(String language)
	throws ProcessFailedException{
		DaoFactory factory = DaoFactory.createInstance();
		HibernateCategoryDao dao = factory.getCategoryDao();
		Session s = factory.getContext().getSession();
		s.beginTransaction();
		try {
			List<org.pangaea.agrigrid.service.agriculture.dao.entity.Category> list
					= dao.listAllCategories();
			Category[] ret = convertCategory(list, language);
			s.getTransaction().commit();
			return ret;
		} catch(RuntimeException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		}
	}

	private Category[] convertCategory(
			Collection<org.pangaea.agrigrid.service.agriculture.dao.entity.Category> set
			, String language){
		Category[] converted = new Category[set.size()];
		int i = 0;
		for(org.pangaea.agrigrid.service.agriculture.dao.entity.Category ic : set){
			converted[i++] = new Category(
					ic.getCategoryId(), ic.getTexts().get(language));
		}
		return converted;
	}


	private String[] convertSecondTurnGroup(
			List<AdjacencyPairSecondTurnGroup> group, String language){
		String[] ret = new String[group.size()];
		for(int i = 0; i < ret.length; i++){
			ret[i] = group.get(i).getSecondTurns().get(language).getText();
		}
		return ret;
	}

	private static Converter converter = new Converter();

	private Set<MatchingMethod> matchingMethods = new HashSet<MatchingMethod>();
}
