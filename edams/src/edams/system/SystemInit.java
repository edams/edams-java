package edams.system;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edams.comm.CommUserObj;
import edams.dao.SysDao;
import edams.io.Logstash;
import jm.com.Encrypt;
import jm.com.JmProperties;
import jm.net.DataEntity;

public class SystemInit extends HttpServlet {

	private static final long serialVersionUID = 2756509334304863664L;

	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html; charset=UTF-8");
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		if (userObj != null) {
			//로그인 성공시 Main 페이지로 이동.
			res.sendRedirect("/Login");
		} else {
			String propertyFile = "edams.properties";
			JmProperties jmProp = new JmProperties();
			String cmd = req.getParameter("cmd");
			if(!jmProp.setResource(propertyFile) || jmProp.get("sysPassswd") == null){
				if(cmd == null || cmd.equals("")){
					req.getRequestDispatcher("/system/install.jsp").include(req, res);
				} else if(cmd.equals("saveInit")){
					//최초 설정 저장 실행.
					DataEntity propData = new DataEntity();
					propData.put("dbType", req.getParameter("dbType"));
					propData.put("dbUrl", req.getParameter("dbUrl"));
					propData.put("dbDb", req.getParameter("dbDb"));
					propData.put("dbUser", req.getParameter("dbUser"));
					propData.put("dbPassswd", req.getParameter("dbPassswd"));
					propData.put("sysPath", req.getParameter("sysPath") + "/EDAMS");
					propData.put("sysPassswd", Encrypt.getSha256(req.getParameter("sysPassswd")));
					/*
					propData.put("lsVersion", "1.4.2");
					*/
					
					/*
					propData.put("esType", req.getParameter("esType"));
					propData.put("esUrl", req.getParameter("esUrl") == null ? "" : req.getParameter("esUrl"));
					propData.put("esPort", req.getParameter("esPort") == null ? "" : req.getParameter("esPort"));
					propData.put("esDPort", req.getParameter("esDPort") == null ? "" : req.getParameter("esDPort"));
					propData.put("esCluster", req.getParameter("esCluster") == null ? "" : req.getParameter("esCluster"));
					propData.put("esPortChk", req.getParameter("esPortChk") == null ? "" : req.getParameter("esPortChk"));
					propData.put("esDPortChk", req.getParameter("esDPortChk") == null ? "" : req.getParameter("esDPortChk"));
					*/
					
					String serverPath = this.getServletContext().getRealPath("/");
					SystemProperty prop = new SystemProperty();
					jmProp = prop.saveProperty(serverPath + "/WEB-INF/classes", propertyFile,  propData);
					
					jmProp.setResource(propertyFile);
					
					File folder = new File(jmProp.get("sysPath"));
			    	if(!folder.exists()){
			    		folder.mkdir();
			    	}
			    	
					SysDao dao = SysDao.getInstance();
					if(dao.checkTab("ac_grp", jmProp.get("dbDb")) == 0){
						dao.insert_ac_grp();
					}
					if(dao.checkTab("ac_user", jmProp.get("dbDb")) == 0){
						dao.insert_ac_user();
					}
					if(dao.checkTab("pj_prj", jmProp.get("dbDb")) == 0){
						dao.insert_pj_prj();
					}
					if(dao.checkTab("fl_processing", jmProp.get("dbDb")) == 0){
						dao.insert_fl_processing();
					}
					if(dao.checkTab("pj_prj_grp", jmProp.get("dbDb")) == 0){
						dao.insert_pj_prj_grp();
					}
					if(dao.checkTab("pj_prj_grp_user", jmProp.get("dbDb")) == 0){
						dao.insert_pj_prj_grp_user();
					}
					if(dao.checkTab("pj_prj_mapping", jmProp.get("dbDb")) == 0){
						dao.insert_pj_prj_mapping();
					}
					
					/**
					 * 2015.01.13 김종민 추가.
					 * 파일 상태 업로드 테이블.
					 */
					if(dao.checkTab("fl_file", jmProp.get("dbDb")) == 0){
						dao.insert_fl_file();
					}
					
					/**
					 * 2015.01.13 김종민 추가.
					 * 매핑 테이블
					 */
					if(dao.checkTab("mp_mapping", jmProp.get("dbDb")) == 0){
						dao.insert_mp_mapping();
					}
					if(dao.checkTab("mp_mapping_fields", jmProp.get("dbDb")) == 0){
						dao.insert_mp_mapping_fields();
					}
					
					/**
					 * 2015.01.21 김종민 추가.
					 * 엘라스틱서치 테이블. 
					 */
					if(dao.checkTab("es_es", jmProp.get("dbDb")) == 0){
						dao.insert_es_es();
					}
			    	
					/* 로그스태시 다운로드 및 설치 시작
					Logstash logstash = Logstash.getInstance();
					logstash.downloadLogstash();
					logstash.unzipLogstash();
					if("common".equals(jmProp.get("esType"))){
						logstash.setConfFile(jmProp.get("esCluster"), jmProp.get("esUrl"), null);
						String lgsConfFile = jmProp.get("sysPath") + "/logstash_config/logstash.conf";
//						logstash.logstashRun(lgsConfFile, jmProp.get("esUrl"), jmProp.get("esPort"));	//로그스태시 실행
					}
					/* 로그스태시 다운로드 및 설치 끝 */
					
					res.sendRedirect("/Login");
				}
			} else {
				String uri = req.getRequestURI();
				if(uri.indexOf("Install") > 0 || uri.indexOf("Setup") > 0 ){
					jmProp.setResource(propertyFile);
					if(cmd == null || cmd.equals("")){
						//시스템 어드민 로그인 페이지로 이동.
						req.getRequestDispatcher("/system/syslogin.jsp").include(req, res);
					} else if(cmd.equals("syslogin")){
						String passwd = req.getParameter("sysPassswd");
						passwd = Encrypt.getSha256(passwd);
						if(passwd.equals(jmProp.get("sysPassswd"))){
							req.getRequestDispatcher("/system/sysModify.jsp").include(req, res);
						} else {
							res.sendRedirect("/Login");
						}
					} else if(cmd.equals("modifySysSettings")){
						DataEntity propData = new DataEntity();
						propData.put("dbType", req.getParameter("dbType"));
						propData.put("dbUrl", req.getParameter("dbUrl"));
						propData.put("dbDb", req.getParameter("dbDb"));
						propData.put("dbUser", req.getParameter("dbUser"));
						propData.put("dbPassswd", req.getParameter("dbPassswd"));
						propData.put("sysPath", req.getParameter("sysPath") + "/EDAMS");
						propData.put("sysPassswd", Encrypt.getSha256(req.getParameter("sysPassswd")));
						propData.put("lsVersion", "1.4.2");
						
						propData.put("esType", req.getParameter("esType"));
						propData.put("esUrl", req.getParameter("esUrl") == null ? "" : req.getParameter("esUrl"));
						propData.put("esPort", req.getParameter("esPort") == null ? "" : req.getParameter("esPort"));
						propData.put("esDPort", req.getParameter("esDPort") == null ? "" : req.getParameter("esDPort"));
						propData.put("esCluster", req.getParameter("esCluster") == null ? "" : req.getParameter("esCluster"));
						propData.put("esPortChk", req.getParameter("esPortChk") == null ? "" : req.getParameter("esPortChk"));
						propData.put("esDPortChk", req.getParameter("esDPortChk") == null ? "" : req.getParameter("esDPortChk"));
						
						String serverPath = this.getServletContext().getRealPath("/");
						SystemProperty prop = new SystemProperty();
						jmProp = prop.saveProperty(serverPath + "/WEB-INF/classes", propertyFile,  propData);
						
						res.sendRedirect("/Login");
					}
				} else {
					res.sendRedirect("/Login");
				}
			}
			
		}
		
	}
}
