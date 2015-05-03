package edams.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import jm.com.JmProperties;
import jm.net.DataEntity;

public class SystemProperty {
	
	public JmProperties saveProperty(String propPath, String propName, DataEntity dataEntity){
		JmProperties jmProp = new JmProperties();
		FileOutputStream fout = null;
		String filePath = propPath + "/" + propName;
		filePath = filePath.replaceAll("//", "/");
		try {
			Properties props = new Properties();
			File f = new File(filePath);
			
			String[] colums = dataEntity.keySet().toArray(new String[0]);
			int columSize = colums.length;
			for(int col=0; col < columSize; col++){
				props.setProperty(colums[col], dataEntity.get(colums[col])+"");
			}
			
			f.createNewFile();
			fout = new FileOutputStream(f);
			props.store(fout, "Edams Config File");
			jmProp.setResource(propName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException ex) {
					System.out.println("IOException: Could not close edams.properties output stream; "+ ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
		return jmProp;
	}
	
}
