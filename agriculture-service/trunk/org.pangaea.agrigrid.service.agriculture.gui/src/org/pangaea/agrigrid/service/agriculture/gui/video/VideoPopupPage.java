package org.pangaea.agrigrid.service.agriculture.gui.video;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequest;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;

public class VideoPopupPage extends WebPage {
	public VideoPopupPage(Long id) {
		HttpServletRequest req = ((WebRequest)getRequest()).getHttpServletRequest();
		String host = "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
		DaoFactory f = DaoFactory.createInstance();
		DaoContext dc = f.getContext();
		String fileName = "";
		try{
			dc.beginTransaction();
			HibernateVideoDao vd = f.getVideoDao();
			Video vm = vd.getVideo(id);
			fileName =  vm.getFileName();
			dc.commit();
		}catch(Exception e){
			dc.rollback();
		}
		add(new HiddenField<String>("videoUrl"
				, new Model<String>(
						host + "/services/AgricultureVideoService/download/" + fileName + "?videoId=" + id
//						host + "/services/AgricultureVideoService/download/" + id + ":" + fileName
//						host + "/js/langrid.ogv"
					)));
	}
}
