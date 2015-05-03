package edams.dao;

import java.io.FileNotFoundException;

import edams.comm.CommProp;
import jm.com.JmProperties;
import jm.net.Dao;

public class SysDao {
	private static SysDao instance = null;
	private JmProperties property = null;
	
	Dao dao = null;
	
	private SysDao() throws FileNotFoundException{
		property = CommProp.getJmProperties();
		dao = Dao.getInstance();
	}
	
	public static SysDao getInstance() throws FileNotFoundException {
		if(instance == null){
			instance = new SysDao();
		}
		return instance;
	}
	
	/**
	 * 태이블 있는지 확인.
	 * @param tableName
	 * @return
	 */
	public int checkTab(String tableName, String dbName){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT EXISTS ( \n");
		sql.append("SELECT 1  FROM Information_schema.tables  \n"); 
		sql.append("WHERE table_name = ?  \n");
		sql.append("AND table_schema = ?  \n");
		sql.append(") AS flag");
		String[] params = {tableName, dbName};
		result = dao.getCount(property, sql.toString(), params);
		return result;
	}
	
	public int insert_ac_grp(){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `ac_grp` ( \n");
		sql.append("`reg_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '등록일', \n");
		sql.append("`grp_id` varchar(50) NOT NULL DEFAULT '' COMMENT '그룹 아이디', \n");
		sql.append("`grp_name` varchar(100) DEFAULT NULL COMMENT '그룹명', \n");
		sql.append("`grp_text` varchar(250) DEFAULT NULL COMMENT '그룹 설명', \n");
		sql.append("`grp_dept_id` varchar(50) DEFAULT NULL COMMENT '그룹 구분 아이디', \n");
		sql.append("`owner` varchar(100) DEFAULT NULL COMMENT '등록자 아이디(이메일)', \n");
		sql.append("`status` varchar(10) DEFAULT 'ACTIVE' COMMENT 'ACTIVE: 활성 / DELETE: 삭제(비활성)', \n");
		sql.append("PRIMARY KEY (`reg_time`,`grp_id`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
	public int insert_ac_grp_dept(){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `ac_grp_dept` ( \n");
		sql.append("`grp_dept_id` varchar(50) NOT NULL DEFAULT '' COMMENT '그룹 구분 아이디', \n");
		sql.append("`grp_dept_name` varchar(100) DEFAULT NULL COMMENT '그룹 구분명', \n");
		sql.append("`grp_dept_text` varchar(250) DEFAULT NULL COMMENT '그룹 구분 설명', \n");
		sql.append("`status` varchar(10) DEFAULT 'ACTIVE' COMMENT 'ACTIVE: 활성 / DELETE: 삭제(비활성)', \n");
		sql.append("PRIMARY KEY (`grp_dept_id`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
	public int insert_ac_user(){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `ac_user` ( \n");
		sql.append("`reg_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '등록일', \n");
		sql.append("`grp_id` varchar(50) NOT NULL DEFAULT '' COMMENT '그룹 아이디', \n");
		sql.append("`email` varchar(100) NOT NULL DEFAULT '' COMMENT '아이디(이메일)', \n");
		sql.append("`user_type` varchar(20) DEFAULT 'USER' COMMENT 'USER: 일반사용자 / ADMIN: 관리자', \n");
		sql.append("`passwd` varchar(200) DEFAULT NULL COMMENT 'SHA256 알고리즘 암호화', \n");
		sql.append("`name` varchar(20) DEFAULT NULL COMMENT '이름(본명)', \n");
		sql.append("`nicname` varchar(50) DEFAULT NULL COMMENT '닉네임', \n");
		sql.append("`birthday` date DEFAULT NULL COMMENT '생년월일', \n");
		sql.append("`phone` int(11) DEFAULT NULL COMMENT '연락처', \n");
		sql.append("`status` varchar(10) DEFAULT 'REG' COMMENT 'REG: 가입승인중 / ACTIVE: 가입완료(활성) / DELETE: 삭제(비활성)', \n");
		sql.append("PRIMARY KEY (`reg_time`,`grp_id`,`email`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
	public int insert_fl_processing(){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `fl_processing` ( \n");
		sql.append("`src_file_path` varchar(250) NOT NULL DEFAULT '' COMMENT '원본 파일', \n");
		sql.append("`ins_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '입력 날짜', \n");
		sql.append("`dest_file_path` varchar(250) DEFAULT NULL COMMENT '처리된 파일', \n");
		sql.append("`prj_id` varchar(50) DEFAULT NULL COMMENT '프로젝트 아이디', \n");
		sql.append("`grp_id` varchar(50) DEFAULT NULL COMMENT '그룹 아이디', \n");
		sql.append("`proc_lines` int(11) DEFAULT '0' COMMENT '처리된 라인 수', \n");
		sql.append("`error_lines` int(11) DEFAULT '0' COMMENT '오류 발생 라인 수', \n");
		sql.append("`tot_lines` int(11) DEFAULT '0' COMMENT '전체 파일 라인 수', \n");
		sql.append("`file_type` varchar(10) DEFAULT NULL COMMENT 'CSV, JSON', \n");
		sql.append("`error_msg` text COMMENT '오류 메시지', \n");
		sql.append("`status` varchar(15) DEFAULT 'PROCESSING' COMMENT 'PROCESSING : 처리중 / DONE : 완료 / ERROR : 오류', \n");
		sql.append("PRIMARY KEY (`src_file_path`,`ins_date`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
	public int insert_pj_prj(){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `pj_prj` ( \n");
		sql.append("`reg_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '프로젝트 등록 날짜', \n");
		sql.append("`prj_id` varchar(50) NOT NULL DEFAULT '' COMMENT '프로젝트 아이디', \n");
		sql.append("`own_grp_id` varchar(50) DEFAULT NULL COMMENT '프로젝트 등록 그룹 아이디', \n");
		sql.append("`own_email` varchar(100) DEFAULT NULL COMMENT '프로젝트 등록자 아이디(이메일)', \n");
		sql.append("`prj_name` varchar(100) DEFAULT NULL COMMENT '프로젝트명', \n");
		sql.append("`prj_text` varchar(250) DEFAULT NULL COMMENT '프로젝트 설명', \n");
		sql.append("`prj_es_url` varchar(200) DEFAULT NULL COMMENT '엘라스틱서치 URL', \n");
		sql.append("`prj_es_port` smallint(6) DEFAULT NULL COMMENT '엘라스틱서치 http 포트. 기본 : 9200', \n");
		sql.append("`prj_es_dport` smallint(6) DEFAULT NULL COMMENT '엘라스틱서치 데이터 포트. 기본 : 9300', \n");
		sql.append("`prj_es_cluster` varchar(50) DEFAULT NULL COMMENT '엘라스틱서치 클러스터명', \n");
		sql.append("`prj_es_node` varchar(50) DEFAULT NULL COMMENT '엘라스틱서치 노드명. 기본 : 랜덤 이름', \n");
		sql.append("`prj_es_index` varchar(50) DEFAULT NULL COMMENT '엘라스틱서치 인덱스명. 기본 : \"프로젝트명-%{+YYYY.MM.dd}\"', \n");
		sql.append("`prj_es_type` varchar(50) DEFAULT NULL COMMENT '엘라스틱서치 타입. 기본 : 입력자 아이디(이메일_변형)', \n");
		sql.append("`status` varchar(10) DEFAULT 'ACTIVE' COMMENT 'ACTIVE: 활성 / DELETE: 삭제(비활성)', \n");
		sql.append("PRIMARY KEY (`reg_time`,`prj_id`), \n");
		sql.append("KEY `PRJ_OWN_GRP_ID` (`own_grp_id`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
	public int insert_pj_prj_grp(){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `pj_prj_grp` ( \n");
		sql.append("`req_date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '요청일자 / 생성일자', \n");
		sql.append("`own_grp_id` varchar(50) NOT NULL DEFAULT '' COMMENT '프로젝트 소유 그룹 아이디', \n");
		sql.append("`prj_id` varchar(50) NOT NULL DEFAULT '' COMMENT '프로젝트 아이디', \n");
		sql.append("`use_grp_id` varchar(50) NOT NULL DEFAULT '' COMMENT '프로젝트 사용 그룹 아이디 (요청자)', \n");
		sql.append("`grp_type` varchar(20) DEFAULT 'VIEWER' COMMENT 'VIEWER: 조회가능 / USER: 데이터 등록 가능 / OWNER: 소유 그룹', \n");
		sql.append("`req_adm_email` varchar(100) DEFAULT NULL COMMENT '요청그룹 관리자 (요청자)', \n");
		sql.append("`req_msg` text COMMENT '요청 메시지', \n");
		sql.append("`res_adm_email` varchar(100) DEFAULT NULL COMMENT '소유그룹 관리자 (승인자)', \n");
		sql.append("`res_date` datetime DEFAULT NULL COMMENT '승인일자', \n");
		sql.append("`res_msg` text COMMENT '승인 메시지', \n");
		sql.append("`status` varchar(10) DEFAULT 'REQUEST' COMMENT 'REQUEST: 신청중 / ACTIVE: 승인 / DENIED: 반려 / DELETE: 삭제(비활성)', \n");
		sql.append("PRIMARY KEY (`req_date`,`own_grp_id`,`prj_id`,`use_grp_id`), \n");
		sql.append("KEY `PRJ_GRP_USER` (`use_grp_id`,`prj_id`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
	public int insert_pj_prj_grp_user(){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `pj_prj_grp_user` ( \n");
		sql.append("`reg_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '등록 시간', \n");
		sql.append("`own_grp_id` varchar(50) NOT NULL DEFAULT '' COMMENT '프로젝트 소유 그룹 아이디', \n");
		sql.append("`prj_id` varchar(50) NOT NULL DEFAULT '' COMMENT '프로젝트 아이디', \n");
		sql.append("`use_grp_id` varchar(50) NOT NULL DEFAULT '' COMMENT '프로젝트 사용 그룹 아이디', \n");
		sql.append("`use_email` varchar(100) NOT NULL DEFAULT '' COMMENT '프로젝트 배정자 아이디(이메일)', \n");
		sql.append("`user_type` varchar(20) DEFAULT 'VIEWER' COMMENT 'VIEWER: 조회 가능 / USER: 데이터 등록 가능 / OWNER: 소유자', \n");
		sql.append("`status` varchar(10) DEFAULT 'ACTIVE' COMMENT 'ACTIVE: 활성 / DELETE: 삭제(비활성)', \n");
		sql.append("PRIMARY KEY (`reg_time`,`own_grp_id`,`prj_id`,`use_grp_id`,`use_email`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
	public int insert_pj_prj_mapping(){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `pj_prj_mapping` ( \n");
		sql.append("`reg_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00', \n");
		sql.append("`prj_id` varchar(50) NOT NULL DEFAULT '', \n");
		sql.append("`onum` int(11) NOT NULL DEFAULT '0', \n");
		sql.append("`field_id` varchar(30) NOT NULL DEFAULT '', \n");
		sql.append("`field_type` varchar(10) DEFAULT NULL, \n");
		sql.append("`field_comment` varchar(200) DEFAULT NULL, \n");
		sql.append("`status` varchar(10) DEFAULT 'ACTIVE' COMMENT 'ACTIVE: 활성 / DELETE: 삭제(비활성)', \n");
		sql.append("PRIMARY KEY (`reg_time`,`prj_id`,`onum`,`field_id`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
	public int insert_fl_file(){
		int result=0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `fl_file` ( \n");
		sql.append("`reg_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '등록일', \n");
		sql.append("`grp_id` varchar(50) NOT NULL DEFAULT '' COMMENT '그룹 아이디', \n");
		sql.append("`email` varchar(100) NOT NULL DEFAULT '' COMMENT '아이디(이메일)', \n");
		sql.append("`file_name` varchar(100) NOT NULL DEFAULT '' COMMENT '파일명', \n");
		sql.append("`src_file_path` varchar(250) NOT NULL DEFAULT '' COMMENT '원본 파일 경로', \n");
		sql.append("`file_size` varchar(20) NOT NULL DEFAULT '' COMMENT '파일 크기', \n");
		sql.append("`proc_lines` int(11) DEFAULT '0' COMMENT '처리된 라인 수', \n");
		sql.append("`error_lines` int(11) DEFAULT '0' COMMENT '오류 발생 라인 수', \n");
		sql.append("`tot_lines` int(11) DEFAULT '0' COMMENT '전체 파일 라인 수', \n");
		sql.append("`file_type` varchar(10) DEFAULT NULL COMMENT 'CSV, JSON', \n");
		sql.append("`status` varchar(10) DEFAULT 'UPLOADED' COMMENT 'UPLOADED: 업로드 / PROCESSING: 분석중 / DONE : 완료 / ERROR : 오류', \n");
		sql.append("`error_msg` text COMMENT '오류 메시지', \n");
		sql.append("PRIMARY KEY (`reg_time`,`grp_id`,`email`,`file_name`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
	public int insert_mp_mapping(){
		int result=0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `mp_mapping` ( \n");
		sql.append("`reg_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '등록일', \n");
		sql.append("`grp_id` varchar(50) NOT NULL DEFAULT '' COMMENT '그룹 아이디', \n");
		sql.append("`email` varchar(100) NOT NULL DEFAULT '' COMMENT '아이디(이메일)', \n");
		sql.append("`map_id` varchar(50) NOT NULL DEFAULT '' COMMENT '매핑 아이디', \n");
		sql.append("`src_file` varchar(100) NOT NULL DEFAULT '' COMMENT '데이터 추출 파일', \n");
		sql.append("PRIMARY KEY (`grp_id`,`email`,`map_id`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
	public int insert_mp_mapping_fields(){
		int result=0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `mp_mapping_fields` ( \n");
		sql.append("`reg_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '등록일', \n");
		sql.append("`grp_id` varchar(50) NOT NULL DEFAULT '' COMMENT '그룹 아이디', \n");
		sql.append("`email` varchar(100) NOT NULL DEFAULT '' COMMENT '아이디(이메일)', \n");
		sql.append("`map_id` varchar(50) NOT NULL DEFAULT '' COMMENT '매핑 아이디', \n");
		sql.append("`onum` int(11) NOT NULL COMMENT '필드 순번', \n");
		sql.append("`name` varchar(100) NOT NULL DEFAULT '' COMMENT '필드명', \n");
		sql.append("`type` varchar(20) NOT NULL DEFAULT '' COMMENT '필드 타입', \n");
		sql.append("`date_format` varchar(100) DEFAULT '' COMMENT '날짜 포맷', \n");
		sql.append("`value` varchar(100) DEFAULT '' COMMENT '추출 예제 데이터', \n");
		sql.append("PRIMARY KEY (`grp_id`,`email`,`map_id`,`onum`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
	public int insert_es_es(){
		int result=0;
		StringBuffer sql = new StringBuffer();
		
		sql.append("CREATE TABLE `es_es` ( \n");
		sql.append("`reg_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '등록일', \n");
		sql.append("`grp_id` varchar(50) NOT NULL DEFAULT '' COMMENT '그룹 아이디', \n");
		sql.append("`email` varchar(100) NOT NULL DEFAULT '' COMMENT '아이디(이메일)', \n");
		sql.append("`es_id` varchar(50) NOT NULL DEFAULT '' COMMENT 'ES 아이디', \n");
		sql.append("`es_url` varchar(200) NOT NULL DEFAULT '' COMMENT 'ES URL', \n");
		sql.append("`es_cluster` varchar(50) NOT NULL DEFAULT '' COMMENT '클러스터명', \n");
		sql.append("`es_port` smallint(50) DEFAULT '9200' COMMENT 'http 포트', \n");
		sql.append("`es_dport` smallint(50) DEFAULT '9300' COMMENT '데이터 포트', \n");
		sql.append("PRIMARY KEY (`grp_id`,`email`,`es_id`) \n");
		sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8; \n");
		
		result = dao.updateSql(property, sql.toString(), null);
		return result;
	}
	
}
