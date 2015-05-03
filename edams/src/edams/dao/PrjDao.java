package edams.dao;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Date;
import java.util.Vector;

import edams.comm.CommProp;
import edams.comm.CommUserObj;
import jm.com.JmProperties;
import jm.net.Dao;
import jm.net.DataEntity;
import jm.util.StringUtil;

public class PrjDao {

	private static PrjDao instance = null;
	private JmProperties property = null;
	
	Dao dao = null;
	
	private PrjDao() throws FileNotFoundException{
		property = CommProp.getJmProperties();
		dao = Dao.getInstance();
	}
	
	public static PrjDao getInstance() throws FileNotFoundException {
		if(instance == null){
			instance = new PrjDao();
		}
		return instance;
	}
	
	/**
	 * 프로젝트 아이디 중복여부 확인
	 * @param prjId
	 * @return
	 */
	public boolean isExistPrjId(String prjId) {
		StringBuffer sql = new StringBuffer();
		String[] param = {prjId};
		sql.append("SELECT count(*) as cnt FROM pj_prj WHERE prj_id = ? AND status <> 'DELETE' ");
		int cnt = dao.getCount(property, sql.toString(), param);
		
		if (cnt == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 프로젝 정보 입력.
	 * @param userObj
	 * @param prj_id
	 * @param prj_name
	 * @param prj_text
	 * @param prj_es_url
	 * @param prj_es_port
	 * @param prj_es_dport
	 * @param prj_es_cluster
	 * @return
	 */
	public int insertPrj(CommUserObj userObj, String prj_id, String prj_name, String prj_text,
			String prj_es_url, String prj_es_port, String prj_es_dport, String prj_es_cluster){
		int result = 0;
		DataEntity data = new DataEntity();
		data.put("prj_id", prj_id);
		data.put("own_grp_id", userObj.getGrpId());
		data.put("own_email", userObj.getEmail());
		data.put("prj_name", prj_name);
		data.put("prj_text", prj_text);
		data.put("reg_time", new Date());
		data.put("prj_es_url", prj_es_url);
		if(prj_es_port != null && !"".equals(prj_es_port)){ data.put("prj_es_port", prj_es_port); }
		if(prj_es_dport != null && !"".equals(prj_es_dport)){ data.put("prj_es_dport", prj_es_dport); }
		data.put("prj_es_cluster", prj_es_cluster);
//		data.put("prj_es_node", prj_es_node);
		data.put("prj_es_index", prj_id);
//		data.put("prj_es_type", prj_es_type);
		result = dao.insertData(property, "pj_prj", data);
		return result;
	}
	
	/**
	 * 프로젝트 공유 요청 입력. 자기 프로젝트 인 경우에도 자신에게 공유요청 된 것으로 생성함.
	 * @param userObj
	 * @param own_grp_id
	 * @param prj_id
	 * @param use_grp_id
	 * @param grp_type
	 * @param status
	 * @return
	 */
	public int insertPrjGrp(String own_grp_id, String prj_id, String use_grp_id, String grp_type,
			String req_adm_email, Date req_date, String req_msg,
			String res_adm_email, Date res_date, String res_msg, String status){
		int result = 0;
		DataEntity data = new DataEntity();
		data.put("own_grp_id", own_grp_id);
		data.put("prj_id", prj_id);
		data.put("use_grp_id", use_grp_id);
		if(grp_type != null && !"".equals(grp_type)){ data.put("grp_type", grp_type); }
		data.put("req_adm_email", req_adm_email);
		data.put("req_date", req_date);
		data.put("req_msg", req_msg);
		if(res_adm_email != null && !"".equals(res_adm_email)){ data.put("res_adm_email", res_adm_email); }
		if(res_date != null && !"".equals(res_date)){ data.put("res_date", res_date); }
		if(res_msg != null && !"".equals(res_msg)){ data.put("res_msg", res_msg); }
		if(status != null && !"".equals(status)){ data.put("status", status); }
		result = dao.insertData(property, "pj_prj_grp", data);
		return result;
	}
	
	/**
	 * 프로젝트 사용자 입력. 프로젝트 관리자가 등록함.
	 * @param own_grp_id
	 * @param prj_id
	 * @param use_grp_id
	 * @param use_email
	 * @param user_type
	 * @return
	 */
	public int insertPrjGrpUser(String own_grp_id, String prj_id, String use_grp_id, String use_email, String user_type){
		int result = 0;
		DataEntity data = new DataEntity();
		data.put("own_grp_id", own_grp_id);
		data.put("prj_id", prj_id);
		data.put("use_grp_id", use_grp_id);
		data.put("use_email", use_email);
		data.put("reg_time", new Date());
		if(user_type != null && !"".equals(user_type)){ data.put("user_type", user_type); }
		result = dao.insertData(property, "pj_prj_grp_user", data);
		return result;
	}
	
	/**
	 * 프로젝트의 데이터 매핑 필드 정보 입력.
	 * @param prj_id
	 * @param onum
	 * @param field_id
	 * @param field_type
	 * @param field_comment
	 * @return
	 */
	public int insertPjrMapping(String prj_id, int onum, String field_id, String field_type, String field_comment){
		int result = 0;
		DataEntity data = new DataEntity();
		data.put("reg_time", new Date());
		data.put("prj_id", prj_id);
		data.put("onum", onum);
		data.put("field_id", field_id);
		data.put("field_type", field_type);
		data.put("status", "ACTIVE");
		if(field_comment != null && !"".equals(field_comment)){ data.put("field_comment", field_comment); }
		result = dao.insertData(property, "pj_prj_mapping", data);
		return result;
	}
	
	/**
	 * 해당 그룹이 소유하거나 공유요청 승인 완료된 프로젝트 정보 불러오기.
	 * @param use_grp_id
	 * @param prj_id
	 * @return
	 */
	public DataEntity[] getGrpPrj(String use_grp_id){
		return getGrpPrj(use_grp_id, null);
	}
	public DataEntity[] getGrpPrj(String use_grp_id, String prj_id){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("A.* \n");
		sql.append(",B.own_grp_id \n");
		sql.append(",B.res_adm_email \n");
		sql.append(",B.grp_type \n");
		sql.append("FROM pj_prj A, pj_prj_grp B \n");
		sql.append("WHERE A.prj_id = B.prj_id \n");
		sql.append("AND A.status <> 'DELETE' \n");
		sql.append("AND B.status <> 'DELETE' \n");
		if(use_grp_id != null && !"".equals(use_grp_id)){
			sql.append("AND B.use_grp_id = ? \n");
			paramV.add(use_grp_id);
		}
		if(prj_id != null && !"".equals(prj_id)){
			sql.append("AND B.prj_id = ? \n");
			paramV.add(prj_id);
		}
		sql.append("ORDER BY B.res_date DESC");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
//		System.out.println(StringUtil.getSqlString(sql.toString(), params));
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * 해당 유저에게 조회 권한이 있는 프로젝트 정보 가져오기.
	 * @param use_grp_id
	 * @param use_email
	 * @return
	 */
	public DataEntity[] getPrjUser(String use_grp_id, String use_email, String user_type){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("A.* \n");
		sql.append("FROM pj_prj A, pj_prj_grp_user B \n");
		sql.append("WHERE A.prj_id = B.prj_id \n");
		sql.append("AND B.status = 'ACTIVE' \n");
		sql.append("AND A.status <> 'DELETE' \n");
		if(use_grp_id != null && !"".equals(use_grp_id)){
			sql.append("AND B.use_grp_id = ? \n");
			paramV.add(use_grp_id);
		}
		if(use_email != null && !"".equals(use_email)){
			sql.append("AND B.use_email = ? \n");
			paramV.add(use_email);
		}
		if(user_type != null && !"".equals(user_type)){
			sql.append("AND B.user_type = ? \n");
			paramV.add(user_type);
		}
		sql.append("ORDER BY B.reg_time DESC");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * pj_prj 테이블 정보 가져오는 메소드.
	 * @param prj_id
	 * @param own_grp_id
	 * @param own_email
	 * @return
	 */
	public DataEntity[] getPrj(String prj_id, String own_grp_id, String own_email){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("* \n");
		sql.append("FROM pj_prj \n");
		sql.append("WHERE status <> 'DELETE' \n");
		if(prj_id != null && !"".equals(prj_id)){
			sql.append("AND prj_id = ? \n");
			paramV.add(prj_id);
		}
		if(own_grp_id != null && !"".equals(own_grp_id)){
			sql.append("AND own_grp_id = ? \n");
			paramV.add(own_grp_id);
		}
		
		if(own_email != null && !"".equals(own_email)){
			sql.append("AND own_email = ? \n");
			paramV.add(own_email);
		}
		sql.append("ORDER BY reg_time DESC");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * pj_prj_grp 테이블 정보 가져오는 메소드.
	 * @param own_grp_id
	 * @param prj_id
	 * @param use_grp_id
	 * @return
	 */
	public DataEntity[] getPrjGrp(String own_grp_id, String prj_id, String use_grp_id){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("* \n");
		sql.append("FROM pj_prj_grp \n");
		sql.append("WHERE status <> 'DELETE' \n");
		if(own_grp_id != null && !"".equals(own_grp_id)){
			sql.append("AND own_grp_id = ? \n");
			paramV.add(own_grp_id);
		}
		if(prj_id != null && !"".equals(prj_id)){
			sql.append("AND prj_id = ? \n");
			paramV.add(prj_id);
		}
		if(use_grp_id != null && !"".equals(use_grp_id)){
			sql.append("AND use_grp_id = ? \n");
			paramV.add(use_grp_id);
		}
		sql.append("ORDER BY grp_type, res_date DESC");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * pj_prj_grp 테이블 정보 가져오는 메소드. 자기 빼고 공유된 그룹만.
	 * @param own_grp_id
	 * @param prj_id
	 * @return
	 */
	public DataEntity[] getPrjGrpShared(String own_grp_id, String prj_id){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("* \n");
		sql.append("FROM pj_prj_grp \n");
		sql.append("WHERE status <> 'DELETE' \n");
		if(own_grp_id != null && !"".equals(own_grp_id)){
			sql.append("AND own_grp_id = ? \n");
			paramV.add(own_grp_id);
		}
		if(prj_id != null && !"".equals(prj_id)){
			sql.append("AND prj_id = ? \n");
			paramV.add(prj_id);
		}
		if(own_grp_id != null && !"".equals(own_grp_id)){
			sql.append("AND use_grp_id <> ? \n");
			paramV.add(own_grp_id);
		}
		sql.append("ORDER BY grp_type, res_date DESC");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * pj_prj_grp_user 테이블 정보를 가져오는 메소드.
	 * @param own_grp_id
	 * @param prj_id
	 * @param use_grp_id
	 * @param use_email
	 * @return
	 */
	public DataEntity[] getPrjGrpUser(String own_grp_id, String prj_id, String use_grp_id, String use_email){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("* \n");
		sql.append("FROM pj_prj_grp_user \n");
		sql.append("WHERE status <> 'DELETE' \n");
		if(own_grp_id != null && !"".equals(own_grp_id)){
			sql.append("AND own_grp_id = ? \n");
			paramV.add(own_grp_id);
		}
		if(prj_id != null && !"".equals(prj_id)){
			sql.append("AND prj_id = ? \n");
			paramV.add(prj_id);
		}
		if(use_grp_id != null && !"".equals(use_grp_id)){
			sql.append("AND use_grp_id = ? \n");
			paramV.add(use_grp_id);
		}
		if(use_email != null && !"".equals(use_email)){
			sql.append("AND use_email = ? \n");
			paramV.add(use_email);
		}
		sql.append("ORDER BY reg_time");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * pj_prj_mapping 테이블 정보를 가져오는 메소드.
	 * @param prj_id
	 * @return
	 */
	public DataEntity[] getPrjMapping(String prj_id){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("* \n");
		sql.append("FROM pj_prj_mapping \n");
		sql.append("WHERE status <> 'DELETE' \n");
		
		if(prj_id != null && !"".equals(prj_id)){
			sql.append("AND prj_id = ? \n");
			paramV.add(prj_id);
		}
		sql.append("ORDER BY onum");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * pj_prj 테이블 데이터 업데이트.
	 * @param updateEntity
	 * @param prj_id
	 * @return
	 */
	public int updatePrj(DataEntity updateEntity, String prj_id){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(prj_id != null){whereEntity.put("prj_id", prj_id);}
		if(!whereEntity.isEmpty()){
			result = dao.updateData(property, "pj_prj", updateEntity, whereEntity);
		}
		return result;
	}
	
	/**
	 * pj_prj_grp 테이블 데이터 업데이트.
	 * @param updateEntity
	 * @param own_grp_id
	 * @param prj_id
	 * @param use_grp_id
	 * @return
	 */
	public int updatePrjGrp(DataEntity updateEntity, String own_grp_id, String prj_id, String use_grp_id){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(own_grp_id != null){whereEntity.put("own_grp_id", own_grp_id);}
		if(prj_id != null){whereEntity.put("prj_id", prj_id);}
		if(use_grp_id != null){whereEntity.put("use_grp_id", use_grp_id);}
		if(!whereEntity.isEmpty()){
			result = dao.updateData(property, "pj_prj_grp", updateEntity, whereEntity);
		}
		return result;
	}
	
	/**
	 * pj_prj 테이블 삭제
	 * @param prj_id
	 * @param own_grp_id
	 * @return
	 */
	public int deletePrj(String prj_id, String own_grp_id){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(prj_id != null){whereEntity.put("prj_id", prj_id);}
		if(own_grp_id != null){whereEntity.put("own_grp_id", own_grp_id);}
		if(!whereEntity.isEmpty()){
			result = dao.deleteData(property, "pj_prj", whereEntity);
		}
		return result; 
	}
	
	/**
	 * pj_prj 테이블 상태 DELETE로 업데이트.
	 * @param prj_id
	 * @param own_grp_id
	 * @return
	 */
	public int delUpdPrj(String prj_id, String own_grp_id){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		DataEntity setEntity = new DataEntity();
		setEntity.put("status", "DELETE");
		if(prj_id != null){whereEntity.put("prj_id", prj_id);}
		if(own_grp_id != null){whereEntity.put("own_grp_id", own_grp_id);}
		if(!whereEntity.isEmpty()){
			result = dao.updateData(property, "pj_prj", setEntity, whereEntity);
		}
		return result;
	}
	
	/**
	 * pj_prj_grp 테이블 데이터 삭제
	 * @param prj_id
	 * @param own_grp_id
	 * @param use_grp_id
	 * @return
	 */
	public int deletePrjGrp(String own_grp_id, String prj_id, String use_grp_id){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(own_grp_id != null){whereEntity.put("own_grp_id", own_grp_id);}
		if(prj_id != null){whereEntity.put("prj_id", prj_id);}
		if(use_grp_id != null){whereEntity.put("use_grp_id", use_grp_id);}
		if(!whereEntity.isEmpty()){
			result = dao.deleteData(property, "pj_prj_grp", whereEntity);
		}
		return result;
	}
	
	/**
	 * pj_prj_grp 테이블 상태 DELETE로 업데이트.
	 * @param own_grp_id
	 * @param prj_id
	 * @param use_grp_id
	 * @return
	 */
	public int delUpdPrjGrp(String own_grp_id, String prj_id, String use_grp_id){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		DataEntity setEntity = new DataEntity();
		setEntity.put("status", "DELETE");
		if(own_grp_id != null){whereEntity.put("own_grp_id", own_grp_id);}
		if(prj_id != null){whereEntity.put("prj_id", prj_id);}
		if(use_grp_id != null){whereEntity.put("use_grp_id", use_grp_id);}
		if(!whereEntity.isEmpty()){
			result = dao.updateData(property, "pj_prj_grp", setEntity, whereEntity);
		}
		return result;
	}
	
	/**
	 * pj_prj_grp_user 테이블 데이터 삭제
	 * @param own_grp_id
	 * @param prj_id
	 * @param use_grp_id
	 * @param use_email
	 * @return
	 */
	public int deletePrjGrpUser(String own_grp_id, String prj_id, String use_grp_id, String use_email){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(own_grp_id != null){whereEntity.put("own_grp_id", own_grp_id);}
		if(prj_id != null){whereEntity.put("prj_id", prj_id);}
		if(use_grp_id != null){whereEntity.put("use_grp_id", use_grp_id);}
		if(use_email != null){whereEntity.put("use_email", use_email);}
		if(!whereEntity.isEmpty()){
			result = dao.deleteData(property, "pj_prj_grp_user", whereEntity);
		}
		return result;
	}
	
	/**
	 * pj_prj_grp_user 테이블 상태 DELETE로 업데이트.
	 * @param own_grp_id
	 * @param prj_id
	 * @param use_grp_id
	 * @param use_email
	 * @return
	 */
	public int delUpdPrjGrpUser(String own_grp_id, String prj_id, String use_grp_id, String use_email){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		DataEntity setEntity = new DataEntity();
		setEntity.put("status", "DELETE");
		if(own_grp_id != null){whereEntity.put("own_grp_id", own_grp_id);}
		if(prj_id != null){whereEntity.put("prj_id", prj_id);}
		if(use_grp_id != null){whereEntity.put("use_grp_id", use_grp_id);}
		if(use_email != null){whereEntity.put("use_email", use_email);}
		if(!whereEntity.isEmpty()){
			result = dao.updateData(property, "pj_prj_grp_user", setEntity, whereEntity);
		}
		return result;
	}
	
	/**
	 * pj_prj_mapping 테이블 데이터 삭제
	 * @param prj_id
	 * @param field_id
	 * @param onum
	 * @return
	 */
	public int deletePrjMapping(String prj_id, String field_id, String onum){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(prj_id != null){whereEntity.put("prj_id", prj_id);}
		if(field_id != null){whereEntity.put("field_id", field_id);}
		if(onum != null){whereEntity.put("onum", onum);}
		if(!whereEntity.isEmpty()){
			result = dao.deleteData(property, "pj_prj_mapping", whereEntity);
		}
		return result;
	}
	
	/**
	 * pj_prj_mapping 테이블 상태 DELETE로 업데이트.
	 * @param prj_id
	 * @param field_id
	 * @param onum
	 * @return
	 */
	public int delUpdPrjMapping(String prj_id, String field_id, String onum){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		DataEntity setEntity = new DataEntity();
		setEntity.put("status", "DELETE");
		if(prj_id != null){whereEntity.put("prj_id", prj_id);}
		if(field_id != null){whereEntity.put("field_id", field_id);}
		if(onum != null){whereEntity.put("onum", onum);}
		if(!whereEntity.isEmpty()){
			result = dao.updateData(property, "pj_prj_mapping", setEntity, whereEntity);
		}
		return result;
	}
	
}
