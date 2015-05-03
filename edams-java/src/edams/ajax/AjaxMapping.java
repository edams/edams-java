package edams.ajax;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import jm.com.JmProperties;
import jm.net.DataEntity;
import edams.comm.CommMsg;
import edams.comm.CommProp;
import edams.comm.CommUserObj;
import edams.dao.FileDao;
import edams.dao.MapDao;
import edams.es.EsRes;
import edams.file.FileUtil;
import edams.io.Indexing;

public class AjaxMapping {
	
	protected AjaxMapping() {}
	
	protected String service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String result = "";
		
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		
		if(userObj == null){
			//로그인 안했으면 오류
			result = CommMsg.approachError();
		} else {
			JSONObject resObj = new JSONObject();
			MapDao mDao = MapDao.getInstance();
			
			if("POST".equals(req.getMethod())){
				StringBuffer data = new StringBuffer();
				BufferedReader bufferR;
				bufferR = req.getReader();		
				String message = "";
				while ((message = bufferR.readLine()) != null) {
					data.append(message);
				}
				JSONObject mapObj = new JSONObject(data.toString());
				
				//mp_mapping, mp_mapping_fields 테이블에 데이터 삭제 & 입력.
				mDao.deleteMpMapping(userObj.getGrpId(), userObj.getEmail(), mapObj.getString("map_id"));
				mDao.deleteMpMappingFields(userObj.getGrpId(), userObj.getEmail(), mapObj.getString("map_id"), null);
				int insRes = 0;
				insRes += mDao.insertMpMapping(userObj.getGrpId(), userObj.getEmail(), mapObj.getString("map_id"), mapObj.getString("src_file"));
				JSONArray mapFields = mapObj.getJSONArray("map_info");
				for(int i=0; i<mapFields.length(); i++){
					insRes += mDao.insertMpMappingFields(userObj.getGrpId(), userObj.getEmail(), mapObj.getString("map_id"), i+"", mapFields.getJSONObject(i).getString("name"), mapFields.getJSONObject(i).getString("type"), mapFields.getJSONObject(i).getString("date_format"), mapFields.getJSONObject(i).getString("value"));
				}
				if(insRes == (mapFields.length() + 1)){
					resObj.put("result", "OK");
				} else {
					resObj.put("result", "ERROR");
				}
			} else if("GET".equals(req.getMethod())){
				String map_id = req.getParameter("map_id");
				DataEntity[] mpMappingData = mDao.getMpMapping(userObj.getGrpId(), userObj.getEmail(), map_id);
				
				JSONArray mpResArr = new JSONArray();
				for(int m=0; m< mpMappingData.length; m++){
					DataEntity[] mpMappingField = mDao.getMpMappingFields(userObj.getGrpId(), userObj.getEmail(), mpMappingData[m].get("map_id")+"", null);
					JSONArray mpFieldArr = new JSONArray();
					for(int mf=0; mf<mpMappingField.length; mf++){
						JSONObject mpField = new JSONObject();
						mpField.put("name",mpMappingField[mf].get("name")+"");
						mpField.put("type",mpMappingField[mf].get("type")+"");
						mpField.put("date_format",mpMappingField[mf].get("date_format")+"");
						mpField.put("value",mpMappingField[mf].get("value")+"");
						
						mpFieldArr.put(mpField);
					}
					JSONObject mpRes = new JSONObject();
					mpRes.put("map_id", mpMappingData[m].get("map_id")+"");
					mpRes.put("src_file", mpMappingData[m].get("src_file")+"");
					mpRes.put("field_cnt", mpMappingField.length);
					mpRes.put("field_info",mpFieldArr);
					mpResArr.put(mpRes);
				}
				resObj.put("map_list", mpResArr);
			} else if("DELETE".equals(req.getMethod())){
				StringBuffer data = new StringBuffer();
				BufferedReader bufferR;
				bufferR = req.getReader();		
				String message = "";
				while ((message = bufferR.readLine()) != null) {
					data.append(message);
				}
				JSONObject mapObj = new JSONObject(data.toString());
				
				//mp_mapping, mp_mapping_fields 테이블에 데이터 삭제 & 입력.
				int insRes = 0;
				insRes += mDao.deleteMpMapping(userObj.getGrpId(), userObj.getEmail(), mapObj.getString("map_id"));
				insRes += mDao.deleteMpMappingFields(userObj.getGrpId(), userObj.getEmail(), mapObj.getString("map_id"), null);
				
				if(insRes > 0 ){
					resObj.put("result", "OK");
				} else {
					resObj.put("result", "ERROR");
				}
				
			}
			result = resObj.toString();
		}
		
		return result;
	}
	
	
}
