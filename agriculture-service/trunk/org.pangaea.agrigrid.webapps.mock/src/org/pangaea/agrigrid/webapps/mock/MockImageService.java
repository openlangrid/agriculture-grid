package org.pangaea.agrigrid.webapps.mock;

import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;

import org.pangaea.agrigrid.service.api.agriculture.Category;
import org.pangaea.agrigrid.service.api.agriculture.Order;
import org.pangaea.agrigrid.service.api.agriculture.image.ImageEntry;
import org.pangaea.agrigrid.service.api.agriculture.image.ImageService;

public class MockImageService
implements ImageService{
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
	public ImageEntry[] searchImages(String text, String textLanguage,
			String matchingMethod, String[] categoryIds, Order[] orders)
			throws InvalidParameterException, ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getThumbnail(String imageId)
			throws InvalidParameterException, ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSupportedLanguages() throws ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}
	
//	@Override
//	public void setThumbnail(String imageId, byte[] body)
//			throws InvalidParameterException, ProcessFailedException {
//		// TODO Auto-generated method stub
//		
//	}
}
