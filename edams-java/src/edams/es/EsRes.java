package edams.es;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EsRes {

	private static EsRes instance;

	private EsRes() {}

	public static EsRes getInstance() {
		if (instance == null) {
			instance = new EsRes();
		}
		return instance;
	}
	
	/**
	 * 엘라스틱서치에서 GET 방식으로 데이터 조회.
	 * @param urls
	 * @param method
	 * @return
	 * @throws Exception 
	 */
	public String getEsGet(String urls, String method) throws Exception {
		StringBuffer res = new StringBuffer();
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(urls);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method);
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				res.append(output);
			}
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			// 오류의 경우 getErrorStream 으로 오류 스트림 전달.
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				String errOutput;
				res = new StringBuffer();
				while ((errOutput = br.readLine()) != null) {
					res.append(errOutput);
				}
			} catch (Exception e2) {
				throw e2;
			}
		} finally {
			conn.disconnect();
		}
		return res.toString();
	}
	
	/**
	 * 엘라스틱서치에서 PUT, POST 방식으로 데이터 조회.
	 * @param urls
	 * @param data
	 * @param method
	 * @return
	 * @throws Exception 
	 */
	public String getEsPost(String urls, String data, String method) throws Exception {
//		System.out.println("urls : "+urls);
//		System.out.println("method : "+method);
//		System.out.println("data :\n"+data);
		
		StringBuffer res = new StringBuffer();
		URL url = null;
		HttpURLConnection conn = null;
		try {

			url = new URL(urls);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(method);
			conn.setRequestProperty("Content-Type", "application/json");

			OutputStream os = conn.getOutputStream();
			os.write(data.getBytes());
			os.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output;
			while ((output = br.readLine()) != null) {
				res.append(output);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			// 오류의 경우 getErrorStream 으로 오류 스트림 전달.
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				String errOutput;
				res = new StringBuffer();
				while ((errOutput = br.readLine()) != null) {
					res.append(errOutput);
				}
			} catch (Exception e2) {
				throw e2;
			}
		} finally {
			conn.disconnect();
		}
		return res.toString();
	}
	
	public String getEsPost(String urls, String data) throws Exception {
		return getEsPost(urls, data, "POST");
	}
	
	/**
	 * 엘라스틱서치에서 DELETE 방식으로 데이터 처리.
	 * @param urls
	 * @throws Exception 
	 */
	public void getEsDelete(String urls, String data) throws Exception{
//		System.out.println("DELETE: "+data);
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(urls);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json" );
			conn.setRequestMethod("DELETE");
//			conn.connect();			
			conn.getResponseCode();
			
		} catch (Exception e2) {
			throw e2;
		} finally {
			if(conn != null){
				conn.disconnect();
			}
		}
	}
	
}
