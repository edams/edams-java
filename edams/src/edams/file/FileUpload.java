package edams.file;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;

import edams.comm.CommProp;
import edams.comm.CommUserObj;
import edams.dao.FileDao;
import jm.com.JmProperties;

public class FileUpload extends HttpServlet {
	
	private static final long serialVersionUID = 4207038734544869055L;
	private JmProperties property = null;
	private File fileUploadPath;
	
	public void init() {
		try {
			property = CommProp.getJmProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
		fileUploadPath = new File(property.get("sysPath")+"/files");
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("text/html; charset=UTF-8");
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		
		if(userObj != null){
			if (req.getParameter("getfile") != null  && !req.getParameter("getfile").isEmpty()) {
				File file = new File(fileUploadPath+"/"+userObj.getGrpId()+"/"+userObj.getEmail(), req.getParameter("getfile"));
				if (file.exists()) {
					int bytes = 0;
					ServletOutputStream op = res.getOutputStream();
					
					res.setContentType(getMimeType(file));
					res.setContentLength((int) file.length());
					res.setHeader( "Content-Disposition", "inline; filename=\"" + file.getName() + "\"" );
					
					byte[] bbuf = new byte[1024];
					DataInputStream in = new DataInputStream(new FileInputStream(file));
					
					while ((in != null) && ((bytes = in.read(bbuf)) != -1)) {
						op.write(bbuf, 0, bytes);
					}
					
					in.close();
					op.flush();
					op.close();
				}
			} else {
				getFileList(req, res);
			}
		}
	}
	
	private void getFileList(HttpServletRequest req, HttpServletResponse res) throws IOException{
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		
		if(userObj != null){
			String url = req.getRequestURL().toString();
			PrintWriter out = res.getWriter();
			res.setContentType("application/json");
			File tempPath = new File( (fileUploadPath + "/").replaceAll("//","/")+"/"+userObj.getGrpId()+"/"+userObj.getEmail() );
			if(!tempPath.exists()){
				tempPath.mkdirs(); 
			}
			File[] items = tempPath.listFiles();
			JSONObject jsonf = new JSONObject();
			JSONArray json = new JSONArray();
			try {
				for (File item : items) {
					JSONObject jsono = new JSONObject();
					jsono.put("id", item.getName().replaceAll("[.]", "_"));
					jsono.put("name", item.getName());
					jsono.put("size", item.length());
					jsono.put("type", this.getSuffix(item.getName()).toUpperCase());
					jsono.put("url", url+"?getfile=" + item.getName());
					jsono.put("deleteUrl", url+"?file=" + item.getName());
					jsono.put("deleteType", "DELETE");
					json.put(jsono);
				}
				jsonf.put("files", json);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				out.write(jsonf.toString());
				out.close();
			}
		}
		
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		FileDao fdao = FileDao.getInstance();
		
		if(userObj != null){
			String url = req.getRequestURL().toString();
			PrintWriter out = res.getWriter();
			res.setContentType("application/json");
			if (!ServletFileUpload.isMultipartContent(req)) {
				throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
			}
			// filePath/<id>/ 경로에 파일 업로드.
			File tempPath = new File( (fileUploadPath + "/").replaceAll("//","/")+"/"+userObj.getGrpId()+"/"+userObj.getEmail() );
			if(!tempPath.exists()){
				tempPath.mkdirs(); 
			}
			ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
			JSONObject jsonf = new JSONObject();
			JSONArray json = new JSONArray();
			try {
				List<FileItem> items = uploadHandler.parseRequest(req);
				for (FileItem item : items) {
					if (!item.isFormField()) {
						File file = new File(tempPath, item.getName());
						item.write(file);
						JSONObject jsono = new JSONObject();
						jsono.put("id", item.getName().replaceAll("[.]", "_"));
						jsono.put("name", item.getName());
						jsono.put("size", item.getSize());
						jsono.put("type", this.getSuffix(item.getName()).toUpperCase());
						jsono.put("url", url+"?getfile=" + item.getName());
						jsono.put("deleteUrl", url+"?file=" + item.getName());
						jsono.put("deleteType", "DELETE");
						json.put(jsono);
					}
					
					/* fl_file 테이블에 데이터 저장 시작. */
					String fPath = tempPath+"/"+item.getName();
					//파일 라인 수 가져오기
					/*
					String flines = "0";
					ProcessBuilder pb = new ProcessBuilder("wc", "-l", fPath);
					Process process = pb.start();
					BufferedReader stdOut = new BufferedReader( new InputStreamReader(process.getInputStream()) );
					if( (flines = stdOut.readLine()) != null ) {
						flines = flines.replaceAll(fPath, "");
						flines = flines.trim();
					}
					*/
					//파일 타입 가져오기. 대문자로 변환.
					String fileType = this.getSuffix(item.getName());
					fileType = fileType.toUpperCase();
					fdao.insertFlFile(userObj.getGrpId(), userObj.getEmail(), item.getName(), fPath, this.readableFileSize(item.getSize()), null, fileType);
					/* fl_file 테이블에 데이터 저장 끝. */
					
				}
				jsonf.put("files", json);
			} catch (FileUploadException e) {
				throw new RuntimeException(e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				out.write(jsonf.toString());
				out.close();
			}
		}
		
	}
	
	public void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException{
		HttpSession session = req.getSession();
		CommUserObj userObj = (CommUserObj) session.getAttribute("CommUserObj");
		FileDao fdao = FileDao.getInstance();
		
		if(userObj != null){
			res.setContentType("application/json");
			String[] files_name = req.getParameterValues("file");
			File tempPath = new File( (fileUploadPath + "/").replaceAll("//","/")+"/"+userObj.getGrpId()+"/"+userObj.getEmail() );
			for(String file_name : files_name){
				File file = new File( tempPath + "/" + file_name );
				if( file.exists() ){
					file.delete();
					/* fl_file 테이블에 데이터 삭제. */
					fdao.deleteFlFile(userObj.getGrpId(), userObj.getEmail(), file_name);
				}
			}
			getFileList(req, res);
		}
	}
	
	private String getMimeType(File file) {
		String mimetype = "";
		if (file.exists()) {
			if (getSuffix(file.getName()).equalsIgnoreCase("png")) {
				mimetype = "image/png";
			} else {
				javax.activation.MimetypesFileTypeMap mtMap = new javax.activation.MimetypesFileTypeMap();
				mimetype  = mtMap.getContentType(file);
			}
		}
		return mimetype;
	}
	
	/**
	 * 파일 확장자 가져오는 메소드.
	 * @param filename
	 * @return
	 */
	private String getSuffix(String filename) {
		String suffix = "";
		int pos = filename.lastIndexOf('.');
		if (pos > 0 && pos < filename.length() - 1) {
			suffix = filename.substring(pos + 1);
		}
		return suffix;
	}
	
	/**
	 * 파일 사이즈 변경.
	 * @param bytes
	 * @return
	 */
	private String readableFileSize(long size) {
		if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1000));
	    return new DecimalFormat("#,##0.##").format(size/Math.pow(1000, digitGroups)) + " " + units[digitGroups];
	}
}
