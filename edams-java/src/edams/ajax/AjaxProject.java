package edams.ajax;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jm.net.DataEntity;
import edams.comm.CommMsg;
import edams.comm.CommUserObj;
import edams.dao.AcDao;
import edams.dao.PrjDao;

public class AjaxProject{
	
	protected String service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String result = "";
		
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		if(userObj == null || !"ADMIN".equals(userObj.getUsrType())){
			//관리자 유저가 아니면 오류
			result = CommMsg.approachError();
		} else {
			String cmd = req.getParameter("cmd");
			if(cmd != null){
				PrjDao dao = PrjDao.getInstance();
				AcDao acDao = AcDao.getInstance();
				StringBuffer str = new StringBuffer();
				if(cmd.equals("checkPrj")){
					//중복 프로젝트 ID 체크.
					String prjId = req.getParameter("prjId");
					if(!dao.isExistPrjId(prjId)){
						result = "{\"result\":\"OK\"}";
					} else {
						result = "{\"result\":\"EXIST\"}";
					}
				} else if(cmd.equals("prjInfo")){
					//pj_prj 프로젝트 정보 불러오기.
					str.append("[");
					DataEntity[] prjEntity = dao.getGrpPrj(userObj.getGrpId());
					for(int p=0; p < prjEntity.length; p++){
						str.append("{");
						str.append(prjEntity[p].toJsonNobrc());
						str.append(",");
						str.append("\n");
						
						//프로젝트 오너 정보.
						str.append("\""+"prj_owner"+"\":");
						DataEntity[] prjOwnerEntity = acDao.getAcUser(prjEntity[p].get("own_email")+"");
						if(prjOwnerEntity.length > 0){
							str.append(prjOwnerEntity[0].toJson());
						} else {
							str.append("\"\"");
						}
						str.append(",");
						str.append("\n");
						
						//pj_prj_grp 해당 프로젝트를 공유중인 그룹 목록.
						str.append("\""+"pj_prj_grp"+"\":");
						DataEntity[] prjGrpEntity = dao.getPrjGrp(prjEntity[p].get("own_grp_id")+"", prjEntity[p].get("prj_id")+"", null);
						str.append("[");
						for(int pg=0; pg < prjGrpEntity.length; pg++){
							str.append("{");
							str.append(prjGrpEntity[pg].toJsonNobrc());
							DataEntity[] prjGrpReqUsrEntity = acDao.getAcUser(prjGrpEntity[pg].get("req_adm_email")+"");
							str.append(",");
							str.append("\""+"pj_prj_grp_req_user"+"\":");
							if(prjGrpReqUsrEntity.length == 1){
								str.append(prjGrpReqUsrEntity[0].toJson());
							} else {
								str.append("null");
							}
							str.append("}");
							if(pg < prjGrpEntity.length-1){ str.append(","); }
						}
						str.append("]");
						
						str.append(",");
						str.append("\n");
						
						//pj_prj_grp 해당 프로젝트를 공유중인 그룹 목록. 자기 빼고.
						str.append("\""+"pj_prj_grp_shared"+"\":");
						DataEntity[] prjGrpShareEntity = dao.getPrjGrpShared(prjEntity[p].get("own_grp_id")+"", prjEntity[p].get("prj_id")+"");
						str.append("[");
						for(int pg=0; pg < prjGrpShareEntity.length; pg++){
							str.append("{");
							str.append(prjGrpShareEntity[pg].toJsonNobrc());
							DataEntity[] prjGrpReqUsrEntity = acDao.getAcUser(prjGrpShareEntity[pg].get("req_adm_email")+"");
							str.append(",");
							str.append("\""+"pj_prj_grp_req_user"+"\":");
							if(prjGrpReqUsrEntity.length == 1){
								str.append(prjGrpReqUsrEntity[0].toJson());
							} else {
								str.append("null");
							}
							str.append("}");
							if(pg < prjGrpShareEntity.length-1){ str.append(","); }
						}
						str.append("]");
						
						str.append(",");
						str.append("\n");
						
						//pj_prj_grp_user 해당 프로젝트에 배정된 사용자 목록.
						str.append("\""+"pj_prj_grp_user"+"\":");
						DataEntity[] prjGrpUserEntity = dao.getPrjGrpUser(prjEntity[p].get("own_grp_id")+"", prjEntity[p].get("prj_id")+"", userObj.getGrpId(), null);
						str.append(DataEntity.toJsonArray(prjGrpUserEntity));
						
						str.append(",");
						str.append("\n");
						
						//pj_prj_grp_user 해당 프로젝트 매핑 정보.
						str.append("\""+"pj_prj_mapping"+"\":");
						DataEntity[] prjMappingEntity = dao.getPrjMapping(prjEntity[p].get("prj_id")+"");
						str.append(DataEntity.toJsonArray(prjMappingEntity));
						
						str.append("}");
						if(p < prjEntity.length-1){
							str.append(",");
							str.append("\n");
						}
					}
					str.append("]");
					result = str.toString();
				} else if(cmd.equals("shPrjInfo")){
					//공유할 해당 기관의 프로젝트 전체 불러오기.
					String grpId = req.getParameter("grpId");
					str.append("[");
					DataEntity[] prjEntity = dao.getPrj(null, grpId, null);
					for(int p=0; p < prjEntity.length; p++){
						str.append("{");
						str.append(prjEntity[p].toJsonNobrc());
						DataEntity[] prjGrpEntity = dao.getPrjGrp(prjEntity[p].get("own_grp_id")+"", prjEntity[p].get("prj_id")+"", userObj.getGrpId());
						str.append(",");
						str.append("\""+"pj_prj_grp_status"+"\":");
						if(prjGrpEntity.length == 1){
							str.append("\""+prjGrpEntity[0].get("status")+"\"");
						} else {
							str.append("null");
						}
						str.append("}");
						if(p < prjEntity.length-1){ str.append(","); }
					}
					str.append("]");
					result = str.toString();
				} else if(cmd.equals("shPrjCtrl")){
					//공유 설정 버튼 눌렀을 때 해당 프로젝트를 공유하고 있는 그룹들 보이기.
					
				} else if(cmd.equals("prjUserInfo")){
					String prjId = req.getParameter("prjId");
					str.append("[");
					DataEntity[] userEntity = acDao.getAcUser(null, userObj.getGrpId());
					for(int p=0; p < userEntity.length; p++){
						str.append("{");
						str.append(userEntity[p].toJsonNobrc());
						DataEntity[] prjGrpUsrEntity = dao.getPrjGrpUser(null, prjId, userObj.getGrpId(), userEntity[p].get("email")+"");
						str.append(",");
						str.append("\""+"pj_prj_grp_user_type"+"\":");
						if(prjGrpUsrEntity.length == 1){
							str.append("\""+prjGrpUsrEntity[0].get("user_type")+"\"");
						} else {
							str.append("null");
						}
						str.append("}");
						if(p < userEntity.length-1){ str.append(","); }
					}
					str.append("]");
					result = str.toString();
				}
				
			}
		}
		return result;
	}
}
