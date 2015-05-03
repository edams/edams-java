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

public class MapDao {

	private static MapDao instance = null;
	private JmProperties property = null;
	Dao dao = null;
	
	private MapDao() throws FileNotFoundException{
		property = CommProp.getJmProperties();
		dao = Dao.getInstance();
	}
	
	public static MapDao getInstance() throws FileNotFoundException {
		if(instance == null){
			instance = new MapDao();
		}
		return instance;
	}
	
	/**
	 * mp_mapping 테이블에 데이터 입력.
	 * @param grp_id
	 * @param email
	 * @param map_id
	 * @param src_file
	 * @return
	 */
	public int insertMpMapping(String grp_id, String email, String map_id, String src_file){
		int result = 0;
		DataEntity data = new DataEntity();
		data.put("reg_time", new Date());
		if(grp_id != null && !"".equals(grp_id)){ data.put("grp_id", grp_id); }
		if(email != null && !"".equals(email)){ data.put("email", email); }
		if(map_id != null && !"".equals(map_id)){ data.put("map_id", map_id); }
		if(src_file != null && !"".equals(src_file)){ data.put("src_file", src_file); }
		result = dao.insertData(property, "mp_mapping", data);
		return result;
	}
	
	/**
	 * mp_mapping 테이블 업데이트.
	 * @param updateEntity
	 * @param grp_id
	 * @param email
	 * @param map_id
	 * @return
	 */
	public int updateMpMapping(DataEntity updateEntity, String grp_id, String email, String map_id ){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(grp_id != null){whereEntity.put("grp_id", grp_id);}
		if(email != null){whereEntity.put("email", email);}
		if(map_id != null){whereEntity.put("map_id", map_id);}
		if(!whereEntity.isEmpty()){
			result = dao.updateData(property, "mp_mapping", updateEntity, whereEntity);
		}
		return result;
	}
	
