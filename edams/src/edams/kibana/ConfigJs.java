package edams.kibana;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigJs extends HttpServlet {
	
	private static final long serialVersionUID = 6653508994148676768L;
	
	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/javascript; charset=UTF-8");
		PrintWriter out = res.getWriter();
		StringBuffer url = req.getRequestURL();
		String urlString = url.toString();
		urlString= urlString.substring(0, url.indexOf("/kibana"));
		
		StringBuffer src = new StringBuffer();
		src.append("define(['settings'],\n");
		src.append("function (Settings) {\n");
		src.append("	return new Settings({\n");
		src.append("		elasticsearch: \""+urlString+"/Es\",\n");
		src.append("		default_route     : '/dashboard/file/default.json',\n");
		src.append("		kibana_index: \"edams-kibana\",\n");
		src.append("		panel_names: [\n");
		src.append("			'histogram',\n");
		src.append("			'map',\n");
		src.append("			'goal',\n");
		src.append("			'table',\n");
		src.append("			'filtering',\n");
		src.append("			'timepicker',\n");
		src.append("			'text',\n");
		src.append("			'hits',\n");
		src.append("			'column',\n");
		src.append("			'trends',\n");
		src.append("			'bettermap',\n");
		src.append("			'query',\n");
		src.append("			'terms',\n");
		src.append("			'stats',\n");
		src.append("			'sparklines'\n");
		src.append("		]\n");
		src.append("	});\n");
		src.append("});\n");
		
		out.print(src);
		
	}
}
