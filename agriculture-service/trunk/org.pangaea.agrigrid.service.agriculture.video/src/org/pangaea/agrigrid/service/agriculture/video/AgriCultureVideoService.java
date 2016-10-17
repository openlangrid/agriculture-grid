package org.pangaea.agrigrid.service.agriculture.video;

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
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;
import org.pangaea.agrigrid.service.agriculture.dao.entity.VideoCaption;
import org.pangaea.agrigrid.service.api.agriculture.Caption;
import org.pangaea.agrigrid.service.api.agriculture.Category;
import org.pangaea.agrigrid.service.api.agriculture.Order;
import org.pangaea.agrigrid.service.api.agriculture.video.Subtitle;
import org.pangaea.agrigrid.service.api.agriculture.video.VideoEntry;
import org.pangaea.agrigrid.service.api.agriculture.video.VideoService;

public class AgriCultureVideoService extends AbstractLanguageService
implements VideoService {
	public AgriCultureVideoService() {
		init();
	}
	
	public AgriCultureVideoService(ServiceContext context) {
		super(context);
		init();
	}
	
	@Override
	public VideoEntry[] searchVideos(String text, String textLanguage,
			String matchingMethod, String[] categoryIds, Order[] orders)
	throws InvalidParameterException, ProcessFailedException {
		checkStartupException();
		Language l = new LanguageValidator("language", textLanguage)
			.notNull().trim().notEmpty().getUniqueLanguage(getSupportedLanguageCollection());
		String txt = new StringValidator("text", text).notNull().trim().getValue();
		MatchingMethod mm = new MatchingMethodValidator(
					"matchingMethod", matchingMethod
				).notNull().trim().notEmpty().getMatchingMethod(matchingMethods);

		processStart();
		try{
			acquireSemaphore();
			try{
				return doSearchVideos(txt, l, mm, categoryIds, orders);
			} finally{
				releaseSemaphore();
			}
		} finally{
			processEnd();
		}
	}
	
	@Override
	public Category[] listAllCategories(String language)
	throws ProcessFailedException {
		DaoFactory factory = DaoFactory.createInstance();
		HibernateCategoryDao dao = factory.getCategoryDao();
		Session s = factory.getContext().getSession();
		s.beginTransaction();
		try {
			List<org.pangaea.agrigrid.service.agriculture.dao.entity.Category> list
					= dao.listAllCategories();
			
			Category[] ret = new Category[list.size()];
			int i = 0;
			for(org.pangaea.agrigrid.service.agriculture.dao.entity.Category ic : list){
				ret[i++] = new Category(
						ic.getCategoryId(), ic.getTexts().get(language)
				);
			}
			
			s.getTransaction().commit();
			return ret;
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
	public byte[] getThumbnail(String videoId)
	throws InvalidParameterException, ProcessFailedException {
		DaoFactory factory = DaoFactory.createInstance();
		HibernateVideoDao dao = factory.getVideoDao();
		Session s = factory.getContext().getSession();
		s.beginTransaction();
		try {
			byte[] ret = StreamUtil.readAsBytes(
					dao.getThumbnail(Long.parseLong(videoId)).getBinaryStream());
			s.getTransaction().commit();
			return ret;
		} catch(RuntimeException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		} catch (EntityNotFoundException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		} catch (SQLException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		} catch (IOException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		}
	}

	@Override
	public Subtitle[] getSubtitles(String videoId, String language)
	throws InvalidParameterException, ProcessFailedException {
		Language l = new LanguageValidator("language", language)
			.notNull().trim().notEmpty().getUniqueLanguage(getSupportedLanguageCollection());
		DaoFactory factory = DaoFactory.createInstance();
		HibernateVideoDao dao = factory.getVideoDao();
		Session s = factory.getContext().getSession();
		s.beginTransaction();
		try {
			Set<org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle> result
				= dao.getSubtitles(Long.parseLong(videoId), l);
			Subtitle[] ret = new Subtitle[result.size()];
			int i = 0;
			for(org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle st : result){
				ret[i++] = new Subtitle(st.getStartMillis(), st.getEndMillis(), st.getText());
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
	
	public void setThumbnail(String videoId, byte[] body)
	throws InvalidParameterException, ProcessFailedException
	{
		DaoFactory factory = DaoFactory.createInstance();
		HibernateVideoDao dao = factory.getVideoDao();
		Session s = factory.getContext().getSession();
		s.beginTransaction();
		try {
			dao.setThumbnail(Long.parseLong(videoId), body);
			s.getTransaction().commit();
		} catch(RuntimeException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		} catch (EntityNotFoundException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		} catch (IOException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		}
	}

	private VideoEntry[] doSearchVideos(String text, Language textLanguage,
			MatchingMethod matchingMethod, String[] categoryIds, Order[] orders)
	throws ProcessFailedException{
		DaoFactory factory = DaoFactory.createInstance();
		HibernateVideoDao dao = factory.getVideoDao();
		Session s = factory.getContext().getSession();
		s.beginTransaction();
		try {
			List<Video> list = dao.searchVideos(text, textLanguage
					, converter.convert(
							matchingMethod
							, org.pangaea.agrigrid.service.agriculture.dao.MatchingMethod.class
							)
					, categoryIds
					, converter.convert(
							orders
							, org.pangaea.agrigrid.service.agriculture.dao.Order[].class
							));
			List<VideoEntry> result = new ArrayList<VideoEntry>();
			for(Video v : list) {
				result.add(new VideoEntry(
						Long.toString(v.getVideoId()), v.getFileName()
						, getServiceContext().getRequestUrl().toString() + "/download?videoId=" + v.getVideoId()
						, convertCaption(v.getCaptions())
						, convertSubtitleLanguages(v.getSubtitles())
						, v.getCopyright(), v.getLicense()
						, convertCategory(v.getCategories().values(), textLanguage.getCode())
						, v.getCreatedAt(), v.getUpdatedAt()
				));
			}
			s.getTransaction().commit();
			return result.toArray(new VideoEntry[]{});
		} catch(RuntimeException e) {
			s.getTransaction().rollback();
			e.printStackTrace();
			throw new ProcessFailedException(e);
		}
	}
	
	private String[] convertSubtitleLanguages(
			Set<org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle> set)
	{
		String[] converted = new String[set.size()];
		int i = 0;
		for(org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle s : set){
			converted[i++] = s.getLanguage();
		}
		return converted;
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
	
	private Caption[] convertCaption(Map<String, VideoCaption> set){
		Collection<VideoCaption> captions = set.values();
		Caption[] converted = new Caption[captions.size()];
		int i = 0;
		for(VideoCaption c : captions){
			converted[i++] = new Caption(c.getLanguage(), c.getText());
		}
		return converted;
	}
	
	private void init(){
		setSupportedLanguageCollection(Arrays.asList(ja, en, vi));
		matchingMethods.add(MatchingMethod.COMPLETE);
		matchingMethods.add(MatchingMethod.PARTIAL);
		matchingMethods.add(MatchingMethod.PREFIX);
		matchingMethods.add(MatchingMethod.SUFFIX);
	}

	private Set<MatchingMethod> matchingMethods = new HashSet<MatchingMethod>();
	private static Converter converter = new Converter();
}
