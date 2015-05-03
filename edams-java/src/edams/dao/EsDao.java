package edams.dao;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import edams.comm.CommProp;
import edams.comm.CommUserObj;
import jm.com.Encrypt;
import jm.com.JmProperties;
import jm.net.Dao;
import jm.net.DataEntity;

public class EsDao {

	private static EsDao instance = null;
	private JmProperties property = null;
	Dao dao = null;
	
	private EsDao() throws FileNotFoundException{
		property = CommProp.getJmProperties();
		dao = Dao.getInstance();
	}
	
	public static EsDao getInstance() throws FileNotFoundException {
		if(instance == null){
			instance = new EsDao();
		}
		return instance;
	}
	
	/**
	 * es_es 테이블에 데이터 입력.
	 * @param grp_id
	 * @param email
	 * @param es_id
	 * @param es_url
	 * @param es_cluster
	 * @param es_port
	 * @param es_dport
	 * @return
	 */
	public int insertEsEs(String grp_id, String email, String es_id, String es_url, String es_cluster, String es_port, String es_dport){
		int result = 0;
		DataEntity data = new DataEntity();
		data.put("reg_time", new Date());
		if(grp_id != null && !"".equals(grp_id)){ data.put("grp_id", grp_id); }
		if(email != null && !"".equals(email)){ data.put("email", email); }
		if(es_id != null && !"".equals(es_id)){ data.put("es_id", es_id); }
		if(es_url != null && !"".equals(es_url)){ data.put("es_url", es_url); }
		if(es_cluster != null && !"".equals(es_cluster)){ data.put("es_cluster", es_cluster); }
		if(es_port != null && !"".equals(es_port)){ data.put("es_port", es_port); }
		if(es_dport != null && !"".equals(es_dport)){ data.put("es_dport", es_dport); }
		result = dao.insertData(property, "es_es", data);
		return result;
	}
	
	/**
	 * es_es 테이블 업데이트.
	 * @param updateEntity
	 * @param grp_id
	 * @param email
	 * @param es_id
	 * @return
	 */
	public int updateEsEs(DataEntity updateEntity, String grp_id, String email, String es_id ){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(grp_id != null){whereEntity.put("grp_id", grp_id);}
		if(email != null){whereEntity.put("email", email);}
		if(es_id != null){whereEntity.put("es_id", es_id);}
		if(!whereEntity.isEmpty()){
			result = dao.updateData(property, "es_es", updateEntity, whereEntity);
		}
		return result;
	}
	
	/**
	 * es_es 테이블 정보 조회
	 * @param grp_id
	 * @param email
	 * @param es_id
	 * @return
	 */
	public DataEntity[] getEsEs(String grp_id, String email, String es_id){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("* \n");
		sql.append("FROM es_es \n");
		sql.append("WHERE 1=1 \n");
		
		if(grp_id != null && !"".equals(grp_id)){
			sql.append("AND grp_id = ? \n");
			paramV.add(grp_id);
		}
		if(email != null && !"".equals(email)){
			sql.append("AND email = ? \n");
			paramV.add(email);
		}
		if(es_id != null && !"".equals(es_id)){
			sql.append("AND es_id = ? \n");
			paramV.add(es_id);
		}
		sql.append("ORDER BY reg_time");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * es_es 테이블 데이터 삭제. 초기화.
	 * @param grp_id
	 * @param email
	 * @param es_id
	 * @return
	 */
	public int deleteEsEs(String grp_id, String email, String es_id){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(grp_id != null){whereEntity.put("grp_id", grp_id);}
		if(email != null){whereEntity.put("email", email);}
		if(es_id != null){whereEntity.put("es_id", es_id);}
		if(!whereEntity.isEmpty()){
			result = dao.deleteData(property, "es_es", whereEntity);
		}
		return result; 		
	}
	
}
