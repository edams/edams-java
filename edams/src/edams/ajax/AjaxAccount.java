package edams.ajax;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jm.net.DataEntity;
import edams.dao.AcDao;

public class AjaxAccount{
	
	protected String service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String result = "";
		
		String cmd = req.getParameter("cmd");
		if(cmd != null){
			AcDao dao = AcDao.getInstance();
			if(cmd.equals("checkMail")){
				//이메일 중복 체크
				String email = req.getParameter("email");
				if(!dao.isExistMail(email)){
					result = "{\"result\":\"OK\"}";
				} else {
					result = "{\"result\":\"EXIST\"}";
				}
			} else if(cmd.equals("checkNick")){
				//닉네임 중복 체크
				String nicname = req.getParameter("nicname");
				if(!dao.isExistNicname(nicname)){
					result = "{\"result\":\"OK\"}";
				} else {
					result = "{\"result\":\"EXIST\"}";
				}
			} else if (cmd.equals("checkGrp")){
				//그룹명 중복 체크 ?
				String grpId = req.getParameter("grpId");
				if(!dao.isExistGrpId(grpId)){
					result = "{\"result\":\"OK\"}";
				} else {
					result = "{\"result\":\"EXIST\"}";
				}
			} else if (cmd.equals("getGrpInfo")){
				DataEntity[] grpDatas = dao.getGrp();
				StringBuffer str = new StringBuffer();
				str.append("[");
				for(int i=0; i < grpDatas.length; i++){
					str.append("{");
					str.append("\""+"grp_id"+"\":"+"\""+grpDatas[i].get("grp_id")+"\"");
					str.append(",");
					str.append("\""+"grp_name"+"\":"+"\""+grpDatas[i].get("grp_name")+"\"");
					str.append(",");
					str.append("\""+"grp_text"+"\":"+"\""+grpDatas[i].get("grp_text")+"\"");
					str.append(",");
					str.append("\""+"grp_dept_id"+"\":"+"\""+grpDatas[i].get("grp_dept_id")+"\"");
					str.append(",");
					str.append("\""+"user_cnt"+"\":"+""+grpDatas[i].get("user_cnt")+"");
					str.append(",");
					str.append("\""+"admin_email"+"\":"+"\""+grpDatas[i].get("admin_email")+"\"");
					str.append("}");
					if(i < grpDatas.length-1){
						str.append(",");
					}
				}
				str.append("]");
				result = str.toString();
			}
		}
		return result;
	}
}
