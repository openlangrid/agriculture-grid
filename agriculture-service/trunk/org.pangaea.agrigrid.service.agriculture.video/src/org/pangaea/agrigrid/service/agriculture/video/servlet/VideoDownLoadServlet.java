package org.pangaea.agrigrid.service.agriculture.video.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.util.Pair;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Session;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Video;

/**
 * <#if locale="ja">
 * 映像ダウンロード用サーブレット実装
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author Masaaki Kamiya
 */
public class VideoDownLoadServlet extends HttpServlet {
	public VideoDownLoadServlet() {
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
	 * 
	 * @param request リクエスト
	 * @param response レスポンス
	 * @throws ServletException 処理に失敗した
	 * @throws IOException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	private void doProcess(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		setParameters(request, response);
		response.setHeader("Accept-Ranges", "bytes");
		OutputStream os = response.getOutputStream();
		InputStream is = null;
		DaoFactory f = DaoFactory.createInstance();
		Session s = f.getContext().getSession();
		s.beginTransaction();
		try{
			try{
				HibernateVideoDao vd = f.getVideoDao();

				if(isThumbnail) {
					is = vd.getThumbnail(Long.parseLong(videoId)).getBinaryStream();
					StreamUtil.transfer(is, os);
					return;
				}
				
				Pair<Integer, Integer> contentRange = getContentRange(request);
				Video v = vd.getVideo(Long.parseLong(videoId));
				if(fileName.equals("")) {
					fileName = v.getFileName();
				}
				Blob b = vd.getVideoBinary(Long.parseLong(videoId));
				int size = (int)b.length();

				response.setHeader("ETag", "W/\"" + size + "-" + getVideoHash(request, v) + "\"");
				SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				response.setHeader("Last-Modified", sdf.format(v.getUpdatedAt().getTime()));
				String hash = "";
				String ifRange = request.getHeader("If-Range");
				if(ifRange != null){
					String[] ranges = ifRange.split("-");
					if(ranges.length == 2){
						hash = ranges[1].replaceAll("\"", "");
					}
				}
				if(contentRange != null){
					if(ifRange != null && ! hash.equals(getVideoHash(request, v))){
						is = b.getBinaryStream();
						response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
						response.setContentLength(size + 1);
						StreamUtil.transfer(is, os);
					}else{
						response.setStatus(206);
//						long sum = 0;
//						int r = 0;
//						byte[] buff = new byte[4096];
						if(contentRange.getSecond() ==  null){
//							sum = contentRange.getFirst();
							response.setContentLength(size - contentRange.getFirst());
							response.setHeader("Content-Range"
									, "bytes " + contentRange.getFirst() + "-" + (size - 1) + "/" + size);

							os.write(b.getBytes(contentRange.getFirst() + 1, size));

//							is = b.getBinaryStream();
//							is.skip(sum);
//							while((r = is.read(buff)) != -1){
//								sum += r;
//								os.write(buff, 0, r);
//							}
						}else if(contentRange.getFirst() == contentRange.getSecond()){
							response.setContentLength(0);
							response.setHeader("Content-Range"
									, "bytes " + contentRange.getFirst() + "-" + contentRange.getSecond() + "/" + size);
							os.write(new byte[0]);
						} else {
//							sum = contentRange.getFirst();
//							is = b.getBinaryStream(contentRange.getFirst() + 1, (contentRange.getSecond() - contentRange.getSecond()));
							response.setContentLength(contentRange.getSecond() - contentRange.getFirst() + 1);
							response.setHeader("Content-Range"
									, "bytes " + contentRange.getFirst() + "-" + contentRange.getSecond() + "/" + size);

							os.write(b.getBytes(contentRange.getFirst() + 1, contentRange.getSecond() - contentRange.getFirst()));
							
//							if(contentRange.getSecond() < buff.length){
//								buff = new byte[contentRange.getSecond()];
//							}
//							int endbuff = buff.length;
//							while((r = is.read(buff, 0, endbuff)) != -1) {
//								sum += r;
//								os.write(buff, 0, r);
//								if((contentRange.getSecond() - sum) < endbuff) {
//									endbuff = (int) (contentRange.getSecond() - sum);
//								}
//								if(contentRange.getSecond() <= sum) {
//									break;
//								}
//							}
						}
					}
				}else{
					is = b.getBinaryStream();
					response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
					response.setContentLength(size + 1);
					StreamUtil.transfer(is, os);
				}
			} catch(EntityNotFoundException e){
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Object not found.");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error.");
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
			if(e instanceof ClientAbortException){
				s.getTransaction().rollback();
			}else{
				e.printStackTrace();
				s.getTransaction().rollback();
			}
		} catch(Exception e){
			
		} finally {
			if(s.isConnected()){
				s.close();
			}
			if(os != null){
				os.close();
			}
		}
	}
	
	private void setParameters(HttpServletRequest request, HttpServletResponse response)
	throws IOException
	{
		videoId = request.getParameter("videoId");
		String thumb = request.getParameter("thumbnail");
		isThumbnail = thumb != null && thumb.equalsIgnoreCase("true");
	}
	
	private Pair<Integer, Integer> getContentRange(HttpServletRequest request){
		String range = request.getHeader("Range");
		if(range == null){
			return null;
		}
		String[] ranges = range.replaceAll("bytes=", "").split("-");
		int start = Integer.parseInt(ranges[0]);
		int end = 0;
		if(ranges.length == 2){
			end = Integer.parseInt(ranges[1]);
		}
		return new Pair<Integer, Integer>(start, end == 0 ? null : end);
	}
	
	private String getVideoHash(HttpServletRequest req, Video v) throws NoSuchAlgorithmException{
		String host = "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
		MessageDigest md = MessageDigest.getInstance("MD5");
//		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] bytes = md.digest((host + v.getVideoId() + v.getUpdatedAt().getTimeInMillis()).getBytes());
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			String hex = String.format("%02x", b);
			sb.append(hex);
		}
		return sb.toString();
	}
	
	private String videoId;
	private String fileName = "";
	private boolean isThumbnail;
}
