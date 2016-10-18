package org.pangaea.agrigrid.service.agriculture.image.dao;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.language.Language;
import junit.framework.TestCase;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateCategoryDao;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateImageDao;
import org.pangaea.agrigrid.service.agriculture.dao.MatchingMethod;
import org.pangaea.agrigrid.service.agriculture.dao.Order;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Image;

public class ImageDaoTest extends TestCase {
	public void testAdd() throws Exception{
		InputStream body = getClass().getResourceAsStream("infrastructure_ja.gif");
		try{
			Image image = imgDao.addImage(
					"infrastructure_ja.gif", "", ""
					, new Language[]{en, ja}
					, new String[]{"test image", "テストイメージ"}
					, body
					);
			imgDao.deleteImage(image.getImageId());
		} catch(HibernateException e){
			if(e.getCause() instanceof SQLException){
				e.printStackTrace();
				throw ((SQLException)e.getCause()).getNextException();
			}
		}
	}

	public void testSearch() throws Exception {
		InputStream body = getClass().getResourceAsStream("infrastructure_ja.gif");
		try{
			Image image = imgDao.addImage(
					"infrastructure_ja.gif", "", ""
					, new Language[]{en, ja}, new String[]{"test image", "テストイメージ"}
					, body
					);
			try{
				List<Image> result = imgDao.searchImages(
						"test", en, MatchingMethod.PARTIAL, new String[]{}, new Order[]{});
				assertEquals(1, result.size());
				assertEquals(image.getImageId(), result.get(0).getImageId());
			} finally{
				imgDao.deleteImage(image.getImageId());
			}
		} catch(HibernateException e){
			if(e.getCause() instanceof SQLException){
				e.printStackTrace();
				throw ((SQLException)e.getCause()).getNextException();
			}
		}
	}

	public void testSearchByCategory() throws Exception {
		try{
			Category cat = catDao.addCategory("cat001"
					, new Language[]{en, ja}
					, new String[]{"Category 001", "カテゴリ001"}
					);
			Image image = imgDao.addImage(
					"infrastructure_ja.gif", "", ""
					, new Language[]{en, ja}, new String[]{"test image", "テストイメージ"}
					, getClass().getResourceAsStream("infrastructure_ja.gif")
					);
			image.getCategories().put(cat.getCategoryId(), cat);
			Image image2 = imgDao.addImage(
					"LangridProject_e.gif", "", ""
					, new Language[]{en, ja}, new String[]{"test image2", "テストイメージ2"}
					, getClass().getResourceAsStream("LangridProject_e.gif")
					);
			try{
				List<Image> result = imgDao.searchImages(
						"test", en, MatchingMethod.PARTIAL, new String[]{"cat001"}, new Order[]{});
				assertEquals(1, result.size());
				assertEquals(image.getImageId(), result.get(0).getImageId());
			} finally{
				catDao.deleteCategory(cat.getCategoryId());
				imgDao.deleteImage(image.getImageId());
				imgDao.deleteImage(image2.getImageId());
			}
		} catch(HibernateException e){
			if(e.getCause() instanceof SQLException){
				e.printStackTrace();
				throw ((SQLException)e.getCause()).getNextException();
			}
		}
	}
	
	public void testManageThumbnail() throws Exception {
		InputStream body = getClass().getResourceAsStream("infrastructure_ja.gif");
		Image image = imgDao.addImage(
				"infrastructure_ja.gif", "", ""
				, new Language[]{en, ja}
				, new String[]{"test image", "テストイメージ"}
				, body
				);
	
		byte[] thumbnail = StreamUtil.readAsBytes(getClass().getResourceAsStream("infrastructure_ja.gif"));
		imgDao.setThumbnail(image.getImageId(), thumbnail);

		InputStream is = imgDao.getThumbnail(image.getImageId()).getBinaryStream();
		try {
			byte[] thumb = StreamUtil.readAsBytes(is);
			assertNotNull(thumb);
			assertEquals(thumbnail.length, thumb.length);
		} finally {
			if(is != null){
				is.close();
			}
			imgDao.deleteImage(image.getImageId());
		}
	}

	@Override
	protected void setUp() throws Exception {
		session = factory.getContext().getSession();
		session.beginTransaction();
	}

	@Override
	protected void tearDown() throws Exception {
		session.getTransaction().commit();
	}

	private DaoFactory factory = DaoFactory.createInstance();
	private HibernateCategoryDao catDao = factory.getCategoryDao();
	private HibernateImageDao imgDao = factory.getImageDao();
	private Session session;
}
