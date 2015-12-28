package com.getmagpie.util;

import com.getmagpie.data.ExcelRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	public static void save(String path, int sheetAt, List<ExcelRow> rows){
		try {
			FileInputStream fis = new FileInputStream(path);
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(sheetAt);
			
			if(rows.size()>0){
				for(int i=0; i<rows.size(); i++){
					ExcelRow itm = rows.get(i);
					XSSFRow row = sheet.getRow(itm.getIndex());
					
					XSSFCell cell = row.getCell(5);	
					if(cell == null)
						cell = row.createCell(5);
					
					cell.setCellValue(itm.getResult());
					
					cell = row.getCell(6);
					if(cell == null)
						cell = row.createCell(6);
						
					cell.setCellValue(itm.getMessage());
					
					if(itm.isEvidence()){
						cell = row.getCell(7);
						if(cell == null)
							cell = row.createCell(7);
							
						cell.setCellValue("x");
					}
					
					cell = row.getCell(8);
					if(cell == null)
						cell = row.createCell(8);
						
					if(itm.getPath() != null)
						cell.setCellValue(itm.getPath());
				}
				
				fis.close();
				FileOutputStream fos =new FileOutputStream(path);
			    workbook.write(fos);
			    fos.close();
			    
			    workbook.close();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	public static List<ExcelRow> load(String path, int sheetAt){
		List<ExcelRow> items = new ArrayList<ExcelRow>();		
		try {
			File f = new File(path);
			if(f.exists() && !f.isDirectory()) { 
				InputStream fs = new FileInputStream(path);
				XSSFWorkbook wb = new XSSFWorkbook(fs);
				
				if(wb.getNumberOfSheets() > sheetAt){
					XSSFSheet sheet = wb.getSheetAt(sheetAt);					
					
					int rowsCount = sheet.getLastRowNum();				
					for (int i = 1; i <= rowsCount; i++) {
		                Row row = sheet.getRow(i);	                
		                ExcelRow item = new ExcelRow();
		                
		                if(row != null){
		                	item.setIndex(i);
		                    item.setCmd(getCellVal(row.getCell(1)));
		                    item.setSelector(getCellVal(row.getCell(2)));
		                    item.setValue(getCellVal(row.getCell(3)));
		                    
		                    String out = getCellVal(row.getCell(4));
		                    if(!out.trim().isEmpty()){
		                    	item.setValue(out);
		                    }
		                    
		                    String evidence = getCellVal(row.getCell(7));
		                    if(!evidence.isEmpty()){
		                    	item.setEvidence(true);
		                    }
		                    
		                    if(!item.getCmd().isEmpty())
		                    	items.add(item);
		                	
		                }
		            }
				}
				else {
					System.out.println("Sheet `" + sheetAt + "` not exists!");
				}
				
				wb.close();
			}
			else {
				System.out.println("`" + path + "` not exists!");
			}			
		} 
		catch (Exception e) {
			e.printStackTrace();			
		}
		
		return items;
	}
	
	private static String getCellVal(Cell col){
		String val = "";
		if(col != null){
			switch(col.getCellType()){
				case Cell.CELL_TYPE_NUMERIC:
					val = String.valueOf(col.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_STRING:
					val = col.getStringCellValue();
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					val = String.valueOf(col.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					val = col.getCellFormula();
					break;
			}
		}
		
		return val.trim();
	}
}
