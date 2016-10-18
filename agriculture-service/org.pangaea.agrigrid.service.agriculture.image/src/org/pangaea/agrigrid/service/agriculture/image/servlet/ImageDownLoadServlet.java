package org.pangaea.agrigrid.service.agriculture.image.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.go.nict.langrid.commons.io.StreamUtil;

import org.hibernate.Session;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.image.util.ImageUtil;

/**
 * <#if locale="ja">
 * 画像ダウンロード用サーブレット実装
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author Takao Nakaguchi
 */
public class ImageDownLoadServlet extends HttpServlet {
	public ImageDownLoadServlet() {
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
		doProcess(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
		doProcess(request, response);
	}

	/**
	 * <#if locale="ja">
	 * ダウンロードの実処理
	 * @param request リクエスト
	 * @param response レスポンス
	 * @throws ServletException 処理に失敗した
	 * @throws IOException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	private void doProcess(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String imageId = request.getParameter("imageId");
		if(imageId == null){
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "parameter 'imageId' is required.");
			return;
		}
		String thumb = request.getParameter("thumbnail");
		boolean thumbnail = thumb != null && thumb.equalsIgnoreCase("true");

		DaoFactory f = DaoFactory.createInstance();
		Session s = f.getContext().getSession();
		s.beginTransaction();
		try{
			try{
				Blob b = f.getImageDao().getImageBinary(Long.parseLong(imageId));
				InputStream is = b.getBinaryStream();
				if(thumbnail) is = ImageUtil.resize(is, 128, "JPG");
				response.setContentType("image/jpeg");
				StreamUtil.transfer(is, response.getOutputStream());
			} catch(EntityNotFoundException e){
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Object not found.");
			}
			s.getTransaction().commit();
		} catch(RuntimeException e) {
			e.printStackTrace();
			s.getTransaction().rollback();
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			s.getTransaction().rollback();
		} catch (IOException e) {
			e.printStackTrace();
			s.getTransaction().rollback();
		} finally {
			if(s.isConnected()){
				s.close();
			}
		}
	}

	private static final long serialVersionUID = 5519462021902080619L;
}
