package edams.ajax;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import jm.com.JmProperties;
import jm.net.DataEntity;
import edams.comm.CommMsg;
import edams.comm.CommProp;
import edams.comm.CommUserObj;
import edams.dao.FileDao;
import edams.file.FileUtil;

public class AjaxFile {
	
	private JmProperties property = null;
	private String srcPath;
	
	protected AjaxFile() {
		try {
			property = CommProp.getJmProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected String service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String result = "";
		
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		
		if(userObj == null){
			//로그인 안했으면 오류
			result = CommMsg.approachError();
		} else {
			String cmd = req.getParameter("cmd");
			srcPath = property.get("sysPath")+"/files"+"/"+userObj.getGrpId()+"/"+userObj.getEmail();
			
			if(cmd != null){
				FileDao dao = FileDao.getInstance();
				FileUtil fUtil = FileUtil.getInstance();
				JSONObject mapJson = new JSONObject();
				JSONObject resJson = new JSONObject();
				String fileName = req.getParameter("fileName");
				String fileType = req.getParameter("fileType");
				
				if(cmd.equals("mapFileList")){
					result = DataEntity.toJsonArray(dao.getFlFile(userObj.getGrpId(), userObj.getEmail(), null));
				} else if(cmd.equals("mapInfo")){
					
					//파일의 매핑 정보 가져오기.
					FileReader fr = null;
					BufferedReader br = null;
					
					try {
						fr = new FileReader(srcPath + "/" + fileName);
						br = new BufferedReader(fr);
						
						if("JSON".equals(fileType)){
							String rLine = null;
							//첫 열 파싱.
							if((rLine = br.readLine())!=null){
								resJson = fUtil.getJSONMapInfo(rLine);
							}
						} else if("CSV".equals(fileType)){
							String rLine = null;
							//첫 열 파싱.
							if((rLine = br.readLine())!=null){
								mapJson = fUtil.getCSVMapInfo(rLine, null);
							}
							// 첫 열이 전부 string인 경우 행 이름이라고 판단하고 두번째 열 다시 파싱.  
							if(mapJson != null && "true".equals(mapJson.get("is_all_str")+"")){
								if((rLine = br.readLine())!=null){
									resJson = fUtil.getCSVMapInfo(rLine, mapJson);
								}
							} else {
								resJson = mapJson;
							}
						}
					} catch (Exception e) {
						
					} finally {
						try {
							if(br != null){
								br.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					result = resJson.toString();
					
				} else if(cmd.equals("")){
					
				} 
			}
		}
		return result;
	}
	
	/**
	 * 파일 확장자 가져오는 메소드.
	 * @param filename
	 * @return
	 */
	private String getSuffix(String filename) {
		String suffix = "";
		int pos = filename.lastIndexOf('.');
		if (pos > 0 && pos < filename.length() - 1) {
			suffix = filename.substring(pos + 1);
		}
		return suffix;
	}
}
