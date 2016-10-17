package org.pangaea.agrigrid.webapps.mock;

import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;

import org.pangaea.agrigrid.service.api.agriculture.Category;
import org.pangaea.agrigrid.service.api.agriculture.Order;
import org.pangaea.agrigrid.service.api.agriculture.video.Subtitle;
import org.pangaea.agrigrid.service.api.agriculture.video.VideoEntry;
import org.pangaea.agrigrid.service.api.agriculture.video.VideoService;

public class MockVideoService
implements VideoService{
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
	public VideoEntry[] searchVideos(String text, String textLanguage,
			String matchingMethod, String[] categoryIds, Order[] orders)
			throws InvalidParameterException, ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Subtitle[] getSubtitles(String videoId, String language)
			throws InvalidParameterException, ProcessFailedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public byte[] getThumbnail(String videoId)
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
