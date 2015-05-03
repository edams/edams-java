package edams.model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jm.com.JmProperties;
import jm.net.DataEntity;
import edams.comm.CommMsg;
import edams.comm.CommProp;
import edams.comm.CommUserObj;
import edams.dao.PrjDao;
import edams.es.EsUtil;
import edams.io.Logstash;

public class PrjConfirm extends HttpServlet {
	
	private static final long serialVersionUID = 424840240129550483L;

	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();
		String cmd = req.getParameter("cmd");
		String toUrl = req.getParameter("toUrl");
		int result = 0;
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		if(userObj == null || !"ADMIN".equals(userObj.getUsrType())){
			//관리자 유저가 아니면 오류
			out.println(CommMsg.approachError());
		} else {
			if(cmd == null || toUrl == null || "".equals(toUrl)){
				out.println(CommMsg.approachError());
			} else {
				PrjDao dao = PrjDao.getInstance();
				JmProperties jmProp = CommProp.getJmProperties();
				if("insertPrj".equals(cmd)){
					//신규 프로젝트 등록
					String prjId = req.getParameter("prjId");
					String prjName = req.getParameter("prjName");
					String prjText = req.getParameter("prjText");
					String esUrl = req.getParameter("esUrl");
					String esPort = req.getParameter("esPort");
					String esCluster = req.getParameter("esCluster");
					String esDPort = req.getParameter("esDPort");
					result += dao.insertPrj(userObj, prjId, prjName, prjText, esUrl, esPort, esDPort, esCluster);
					result += dao.insertPrjGrp(userObj.getGrpId(), prjId, userObj.getGrpId(), "OWNER", userObj.getEmail(), new Date(), "", userObj.getEmail(), new Date(), "", "ACTIVE");
//					result += dao.insertPrjGrpUser(userObj.getGrpId(), prjId, userObj.getGrpId(), userObj.getEmail(), "OWNER");
					
					String[] fieldId = req.getParameterValues("fieldName");
					String[] fieldType = req.getParameterValues("fieldType");
					String[] fieldTxt = req.getParameterValues("fieldTxt");
					if(fieldId != null && fieldType != null && fieldTxt != null
							&& fieldId.length == fieldType.length && fieldId.length == fieldTxt.length){
						for(int fc=0; fc < fieldId.length; fc++){
							result += dao.insertPjrMapping(prjId, fc, fieldId[fc], fieldType[fc], fieldTxt[fc]);
						}
					}
					
					if(result == (2 + fieldId.length)){
						res.sendRedirect(toUrl);
					} else {
						out.println(CommMsg.saveError());
					}
				} else if("modifPrj".equals(cmd)){
					//프로젝트 정보 수정.
					String prjId = req.getParameter("prjId");
					String prjName = req.getParameter("prjName");
					String prjText = req.getParameter("prjText");
					String esUrl = req.getParameter("esUrl");
					String esPort = req.getParameter("esPort");
					String esCluster = req.getParameter("esCluster");
					String esDPort = req.getParameter("esDPort");
					
					DataEntity updateEntity = new DataEntity();
					updateEntity.put("prj_name", prjName);
					updateEntity.put("prj_text", prjText);
					updateEntity.put("prj_es_url", esUrl);
					if(esPort != null && !"".equals(esPort)){updateEntity.put("prj_es_port", esPort);}
					updateEntity.put("prj_es_cluster", esCluster);
					if(esPort != null && !"".equals(esPort)){updateEntity.put("prj_es_dport", esDPort);}
					result = dao.updatePrj(updateEntity, prjId);
					
					if(result == 1){
						res.sendRedirect(toUrl);
					} else {
						out.println(CommMsg.saveError());
					}
				} else if("reqSharePrj".equals(cmd)){
					//프로젝트 공유 요청.
					String[] shPrjListChk = req.getParameterValues("shPrjListChk");
					if(shPrjListChk != null && shPrjListChk.length > 0){
						for(int i=0; i < shPrjListChk.length; i++){
							String shRdVal = req.getParameter("shRd_"+shPrjListChk[i]);
							String shGrpPrjMsg = req.getParameter("shGrpPrjMsg");
							shGrpPrjMsg = shGrpPrjMsg.replaceAll("\r\n", "\\\\n");
							shGrpPrjMsg = shGrpPrjMsg.replaceAll("\"", "\\\\\"");
							DataEntity[] data = dao.getPrj(shPrjListChk[i], null, null);
							if(data.length == 1){
								result = dao.insertPrjGrp(data[0].get("own_grp_id")+"", shPrjListChk[i], userObj.getGrpId(), shRdVal, userObj.getEmail(), new Date(), shGrpPrjMsg, null, null, null, "REQUEST");
							}
						}
					}
					if(result > 0){
						res.sendRedirect(toUrl);
					} else {
						out.println(CommMsg.saveError());
					}
				} else if("prjShareRemove".equals(cmd)){
					//프로젝트 공유 삭제.
					String sharePrjId = req.getParameter("sharePrjId");
					String ownGrpId = req.getParameter("ownGrpId");
					result = dao.deletePrjGrp(ownGrpId, sharePrjId, userObj.getGrpId());
					if(result > 0){
						res.sendRedirect(toUrl);
					} else {
						out.println(CommMsg.saveError());
					}
				}  else if("prjShareReply".equals(cmd)){
					//프로젝트 공유 삭제.
					String prjId = req.getParameter("prjId");
					String prjRepType = req.getParameter("prjRepType");
					String resMsg = req.getParameter("resMsg");
					String reqGrpId = req.getParameter("reqGrpId");
					
					DataEntity updateEntity = new DataEntity();
					updateEntity.put("status", prjRepType);
					updateEntity.put("res_msg", resMsg);
					updateEntity.put("res_date", new Date());
					updateEntity.put("res_adm_email", userObj.getEmail());
					
					result = dao.updatePrjGrp(updateEntity, userObj.getGrpId(), prjId, reqGrpId);
					if(result > 0){
						res.sendRedirect(toUrl);
					} else {
						out.println(CommMsg.saveError());
					}
				} else if("prjUserSet".equals(cmd)){
					//프로젝트 담당자 배정
					String prjId = req.getParameter("prjId");
					String prjOwnGrpId = req.getParameter("prjOwnGrpId");
					String[] usrEmail = req.getParameterValues("prjUsrChk");
					result += dao.deletePrjGrpUser(prjOwnGrpId, prjId, userObj.getGrpId(), null);
					if(usrEmail != null){
						for(int i=0; i < usrEmail.length; i++){
							String usrType = req.getParameter("rd_"+usrEmail[i]);
							result += dao.insertPrjGrpUser(prjOwnGrpId, prjId, userObj.getGrpId(), usrEmail[i], usrType);
						}
					}
					if(result > 0){
						res.sendRedirect(toUrl);
					} else {
						out.println(CommMsg.saveError());
					}
				} else if("prjShProvide".equals(cmd)){
					//프로젝트 공유 (제안)
					String prjId = req.getParameter("prjId");
					String[] prvidGrpId = req.getParameterValues("prvidGrpId");
					if(prvidGrpId != null){
						for(int i=0; i<prvidGrpId.length; i++){
							String provRd = req.getParameter("provRd_"+prvidGrpId[i]);
							String admEmail = req.getParameter("admEmail_"+prvidGrpId[i]);
							result += dao.insertPrjGrp(userObj.getGrpId(), prjId, prvidGrpId[i], provRd, admEmail, new Date(), userObj.getGrpName() + " 으로 부터 직접 공유됨", userObj.getEmail(), new Date(), userObj.getGrpName() + " 으로 부터 직접 공유됨", "PROVIDED");
						}
					}
					if(result == prvidGrpId.length){
						res.sendRedirect(toUrl);
					} else {
						out.println(CommMsg.saveError());
					}
				} else if("removePrj".equals(cmd)){
					//프로젝트 삭제
					String prjId = req.getParameter("prjId");
//					result = dao.deletePrj(prjId, userObj.getGrpId());
//					dao.deletePrjGrp(null, prjId, null);
//					dao.deletePrjGrpUser(null, prjId, null, null);
//					dao.deletePrjMapping(prjId, null, null);
					EsUtil esUtil = EsUtil.getInstance();
					if("common".equals(jmProp.get("esType"))){
						esUtil.deleteIndex(jmProp.get("esUrl"), jmProp.get("esPort"), prjId);
					} else {
						DataEntity[] prjEntitys = dao.getPrj(prjId, userObj.getGrpId(), null);
						if(prjEntitys != null && prjEntitys.length > 0){
							DataEntity prjEntity = prjEntitys[0];
							esUtil.deleteIndex(prjEntity.get("prj_es_url")+"", prjEntity.get("prj_es_port")+"", prjId);
						}
					}
					result = dao.delUpdPrj(prjId, userObj.getGrpId());
					dao.delUpdPrjGrp(null, prjId, null);
					dao.delUpdPrjGrpUser(null, prjId, null, null);
					dao.delUpdPrjMapping(prjId, null, null);
					
					if(result > 0){
						res.sendRedirect(toUrl);
					} else {
						out.println(CommMsg.saveError());
					}
				} else if("runLogstash".equals(cmd)){
					Logstash logstash = Logstash.getInstance();
					if("common".equals(jmProp.get("esType"))){
						String lgsConfFile = jmProp.get("sysPath") + "/logstash_config/logstash.conf";
						File lgsFile = new File(lgsConfFile);
						lgsFile.delete();
						logstash.setConfFile(jmProp.get("esCluster"), jmProp.get("esUrl"), null);
						result = logstash.logstashRun(lgsConfFile, jmProp.get("esUrl"), jmProp.get("esPort"));
					} else {
						String prjId = req.getParameter("prjId");
						DataEntity[] prjEntitys = dao.getPrj(prjId, userObj.getGrpId(), null);
						if(prjEntitys != null && prjEntitys.length > 0){
							DataEntity prjEntity = prjEntitys[0];
							String lgsConfFileName = "logstash-"+prjId+".conf";
							String lgsConfFile = jmProp.get("sysPath") + "/logstash_config/"+lgsConfFileName;
							File lgsFile = new File(lgsConfFile);
							lgsFile.delete();
							logstash.setConfFile(prjEntity.get("prj_es_cluster")+"", prjEntity.get("prj_es_url")+"", lgsConfFileName);
							result = logstash.logstashRun(lgsConfFile, prjEntity.get("prj_es_url")+"", prjEntity.get("prj_es_port")+"");
						}
					}
					if(result > 0){
						res.sendRedirect(toUrl);						
					} else {
						out.println(CommMsg.saveError());
					}
				} else if("createIndex".equals(cmd)){
					String prjId = req.getParameter("prjId");
					EsUtil esUtil = EsUtil.getInstance();
					DataEntity[] prjEntitys = dao.getPrj(prjId, null, null);
					if(prjEntitys != null && prjEntitys.length > 0){
						DataEntity prjEntity = prjEntitys[0];
						if("common".equals(jmProp.get("esType"))){
							result += esUtil.putMapping(jmProp.get("esUrl"), jmProp.get("esPort"), prjId, userObj.getGrpId());
							result += esUtil.iniKibana(jmProp.get("esUrl"), jmProp.get("esPort"), prjId, userObj.getGrpId(), prjEntity.get("own_grp_id")+"");
						} else {
							result += esUtil.putMapping(prjEntity.get("prj_es_url")+"", prjEntity.get("prj_es_port")+"", prjId, userObj.getGrpId());
							result += esUtil.iniKibana(prjEntity.get("prj_es_url")+"", prjEntity.get("prj_es_port")+"", prjId, userObj.getGrpId(), prjEntity.get("own_grp_id")+"");
						}
					}
					if(result > 1){
						res.sendRedirect(toUrl);						
					} else {
						out.println(CommMsg.saveError());
					}
				}
				
			}
		}
	}
}
