package com.getmagpie;

import java.io.File;
import java.util.List;

import com.getmagpie.data.ExcelRow;
import com.getmagpie.data.Result;
import com.getmagpie.util.CmdUtil;
import com.getmagpie.util.ExcelUtil;
import com.getmagpie.util.FileUtil;
import com.getmagpie.util.QueryUtil;

public class Test {
	public static void main(String[] args) {
		String path = "./excel/Login.xlsx";
		String sheetAt = "0";
		try {
			List<ExcelRow> items = ExcelUtil.load(path, Double.valueOf(sheetAt).intValue());
			String imagePath = FileUtil.makeImagePath(path);
			
			if(items.size()>0){
				for(int i=0; i<items.size(); i++){
					ExcelRow itm = items.get(i);
					Result result = QueryUtil.row(itm.getCmd(), itm.getValue(), itm.getSelector());
					
					itm.setResult(result.getStatus() ? "PASS" : "FAIL");
					itm.setMessage(result.getMessage());
					
					if(!result.getStatus() || itm.isEvidence()){
						String evPath = imagePath + sheetAt + File.separator + "EV" + i + ".png";
						CmdUtil.screenshot(evPath);
						itm.setPath(evPath);
					}
				}
				
				ExcelUtil.save(path, Double.valueOf(sheetAt).intValue(), items);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();			
		}
	}
}
