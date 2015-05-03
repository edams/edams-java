package edams.es;

import java.io.FileNotFoundException;

import edams.dao.PrjDao;
import jm.net.DataEntity;

public class EsUtil {

	private static EsUtil instance = null;
	
	private EsUtil() throws FileNotFoundException{
	}
	
	public static EsUtil getInstance() throws FileNotFoundException{
		if(instance == null){
			instance = new EsUtil();
		}
		return instance;
	}
	
	/**
	 * 클러스터 정보 가져오는 메소드.
	 * @param esUrl
	 * @param esPort
	 * @return
	 */
	public String checkCluster(String esUrl, String esPort){
		esUrl = esUrl.replaceAll("http", "");
		esUrl = esUrl.replaceAll(":", "");
		esUrl = esUrl.replaceAll("/", "");
		if(esPort == null || "".equals(esPort)){
			esPort = "9200";
		}
		String retStr = "";
		EsRes esr = EsRes.getInstance();
		try {
			retStr = esr.getEsGet("http://"+esUrl+":"+esPort+"/_cluster/health", "GET");
		} catch (Exception e) {
			retStr = "";
		}
		return retStr;
	}
	
	/**
	 * 로그스태시 노드 정보 가져오는 메소드.
	 * @param esUrl
	 * @param esPort
	 * @return
	 */
	public String checkLogstash(String esUrl, String esPort){
		esUrl = esUrl.replaceAll("http", "");
		esUrl = esUrl.replaceAll(":", "");
		esUrl = esUrl.replaceAll("/", "");
		if(esPort == null || "".equals(esPort)){
			esPort = "9200";
		}
		String retStr = "";
		EsRes esr = EsRes.getInstance();
		try {
			retStr = esr.getEsGet("http://"+esUrl+":"+esPort+"/_nodes/edams-logstash/stats", "GET");
		} catch (Exception e) {
			retStr = "";
		}
		return retStr;
	}
	
	/**
	 * 인덱스 매핑 정보 가져오는 메소드.
	 * @param esUrl
	 * @param esPort
	 * @param prjId
	 * @return
	 */
	public String getMapping(String esUrl, String esPort, String prjId, String grpId){
		esUrl = esUrl.replaceAll("http", "");
		esUrl = esUrl.replaceAll(":", "");
		esUrl = esUrl.replaceAll("/", "");
		if(esPort == null || "".equals(esPort)){
			esPort = "9200";
		}
		String retStr = "";
		EsRes esr = EsRes.getInstance();
		try {
			retStr = esr.getEsGet("http://"+esUrl+":"+esPort+"/"+prjId+"-"+grpId+"/"+prjId+"/_mapping", "GET");
		} catch (Exception e) {
			retStr = "";
		}
		return retStr;
	}
	
