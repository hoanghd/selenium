package com.getmagpie;

import java.io.File;
import java.util.List;

import com.getmagpie.data.ExcelRow;
import com.getmagpie.data.Result;
import com.getmagpie.util.CmdUtil;
import com.getmagpie.util.ExcelUtil;
import com.getmagpie.util.FileUtil;
import com.getmagpie.util.QueryUtil;

public class Main {
	public static void main(String[] args) {
		if(args.length == 2){
			String path = args[0];
			String sheetAt = args[1];
			try {
				List<ExcelRow> items = ExcelUtil.load(path, Double.valueOf(sheetAt).intValue());
				String imagePath = FileUtil.makeImagePath(path);
				
				if(items.size()>0){
					for(int i=0; i<items.size(); i++){
						ExcelRow itm = items.get(i);
						Result result = QueryUtil.row(itm.getCmd(), itm.getValue(), itm.getSelector());
						System.out.println(result.getStatus() ? "\t|\tOK" : "\t|\tNG -> " + result.getMessage());
						
						itm.setResult(result.getStatus() ? "OK" : "NG");
						itm.setMessage(result.getMessage());
						
						if(!result.getStatus() || itm.isEvidence()){							
							String evPath = imagePath + ExcelUtil.sheetName + File.separator;
							
							if(!result.getStatus()){
								evPath += "B";
							} else {
								evPath += ((itm.getId().isEmpty() || itm.getId() == null) ? "EV" : itm.getId());
							}
							
							evPath += (itm.getIndex() + 1) + ".png";
							
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
		else {
			System.out.println("Usage: `java -jar getmagpie-0.0.1-SNAPSHOT.jar ./excel/sample.xlsx 0`");
		}
	}
}
