package edams.ajax;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edams.comm.CommMsg;

public class Ajax extends HttpServlet{
	
	private static final long serialVersionUID = 3247236442330002766L;

	protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("application/json; charset=UTF-8");
		PrintWriter out = res.getWriter();
		String uri = req.getRequestURI();
		uri = uri.replaceFirst("/Ajax/", "");
		if(uri.indexOf("[?]") > 0){
			uri = uri.substring(0, uri.indexOf("[?]"));
		}
//		System.out.println(uri);
		
		if("Account".equals(uri)){
			AjaxAccount ajax = new AjaxAccount();
			out.print(ajax.service(req, res));
		} else if("Elasticsearch".equals(uri)){
			AjaxElasticsearch ajax = new AjaxElasticsearch();
			out.print(ajax.service(req, res));
		} else if("Project".equals(uri)){
			AjaxProject ajax = new AjaxProject();
			out.print(ajax.service(req, res));
		} else if("File".equals(uri)){
			AjaxFile ajax = new AjaxFile();
			out.print(ajax.service(req, res));
		} else if("Mapping".equals(uri)){
			AjaxMapping ajax = new AjaxMapping();
			out.print(ajax.service(req, res));
		} else {
			out.println(CommMsg.approachError());
		}
	}
	
}
