package org.pangaea.agrigrid.service.agriculture.image;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.vi;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.go.nict.langrid.commons.beanutils.Converter;
import jp.go.nict.langrid.commons.io.StreamUtil;
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
import org.pangaea.agrigrid.service.agriculture.dao.HibernateCategoryDao;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateImageDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Image;
import org.pangaea.agrigrid.service.agriculture.dao.entity.ImageCaption;
import org.pangaea.agrigrid.service.agriculture.image.util.ImageUtil;
import org.pangaea.agrigrid.service.api.agriculture.Caption;
import org.pangaea.agrigrid.service.api.agriculture.Category;
import org.pangaea.agrigrid.service.api.agriculture.Order;
import org.pangaea.agrigrid.service.api.agriculture.image.ImageEntry;
import org.pangaea.agrigrid.service.api.agriculture.image.ImageService;

/**
 * <#if locale="ja">
 * 画像サービスの実装
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 14518 $
 */
public class AgriCultureImageService extends AbstractLanguageService
implements ImageService {
	public AgriCultureImageService(ServiceContext serviceContext){
		super(serviceContext);
		init();
	}

	public AgriCultureImageService() {
		init();
	}

	@Override
	public ImageEntry[] searchImages(String text, String textLanguage,
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
				return doSearchImages(txt, l, mm, categoryIds, orders);
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
	public byte[] getThumbnail(String imageId)
	throws InvalidParameterException, ProcessFailedException {
		DaoFactory factory = DaoFactory.createInstance();
		HibernateImageDao dao = factory.getImageDao();
		Session s = factory.getContext().getSession();
		s.beginTransaction();
		try {
			byte[] ret = StreamUtil.readAsBytes(ImageUtil.resize(
					dao.getImageBinary(Long.parseLong(imageId)).getBinaryStream()
					, 128, "JPG"
					));
			s.getTransaction().commit();
			return ret;
		} catch(EntityNotFoundException e){
			s.getTransaction().rollback();
			throw new ProcessFailedException(e);
		} catch(SQLException e){
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		} catch(IOException e){
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		} catch(RuntimeException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
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

	private ImageEntry[] doSearchImages(String text, Language textLanguage,
			MatchingMethod matchingMethod, String[] categoryIds, Order[] orders)
	throws ProcessFailedException{
		DaoFactory factory = DaoFactory.createInstance();
		HibernateImageDao dao = factory.getImageDao();
		Session s = factory.getContext().getSession();
		s.beginTransaction();
		try {
			List<Image> list = dao.searchImages(
					text, textLanguage
					, converter.convert(
							matchingMethod
							, org.pangaea.agrigrid.service.agriculture.dao.MatchingMethod.class)
					, categoryIds
					, converter.convert(
							orders
							, org.pangaea.agrigrid.service.agriculture.dao.Order[].class)
							);
			List<ImageEntry> result = new ArrayList<ImageEntry>();
			for(Image i : list) {
				result.add(new ImageEntry(
						Long.toString(i.getImageId()), i.getFileName()
						, getServiceContext().getRequestUrl().toString() + "/download?imageId=" + i.getImageId()
						, convertCaption(i.getCaptions())
						, i.getCopyright(), i.getLicense()
						, convertCategory(i.getCategories().values(), textLanguage.getCode())
						, i.getCreatedAt(), i.getUpdatedAt()
				));
			}
			s.getTransaction().commit();
			return result.toArray(new ImageEntry[]{});
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
	
	private Caption[] convertCaption(Map<String, ImageCaption> set){
		Collection<ImageCaption> captions = set.values();
		Caption[] converted = new Caption[captions.size()];
		int i = 0;
		for(ImageCaption c : captions){
			converted[i++] = new Caption(c.getLanguage(), c.getText());
		}
		return converted;
	}

	private static Converter converter = new Converter();

	private Set<MatchingMethod> matchingMethods = new HashSet<MatchingMethod>();
}