	/**
	 * mp_mapping 테이블 정보 조회
	 * @param grp_id
	 * @param email
	 * @param map_id
	 * @return
	 */
	public DataEntity[] getMpMapping(String grp_id, String email, String map_id){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("* \n");
		sql.append("FROM mp_mapping \n");
		sql.append("WHERE 1=1 \n");
		
		if(grp_id != null && !"".equals(grp_id)){
			sql.append("AND grp_id = ? \n");
			paramV.add(grp_id);
		}
		if(email != null && !"".equals(email)){
			sql.append("AND email = ? \n");
			paramV.add(email);
		}
		if(map_id != null && !"".equals(map_id)){
			sql.append("AND map_id = ? \n");
			paramV.add(map_id);
		}
		sql.append("ORDER BY reg_time");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * mp_mapping 테이블 데이터 삭제. 초기화.
	 * @param grp_id
	 * @param email
	 * @param map_id
	 * @return
	 */
	public int deleteMpMapping(String grp_id, String email, String map_id){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(grp_id != null){whereEntity.put("grp_id", grp_id);}
		if(email != null){whereEntity.put("email", email);}
		if(map_id != null){whereEntity.put("map_id", map_id);}
		if(!whereEntity.isEmpty()){
			result = dao.deleteData(property, "mp_mapping", whereEntity);
		}
		return result; 		
	}
	
	/**
	 * mp_mapping_fields 테이블에 데이터 입력.
	 * @param grp_id
	 * @param email
	 * @param map_id
	 * @param name
	 * @param type
	 * @param date_format
	 * @return
	 */
	public int insertMpMappingFields(String grp_id, String email, String map_id, String onum, String name, String type, String date_format, String value){
		int result = 0;
		DataEntity data = new DataEntity();
		data.put("reg_time", new Date());
		if(grp_id != null && !"".equals(grp_id)){ data.put("grp_id", grp_id); }
		if(email != null && !"".equals(email)){ data.put("email", email); }
		if(map_id != null && !"".equals(map_id)){ data.put("map_id", map_id); }
		if(onum != null && !"".equals(onum)){ data.put("onum", onum); }
		if(name != null && !"".equals(name)){ data.put("name", name); }
		if(type != null && !"".equals(type)){ data.put("type", type); }
		if(date_format != null && !"".equals(date_format)){ data.put("date_format", date_format); }
		if(value != null && !"".equals(value)){ data.put("value", value); }
		result = dao.insertData(property, "mp_mapping_fields", data);
		return result;
	}
	
	/**
	 * mp_mapping_fields 테이블 업데이트.
	 * @param updateEntity
	 * @param grp_id
	 * @param email
	 * @param map_id
	 * @return
	 */
	public int updateMpMappingFields(DataEntity updateEntity, String grp_id, String email, String map_id, String onum){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(grp_id != null){whereEntity.put("grp_id", grp_id);}
		if(email != null){whereEntity.put("email", email);}
		if(map_id != null){whereEntity.put("map_id", map_id);}
		if(onum != null){whereEntity.put("onum", onum);}
		if(!whereEntity.isEmpty()){
			result = dao.updateData(property, "mp_mapping_fields", updateEntity, whereEntity);
		}
		return result;
	}
	
	/**
	 * mp_mapping_fields 테이블 정보 조회
	 * @param grp_id
	 * @param email
	 * @param map_id
	 * @return
	 */
	public DataEntity[] getMpMappingFields(String grp_id, String email, String map_id, String onum){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("* \n");
		sql.append("FROM mp_mapping_fields \n");
		sql.append("WHERE 1=1 \n");
		
		if(grp_id != null && !"".equals(grp_id)){
			sql.append("AND grp_id = ? \n");
			paramV.add(grp_id);
		}
		if(email != null && !"".equals(email)){
			sql.append("AND email = ? \n");
			paramV.add(email);
		}
		if(map_id != null && !"".equals(map_id)){
			sql.append("AND map_id = ? \n");
			paramV.add(map_id);
		}
		if(onum != null && !"".equals(onum)){
			sql.append("AND onum = ? \n");
			paramV.add(map_id);
		}
		sql.append("ORDER BY onum");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * mp_mapping_fields 필드 갯수 가져오는 메서드.
	 * @param grp_id
	 * @param email
	 * @param map_id
	 * @param onum
	 * @return
	 */
	public int getMpMappingFieldCnt(String grp_id, String email, String map_id, String onum){
		int cnt = 0;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("count(*) \n");
		sql.append("FROM mp_mapping_fields \n");
		sql.append("WHERE 1=1 \n");
		
		if(grp_id != null && !"".equals(grp_id)){
			sql.append("AND grp_id = ? \n");
			paramV.add(grp_id);
		}
		if(email != null && !"".equals(email)){
			sql.append("AND email = ? \n");
			paramV.add(email);
		}
		if(map_id != null && !"".equals(map_id)){
			sql.append("AND map_id = ? \n");
			paramV.add(map_id);
		}
		if(onum != null && !"".equals(onum)){
			sql.append("AND onum = ? \n");
			paramV.add(map_id);
		}
		sql.append("ORDER BY onum");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		cnt = dao.getCount(property, sql.toString(), params);
		return cnt;
	}
	
	/**
	 * mp_mapping_fields 테이블 데이터 삭제. 초기화.
	 * @param grp_id
	 * @param email
	 * @param map_id
	 * @return
	 */
	public int deleteMpMappingFields(String grp_id, String email, String map_id, String onum){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(grp_id != null){whereEntity.put("grp_id", grp_id);}
		if(email != null){whereEntity.put("email", email);}
		if(map_id != null){whereEntity.put("map_id", map_id);}
		if(onum != null){whereEntity.put("onum", onum);}
		if(!whereEntity.isEmpty()){
			result = dao.deleteData(property, "mp_mapping_fields", whereEntity);
		}
		return result; 		
	}
	
}