	/**
	 * 매핑 정보 입력하는 메소드.
	 * @param esUrl
	 * @param esPort
	 * @param prjId
	 * @param grpId
	 */
	public int putMapping(String esUrl, String esPort, String prjId, String grpId){
		int result = 0;
		PrjDao dao;
		EsRes esr = EsRes.getInstance();
		StringBuffer mapStr = new StringBuffer();
		try {
			dao = PrjDao.getInstance();
			DataEntity[] mappingEntity = dao.getPrjMapping(prjId);
			
			mapStr.append("{");
			mapStr.append("	\"mappings\":");
			mapStr.append("	{");
			mapStr.append("		\""+prjId+"\":");		//Type name.
			mapStr.append("		{");
			mapStr.append("			\"properties\":");
			mapStr.append("			{");
			mapStr.append("\n");
			
			for(int i=0; i < mappingEntity.length; i++){
				String fType = mappingEntity[i].get("field_type") + "";
				String fId = mappingEntity[i].get("field_id") + "";
				if("string".equals(fType.toLowerCase())){
					mapStr.append("\""+fId+"\":");
					mapStr.append("{");
					mapStr.append("	\"type\":\"string\",");
					mapStr.append("	\"index\":\"analyzed\",");
					mapStr.append("	\"analyzer\":\"standard\",");
					mapStr.append("	\"fields\":");
					mapStr.append("	{");
					mapStr.append("		\"raw\":");
					mapStr.append("		{");
					mapStr.append("		\"type\":\"string\",");
					mapStr.append("		\"index\":\"not_analyzed\"");
					mapStr.append("		}");
					mapStr.append("	}");
					mapStr.append("}");
				} else if("date".equals(fType.toLowerCase())){
					mapStr.append("\""+fId+"\":");
					mapStr.append("{");
					mapStr.append("	\"type\":\"date\",");
					mapStr.append("	\"format\":\"dateOptionalTime\"");
					mapStr.append("}");
				} else {
					mapStr.append("\""+fId+"\":");
					mapStr.append("{");
					mapStr.append("	\"type\":\""+fType.toLowerCase()+"\",");
					mapStr.append("	\"index\":\"analyzed\",");
					mapStr.append("	\"analyzer\":\"standard\"");
					mapStr.append("}");
				}
				if(i < mappingEntity.length - 1){
					mapStr.append(",");
					mapStr.append("\n");
				}
			}
			
			mapStr.append("			}");
			mapStr.append("		}");
			mapStr.append("	}");
			mapStr.append("}");
			esUrl = esUrl.replaceAll("http", "");
			esUrl = esUrl.replaceAll(":", "");
			esUrl = esUrl.replaceAll("/", "");
			if(esPort == null || "".equals(esPort)){
				esPort = "9200";
			}
			String mapUrl = "http://"+esUrl+":"+esPort+"/"+prjId+"-"+grpId;
			esr.getEsPost(mapUrl, mapStr.toString(), "PUT");
			
			result = 1;
			Thread.sleep(1000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = 0;
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
		}
		
		return result;
	}
	
	/**
	 * 키바나에 대시보드 입력.
	 * @param esUrl
	 * @param esPort
	 * @param prjId
	 * @param grpId
	 * @return
	 */
	public int iniKibana(String esUrl, String esPort, String prjId, String grpId, String ownerId){
		int result = 0;
		
		EsRes esr = EsRes.getInstance();
		StringBuffer kibanaStr = new StringBuffer();
		try {
			kibanaStr.append("{");
			kibanaStr.append("\"user\":\"guest\",");
			kibanaStr.append("\"group\":\"guest\",");
			kibanaStr.append("\"title\":\""+prjId+"-"+grpId+"\",");
			kibanaStr.append("\"dashboard\":\"{");
			kibanaStr.append("\\\"title\\\":\\\""+prjId+"-"+grpId+"\\\",");
			kibanaStr.append("\\\"services\\\":{");
			kibanaStr.append("\\\"query\\\":{");
			kibanaStr.append("\\\"list\\\":{");
			kibanaStr.append("\\\"0\\\":{");
			kibanaStr.append("\\\"query\\\":\\\"*\\\",");
			kibanaStr.append("\\\"alias\\\":\\\"\\\",");
			kibanaStr.append("\\\"color\\\":\\\"#7EB26D\\\",");
			kibanaStr.append("\\\"id\\\":0,");
			kibanaStr.append("\\\"pin\\\":false,");
			kibanaStr.append("\\\"type\\\":\\\"lucene\\\",");
			kibanaStr.append("\\\"enable\\\":true");
			kibanaStr.append("}");
			kibanaStr.append("},");
			kibanaStr.append("\\\"ids\\\":[0]");
			kibanaStr.append("},");
			kibanaStr.append("\\\"filter\\\":{");
			kibanaStr.append("\\\"list\\\":{},");
			kibanaStr.append("\\\"ids\\\":[]");
			kibanaStr.append("}");
			kibanaStr.append("},");
			kibanaStr.append("\\\"rows\\\":[");
			
			kibanaStr.append("{");
			kibanaStr.append("\\\"title\\\":\\\"row1\\\",");
			kibanaStr.append("\\\"height\\\":\\\"300px\\\",");
			kibanaStr.append("\\\"editable\\\":true,");
			kibanaStr.append("\\\"collapse\\\":false,");
			kibanaStr.append("\\\"collapsable\\\":true,");
			kibanaStr.append("\\\"panels\\\":[");
			kibanaStr.append("{");
			kibanaStr.append("\\\"span\\\":3,");
			kibanaStr.append("\\\"editable\\\":true,");
			kibanaStr.append("\\\"type\\\":\\\"hits\\\",");
			kibanaStr.append("\\\"loadingEditor\\\":false,");
			kibanaStr.append("\\\"style\\\":{\\\"font-size\\\":\\\"32pt\\\"},");
			kibanaStr.append("\\\"arrangement\\\":\\\"horizontal\\\",");
			kibanaStr.append("\\\"chart\\\":\\\"total\\\",");
			kibanaStr.append("\\\"counter_pos\\\":\\\"above\\\",");
			kibanaStr.append("\\\"donut\\\":false,");
			kibanaStr.append("\\\"tilt\\\":false,");
			kibanaStr.append("\\\"labels\\\":true,");
			kibanaStr.append("\\\"spyable\\\":true,");
			kibanaStr.append("\\\"queries\\\":{");
			kibanaStr.append("\\\"mode\\\":\\\"all\\\",");
			kibanaStr.append("\\\"ids\\\":[0]");
			kibanaStr.append("},");
			kibanaStr.append("\\\"title\\\":\\\"Total\\\"");
			kibanaStr.append("}");
			kibanaStr.append("],");
			kibanaStr.append("\\\"notice\\\":false");
			kibanaStr.append("},");
			
			kibanaStr.append("{");
			kibanaStr.append("\\\"title\\\":\\\"row2\\\",");
			kibanaStr.append("\\\"height\\\":\\\"300px\\\",");
			kibanaStr.append("\\\"editable\\\":true,");
			kibanaStr.append("\\\"collapse\\\":false,");
			kibanaStr.append("\\\"collapsable\\\":true,");
			kibanaStr.append("\\\"panels\\\":[");
			kibanaStr.append("],");
			kibanaStr.append("\\\"notice\\\":false");
			kibanaStr.append("},");
			
			kibanaStr.append("{");
			kibanaStr.append("\\\"title\\\":\\\"row3\\\",");
			kibanaStr.append("\\\"height\\\":\\\"300px\\\",");
			kibanaStr.append("\\\"editable\\\":true,");
			kibanaStr.append("\\\"collapse\\\":false,");
			kibanaStr.append("\\\"collapsable\\\":true,");
			kibanaStr.append("\\\"panels\\\":[");
			kibanaStr.append("],");
			kibanaStr.append("\\\"notice\\\":false");
			kibanaStr.append("}");
			
			kibanaStr.append("],");
			kibanaStr.append("\\\"editable\\\":true,");
			kibanaStr.append("\\\"failover\\\":false,");
			kibanaStr.append("\\\"index\\\":{");
			kibanaStr.append("\\\"interval\\\":\\\"none\\\",");
			kibanaStr.append("\\\"pattern\\\":\\\"[logstash-]YYYY.MM.DD\\\",");
			if(ownerId.equals(grpId)){
				//프로젝트 오너인 경우 모든 그룹의 데이터 조회 가능.
				kibanaStr.append("\\\"default\\\":\\\""+prjId+"-*\\\",");
			} else {
				//프로젝트 오너가 아닌 경우 자신의 그룹 + 오너 그룹의 데이터 조회 가능.
				kibanaStr.append("\\\"default\\\":\\\""+prjId+"-"+ownerId+","+prjId+"-"+grpId+"\\\",");
			}
			kibanaStr.append("\\\"warm_fields\\\":false");
			kibanaStr.append("},");
			kibanaStr.append("\\\"style\\\":\\\"light\\\",");
			kibanaStr.append("\\\"panel_hints\\\":true,");
			kibanaStr.append("\\\"pulldowns\\\":[");
			kibanaStr.append("{");
			kibanaStr.append("\\\"type\\\":\\\"query\\\",");
			kibanaStr.append("\\\"collapse\\\":true,");
			kibanaStr.append("\\\"notice\\\":false,");
			kibanaStr.append("\\\"enable\\\":true,");
			kibanaStr.append("\\\"query\\\":\\\"*\\\",");
			kibanaStr.append("\\\"pinned\\\":true,");
			kibanaStr.append("\\\"history\\\":[],");
			kibanaStr.append("\\\"remember\\\":10");
			kibanaStr.append("},{");
			kibanaStr.append("\\\"type\\\":\\\"filtering\\\",");
			kibanaStr.append("\\\"collapse\\\":true,");
			kibanaStr.append("\\\"notice\\\":false,");
			kibanaStr.append("\\\"enable\\\":true");
			kibanaStr.append("}");
			kibanaStr.append("],");
			kibanaStr.append("\\\"nav\\\":[");
			kibanaStr.append("{");
			kibanaStr.append("\\\"type\\\":\\\"timepicker\\\",");
			kibanaStr.append("\\\"collapse\\\":false,");
			kibanaStr.append("\\\"notice\\\":false,");
			kibanaStr.append("\\\"enable\\\":true,");
			kibanaStr.append("\\\"status\\\":\\\"Stable\\\",");
			kibanaStr.append("\\\"time_options\\\":[\\\"5m\\\",\\\"15m\\\",\\\"1h\\\",\\\"6h\\\",\\\"12h\\\",\\\"24h\\\",\\\"2d\\\",\\\"7d\\\",\\\"30d\\\"],");
			kibanaStr.append("\\\"refresh_intervals\\\":[\\\"5s\\\",\\\"10s\\\",\\\"30s\\\",\\\"1m\\\",\\\"5m\\\",\\\"15m\\\",\\\"30m\\\",\\\"1h\\\",\\\"2h\\\",\\\"1d\\\"],");
			kibanaStr.append("\\\"timefield\\\":\\\"@timestamp\\\"");
			kibanaStr.append("}");
			kibanaStr.append("],");
			kibanaStr.append("\\\"loader\\\":{");
			kibanaStr.append("\\\"save_gist\\\":false,");
			kibanaStr.append("\\\"save_elasticsearch\\\":true,");
			kibanaStr.append("\\\"save_local\\\":true,");
			kibanaStr.append("\\\"save_default\\\":true,");
			kibanaStr.append("\\\"save_temp\\\":true,");
			kibanaStr.append("\\\"save_temp_ttl_enable\\\":true,");
			kibanaStr.append("\\\"save_temp_ttl\\\":\\\"30d\\\",");
			kibanaStr.append("\\\"load_gist\\\":false,");
			kibanaStr.append("\\\"load_elasticsearch\\\":true,");
			kibanaStr.append("\\\"load_elasticsearch_size\\\":20,");
			kibanaStr.append("\\\"load_local\\\":false,");
			kibanaStr.append("\\\"hide\\\":false");
			kibanaStr.append("},");
			kibanaStr.append("\\\"refresh\\\":false");
			kibanaStr.append("}");
			kibanaStr.append("\"}");
			
//			System.out.println(kibanaStr.toString());
			
			esUrl = esUrl.replaceAll("http", "");
			esUrl = esUrl.replaceAll(":", "");
			esUrl = esUrl.replaceAll("/", "");
			if(esPort == null || "".equals(esPort)){
				esPort = "9200";
			}
			String mapUrl = "http://"+esUrl+":"+esPort+"/edams-kibana/dashboard/"+prjId+"-"+grpId;
			esr.getEsPost(mapUrl, kibanaStr.toString(), "PUT");
			
			result = 1;
			Thread.sleep(1000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = 0;
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
		}
		
		return result;
	}
	
	/**
	 * 인덱스를 삭제하는 메서드.
	 * @param esUrl
	 * @param esPort
	 * @return
	 */
	public String deleteIndex(String esUrl, String esPort, String  prjId){
		esUrl = esUrl.replaceAll("http", "");
		esUrl = esUrl.replaceAll(":", "");
		esUrl = esUrl.replaceAll("/", "");
		if(esPort == null || "".equals(esPort)){
			esPort = "9200";
		}
		String retStr = "";
		EsRes esr = EsRes.getInstance();
		try {
			PrjDao dao = PrjDao.getInstance();
			DataEntity[] prjEntitys = dao.getPrjGrp(null, prjId, null);
			if(prjEntitys != null && prjEntitys.length > 0){
				for(int i=0; i < prjEntitys.length; i++){
					esr.getEsDelete("http://"+esUrl+":"+esPort+"/"+prjId+"-"+prjEntitys[i].get("use_grp_id"), "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			retStr = "";
		}
		return retStr;
	}
}
