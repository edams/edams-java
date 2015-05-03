package edams.view;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jm.net.DataEntity;
import edams.comm.CommMsg;
import edams.comm.CommUserObj;
import edams.dao.PrjDao;

public class Files extends HttpServlet {
	
	private static final long serialVersionUID = -6014145512463953125L;

	protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();
		String uri = req.getRequestURI();
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		
		if(userObj == null){
			out.println(CommMsg.loginGoFirst());
		} else {
			if(uri.indexOf("Files") > 0){
				uri = req.getRequestURI();
				uri = uri.replaceAll("Files", "");
				uri = uri.replaceAll("/", "");
				if(uri.equals("")){
					req.getRequestDispatcher("/view/files/files.jsp").include(req, res);
				} else {
					res.sendRedirect("/Files");
				}
			}
		}
	}
}
