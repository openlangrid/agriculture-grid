package org.pangaea.agrigrid.service.agriculture.gui.video;

import java.sql.Blob;
import java.sql.SQLException;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.markup.html.pages.InternalErrorPage;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;

public class ThumbnailPopupPage extends WebPage {
	public ThumbnailPopupPage(Long id) {
		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		HibernateVideoDao imgd = f.getVideoDao();
		dc.beginTransaction();
		try {
			Blob b = imgd.getThumbnail(id);
			byte[] image = b.getBytes(1, (int) b.length());
			add(new Image("image", new ImageComponent(image)));
			dc.commit();
		} catch (EntityNotFoundException e) {
			dc.rollback();
			e.printStackTrace();
			throw new RestartResponseException(new InternalErrorPage());
		} catch (SQLException e) {
			dc.rollback();
			e.printStackTrace();
			throw new RestartResponseException(new InternalErrorPage());
		}
	}
	
	private class ImageComponent extends DynamicImageResource{		
		public ImageComponent(byte[] image) {
			this.image = image;
		}
		
		@Override
		protected byte[] getImageData() {
			return image;
		}

		private byte[] image;
	}
}
