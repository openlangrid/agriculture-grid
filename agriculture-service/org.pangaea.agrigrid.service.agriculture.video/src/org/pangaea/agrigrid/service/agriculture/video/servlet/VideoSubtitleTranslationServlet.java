package org.pangaea.agrigrid.service.agriculture.video.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.net.SocketFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import jp.go.nict.langrid.client.axis.ProxySelectingAxisSocketFactory;
import jp.go.nict.langrid.commons.cs.binding.BindingNode;
import jp.go.nict.langrid.commons.cs.binding.DynamicBindingUtil;
import jp.go.nict.langrid.commons.ws.LangridConstants;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.ws_1_2.InvalidParameterException;
import jp.go.nict.langrid.ws_1_2.LangridException;
import jp.go.nict.langrid.ws_1_2.ProcessFailedException;
import jp.go.nict.langrid.ws_1_2.translation.AsyncTranslationResult;
import localhost.langrid_composite_service_2_0.services.AsyncTranslation.AsyncTranslationService;
import localhost.langrid_composite_service_2_0.services.AsyncTranslation.AsyncTranslationServiceServiceLocator;
import net.arnx.jsonic.JSON;

import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.client.Stub;
import org.hibernate.Session;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateVideoDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;

/**
 * <#if locale="ja">
 * 字幕翻訳用サーブレット実装
 * <#elseif locale="en">
 * </#if>
 * @author Masaaki Kamiya
 * @author Masaaki Kamiya
 */
public class VideoSubtitleTranslationServlet extends HttpServlet {
	public VideoSubtitleTranslationServlet() {
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
	 * 翻訳、結果取得の実処理
	 * @param request リクエスト
	 * @param response レスポンス
	 * @throws ServletException 処理に失敗した
	 * @throws IOException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	private void doProcess(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String videoId = request.getParameter("videoId");
		String token = request.getParameter("token");
		final String serviceId = request.getParameter("serviceId") == null
			|| request.getParameter("serviceId").equals("")
			? "jserver" : request.getParameter("serviceId");
		if(videoId == null){
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "parameter 'videoId' is required.");
			return;
		}
		PrintWriter w = response.getWriter();
		try{
			if(token == null){
				String sourceLang = request.getParameter("sourceLang");
				String targetLang = request.getParameter("targetLang");
				String[] sources = null;
				List<Long[]> times = null;
				DaoFactory f = DaoFactory.createInstance();
				Session s = f.getContext().getSession();
				s.beginTransaction();
				try{
					HibernateVideoDao vd = f.getVideoDao();
					Set<Subtitle> set = vd.getSubtitles(Long.parseLong(videoId), new Language(sourceLang));
					sources = new String[set.size()];
					times = new ArrayList<Long[]>();
					int i = 0;
					for(Subtitle sub : set){
						sources[i] = sub.getText();
						times.add(new Long[]{sub.getStartMillis(), sub.getEndMillis()});
						i++;
					}
					s.getTransaction().commit();
				} catch(EntityNotFoundException e){
					e.printStackTrace();
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "Object not found.");
				} catch(RuntimeException e) {
					e.printStackTrace();
					s.getTransaction().rollback();
					throw e;
				} catch (InvalidLanguageTagException e) {
					e.printStackTrace();
					s.getTransaction().rollback();
				} finally {
					if(s.isConnected()){
						s.close();
					}
				}
				String translateToken = startTranslation(serviceId, sourceLang, targetLang, sources);
				TranslateRequestJson trj = new TranslateRequestJson();
				trj.setToken(translateToken);
				trj.setTimes(times);
				w.print(JSON.encode(trj));
				w.close();
			}else{
				AsyncTranslationResult result = getCurrentResult(token);
				TranslateResultJson trj = new TranslateResultJson();
				if(result.isFinished()){
					trj.setFinish(true);
				}
				if(result.getResults().length != 0){
					trj.setTexts(result.getResults());
				}
				w.print(JSON.encode(trj));
				w.close();
			}
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Parameter.");
		} catch (ProcessFailedException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Process failed..");
		} catch(ServiceException e){
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Process failed..");
		} catch(AxisFault e){
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Process failed..");
		}
	}

	private String startTranslation(String serviceId, String sourceLang, String targetLang, String[] sources)
	throws ServiceException, MalformedURLException, LangridException, RemoteException{
		AsyncTranslationService s = new AsyncTranslationServiceServiceLocator()
			.getAsyncTranslation(new URL(
					"http://ymcviet.net/agrigrid/invoker/AsyncTranslation"));
		Stub stub = (Stub)s;
		stub.setUsername("agrigrid");
		stub.setPassword("agrigridPasswd");
		List<BindingNode> bindings = new ArrayList<BindingNode>();
		bindings.add(new BindingNode("TranslationPL", serviceId));
		stub.setHeader(
				LangridConstants.ACTOR_SERVICE_TREEBINDING
				, "binding", DynamicBindingUtil.encodeTree(bindings)
				);
		return s.startTranslation(sourceLang, targetLang, sources);
	}

	private AsyncTranslationResult getCurrentResult(String token)
	throws ServiceException, MalformedURLException, LangridException, RemoteException{
		AsyncTranslationService s = new AsyncTranslationServiceServiceLocator()
			.getAsyncTranslation(new URL(
					"http://ymcviet.net/agrigrid/invoker/AsyncTranslation"));
		Stub stub = (Stub)s;
		stub.setUsername("agrigrid");
		stub.setPassword("agrigridPasswd");
		return s.getCurrentResult(token);
	}
	static{
		AxisProperties.setProperty(SocketFactory.class.getName(),
				ProxySelectingAxisSocketFactory.class.getName());
	}
}
