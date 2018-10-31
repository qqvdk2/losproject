package datavisian.lospredict.processdata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javafx.util.*;

import com.google.common.base.Strings;

import datavisian.lospredict.readfile.PatientInfoDto;
import datavisian.lospredict.util.ExampleEventUserModel;

public class StatisticalData {
	
	private static final String FILE_NAME = "summary.txt";
	public void statisticData(String dirPath) {
		File directory = new File(dirPath);
		File[] files = directory.listFiles();
		List<String> fields = new ArrayList<String>();
		findFields(files[0], fields);
		DataProcessDom.setFieldMap(createFieldMap(fields));
		
		for (File file : files) {
			if(FILE_NAME.equals(file.getName())){
				continue;
			}
			processData(file,fields);
		}
		
		System.out.println(DataProcessDom.getTotal());
		System.out.println(DataProcessDom.getHasSex());
		for(Pair<String,Integer> field : DataProcessDom.getFieldMap()) {
			System.out.println(field.getKey() + ": " + field.getValue());
		}
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(dirPath+"\\" +FILE_NAME));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			writer.write("Total records: "+DataProcessDom.getTotal()+"\n");
			writer.write("Total records have sex: "+DataProcessDom.getHasSex()+"\n");
			for(Pair<String,Integer> field : DataProcessDom.getFieldMap()) {
				writer.write(field.getKey() + ": " + field.getValue()+";");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	    try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<Pair<String, Integer>> createFieldMap(List<String> fields) {
		List<Pair<String, Integer>> result = new ArrayList<Pair<String, Integer>>();
		for(String field: fields) {
			result.add(new Pair<String,Integer>(field,0));
		}
		return result;
	}

	private void processData(File file, List<String> fields) {
		Workbook dataFile = null;
		try {
			dataFile = WorkbookFactory.create(file);
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
		
		Sheet dataSheet = dataFile.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		Iterator<Row> rowIterator = dataSheet.rowIterator();
		rowIterator.next(); // skip header
		while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            DataProcessDom.setTotal(DataProcessDom.getTotal()+1);
            Iterator<Cell> cellIterator = row.cellIterator();
            List<Pair<String,Integer>> newfieldMap = new ArrayList<Pair<String, Integer>>();
            List<Pair<String,Integer>> oldfieldMap = DataProcessDom.getFieldMap();
            for(int index = 0; index < row.getLastCellNum(); index++) {
            	Cell cell = row.getCell(index);
            	
            	String data = dataFormatter.formatCellValue(cell);
            	if(index == 0) {
            		if (data.contains(" THỊ ") || data.contains(" VĂN ")) {
            			DataProcessDom.setHasSex(DataProcessDom.getHasSex()+1);
            		}
            	}
            	if(!Strings.isNullOrEmpty(data)) {
            		if ("no".equals(data)) {
            			newfieldMap.add(oldfieldMap.get(index));
            		} else {
            			Pair<String,Integer> field = new Pair<String, Integer>(oldfieldMap.get(index).getKey(), oldfieldMap.get(index).getValue()+1);
                		newfieldMap.add(field);
            		}
            		
            	} else {
            		newfieldMap.add(oldfieldMap.get(index));
            	}
            }
            DataProcessDom.setFieldMap(newfieldMap);
        }
	}
	

	
	private static void findFields(File file, List<String> fields)  {
		Workbook dataFile = null;
		try {
			dataFile = WorkbookFactory.create(file);
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
		try {
			dataFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
