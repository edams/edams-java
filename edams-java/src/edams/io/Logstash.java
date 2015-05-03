package edams.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import edams.comm.CommProp;
import edams.es.EsUtil;
import jm.com.JmProperties;

public class Logstash {
	
	private static Logstash instance = null;
	private JmProperties property = null;
	
	private Logstash() throws FileNotFoundException{
		property = CommProp.getJmProperties();
	}
	
	public static Logstash getInstance() throws FileNotFoundException{
		if(instance == null){
			instance = new Logstash();
		}
		return instance;
	}
	
	/**
	 * 로그스태시 설치파일 다운로드.
	 * @param lsVersion
	 */
	public void downloadLogstash(){
		String lsVersion  = property.get("lsVersion");
		String lgUrl ="https://download.elasticsearch.org/logstash/logstash/logstash-"+lsVersion+".zip";
		String targetFilename = "logstash.zip";
		String setupUrl = property.get("sysPath")+"/download";
		
		//디렉토리가 없으면 생성.
		File folder = new File(setupUrl);
    	if(!folder.exists()){
    		folder.mkdir();
    	}
		
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			fos = new FileOutputStream(setupUrl+"/" + targetFilename);
			URL url = new URL(lgUrl);
			URLConnection urlConnection = url.openConnection();
			is = urlConnection.getInputStream();
			byte[] buffer = new byte[1024];
			int readBytes;
			while ((readBytes = is.read(buffer)) != -1) {
				fos.write(buffer, 0, readBytes);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 로그스태시 파일 압축 풀기.
	 */
	public void unzipLogstash(){
		String zipFile = property.get("sysPath")+"/download/logstash.zip";
		String logstashInstallPath = property.get("sysPath");
		try {
			ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry ze;

			while ((ze = in.getNextEntry()) != null) {
				if(ze.isDirectory()){
					new File(logstashInstallPath + "/"+ ze.getName()).mkdirs();
					continue;
				}
				OutputStream out = new FileOutputStream(logstashInstallPath + "/" + ze.getName());
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				if(ze.getName().indexOf("bin") > 0){
					setPermission(logstashInstallPath + "/"+ ze.getName());
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 권한 755 로 설정.
	 * @param filePath
	 */
	private void setPermission(String filePath){
		File file = new File(filePath);
		
		if (file.exists()) {
			file.setReadable(true);
			file.setWritable(true);
			file.setExecutable(true);
		} else {
			System.out.println("File cannot exists: ");
		}
	}
	
	/**
	 * config 파일 생성.
	 * @param esCluster
	 * @param esUrl
	 * @param esPort
	 */
	public void setConfFile(String esCluster, String esUrl, String confFileName){
		String logstashConfPath = property.get("sysPath") + "/logstash_config";
		if(confFileName == null || "".equals(confFileName)){
			confFileName = "logstash.conf";
		}
		try {
			File folder = new File(logstashConfPath);
			if(!folder.exists()){
				folder.mkdir();
			}
			folder = new File(property.get("sysPath") + "/es_datas");
			if(!folder.exists()){
				folder.mkdir();
			}
			FileWriter fw = new FileWriter(logstashConfPath+"/"+confFileName);
			BufferedWriter bw = new BufferedWriter(fw);
			StringBuffer str = new StringBuffer();
			
			str.append("input { \n");
			str.append("	file{ \n");
			str.append("		codec => json \n");
			str.append("		path => [\""+property.get("sysPath")+"/es_datas/*.json\"] \n");
			str.append("		start_position => \"beginning\" \n");
			str.append("	} \n");
			str.append("} \n");
			
			str.append("output { \n");
			str.append("	elasticsearch{ \n");
			str.append("		cluster => \""+esCluster+"\" \n");
			str.append("		host => \""+esUrl+"\" \n");
			str.append("		node_name => \"edams-logstash\" \n");
			str.append("		index => \"%{es_index}\" \n");
			str.append("	index_type => \"%{es_type}\" \n");
			str.append("	} \n");
			str.append("} \n");
			bw.write(str.toString());
			bw.newLine(); // 줄바꿈
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 로그스태시 실행.
	 * @param confFile
	 * @return
	 */
	public int logstashRun(String confFile, String esUrl, String esPort){
		String lgsRun = property.get("sysPath")+"/logstash-"+property.get("lsVersion")+"/bin/logstash";
		ProcessBuilder pb = new ProcessBuilder(lgsRun, "-f", confFile);
		pb.redirectErrorStream(true);
		try {
			pb.start();
			EsUtil esUtil = EsUtil.getInstance();
			String esStat = "";
			//로그스태시 시작되면 넘어감..
			while(esStat.length() < 100){
				esStat = esUtil.checkLogstash(esUrl, esPort);
//				System.out.println(esStat);
				Thread.sleep(1000);
			}
			/*
			BufferedReader stdOut = new BufferedReader( new InputStreamReader(process.getInputStream()) );
			String str;
			if( (str = stdOut.readLine()) != null ) {
				System.out.println(str);
			}
			*/
			return 1;
		} catch (IOException e) {
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
}
