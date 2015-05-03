CREATE TABLE `ac_grp` (
  `grp_id` varchar(10) NOT NULL DEFAULT '' COMMENT '그룹 아이디',
  `grp_name` varchar(50) DEFAULT NULL COMMENT '그룹명',
  `grp_text` varchar(200) DEFAULT NULL COMMENT '그룹 설명',
  `grp_dept_id` varchar(10) DEFAULT NULL COMMENT '그룹 구분 아이디',
  `reg_time` datetime DEFAULT NULL COMMENT '등록일',
  `owner` varchar(100) DEFAULT NULL COMMENT '등록자 아이디(이메일)',
  `status` varchar(10) DEFAULT 'ACTIVE' COMMENT 'ACTIVE: 활성 / DELETE: 삭제(비활성)',
  PRIMARY KEY (`grp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ac_grp_dept` (
  `grp_dept_id` varchar(10) NOT NULL DEFAULT '' COMMENT '그룹 구분 아이디',
  `grp_dept_name` varchar(50) DEFAULT NULL COMMENT '그룹 구분명',
  `grp_dept_text` varchar(200) DEFAULT NULL COMMENT '그룹 구분 설명',
  `status` varchar(10) DEFAULT 'ACTIVE' COMMENT 'ACTIVE: 활성 / DELETE: 삭제(비활성)',
  PRIMARY KEY (`grp_dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ac_user` (
  `grp_id` varchar(10) NOT NULL DEFAULT '' COMMENT '그룹 아이디',
  `email` varchar(100) NOT NULL DEFAULT '' COMMENT '아이디(이메일)',
  `user_type` varchar(11) DEFAULT 'USER' COMMENT 'USER: 일반사용자 / ADMIN: 관리자',
  `passwd` varchar(200) DEFAULT NULL COMMENT 'SHA256 알고리즘 암호화',
  `name` varchar(20) DEFAULT NULL COMMENT '이름(본명)',
  `nicname` varchar(50) DEFAULT NULL COMMENT '닉네임',
  `birthday` date DEFAULT NULL COMMENT '생년월일',
  `phone` int(11) DEFAULT NULL COMMENT '연락처',
  `reg_time` datetime DEFAULT NULL COMMENT '등록일',
  `status` varchar(10) DEFAULT 'REG' COMMENT 'REG: 가입승인중 / ACTIVE: 가입완료(활성) / DELETE: 삭제(비활성)',
  PRIMARY KEY (`grp_id`,`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pj_prj` (
  `prj_id` varchar(10) NOT NULL DEFAULT '' COMMENT '프로젝트 아이디',
  `own_grp_id` varchar(10) DEFAULT NULL COMMENT '프로젝트 등록 그룹 아이디',
  `own_email` varchar(100) DEFAULT NULL COMMENT '프로젝트 등록자 아이디(이메일)',
  `prj_name` varchar(50) DEFAULT NULL COMMENT '프로젝트명',
  `prj_text` varchar(200) DEFAULT NULL COMMENT '프로젝트 설명',
  `reg_time` datetime DEFAULT NULL COMMENT '프로젝트 등록 날짜',
  `prj_es_url` varchar(200) DEFAULT NULL COMMENT '엘라스틱서치 URL',
  `prj_es_port` smallint(6) DEFAULT NULL COMMENT '엘라스틱서치 http 포트. 기본 : 9200',
  `prj_es_dport` smallint(6) DEFAULT NULL COMMENT '엘라스틱서치 데이터 포트. 기본 : 9300',
  `prj_es_cluster` varchar(50) DEFAULT NULL COMMENT '엘라스틱서치 클러스터명',
  `prj_es_node` varchar(50) DEFAULT NULL COMMENT '엘라스틱서치 노드명. 기본 : 랜덤 이름',
  `prj_es_index` varchar(50) DEFAULT NULL COMMENT '엘라스틱서치 인덱스명. 기본 : "프로젝트명-%{+YYYY.MM.dd}"',
  `prj_es_type` varchar(50) DEFAULT NULL COMMENT '엘라스틱서치 타입. 기본 : 입력자 아이디(이메일_변형)',
  `status` varchar(10) DEFAULT 'ACTIVE' COMMENT 'ACTIVE: 활성 / DELETE: 삭제(비활성)',
  PRIMARY KEY (`prj_id`),
  KEY `PRJ_OWN_GRP_ID` (`own_grp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pj_prj_grp` (
  `own_grp_id` varchar(10) NOT NULL DEFAULT '' COMMENT '프로젝트 소유 그룹 아이디',
  `prj_id` varchar(10) NOT NULL DEFAULT '' COMMENT '프로젝트 아이디',
  `use_grp_id` varchar(10) NOT NULL DEFAULT '' COMMENT '프로젝트 사용 그룹 아이디 (요청자)',
  `grp_type` varchar(20) DEFAULT 'VIEWER' COMMENT 'VIEWER: 조회가능 / USER: 데이터 등록 가능 / OWNER: 소유 그룹',
  `req_adm_email` varchar(100) DEFAULT NULL COMMENT '요청그룹 관리자 (요청자)',
  `req_date` datetime DEFAULT NULL COMMENT '요청일자 / 생성일자',
  `req_msg` text COMMENT '요청 메시지',
  `res_adm_email` varchar(100) DEFAULT NULL COMMENT '소유그룹 관리자 (승인자)',
  `res_date` datetime DEFAULT NULL COMMENT '승인일자',
  `res_msg` text COMMENT '승인 메시지',
  `status` varchar(10) DEFAULT 'REQUEST' COMMENT 'REQUEST: 신청중 / ACTIVE: 승인 / DENIED: 반려 / DELETE: 삭제(비활성)',
  PRIMARY KEY (`own_grp_id`,`prj_id`,`use_grp_id`),
  KEY `PRJ_GRP_USER` (`use_grp_id`,`prj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pj_prj_grp_user` (
  `own_grp_id` varchar(10) NOT NULL DEFAULT '' COMMENT '프로젝트 소유 그룹 아이디',
  `prj_id` varchar(10) NOT NULL DEFAULT '' COMMENT '프로젝트 아이디',
  `use_grp_id` varchar(10) NOT NULL DEFAULT '' COMMENT '프로젝트 사용 그룹 아이디',
  `use_email` varchar(100) NOT NULL DEFAULT '' COMMENT '프로젝트 배정자 아이디(이메일)',
  `user_type` varchar(20) DEFAULT 'VIEWER' COMMENT 'VIEWER: 조회 가능 / USER: 데이터 등록 가능 / OWNER: 소유자',
  `reg_time` datetime DEFAULT NULL COMMENT '등록 시간',
  `status` varchar(10) DEFAULT 'ACTIVE' COMMENT 'ACTIVE: 활성 / DELETE: 삭제(비활성)',
  PRIMARY KEY (`own_grp_id`,`prj_id`,`use_grp_id`,`use_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
