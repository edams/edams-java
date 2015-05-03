package edams.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import edams.dao.FileDao;
import edams.dao.PrjDao;
import jm.net.DataEntity;

public class Indexing {
	
	private static Indexing instance = null;
	
	private Indexing() throws FileNotFoundException{
	}
	
	public static Indexing getInstance() throws FileNotFoundException{
		if(instance == null){
			instance = new Indexing();
		}
		return instance;
	}
	
	/**
	 * CSV 파일 변환.
	 * @param srcPath
	 * @param srcFile
	 * @param destPath
	 * @param destFile
	 * @param prjId
	 * @param grpId
	 * @param req
	 * @param totLine
	 * @throws FileNotFoundException
	 */
	public void indexCsv(String srcPath, String srcFile, String destPath, String destFile, String prjId, String grpId, HttpServletRequest req, long totLine) throws FileNotFoundException{
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		long lineCnt = 0;
		long errCnt = 0;
		FileDao fDao = FileDao.getInstance(); 
		PrjDao prjDao = PrjDao.getInstance();
		DataEntity[] mapEntity = prjDao.getPrjMapping(prjId);
		long divVal = 0;
		if(totLine > 1000){
			while(divVal < 30){
				divVal = Math.round(Math.random() * 100);
			}
		} else {
			while(divVal < 1000){
				divVal = Math.round(Math.random() * 2000);
			}
		}
		if(mapEntity != null && mapEntity.length > 0){
			int mapLen = mapEntity.length;
			
			try {
				fr = new FileReader(srcPath + "/" + srcFile);
				br = new BufferedReader(fr);
				
				File destPathDir = new File(destPath);
				if(!destPathDir.exists()){ destPathDir.mkdirs(); }
				
//				fw = new FileWriter(destPath+"/"+destFile);
				fw = new FileWriter(destFile);
				bw = new BufferedWriter(fw);
				
				String rLine = null;
				while((rLine = br.readLine())!=null && errCnt < 100){
					String destKey = "";
					String destVal = "";
					lineCnt++;
					
					Vector<String> mapVec = new Vector<String>();
//					String[] items = rLine.split("\",\"");
					//string, date 형식은 ", 구분. 그 외는 , 로 구분.
					
					boolean prt = false;
					/*
					if(rLine.indexOf("300000000022479") > -1){
						prt = true;
					}
					*/
					for(int v=0; v < mapLen; v++){
						String part = "";
						if(prt){
							System.out.println("rLine.length() : "+rLine.length());
						}
						if("string".equals(mapEntity[v].get("field_type")+"") || "date".equals(mapEntity[v].get("field_type")+"")){
							if(rLine.indexOf(",") == 0){
								mapVec.add("");
								rLine = rLine.substring(1);
								if(prt){
									System.out.println("");
									System.out.println(rLine);
								}
							} else {
								if(rLine.indexOf("\",") > -1){
									part = rLine.substring(0,rLine.indexOf("\","));
									mapVec.add(part.replaceAll("\"", ""));
									rLine = rLine.substring(rLine.indexOf("\",")+2);
									if(prt){
										System.out.println(part);
										System.out.println(rLine);
									}
								} else if(rLine.length() > 0){
									mapVec.add(rLine.replaceAll("\"", ""));
									rLine = "";
									if(prt){
										System.out.println(part);
										System.out.println(rLine);
									}
								}
							}
						} else {
							if(rLine.indexOf(",") > -1){
								part = rLine.substring(0,rLine.indexOf(","));
								mapVec.add(part.replaceAll("\"", ""));
								rLine = rLine.substring(rLine.indexOf(",")+1);
								if(prt){
									System.out.println(part);
									System.out.println(rLine);
								}
							} else if(rLine.length() > 0){
								mapVec.add(rLine.replaceAll("\"", ""));
								rLine = "";
								if(prt){
									System.out.println(part);
									System.out.println(rLine);
								}
							}
						}
					}
					String[] items = mapVec.toArray(new String[mapVec.size()]);
//					System.out.println("items.length("+lineCnt+") : "+items.length);
					int fldLoop = items.length;
					if(items.length >= mapLen){
						//필드 길이와 매핑 필드 중 작은 값으로 루프.
						fldLoop = mapLen;
					}
//					System.out.println("fldLoop : "+fldLoop);
					try{
						JSONObject json = new JSONObject();
						for(int it=0; it < fldLoop; it++){
//							System.out.println("	"+it+") : "+items[it]);
							if("date".equals(mapEntity[it].get("field_type")+"")){
								destKey = mapEntity[it].get("field_id")+"";
								String dataType = req.getParameter(mapEntity[it].get("field_id")+"");
								dataType = dataType.replaceAll("-", "");
								dataType = dataType.replaceAll(":", "");
								dataType = dataType.replaceAll(" ", "");
								SimpleDateFormat transFormat = new SimpleDateFormat(dataType);
								String tmpDateVal = items[it].replaceAll("\\\"", "");
								tmpDateVal = tmpDateVal.replaceAll("-", "");
								tmpDateVal = tmpDateVal.replaceAll(":", "");
								tmpDateVal = tmpDateVal.replaceAll(" ", "");
								Date dateVal = transFormat.parse(tmpDateVal);
								SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd");
								SimpleDateFormat toTime = new SimpleDateFormat("HH:mm:ss");
								destVal = toDate.format(dateVal)+"T"+toTime.format(dateVal);
							} else {
								destKey = mapEntity[it].get("field_id")+"";
								destVal = items[it].replaceAll("\\\"", "");
							}
							json.put(destKey, destVal);
						}
						json.put("es_index", prjId+"-"+grpId);
						json.put("es_type", prjId);
						
						if(prt){
							System.out.println(lineCnt + ": " + json.toString());
						}
						
					    bw.write(json.toString());
					    bw.newLine();
					} catch (ParseException pe){
						//데이터 형변환 오류.
						DataEntity[] fEntitys = fDao.getFiProc(srcPath + "/" + srcFile, null, null);
						errCnt++;
						if(fEntitys != null && fEntitys.length > 0){
							String error_msg = fEntitys[0].get("error_msg")+"";
							DataEntity updateEntity = new DataEntity();
							updateEntity.put("error_lines", errCnt);
							updateEntity.put("proc_lines", lineCnt);
							updateEntity.put("error_msg", error_msg + "\n날짜 형식 오류. ("+lineCnt+")");
							fDao.updateFiProc(updateEntity, srcPath + "/" + srcFile);
						}
					}
					if(errCnt == 99){
						DataEntity updateEntity = new DataEntity();
						updateEntity.put("proc_lines", lineCnt);
						updateEntity.put("status", "ERROR");
						fDao.updateFiProc(updateEntity, srcPath + "/" + srcFile);
					}
					if(lineCnt%divVal == 0){
						DataEntity updateEntity = new DataEntity();
						updateEntity.put("proc_lines", lineCnt);
						fDao.updateFiProc(updateEntity, srcPath + "/" + srcFile);
					}
					if(lineCnt == totLine){
						DataEntity updateEntity = new DataEntity();
						updateEntity.put("proc_lines", lineCnt);
						if(errCnt == 0){
							updateEntity.put("status", "DONE");
						} else {
							updateEntity.put("status", "ERROR");
						}
						fDao.updateFiProc(updateEntity, srcPath + "/" + srcFile);
					}
				}
				
			} catch (Exception e) {
				errCnt++;
				DataEntity updateEntity = new DataEntity();
				updateEntity.put("error_lines", errCnt);
				updateEntity.put("error_msg", e.toString());
				updateEntity.put("status", "ERROR");
				fDao.updateFiProc(updateEntity, srcPath + "/" + srcFile);
			} finally {
				try {
					br.close();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	/**
	 * JSON 파일 변환.
	 * @param srcPath
	 * @param srcFile
	 * @param destPath
	 * @param destFile
	 * @param prjId
	 * @param grpId
	 * @param req
	 * @param totLine
	 * @throws FileNotFoundException
	 */
	public void indexJson(String srcPath, String srcFile, String destPath, String destFile, String prjId, String grpId, HttpServletRequest req, long totLine) throws FileNotFoundException{
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		long lineCnt = 0;
		long errCnt = 0;
		FileDao fDao = FileDao.getInstance(); 
		PrjDao prjDao = PrjDao.getInstance();
		DataEntity[] mapEntity = prjDao.getPrjMapping(prjId);
		long divVal = 0;
		if(totLine > 1000){
			while(divVal < 30){
				divVal = Math.round(Math.random() * 100);
			}
		} else {
			while(divVal < 1000){
				divVal = Math.round(Math.random() * 2000);
			}
		}
		if(mapEntity != null && mapEntity.length > 0){
			int mapLen = mapEntity.length;
			
			try {
				fr = new FileReader(srcPath + "/" + srcFile);
				br = new BufferedReader(fr);
				
				File destPathDir = new File(destPath);
				if(!destPathDir.exists()){ destPathDir.mkdirs(); }
				
//				fw = new FileWriter(destPath+"/"+destFile);
				fw = new FileWriter(destFile);
				bw = new BufferedWriter(fw);
				
				String rLine = null;
				while((rLine = br.readLine())!=null){
					String destKey = "";
					String destVal = "";
					lineCnt++;
					JSONObject sJson = new JSONObject(rLine);
					try{
						JSONObject dJson = new JSONObject();
						for(int it=0; it<mapLen; it++){
							if("date".equals(mapEntity[it].get("field_type")+"")){
								destKey = mapEntity[it].get("field_id")+"";
								String dataType = req.getParameter(mapEntity[it].get("field_id")+"");
								dataType = dataType.replaceAll("-", "");
								dataType = dataType.replaceAll(":", "");
								dataType = dataType.replaceAll(" ", "");
								dataType = dataType.replaceAll("T", "");
								SimpleDateFormat transFormat = new SimpleDateFormat(dataType);
								
								String tmpDateVal = sJson.getString(destKey);
								tmpDateVal = tmpDateVal.replaceAll("-", "");
								tmpDateVal = tmpDateVal.replaceAll(":", "");
								tmpDateVal = tmpDateVal.replaceAll(" ", "");
								tmpDateVal = tmpDateVal.replaceAll("T", "");
								Date dateVal = transFormat.parse(tmpDateVal);
								SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd");
								SimpleDateFormat toTime = new SimpleDateFormat("HH:mm:ss");
								destVal = toDate.format(dateVal)+"T"+toTime.format(dateVal);
								
								dJson.put(destKey, destVal);
							} else {
								destKey = mapEntity[it].get("field_id")+"";
								dJson.put(destKey, sJson.get(destKey));
							}
						}
						dJson.put("es_index", prjId+"-"+grpId);
						dJson.put("es_type", prjId);
//						System.out.println(dJson.toString());
					    bw.write(dJson.toString());
					    bw.newLine();
					} catch (ParseException pe){
						//데이터 형변환 오류.
						DataEntity[] fEntitys = fDao.getFiProc(srcPath + "/" + srcFile, null, null);
						errCnt++;
						if(fEntitys != null && fEntitys.length > 0){
							String error_msg = fEntitys[0].get("error_msg")+"";
							DataEntity updateEntity = new DataEntity();
							updateEntity.put("error_lines", errCnt);
							updateEntity.put("error_msg", error_msg + "\n날짜 형식 오류. ("+lineCnt+")");
							fDao.updateFiProc(updateEntity, srcPath + "/" + srcFile);
						}
					}
					if(lineCnt%divVal == 0){
						DataEntity updateEntity = new DataEntity();
						updateEntity.put("proc_lines", lineCnt);
						fDao.updateFiProc(updateEntity, srcPath + "/" + srcFile);
					}
					if(lineCnt == totLine){
						DataEntity updateEntity = new DataEntity();
						updateEntity.put("proc_lines", lineCnt);
						updateEntity.put("status", "DONE");
						fDao.updateFiProc(updateEntity, srcPath + "/" + srcFile);
					}
					
				}
				
			} catch (Exception e) {
				errCnt++;
				DataEntity updateEntity = new DataEntity();
				updateEntity.put("error_lines", errCnt);
				updateEntity.put("error_msg", e.toString());
				updateEntity.put("status", "ERROR");
				fDao.updateFiProc(updateEntity, srcPath + "/" + srcFile);
			} finally {
				try {
					br.close();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
}
