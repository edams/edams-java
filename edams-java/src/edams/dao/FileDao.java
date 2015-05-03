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

public class FileDao {

	private static FileDao instance = null;
	private JmProperties property = null;
	Dao dao = null;
	
	private FileDao() throws FileNotFoundException{
		property = CommProp.getJmProperties();
		dao = Dao.getInstance();
	}
	
	public static FileDao getInstance() throws FileNotFoundException {
		if(instance == null){
			instance = new FileDao();
		}
		return instance;
	}
	
	/**
	 * fl_file 테이블에 데이터 입력.
	 * @param grp_id
	 * @param email
	 * @param filename
	 * @param src_file_path
	 * @param tot_lines
	 * @param file_type
	 * @return
	 */
	public int insertFlFile(String grp_id, String email, String file_name, String src_file_path, String file_size, String tot_lines, String file_type){
		int result = 0;
		DataEntity data = new DataEntity();
		data.put("reg_time", new Date());
		if(grp_id != null && !"".equals(grp_id)){ data.put("grp_id", grp_id); }
		if(email != null && !"".equals(email)){ data.put("email", email); }
		if(file_name != null && !"".equals(file_name)){ data.put("file_name", file_name); }
		if(src_file_path != null && !"".equals(src_file_path)){ data.put("src_file_path", src_file_path); }
		if(file_size != null && !"".equals(file_size)){ data.put("file_size", file_size); }
		if(tot_lines != null && !"".equals(tot_lines)){ data.put("tot_lines", tot_lines); }
		if(file_type != null && !"".equals(file_type)){ data.put("file_type", file_type); }
		result = dao.insertData(property, "fl_file", data);
		return result;
	}
	
	/**
	 * fl_file 테이블 업데이트.
	 * @param updateEntity
	 * @param grp_id
	 * @param email
	 * @param filename
	 * @return
	 */
	public int updateFlFile(DataEntity updateEntity, String grp_id, String email, String file_name ){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(grp_id != null){whereEntity.put("grp_id", grp_id);}
		if(email != null){whereEntity.put("email", email);}
		if(file_name != null){whereEntity.put("file_name", file_name);}
		if(!whereEntity.isEmpty()){
			result = dao.updateData(property, "fl_file", updateEntity, whereEntity);
		}
		return result;
	}
	
	/**
	 * fl_file 테이블 정보 조회
	 * @param grp_id
	 * @param email
	 * @param filename
	 * @return
	 */
	public DataEntity[] getFlFile(String grp_id, String email, String file_name){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("* \n");
		sql.append("FROM fl_file \n");
		sql.append("WHERE 1=1 \n");
		
		if(grp_id != null && !"".equals(grp_id)){
			sql.append("AND grp_id = ? \n");
			paramV.add(grp_id);
		}
		if(email != null && !"".equals(email)){
			sql.append("AND email = ? \n");
			paramV.add(email);
		}
		if(file_name != null && !"".equals(file_name)){
			sql.append("AND file_name = ? \n");
			paramV.add(file_name);
		}
		sql.append("ORDER BY reg_time");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * fl_file 테이블 데이터 삭제. 초기화.
	 * @param grp_id
	 * @param email
	 * @param filename
	 * @return
	 */
	public int deleteFlFile(String grp_id, String email, String file_name){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(grp_id != null){whereEntity.put("grp_id", grp_id);}
		if(email != null){whereEntity.put("email", email);}
		if(file_name != null){whereEntity.put("file_name", file_name);}
		if(!whereEntity.isEmpty()){
			result = dao.deleteData(property, "fl_file", whereEntity);
		}
		return result; 		
	}
	
	/**
	 * fl_processing 테이블에 데이터 입력.
	 * @param src_file_path
	 * @param dest_file_path
	 * @param proc_lines
	 * @param tot_lines
	 * @param file_type
	 * @param status
	 * @return
	 */
	public int insertFiProc(String src_file_path, String dest_file_path, String prj_id, String grp_id, String proc_lines, String tot_lines, String file_type, String status){
		int result = 0;
		DataEntity data = new DataEntity();
		data.put("src_file_path", src_file_path);
		data.put("ins_date", new Date());
		if(dest_file_path != null && !"".equals(dest_file_path)){ data.put("dest_file_path", dest_file_path); }
		if(prj_id != null && !"".equals(prj_id)){ data.put("prj_id", prj_id); }
		if(grp_id != null && !"".equals(grp_id)){ data.put("grp_id", grp_id); }
		if(proc_lines != null && !"".equals(proc_lines)){ data.put("proc_lines", proc_lines); }
		if(tot_lines != null && !"".equals(tot_lines)){ data.put("tot_lines", tot_lines); }
		if(file_type != null && !"".equals(file_type)){ data.put("file_type", file_type); }
		if(status != null && !"".equals(status)){ data.put("status", status); }
		result = dao.insertData(property, "fl_processing", data);
		return result;
	}
	
	/**
	 * fl_processing 테이블 업데이트.
	 * @param updateEntity
	 * @param src_file_path
	 * @return
	 */
	public int updateFiProc(DataEntity updateEntity, String src_file_path){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(src_file_path != null){whereEntity.put("src_file_path", src_file_path);}
		if(!whereEntity.isEmpty()){
			result = dao.updateData(property, "fl_processing", updateEntity, whereEntity);
		}
		return result;
	}
	
	/**
	 * fl_processing 테이블 정보 가져오는 메소드.
	 * @param src_file_path
	 * @return
	 */
	public DataEntity[] getFiProc(String src_file_path, String prj_id, String grp_id){
		DataEntity[] data = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT \n");
		sql.append("* \n");
		sql.append("FROM fl_processing \n");
		sql.append("WHERE 1=1 \n");
		
		if(src_file_path != null && !"".equals(src_file_path)){
			sql.append("AND src_file_path = ? \n");
			paramV.add(src_file_path);
		}
		if(prj_id != null && !"".equals(prj_id)){
			sql.append("AND prj_id = ? \n");
			paramV.add(prj_id);
		}
		if(grp_id != null && !"".equals(grp_id)){
			sql.append("AND grp_id = ? \n");
			paramV.add(grp_id);
		}
		sql.append("ORDER BY ins_date");
		
		String[] params = paramV.toArray(new String[paramV.size()]);
		data = dao.getResult(property, sql.toString(), params);
		return data;
	}
	
	/**
	 * fl_processing 테이블 데이터 삭제. 초기화.
	 * @param src_file_path
	 * @return
	 */
	public int deleteFiProc(String src_file_path, String prj_id, String grp_id){
		int result = 0;
		DataEntity whereEntity = new DataEntity();
		if(src_file_path != null){whereEntity.put("src_file_path", src_file_path);}
		if(prj_id != null){whereEntity.put("prj_id", prj_id);}
		if(grp_id != null){whereEntity.put("grp_id", grp_id);}
		if(!whereEntity.isEmpty()){
			result = dao.deleteData(property, "fl_processing", whereEntity);
		}
		return result; 		
	}
	
}
