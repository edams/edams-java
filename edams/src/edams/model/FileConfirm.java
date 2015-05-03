package edams.model;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jm.com.JmProperties;
import edams.comm.CommMsg;
import edams.comm.CommProp;
import edams.comm.CommUserObj;
import edams.dao.FileDao;

public class FileConfirm extends HttpServlet {
	
	private static final long serialVersionUID = 424840240129550483L;

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();
		String cmd = req.getParameter("cmd");
		String toUrl = req.getParameter("toUrl");
		int result = 0;
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		if(userObj == null){
			out.println(CommMsg.approachError());
		} else {
			if(cmd == null || toUrl == null || "".equals(toUrl)){
				out.println(CommMsg.approachError());
			} else {
				FileDao dao = FileDao.getInstance();
				JmProperties jmProp = CommProp.getJmProperties();
				if("reset".equals(cmd)){
					//초기화
					String src_file_path = "";
					String fileName = req.getParameter("fileName");
					String prjId = (String) session.getAttribute("ProjectId");
					src_file_path = jmProp.get("sysPath")+"/files"+"/"+prjId+"/"+userObj.getEmail() + "/" + fileName;
					result = dao.deleteFiProc(src_file_path, null, null);
					if(result > 0){
						res.sendRedirect(toUrl);
					} else {
						out.println(CommMsg.saveError());
					}
				}
				
			}
		}
	}
}
