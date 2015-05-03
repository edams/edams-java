package edams.view;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edams.comm.CommMsg;
import edams.comm.CommUserObj;

public class View extends HttpServlet {
	
	private static final long serialVersionUID = -3451094404332141203L;

	protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		
		if(userObj == null){
			out.println(CommMsg.loginGoFirst());
		} else {
			res.sendRedirect("/Dashboard");
		}
	}

}
