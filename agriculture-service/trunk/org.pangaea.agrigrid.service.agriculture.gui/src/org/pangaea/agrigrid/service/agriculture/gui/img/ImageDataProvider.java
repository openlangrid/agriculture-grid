package org.pangaea.agrigrid.service.agriculture.gui.img;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Image;
import org.pangaea.agrigrid.service.agriculture.dao.entity.ImageCaption;

public class ImageDataProvider extends SortableDataProvider<ImageModel> {
	@Override
	public Iterator<? extends ImageModel> iterator(int first, int count) {
		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		dc.beginTransaction();
		List<ImageModel> list = new ArrayList<ImageModel>();
		try{
			for(Image img : DaoFactory.createInstance().getImageDao().listImage(first, count)){
				ImageModel im = new ImageModel();

				List<Category> cats = new ArrayList<Category>(img.getCategories().values());
				Collections.sort(cats, new Comparator<Category>() {
					@Override
					public int compare(Category arg0, Category arg1) {
						return arg0.getCategoryId().compareTo(arg1.getCategoryId());
					}
				});
				im.getCategories().addAll(cats);
				Blob b = img.getThumbnail();
				if(b != null){
					long length = b.length();
					im.setThumbnail(b.getBytes(1, (int)length));
				}
				im.setId(img.getImageId());
				
				im.setFileName(img.getFileName());
				for(ImageCaption caption : img.getCaptions().values()){
					im.getCaptions().put(caption.getLanguage(), caption.getText());
				}
				im.setUpdatedAt(img.getUpdatedAt().getTime());
				list.add(im);
			}
			dc.commit();
		} catch (SQLException e) {
			dc.rollback();
			e.printStackTrace();
		}
		return list.iterator();
	}
	
	@Override
	public IModel<ImageModel> model(ImageModel i) {
		return new ImageDetachableModel(i);
	}
	
	@Override
	public int size() {
		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		dc.beginTransaction();
		try{
			return (int)f.getImageDao().getTotalCount();
		}finally{
			dc.commit();
		}
	}
	
	private class ImageDetachableModel extends LoadableDetachableModel<ImageModel> {
		public ImageDetachableModel(ImageModel v) {
			setObject(v);
		}
		@Override
		protected ImageModel load() {
			return getObject();
		}
	}
}
