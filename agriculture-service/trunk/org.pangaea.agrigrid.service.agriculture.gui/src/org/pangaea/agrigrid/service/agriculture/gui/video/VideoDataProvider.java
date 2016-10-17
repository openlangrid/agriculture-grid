package org.pangaea.agrigrid.service.agriculture.gui.video;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;
import org.pangaea.agrigrid.service.agriculture.dao.entity.VideoCaption;

public class VideoDataProvider extends SortableDataProvider<VideoModel> {
	@Override
	public Iterator<? extends VideoModel> iterator(int first, int count) {
		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		dc.beginTransaction();
		HibernateVideoDao vd = f.getVideoDao();
		List<VideoModel> list = new ArrayList<VideoModel>();
		try{
			for(Video v : vd.listVideo(first, count)){
				VideoModel vm = new VideoModel();
				Blob b = v.getThumbnail();
				if(b != null){
					long length = b.length();
					vm.setThumbnail(b.getBytes(1, (int)length));
				}
				vm.setId(v.getVideoId());
				vm.setFileName(v.getFileName());
				vm.setUpdatedAt(v.getUpdatedAt().getTime());
				
				List<Category> cats = new ArrayList<Category>(v.getCategories().values());
				Collections.sort(cats, new Comparator<Category>() {
					@Override
					public int compare(Category arg0, Category arg1) {
						return arg0.getCategoryId().compareTo(arg1.getCategoryId());
					}
				});
				vm.getCategories().addAll(cats);
				for(VideoCaption caption : v.getCaptions().values()){
					vm.getCaptions().put(caption.getLanguage(), caption.getText());
				}
				
				for(Subtitle sub : v.getSubtitles()){
					if(vm.getSubtitles().containsKey(sub.getLanguage())){
						vm.getSubtitles().get(sub.getLanguage()).add(sub);
					}else{
						ArrayList<Subtitle> subList = new ArrayList<Subtitle>();
						subList.add(sub);
						vm.getSubtitles().put(sub.getLanguage(), subList);
					}
				}
					
				list.add(vm);
			}
			dc.commit();
		} catch (SQLException e) {
			dc.rollback();
			e.printStackTrace();
		}
		return list.iterator();
	}
	
	@Override
	public IModel<VideoModel> model(VideoModel v) {
		return new VideoDetachableModel(v);
	}
	
	@Override
	public int size() {
		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		dc.beginTransaction();
		try{
			return (int)f.getVideoDao().getTotalCount();
		}finally{
			dc.commit();
		}
	}
	
	private class VideoDetachableModel extends LoadableDetachableModel<VideoModel> {
		public VideoDetachableModel(VideoModel v) {
			setObject(v);
		}
		@Override
		protected VideoModel load() {
			return getObject();
		}
	}
}
