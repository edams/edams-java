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

public class AcDao {

	private static AcDao instance = null;
	private JmProperties property = null;
	Dao dao = null;
	
	private AcDao() throws FileNotFoundException{
		property = CommProp.getJmProperties();
		dao = Dao.getInstance();
	}
	
	public static AcDao getInstance() throws FileNotFoundException {
		if(instance == null){
			instance = new AcDao();
		}
		return instance;
	}
	

	/**
	 *  request 로 부터 CommUserObj 객체를 생성하는 메서드.
	 * @param req
	 * @return
	 */
	public CommUserObj getUserObj(HttpServletRequest req){
		return getUserObj(req, new CommUserObj());
	}
	
	/**
	 * request 로 부터 CommUserObj 객체를 생성하는 메서드.
	 * @param req
	 * @param obj
	 * @return
	 */
	public CommUserObj getUserObj(HttpServletRequest req, CommUserObj obj){
		try {
			if(req.getParameter("grpId") != null)
				obj.setGrpId(req.getParameter("grpId"));
			if(req.getParameter("usrType") != null)
				obj.setUsrType(req.getParameter("usrType"));
			if(req.getParameter("email") != null)
				obj.setEmail(req.getParameter("email"));
			if(req.getParameter("passwd") != null)
				obj.setPasswd(req.getParameter("passwd"));
			if(req.getParameter("name") != null)
				obj.setName(req.getParameter("name"));
			if(req.getParameter("nicname") != null)
				obj.setNicname(req.getParameter("nicname"));
			if(req.getParameter("birthY") != null && req.getParameter("birthM") != null && req.getParameter("birthD") != null){
				Date fullDate = new Date((new SimpleDateFormat("yyyyMMdd").parse(req.getParameter("birthY")+req.getParameter("birthM")+req.getParameter("birthD"))).getTime());
				obj.setBirthday(fullDate);
			}
			if(req.getParameter("phone") != null)
				obj.setPhone(req.getParameter("phone"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	/**
	 * 아이디(이메일)로 부터 CommUserObj 객체 정보를 가져오는 메소드.
	 * @param email
	 * @return
	 */
	public CommUserObj getUserObj(String email){
		CommUserObj userObj  = null;
		DataEntity[] datas = null;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT \n");
		sql.append("A.* \n");
		sql.append(",B.grp_name \n");
		sql.append(",B.grp_text \n");
		sql.append(",B.grp_dept_id \n");
		sql.append("FROM \n");
		sql.append("ac_user A, ac_grp B \n");
		sql.append("WHERE A.grp_id = B.grp_id \n");
		sql.append("AND A.email = ? \n");
		
		String[] params = { email };
		datas = dao.getResult(property, sql.toString(), params);
		if(datas.length == 1){
			userObj = new CommUserObj(datas[0]);
		}
		return userObj;
	}
	
	/**
	 * CommUserObj 객체를 DB에 저장.
	 * @param userObj
	 * @return
	 */
	public int insertUserObj(CommUserObj userObj){
		int result = 0;
		DataEntity data = new DataEntity();
		
		String passwd = Encrypt.getSha256(userObj.getPasswd());
		if(passwd != null && !"".equals(passwd)){
			data.put("grp_id", userObj.getGrpId());
			data.put("user_type", userObj.getUsrType());
			data.put("email", userObj.getEmail());
			data.put("passwd", passwd);
			data.put("name", userObj.getName());
			data.put("nicname", userObj.getNicname());
			data.put("birthday", userObj.getBirthday());
			if(userObj.getPhone() != null && !"".equals(userObj.getPhone())){
				data.put("phone", userObj.getPhone());
			}
			data.put("reg_time", new Date());
			result = dao.insertData(property, "ac_user", data);
		}
		return result;
	}
	
	/**
	 * CommUserObj 객체를 DB에 업데이트.
	 * @param userObj
	 * @return
	 */
	public int updateUserObj(CommUserObj userObj){
		int result = 0;
		
		String passwd = Encrypt.getSha256(userObj.getPasswd());
		DataEntity setData = new DataEntity();
		setData.put("email", userObj.getEmail());
		setData.put("passwd", passwd);
		setData.put("name", userObj.getName());
		setData.put("nicname", userObj.getNicname());
		setData.put("birthday", userObj.getBirthday());
		if(userObj.getPhone() != null && !"".equals(userObj.getPhone())){
			setData.put("phone", userObj.getPhone());
		}
		DataEntity whereData = new DataEntity();
		whereData.put("email", userObj.getEmail());
		
		result = dao.updateData(property, "mp_user", setData, whereData);
		
		return result;
	}
	
	/**
	 * 로그인 확인 메서드.
	 * 0:ID 없음, 1:PW오류, 2:로그인, 9:오류
	 * @param id
	 * @param passwd
	 * @return
	 */
	public int login(String email, String rawPasswd) {
		String passwd = Encrypt.getSha256(rawPasswd);
		
		StringBuffer sql = new StringBuffer();
		String tempPw = "";
		String[] param = {email};
		int result = 9;
		
		sql.append("SELECT passwd FROM ac_user WHERE email = ?");
		
		DataEntity[] entity = dao.getResult(property, sql.toString(), param);
		
		if(entity != null && entity.length == 1){
			tempPw = (String)entity[0].get("passwd");
			if (tempPw.equals(passwd)) {
				result = 2;
			} else {
				result = 1;
			}
		} else { 
			result = 0;
		}
		return result;
	}
	
	/**
	 * 그룹 정보 입력
	 * @param grpId
	 * @param grpName
	 * @param grpText
	 * @param grpDeptId
	 * @return
	 */
	public int insertGrp(String grpId, String grpName, String grpText, String grpDeptId){
		int result = 0;
		DataEntity data = new DataEntity();
		data.put("grp_id", grpId);
		data.put("grp_name", grpName);
		data.put("grp_text", grpText);
		if(grpDeptId != null && !"".equals(grpDeptId)){ data.put("grp_dept_id", grpDeptId); }
		data.put("reg_time", new Date());
		result = dao.insertData(property, "ac_grp", data);
		return result;
	}
	
	/**
	 * 그룹 정보 불러오기
	 * @return
	 */
	public DataEntity[] getGrp(){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT \n");
		sql.append("A.* \n");
		sql.append(",(SELECT COUNT(*) FROM ac_user B WHERE A.grp_id = B.grp_id) AS user_cnt \n");
		sql.append(",(SELECT MAX(C.email) FROM ac_user C WHERE A.grp_id = C.grp_id AND C.user_type = 'ADMIN') AS admin_email \n");
		sql.append("FROM ac_grp A \n");
		sql.append("WHERE status <> 'DELETE' \n");
		return dao.getResult(property, sql.toString(), null);
		
	}
	
	/**
	 * 그룹 소속 정보 불러오기
	 * @return
	 */
	public DataEntity[] getGrpDept(){
		String sql = "SELECT * FROM ac_grp_dept WHERE status <> 'DELETE'";
		return dao.getResult(property, sql, null);
	}
	
	/**
	 * 이메일 중복여부 확인
	 * @param email
	 * @return
	 */
	public boolean isExistMail(String email) {
		StringBuffer sql = new StringBuffer();
		String[] param = {email};
		sql.append("SELECT count(*) as cnt FROM ac_user WHERE email = ?");
		int cnt = dao.getCount(property, sql.toString(), param);
		
		if (cnt == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 닉네임 중복여부 확인
	 * @param email
	 * @return
	 */
	public boolean isExistNicname(String nicname) {
		StringBuffer sql = new StringBuffer();
		String[] param = {nicname};
		sql.append("SELECT count(*) as cnt FROM ac_user WHERE nicname = ?");
		int cnt = dao.getCount(property, sql.toString(), param);
		
		if (cnt == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 그룹 ID 중복여부 확인
	 * @param grpId
	 * @return
	 */
	public boolean isExistGrpId(String grpId){
		StringBuffer sql = new StringBuffer();
		String[] param = {grpId};
		sql.append("SELECT count(*) as cnt FROM ac_grp WHERE grp_id = ?");
		int cnt = dao.getCount(property, sql.toString(), param);
		
		if (cnt == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	
	/**
	 * 사용자 정보를 DataEntity 로 돌려주는 메서드.
	 * @param email
	 * @param grp_id
	 * @return
	 */
	public DataEntity[] getAcUser(String email, String grp_id){
		DataEntity[] datas = null;
		Vector<String> paramV = new Vector<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT \n");
		sql.append("A.* \n");
		sql.append(",B.grp_name \n");
		sql.append(",B.grp_text \n");
		sql.append(",B.grp_dept_id \n");
		sql.append("FROM \n");
		sql.append("ac_user A, ac_grp B \n");
		sql.append("WHERE A.grp_id = B.grp_id \n");
		if(email != null && !"".equals(email)){
			sql.append("AND A.email = ? \n");
			paramV.add(email);
		}
		if(grp_id != null && !"".equals(grp_id)){
			sql.append("AND A.grp_id = ? \n");
			paramV.add(grp_id);
		}
		String[] params = paramV.toArray(new String[paramV.size()]);
		datas = dao.getResult(property, sql.toString(), params);
		return datas;
	}
	public DataEntity[] getAcUser(String email){
		return getAcUser(email, null);
	}
}
