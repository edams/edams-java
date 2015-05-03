package edams.file;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.opencsv.CSVParser;

import edams.comm.CommVars;

public class FileUtil {

	private static FileUtil instance = null;
	private FileUtil(){}
	
	public static FileUtil getInstance(){
		if(instance == null){
			instance = new FileUtil();
		}
		return instance;
	}
	
	/**
	 * CSV 파싱해서 매핑 정보를 JSONObject로 리턴.
	 * @param fLine: CSV 문자열.
	 * @param jTitleInfo: 첫 열이 제목인 경우 제목행 리턴.
	 * @return
	 * @throws IOException
	 */
	public JSONObject getCSVMapInfo(String fLine, JSONObject jTitleInfo) throws IOException{
		JSONObject resObj = new JSONObject();
		boolean isAllStr = true;
		
		CSVParser parser = new CSVParser(',');
		String[] cols = parser.parseLine(fLine); 
		
		JSONArray colsObj = new JSONArray();
		
		for(int i=0; i<cols.length; i++){
			JSONObject jCols = new JSONObject();
			// 두번째 라인일 때 첫번째 라인의 값을 이름으로 지정.
			if(jTitleInfo != null){
				JSONArray nameArr = (JSONArray)jTitleInfo.get("map_info");
				if(i < nameArr.length()){
					JSONObject nameObj = (JSONObject)nameArr.get(i);
					if(nameObj != null && !"".equals(nameObj.get("value"))){
						jCols.put("name",nameObj.get("value"));
					} else {
						jCols.put("name","column_"+(i+1));
					}
				} else {
					jCols.put("name","column_"+(i+1));
				}
			} else {
				jCols.put("name","column_"+(i+1));
			}
			
			if("true".equals(cols[i].toLowerCase()) || "false".equals(cols[i].toLowerCase())) {
//				System.out.println("cols["+i+"] - type: boolean");
				isAllStr = false;
				jCols.put("type","boolean");
				jCols.put("date_format","");
				jCols.put("value",cols[i].toLowerCase());
			} else if(NumberUtils.isNumber(cols[i])){
				isAllStr = false;
				if(NumberUtils.isDigits(cols[i])){
//					System.out.println("cols["+i+"] - type: long");
					jCols.put("type","long");
					jCols.put("date_format","");
					jCols.put("value",Long.parseLong(cols[i])); 
				} else {
//					System.out.println("cols["+i+"] - type: double");
					jCols.put("type","double");
					jCols.put("date_format","");
					jCols.put("value",Double.parseDouble(cols[i]));
				}
			} else {
				int datePatternNum = getDateFormatPattern(cols[i]);
				if(datePatternNum > 0){
					isAllStr = false;
//					System.out.println("cols["+i+"] - type: datetime ("+CommVars.dateFormatPattern[datePatternNum]+")");
					jCols.put("type","datetime");
					jCols.put("date_format",CommVars.dateFormatPattern[datePatternNum]);
					jCols.put("value",cols[i]);
				} else {
//					System.out.println("cols["+i+"] - type: string");
					jCols.put("type","string");
					jCols.put("date_format","");
					jCols.put("value",cols[i]);
				}
			}
			colsObj.put(jCols);
		}
		
		resObj.put("map_info", colsObj);
		resObj.put("is_all_str", isAllStr);
		
		return resObj;
	}
	
	/**
	 * CSV 파싱해서 매핑 정보를 JSONObject로 리턴.
	 * @param jString
	 * @return
	 * @throws IOException
	 */
	public JSONObject getJSONMapInfo(String jString) throws IOException{
		JSONObject resObj = new JSONObject();
		JSONObject srcJson = new JSONObject(jString);
		
		// JSON 키 순서를 맞추기 위해 정렬.	
		String[] cols = JSONObject.getNames(srcJson);
		int[] colsIndexOf = new int[cols.length];
		for(int i=0; i < cols.length; i++){
			colsIndexOf[i] = jString.indexOf("\""+cols[i]+"\"");
//			System.out.println(i + " : ["+colsIndexOf[i]+"] "+cols[i]);
		}
		
		for(int i=0; i < colsIndexOf.length; i++){
			for(int j=i; j < colsIndexOf.length; j++){
				if(colsIndexOf[j] < colsIndexOf[i]){
					int tmp = colsIndexOf[j];
					colsIndexOf[j] = colsIndexOf[i];
					colsIndexOf[i] = tmp;
					String tmpStr = cols[j];
					cols[j] = cols[i];
					cols[i] = tmpStr;
				}
			}
//			System.out.println(i + " : ["+colsIndexOf[i]+"] "+cols[i]);
		}
		
		JSONArray colsObj = new JSONArray();
		
		for(int i=0; i<cols.length; i++){
			JSONObject jCols = new JSONObject();
			jCols.put("name",cols[i]);
			if("true".equals(((String)srcJson.get(cols[i])).toLowerCase()) || "false".equals(((String)srcJson.get(cols[i])).toLowerCase())) {
//				System.out.println("cols["+i+"] - type: boolean");
				jCols.put("type","boolean");
				jCols.put("date_format","");
				jCols.put("value",((String)srcJson.get(cols[i])).toLowerCase());
			} else if(NumberUtils.isNumber((String)srcJson.get(cols[i]))){
				if(NumberUtils.isDigits((String)srcJson.get(cols[i]))){
//					System.out.println("cols["+i+"] - type: long");
					jCols.put("type","long");
					jCols.put("date_format","");
					jCols.put("value",Long.parseLong((String)srcJson.get(cols[i]))); 
				} else {
//					System.out.println("cols["+i+"] - type: double");
					jCols.put("type","double");
					jCols.put("date_format","");
					jCols.put("value",Double.parseDouble((String)srcJson.get(cols[i])));
				}
			} else {
				int datePatternNum = getDateFormatPattern((String)srcJson.get(cols[i]));
				if(datePatternNum > 0){
//					System.out.println("cols["+i+"] - type: datetime ("+CommVars.dateFormatPattern[datePatternNum]+")");
					jCols.put("type","datetime");
					jCols.put("date_format",CommVars.dateFormatPattern[datePatternNum]);
					jCols.put("value",(String)srcJson.get(cols[i]));
				} else {
//					System.out.println("cols["+i+"] - type: string");
					jCols.put("type","string");
					jCols.put("date_format","");
					jCols.put("value",(String)srcJson.get(cols[i]));
				}
			}
			
			colsObj.put(jCols);
		}
		resObj.put("map_info", colsObj);
		return resObj;
	}
	
	/**
	 * 날짜 포맷 체크하는 메서드.
	 * edams.comm.CommVars 클래스의 dataFormatPattern 문자열 사용.
	 * @param dateTimeStr
	 * @return
	 */
	private int getDateFormatPattern(String dateTimeStr){
		int res = -1;
		Date date = null;
		SimpleDateFormat format = null;
		for(int i=0; i<CommVars.dateFormatPattern.length; i++){
			format = new SimpleDateFormat(CommVars.dateFormatPattern[i]);
			try {
                format.setLenient(false);
                date = format.parse(dateTimeStr);
            } catch (ParseException e) {
                //다른 포맷 시도.
            }
            if (date != null) {
            	res = i;
                break;
            }
		}
		return res;
	}
	
}
