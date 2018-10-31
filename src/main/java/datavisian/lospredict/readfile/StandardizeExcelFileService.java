package datavisian.lospredict.readfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.base.Strings;

public class StandardizeExcelFileService {
	
	 public static void standardizeExcelFiles(String dirPath, String resultDirPath) {
		 File directory = new File(dirPath);
			File[] files = directory.listFiles();
			List<String> fields = new ArrayList<String>();
			for (File file : files) {
				try {
					findFields(file,fields);
				} catch (EncryptedDocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			for(File file: files) {
				try {
					standardizeFields(file,fields,resultDirPath);
				} catch (EncryptedDocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
	 }

	private static void findFields(File file, List<String> fields) throws EncryptedDocumentException, InvalidFormatException, IOException {
		Workbook dataFile = WorkbookFactory.create(file);
		Sheet dataSheet = dataFile.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		Row headerRow = dataSheet.getRow(0);
		Iterator<Cell> cellIterator = headerRow.iterator();
		while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String field = dataFormatter.formatCellValue(cell);
            if (!Strings.isNullOrEmpty(field) && !fields.contains(field)) {
            	fields.add(field);
            }
        }
		dataFile.close();
		
	}

	private static void standardizeFields(File file, List<String> fields, String resultDirPath) throws EncryptedDocumentException, InvalidFormatException, IOException {
		Workbook dataFile = WorkbookFactory.create(file);
		Sheet dataSheet = dataFile.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		Row headerRow = dataSheet.getRow(0);
		Iterator<Cell> cellIterator = headerRow.cellIterator();
		List<String> fileFields = new ArrayList<String>();
		while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String field = dataFormatter.formatCellValue(cell);
            if (!Strings.isNullOrEmpty(field) && !fileFields.contains(field)) {
            	fileFields.add(field);
            }
        }
		int numOfSplit = dataSheet.getLastRowNum()/1000;
		Iterator<Row> rowIterator = dataSheet.rowIterator();
		int rowNum = 1;
		rowIterator.next(); // skip header
		
		for (int split = 0; split< numOfSplit + 1 ; split++) {
			Workbook newWorkbook = new SXSSFWorkbook();
			CreationHelper createHelper = newWorkbook.getCreationHelper();
			Sheet sheet = newWorkbook.createSheet("SeparatedData");
			
			//create header
			Row newHeaderRow = sheet.createRow(0);
			for(int index = 0; index < fields.size(); index++) {
				Cell cell = newHeaderRow.createCell(index);
				cell.setCellValue(fields.get(index));
			}
			
			
			while (rowIterator.hasNext()) {
				Row dataRow = rowIterator.next();
				Row newRow = null;
				if(rowNum % 1000 == 0) {
					newRow = sheet.createRow(1000); 
				} else {
					newRow = sheet.createRow(rowNum % 1000); 
				}
	            for(int index = 0; index < fields.size(); index++) {
	    			Cell newCell = newRow.createCell(index);
	    			boolean hasValue = false;
	    			String field = fields.get(index);
	    			for(String fileField : fileFields) {
	    				if (fileField.equals(field)) {
	    					newCell.setCellValue(dataFormatter.formatCellValue(dataRow.getCell(fileFields.indexOf(fileField))));
	    					hasValue = true;
	    					break;
	    				}
	    			}
	    			if(!hasValue) {
	    				newCell.setCellValue("");
	    			}
	    		}
	            rowNum++;
	            if (rowNum > 1000 && rowNum % 1000 == 1 ) {
	            	break;
	            }
			}
			
			FileOutputStream fileOut = new FileOutputStream(resultDirPath+"\\"+split+"_"+file.getName());
	        newWorkbook.write(fileOut);
	        fileOut.close();

	        newWorkbook.close();
		}
		
	}
	
}
