package edams.ajax;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jm.net.DataEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import edams.comm.CommMsg;
import edams.comm.CommUserObj;
import edams.dao.EsDao;

public class AjaxElasticsearch{
	
	protected String service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String result = "";
		
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		
		if(userObj == null){
			result = CommMsg.approachError();
		} else {
			
			JSONObject resObj = new JSONObject();
			EsDao eDao = EsDao.getInstance();
			
			if("POST".equals(req.getMethod())){
				StringBuffer data = new StringBuffer();
				BufferedReader bufferR;
				bufferR = req.getReader();		
				String message = "";
				while ((message = bufferR.readLine()) != null) {
					data.append(message);
				}
				JSONObject esObj = new JSONObject(data.toString());
				
				//es_es 테이블에 데이터 삭제 & 입력.
				eDao.deleteEsEs(userObj.getGrpId(), userObj.getEmail(), esObj.getString("es_id"));
				int insRes = eDao.insertEsEs(userObj.getGrpId(), userObj.getEmail(), esObj.getString("es_id"), esObj.getString("es_url"), esObj.getString("es_cluster"), esObj.getString("es_port"), esObj.getString("es_dport"));
				if(insRes > 0){
					resObj.put("result", "OK");
				} else {
					resObj.put("result", "ERROR");
				}
			} else if("GET".equals(req.getMethod())){
				DataEntity[] esListData = eDao.getEsEs(userObj.getGrpId(), userObj.getEmail(), null);
				
				JSONArray esResArr = new JSONArray();
				for(int e=0; e< esListData.length; e++){
					JSONObject esJsonObj = new JSONObject(esListData[e].toJson());
					esResArr.put(esJsonObj);
				}
				resObj.put("es_list", esResArr);
			} else if("DELETE".equals(req.getMethod())){
				StringBuffer data = new StringBuffer();
				BufferedReader bufferR;
				bufferR = req.getReader();		
				String message = "";
				while ((message = bufferR.readLine()) != null) {
					data.append(message);
				}
				JSONObject esObj = new JSONObject(data.toString());
				
				//es_es 테이블에 데이터 삭제 & 입력.
				int insRes = eDao.deleteEsEs(userObj.getGrpId(), userObj.getEmail(), esObj.getString("es_id"));
				if(insRes > 0){
					resObj.put("result", "OK");
				} else {
					resObj.put("result", "ERROR");
				}
			}
			result = resObj.toString();
			
			/*
			String cmd = req.getParameter("cmd");
			if(cmd == null){
				result = CommMsg.approachError();
			} else {
				EsUtil esUtil = EsUtil.getInstance();
				PrjDao prjDao = PrjDao.getInstance();
				JmProperties prop = CommProp.getJmProperties();
				if("esStatus".equals(cmd)){
					//클러스터 상태 가져오기.
					if("common".equals(prop.get("esType"))){
						result = esUtil.checkCluster(prop.get("esUrl"), prop.get("esPort"));
					} else {
						String prj_id = req.getParameter("prjId");
						DataEntity[] prjDatas = prjDao.getPrj(prj_id, null, null);
						if(prjDatas != null && prjDatas.length > 0){
							DataEntity prjData = prjDatas[0];
							result = esUtil.checkCluster(prjData.get("prj_es_url")+"", prjData.get("prj_es_port")+"");
						}
					}
				} else if("esLogstash".equals(cmd)){
					//로그스태시 실행중인지 여부 가져오기.
					if("common".equals(prop.get("esType"))){
						result = esUtil.checkLogstash(prop.get("esUrl"), prop.get("esPort"));
					} else {
						String prj_id = req.getParameter("prjId");
						DataEntity[] prjDatas = prjDao.getPrj(prj_id, null, null);
						if(prjDatas != null && prjDatas.length > 0){
							DataEntity prjData = prjDatas[0];
							result = esUtil.checkLogstash(prjData.get("prj_es_url")+"", prjData.get("prj_es_port")+"");
						}
					}
				} else if("esIndex".equals(cmd)){
					//인덱스 존재 여부 가져오기.
					String prj_id = req.getParameter("prjId");
					if("common".equals(prop.get("esType"))){
						result = esUtil.getMapping(prop.get("esUrl"), prop.get("esPort"),prj_id, userObj.getGrpId());
					} else {
						DataEntity[] prjDatas = prjDao.getPrj(prj_id, null, null);
						if(prjDatas != null && prjDatas.length > 0){
							DataEntity prjData = prjDatas[0];
							result = esUtil.getMapping(prjData.get("prj_es_url")+"", prjData.get("prj_es_port")+"",prj_id, userObj.getGrpId());
						}
					}
				}
			}
			*/
		}
		return result;
	}
}
