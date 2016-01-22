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
		//if(args.length == 2){

			String path = "./excel/Magpie.xlsx";
			String sheetAt = "0";
			try {
				List<ExcelRow> items = ExcelUtil.load(path, Double.valueOf(sheetAt).intValue());
				String imagePath = FileUtil.makeImagePath(path);
				
				if(items.size()>0){
					for(int i=0; i<items.size(); i++){
						ExcelRow itm = items.get(i);
						Result result = QueryUtil.row(itm.getCmd(), itm.getValue(), itm.getSelector());
						System.out.println(result.getStatus() ? "\t|OK" : "\t|NG");
						
						itm.setResult(result.getStatus() ? "OK" : "NG");
						itm.setMessage(result.getMessage());
						
						if(!result.getStatus() || itm.isEvidence()){
							String evPath = imagePath + ExcelUtil.sheetName + File.separator;
							
							if(!result.getStatus()){
								evPath += "B";
							} else {
								evPath += ((itm.getId().isEmpty() || itm.getId() == null) ? "EV" : itm.getId());
							}
							
							evPath += i + ".png";
							
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
//		}
//		else {
//			System.out.println("Usage: `java -jar getmagpie-0.0.1-SNAPSHOT.jar ./excel/sample.xlsx 0`");
//		}
	}
}
