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

public class Dashboard extends HttpServlet {
	
	private static final long serialVersionUID = -3403357063273914327L;

	protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();
		String uri = req.getRequestURI();
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		
		if(userObj == null){
			out.println(CommMsg.loginGoFirst());
		} else {
			if(uri.indexOf("Dashboard") > 0){
				uri = req.getRequestURI();
				uri = uri.replaceAll("Dashboard", "");
				uri = uri.replaceAll("/", "");
				PrjDao pjDao = PrjDao.getInstance();
				if(uri.equals("")){
					DataEntity[] pjDatas = pjDao.getPrjUser(userObj.getGrpId(), userObj.getEmail(), null);
					if(pjDatas != null && pjDatas.length > 0){
						res.sendRedirect("/Dashboard/"+pjDatas[0].get("prj_id"));
					} else {
						req.getRequestDispatcher("/view/dashboard/dashboard.jsp").include(req, res);
					}
				} else {
					req.getRequestDispatcher("/view/dashboard/dashboard.jsp").include(req, res);
				}
				
			}
		}
	}
}
