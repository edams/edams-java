package edams.es;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jm.com.JmProperties;
import jm.net.DataEntity;
import edams.comm.CommProp;
import edams.comm.CommUserObj;
import edams.dao.PrjDao;

public class EsReq extends HttpServlet {
	
	private static final long serialVersionUID = 6136860156967845591L;
	
	protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("application/json; charset=UTF-8");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		if(userObj == null){
			out.println("로그인 후 이용해 주시기 바랍니다.");
		} else {
			JmProperties jmProp = CommProp.getJmProperties();
			String url = "";
			StringBuffer requestURL = req.getRequestURL();
			String queryString = req.getQueryString();
			if (queryString == null) {
				url = requestURL.toString();
			} else {
				url = requestURL.append('?').append(queryString).toString();
			}
			
			url = url.replaceFirst(":8080", "");
			String esUrl="", esPort="";
			if("common".equals(jmProp.get("esType"))){
				esUrl = jmProp.get("esUrl");
				esPort = jmProp.get("esPort");
				esUrl = esUrl.replaceAll("http", "");
				esUrl = esUrl.replaceAll(":", "");
				esUrl = esUrl.replaceAll("/", "");
				if(esPort == null || "".equals(esPort)){
					esPort = "9200";
				}
			} else {
				PrjDao dao = PrjDao.getInstance();
				String prjId = (String)session.getAttribute("ProjectId");
				DataEntity[] prjEntitys = dao.getPrj(prjId, userObj.getGrpId(), null);
				if(prjEntitys != null && prjEntitys.length > 0){
					DataEntity prjEntity = prjEntitys[0];
					esUrl = prjEntity.get("prj_es_url")+"";
					esPort = prjEntity.get("prj_es_url")+"";
					esUrl = esUrl.replaceAll("http", "");
					esUrl = esUrl.replaceAll(":", "");
					esUrl = esUrl.replaceAll("/", "");
					if(esPort == null || "".equals(esPort)){
						esPort = "9200";
					}
				}
			}
			url = "http://" + esUrl + url.substring(url.indexOf("/Es/"));
			url = url.replaceFirst("/Es/", ":"+esPort+"/");
			
			res.setHeader("Access-Control-Allow-Origin", "*");
			res.setHeader("Access-Control-Allow-Methods", "OPTIONS, HEAD, GET, POST, PUT, DELETE");
			res.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Content-Length");
			
			try {
				if (req.getMethod().equals("OPTIONS")) {
					this.doEsPost(req, res, "OPTIONS", url);
				} else if (req.getMethod().equals("HEAD")) {
					this.doEsPost(req, res, "HEAD", url);
				} else if (req.getMethod().equals("PUT")) {
					this.doEsPost(req, res, "PUT", url);
				} else if (req.getMethod().equals("DELETE")) {
					this.doEsDelete(req, res, url);
				} else if (req.getMethod().equals("POST")) {
					this.doEsPost(req, res, "POST", url);
				} else {
					this.doEsGet(req, res, url);
				}
			} catch(Exception e){
//				e.printStackTrace();
			}
		}
	}
	
	/**
	 * GET 방식 명령어 처리
	 */
	private void doEsGet(HttpServletRequest req, HttpServletResponse res, String url) throws Exception {
		StringBuffer data = new StringBuffer();
		BufferedReader bufferR;
		bufferR = req.getReader();

		EsRes esr = EsRes.getInstance();
		String message = "";
		while ((message = bufferR.readLine()) != null) {
			data.append(message);
		}

		PrintWriter out = res.getWriter();
		out.print(esr.getEsGet(url, req.getMethod()));
	}
	
	/**
	 * PUT, POST 방식 명령어 처리.
	 */
	private void doEsPost(HttpServletRequest req, HttpServletResponse res, String method, String url) throws Exception {
		StringBuffer data = new StringBuffer();
		BufferedReader bufferR;
		bufferR = req.getReader();

		EsRes esr = EsRes.getInstance();
		
		String message = "";
		while ((message = bufferR.readLine()) != null) {
			data.append(message);
		}
		
		PrintWriter out = res.getWriter();
		out.print(esr.getEsPost(url, data.toString(), method));
	}
	
	/**
	 * DELETE 방식 명령어 처리.
	 * @param req
	 * @param res
	 * @param url
	 * @throws IOException
	 */
	private void doEsDelete(HttpServletRequest req, HttpServletResponse res, String url) throws Exception {
		StringBuffer data = new StringBuffer();
		BufferedReader bufferR;
		bufferR = req.getReader();

		EsRes esr = EsRes.getInstance();
		
		String message = "";
		while ((message = bufferR.readLine()) != null) {
			data.append(message);
		}
		
		esr.getEsDelete(url, data.toString());
	}
}
