package com.getmagpie.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class FileUtil {
	public static String makeImagePath(String excelFile){
		String path = null;
		
		File file = new File(excelFile);
	    if(file.exists()){
	    	path = file.getParentFile().getPath() + File.separator + FilenameUtils.getBaseName(excelFile) + File.separator;
	    	File image = new File(path);
	    	
	    	if(!image.isDirectory()){	    		
	    		try {	    			
					FileUtils.forceMkdir(image);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}	    	
	    }
	    
		return path;
	}
}
