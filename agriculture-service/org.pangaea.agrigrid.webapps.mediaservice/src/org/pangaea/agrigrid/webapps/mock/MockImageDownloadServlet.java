package org.pangaea.agrigrid.webapps.mock;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ImageDownloadServlet
 */
public class MockImageDownloadServlet extends HttpServlet {
	/**
     * @see HttpServlet#HttpServlet()
     */
    public MockImageDownloadServlet() {
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
		doProcess(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
		doProcess(request, response);
	}

	private void doProcess(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
		PrintWriter w = response.getWriter();
		w.println("hello");
	}

	private static final long serialVersionUID = 8238136677314310532L;
}
