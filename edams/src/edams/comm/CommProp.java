package edams.comm;

import java.io.FileNotFoundException;

import jm.com.JmProperties;

public class CommProp {
	
	JmProperties jmProp = null;
	
	private CommProp() throws FileNotFoundException{
		if(jmProp == null){
			jmProp = new JmProperties();
		}
		if(!jmProp.setResource("edams.properties")){
			FileNotFoundException fe = new FileNotFoundException("시스템 설정 파일이 존재하지 않습니다.");
			throw fe;
		}
	}
	
	public static JmProperties getJmProperties() throws FileNotFoundException{
		CommProp prop = new CommProp();
		return prop.jmProp;
	}
	
}
