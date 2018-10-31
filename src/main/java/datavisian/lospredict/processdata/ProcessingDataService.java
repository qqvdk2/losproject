package datavisian.lospredict.processdata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.base.Strings;
import com.google.common.primitives.Doubles;

import javafx.util.*;

public class ProcessingDataService {
	
	private static final int DIAGNOSE_COLUMN = 6;
	private static final int AGE_COLUMN = 45;
	private static final int EGFR_COLUMN = 84	;
	private static final int BLOOD_TYPE_COLUMN = 90;
	private static final String SUMMARY_FILE_NAME = "summary.txt";
	public static final int MIN_RECORD_TO_KEEP = 400;
	private static final String DIAGNOSE = "diagnose";
	private static final int ArrayList = 0;
	
	public void removeFieldWithLessData(String dirPath, String resultPath, int minValueToKeep) {
		List<Pair<String,Integer>> fieldList = null;
		try {
			 fieldList = getFieldMap(dirPath+"\\"+SUMMARY_FILE_NAME, minValueToKeep);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		for (Pair<String,Integer> field : fieldList) {
//			System.out.println(field.getKey()+":"+field.getValue());
//		}
		File directory = new File(dirPath);
		File[] files = directory.listFiles();
		for (File file : files) {
			if (SUMMARY_FILE_NAME.equals(file.getName())) {
				continue;
			}
			try {
				removeFieldWithLessData(file,fieldList,resultPath);
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
	public void removeFields(String dirPath, String resultPath, List<String> fieldsToRemove) {
		List<Pair<String,Integer>> fieldList = null;
		try {
			 fieldList = getFieldMap(dirPath+"\\"+SUMMARY_FILE_NAME, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		for (Pair<String,Integer> field : fieldList) {
//			System.out.println(field.getKey()+":"+field.getValue());
//		}
		List<Pair<String,Integer>> newfieldList = new ArrayList<Pair<String, Integer>>();
		newfieldList.addAll(fieldList);
		for (Pair<String, Integer> field : fieldList) {
			if (fieldsToRemove.contains(field.getKey())) {
				newfieldList.remove(field);
			}
			
		}
		
		File directory = new File(dirPath);
		File[] files = directory.listFiles();
		for (File file : files) {
			if (SUMMARY_FILE_NAME.equals(file.getName())) {
				continue;
			}
			try {
				removeFieldWithLessData(file,newfieldList,resultPath);
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
	public void separateDiagnoseFields(String dirPath, String resultPath) {
		List<String> rawDiagnoseList = new ArrayList<String>();
		File directory = new File(dirPath);
		File[] files = directory.listFiles();
		for (File file: files) {
			if (SUMMARY_FILE_NAME.equals(file.getName())) {
				continue;
			}
			List<String> rawDiagnoses = null;
			try {
				rawDiagnoses = findDiagnose(file);
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
			for (String rawDiagnose : rawDiagnoses) {
				if(!rawDiagnoseList.contains(rawDiagnose)) {
					rawDiagnoseList.add(rawDiagnose);
				}
			}
		}
		List<String> diagnoseList = new ArrayList<String>();
		for (String rawDiagnose : rawDiagnoseList) {
			String diagnose = normalizeDiagnose(rawDiagnose);
			if (!diagnoseList.contains(diagnose)) {
				diagnoseList.add(diagnose);
			}
		}
		for(String diagnose : diagnoseList) {
			System.out.println(diagnose);
		}
		List<Pair<String,Integer>> fieldList = null;
		try {
			 fieldList = getFieldMap(dirPath+"\\"+SUMMARY_FILE_NAME, MIN_RECORD_TO_KEEP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (File file: files) {
			if (SUMMARY_FILE_NAME.equals(file.getName())) {
				continue;
			}
			try {
				addDiagnoseList(file,fieldList,diagnoseList,resultPath);
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
	
	public void normalizeFields(String dirPath, String resultPath) {
		File directory = new File(dirPath);
		File[] files = directory.listFiles();
		for (File file: files) {
			if (SUMMARY_FILE_NAME.equals(file.getName())) {
				continue;
			}
			try {
				normalizeFields(file, resultPath);
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
	private void normalizeFields(File file, String resultPath) throws EncryptedDocumentException, InvalidFormatException, IOException {
		Workbook dataFile = WorkbookFactory.create(file);
		Workbook workbook = new XSSFWorkbook();
		Sheet dataSheet = dataFile.getSheetAt(0);
		Sheet sheet = workbook.createSheet("normalizedFields");
		copySheet(dataSheet,sheet);
		DataFormatter dataFormatter = new DataFormatter();
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<Row> rowIterator = sheet.rowIterator();
		Row headerRow = rowIterator.next();
		int totalColumn = headerRow.getLastCellNum();
		String numberRegex = "(\\d+)|(\\d+\\.\\d+)";
		
		
		while(rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String id =  dataFormatter.formatCellValue(row.getCell(0));
			for (int cellNum = AGE_COLUMN; cellNum < totalColumn; cellNum++) {
				Cell cell = row.getCell(cellNum);
				String cellData = dataFormatter.formatCellValue(cell);
				if (Strings.isNullOrEmpty(cellData)) {
					continue;
				}
				if (cellNum == BLOOD_TYPE_COLUMN) {
					if (cellData.contains("AB")) {
						cell.setCellValue(3);
					} else if (cellData.contains("O")) {
						cell.setCellValue(2);
					} else if (cellData.contains("B")) {
						cell.setCellValue(1);
					} else if (cellData.contains("A")) {
						cell.setCellValue(0);
					} else {
						cell.setCellValue("");
						//System.out.println(file.getName()+ " | " + id + " | " + cellNum + " | " + cellData );
					}
				}  else if(!cellData.matches(numberRegex)) {
					String regex_before = "([^\\d]+)(?=(\\d+//.\\d+)|(\\d+))"; // anything before the first double/integer value
					String regex_after = "(?<=\\d)([^\\d.]).*"; // anything after the last digit of first double/integer value
					
					
					String cleanCellData = cellData.replaceFirst(regex_before, "").replaceFirst(regex_after, "");
//					stringBuilder.setLength(0);
//					stringBuilder.append(cleanCellData);
//					stringBuilder.reverse();
//					cleanCellData = stringBuilder.toString().replaceAll(regex_before, "");
//					stringBuilder.setLength(0);
//					stringBuilder.append(cleanCellData);
//					stringBuilder.reverse();
//					cleanCellData = stringBuilder.toString();
					if(cleanCellData.matches(numberRegex)) {
						if (cellNum == EGFR_COLUMN) {
							double eGFR = Double.parseDouble(cleanCellData);
							if (eGFR >= 59.999) {
								cell.setCellValue(0);
							} else if (eGFR >= 45) {
								cell.setCellValue(1);
							} else if (eGFR >= 30) {
								cell.setCellValue(2);
							} else if (eGFR >= 15) {
								cell.setCellValue(3);
							} else {
								cell.setCellValue(4);
							}
						} else {
							cell.setCellValue(cleanCellData);
						}
					} else {
						cell.setCellValue("");
						//System.out.println(file.getName()+ " | " + id + " | " + cellNum + " |" + cellData + "|" + cleanCellData);
					}
				}
			}
		}
		
		FileOutputStream fileOut = new FileOutputStream(resultPath+"\\"+file.getName());
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
	}
	private List<String> findDiagnose(File file) throws EncryptedDocumentException, InvalidFormatException, IOException {
		List<String> result = new ArrayList<String>();
		Workbook dataFile = WorkbookFactory.create(file);
		Sheet dataSheet = dataFile.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		Iterator<Row> rowIterator = dataSheet.rowIterator();
		rowIterator.next(); // skip header
		
		while(rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Cell diagnoseCell = row.getCell(DIAGNOSE_COLUMN);
			
			List<String> diagnoseRawData = findDiagnoseFromCell(dataFormatter, diagnoseCell);
			
			for (String rawDiagnose : diagnoseRawData) {
				if (!result.contains(rawDiagnose)) {
					result.add(rawDiagnose);
				}
			}
		}
		return result;
		
	}
	private List<String> findDiagnoseFromCell(DataFormatter dataFormatter, Cell diagnoseCell) {
		String diagnoseRawData = dataFormatter.formatCellValue(diagnoseCell);
		String[] separatedSymbols = {",",";","/","\\+","-","\\.","#","_","=","-"};
		for (String separatedSymbol : separatedSymbols) {
			diagnoseRawData = diagnoseRawData
					.replaceAll(separatedSymbol, "|");
		}
		return Arrays.asList(diagnoseRawData.split("\\|"));
	}
	private String normalizeDiagnose(String diagnose) {
		if (diagnose.contains("ĐTĐ") || diagnose.contains("ĐT Đ") || diagnose.contains("ĐTD") || diagnose.contains("DTĐ") || 
					((diagnose.contains("ĐÁI") || diagnose.contains("TIỂU") || diagnose.contains("TIÊU") || diagnose.contains("ĐÓI") || diagnose.contains("DÍA") || diagnose.contains("ĐÀI")|| diagnose.contains("ĐÁY") || diagnose.contains("DT")) && (diagnose.contains("ƯỜNG") || diagnose.contains("UỒNG") || diagnose.contains("ƯƠNG") || diagnose.contains("UONG")))) {
			return "DTD";
		}
		if ("THA" == diagnose || diagnose.contains("TĂNG HA") || diagnose.contains("THA ") ||  diagnose.contains("TH AP") || diagnose.contains(" THA") || diagnose.contains("THA1") || diagnose.contains("THA2") || ((diagnose.contains("HUYẾ")|| diagnose.contains("HUYET")) && (diagnose.contains("TĂNG") || diagnose.contains("CAO")))) {
			return "THA";
		}
		if (diagnose.contains("SUY THẬN") || diagnose.contains("SUY TAH65N") || diagnose.contains("SUY THA65N") || diagnose.contains("THẬN M") || diagnose.contains("THẬM MẠN") || (diagnose.contains("SUY") && (diagnose.contains("THẬN") || diagnose.contains("THẠN") ||  diagnose.contains("THÂN")))) {
			return "SUYTHAN";
		}
		if (diagnose.contains("VIÊM") && (diagnose.contains("PHỔI") || diagnose.contains("HÔ HẤP"))) {
			return "VPHOI";
		}
		if (diagnose.contains("VIÊM TAI")) {
			return "VTAI";
		}
		if (diagnose.contains("TMCB") || diagnose.contains("THIẾU MÁU CỤC BỘ")) {
			return "TMCB";
		}
		if (diagnose.contains("NTT") || diagnose.contains("ĐSL") || diagnose.contains("NGOẠI TÂM THU")) {
			return "NTT";
		}
		if (diagnose.contains("THOÁT VỊ") || diagnose.contains("TVĐĐ") || (diagnose.contains("THOÁT") && (diagnose.contains("ĐĨA ĐỆM") || diagnose.contains("DĨA ĐỆM")))) {
			return "TVDD";
		}
		if (diagnose.contains("SỎI") && diagnose.contains("MẬT")) {
			return "SM";
		}
		if (diagnose.contains("SỎI") && diagnose.contains("THẬN")) {
			return "ST";
		}
		if (diagnose.contains("VIÊM") && diagnose.contains("GAN")) {
			return "VGAN";
		}
		if (diagnose.contains("RỐI LOẠN") && diagnose.contains("TIỀN ĐÌNH")) {
			return "RLTD";
		}
		if (diagnose.contains("RỐI LOẠN") && diagnose.contains("TIÊU HOÁ")) {
			return "RLTH";
		}
		if (diagnose.contains("RỐI LOẠN") && diagnose.contains("TÂM THẦN")) {
			return "RLTT";
		}
		if (diagnose.contains("RLCHLP") || diagnose.contains("RLLP") || diagnose.contains("RL LIPID") ||((diagnose.contains("RỐI LOẠN")|| diagnose.contains("RL")) && (diagnose.contains("LIPIP") || diagnose.contains("LIPID")))) {
			return "RLLP";
		}
		if (diagnose.contains("RỐI LOẠN") && diagnose.contains("TUẦN HOÀN")) {
			return "RLTHN";
		}
		if ((diagnose.contains("PHẾ QUẢN") || diagnose.contains("PQ")) && (diagnose.contains("HEN") || diagnose.contains("VIÊM"))) {
			return "VHPQ";
		}
		if (diagnose.contains("ĐỘNG KINH")) {
			return "DK";
		}
		if (diagnose.contains("DẠ DÀY") && (diagnose.contains("VIÊM") || diagnose.contains("VIÊN"))) {
			return "VDD";
		}
		if (diagnose.contains("SUY NHƯỢC") || diagnose.contains("SUY KIỆT")) {
			return "SNCTTK";
		}
		if (diagnose.contains("CHẤN THƯƠNG") && !diagnose.contains("NÃO")) {
			return "CTVL";
		}
		if (diagnose.contains("CHẤN THƯƠNG") && diagnose.contains("NÃO")) {
			return "CTSN";
		}
		if ((diagnose.contains("ĐỘNG MẠCH") || diagnose.contains("ĐM")) && (diagnose.contains("HẸP") || diagnose.contains("HEP"))) {
			return "HDM";
		}	
		if ((diagnose.contains("NHỒI MÁU")) && (diagnose.contains("NÃO") || diagnose.contains("NẢO"))) {
			return "NMN";
		}	
		if ((diagnose.contains("TAI BIẾN")) && (diagnose.contains("NÃO") || diagnose.contains("NẢO"))) {
			return "TBMN";
		}
		if (diagnose.contains("STIM") || diagnose.contains("SUY") && (diagnose.contains("TIM")|| diagnose.contains("TIIM"))) {
			return "STIM";
		}
		if (diagnose.contains("ĐẠI TRÀNG")) {
			return "DTRANG";
		}
		if ( (diagnose.contains("THẦN KINH") || diagnose.contains("TK")) && (diagnose.contains("DÂY") || diagnose.contains("TOẠ") || diagnose.contains("ĐAU") || diagnose.contains("NGOẠI BIÊN"))) {
			return "DTKT";
		}
		if (diagnose.contains("LAO") && diagnose.contains("PHỔI")) {
			return "LAOP";
		}
		if (diagnose.contains("NHIỄM TRÙNG") || diagnose.contains("NHIỂM TRÙNG")) {
			return "NTRUNG";
		}
		if (diagnose.contains("VÀNH CẤP")) {
			return "HCVC";
		}
		if (diagnose.contains("XƠ") && diagnose.contains("GAN")) {
			return "XGAN";
		}
		if (diagnose.contains("TRẦM CẢM") || diagnose.contains("STRESS")) {
			return "STRESS";
		}
		if (diagnose.contains("SUY") && diagnose.contains("VAN")) {
			return "SVTM";
		}
		if (diagnose.contains("SUY") && diagnose.contains("TUẦN HOÀN")) {
			return "STH";
		}
		if ( (diagnose.contains("MẠCH VÀNH") || diagnose.contains("MV")) && diagnose.contains("STENT")) {
			return "MVSTENT";
		}
		if (diagnose.contains("BÀNG QUANG") && (diagnose.contains("HẸP") || diagnose.contains("HEP"))) {
			return "HBQ";
		}
		if (diagnose.contains("BÀNG QUANG") && diagnose.contains("VIÊM")) {
			return "VBQ";
		}
		if (diagnose.contains("BÀNG QUANG") && diagnose.contains("VIÊM")) {
			return "VBQ";
		}
		if (diagnose.contains("K ") && diagnose.contains("TRỰC TRÀNG")) {
			return "KTT";
		}
		if (diagnose.contains("SỐT") && diagnose.contains("SIÊU VI")) {
			return "SSV";
		}
		if (diagnose.contains("SỐT") && diagnose.contains("HUYẾT")) {
			return "SXH";
		}
		if (diagnose.contains("VIÊM") && diagnose.contains("XOANG")) {
			return "VXOANG";
		}
		if (diagnose.contains("TẢ")) {
			return "TA";
		}
		if (diagnose.contains("TIỀN") && diagnose.contains("TUYẾN") && (diagnose.contains("K ") || diagnose.contains("BƯỚU"))) {
			return "UTTTL";
		}
		if (diagnose.contains("TIỀN") && diagnose.contains("TUYẾN") && diagnose.contains("VIÊM")) {
			return "VTTL";
		}
		if (diagnose.contains("CHÓNG MẶT")) {
			return "CM";
		}
		if (diagnose.contains("RUNG NHĨ")) {
			return "RN";
		}
		if (diagnose.contains("NHỒI MÁU CƠ TIM")) {
			return "NMCT";
		}
		if (diagnose.contains("PARKINSON")) {
			return "PARK";
		}
		if (diagnose.contains("K ") && diagnose.contains("PHỔI")) {
			return "UTP";
		}
		if (diagnose.contains("K ") && (diagnose.contains("VU") || diagnose.contains("VÚ"))) {
			return "UTV";
		}
		if (diagnose.contains("K ") && diagnose.contains("DẠ DÀY")) {
			return "UTDD";
		}
		if (diagnose.contains("GÚT") || diagnose.contains("GOUT")) {
			return "GOUTE";
		}
		if (diagnose.contains("U Ổ BỤNG") || diagnose.contains("Ổ BỤNG")) {
			return "OBUNG";
		}
		if (diagnose.contains("HẠCH BẠCH HUYẾT") || diagnose.contains("BẠCH HUYẾT") || diagnose.contains("BH")) {
			return "HBH";
		}
		if (diagnose.contains("\\3 NHÁNH") || diagnose.contains("BMV") || diagnose.contains("BỆNH MẠCH VÀNH") || diagnose.contains("BMV 3 NHÁNH")) {
			return "BMV3N";
		}
		if (diagnose.contains("GÃY")) {
			return "GXUONG";
		}
		if (diagnose.contains("XUẤT HUYẾT NÃO")) {
			return "XHN";
		}
		if (diagnose.contains("THIẾU MÁU")) {
			return "TMAU";
		}
		if (diagnose.contains("THOÁI HÓA") || diagnose.contains("THOÁI HOÁ") || diagnose.contains("THCS") ) {
			return "THHOA";
		}
		if (diagnose.contains("CUSHING")) {
			return "CUSHING";
		}
		if (diagnose.contains("TBMMN") || diagnose.contains("TAI BIẾN") && diagnose.contains("NÃO")) {
			return "TBMMN";
		}
		if (diagnose.contains("TRÀN DỊCH MÀNG PHỔI")) {
			return "TDMPHOI";
		}
		if (diagnose.contains("ĐAU THẮT NGỰC")) {
			return "DTN";
		}
		if (diagnose.contains("XƠ VỮA ĐỘNG MẠCH")) {
			return "XVDM";
		}
		if (diagnose.contains("LOÃNG XƯƠNG")) {
			return "LXUONG";
		}
		if (diagnose.contains("LAO MÀNG NÃO")) {
			return "LMN";
		}
		if (diagnose.contains("ĐỘT QUỴ NÃO")) {
			return "DQN";
		}
		if (diagnose.contains("TÁO BÓN")) {
			return "TB";
		}
		if (diagnose.contains("VIÊM MÀNG NÃO")) {
			return "VMN";
		}
		if (diagnose.contains("ỐNG CỔ TAY")) {
			return "HCOCT";
		}
		if (diagnose.contains("THA")) {
			return "THA";
		}
		//System.out.println(diagnose);
		return "OTHER";
	}
	private void addDiagnoseList(File file, List<Pair<String, Integer>> fieldList, List<String> diagnoseList, String resultPath) throws EncryptedDocumentException, InvalidFormatException, IOException {
		Workbook dataFile = WorkbookFactory.create(file);
		Sheet dataSheet = dataFile.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		Iterator<Row> rowIterator = dataSheet.rowIterator();
		rowIterator.next(); // skip header
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("fullData");
		//create header
		Row headerRow = sheet.createRow(0);
		for(int cellNum = 0; cellNum < fieldList.size();cellNum++) {
			if(cellNum < DIAGNOSE_COLUMN) {
				Cell cell = headerRow.createCell(cellNum);
				cell.setCellValue(fieldList.get(cellNum).getKey());
			} else if(cellNum > DIAGNOSE_COLUMN) {
				Cell cell = headerRow.createCell(cellNum+diagnoseList.size()-1);
				cell.setCellValue(fieldList.get(cellNum).getKey());
			} else {
				for (int cn = 0; cn < diagnoseList.size(); cn++) {
					Cell cell = headerRow.createCell(DIAGNOSE_COLUMN + cn);
					cell.setCellValue(diagnoseList.get(cn));
				}
			}
			
		}
		
		int rowNum = 1;
		while (rowIterator.hasNext()) {
            Row dataRow = rowIterator.next();
            Row row = sheet.createRow(rowNum++);
            for (Pair<String,Integer> field : fieldList) {
            	int fieldColumn = fieldList.indexOf(field);
            	if (fieldColumn < DIAGNOSE_COLUMN) {
            		Cell cell = row.createCell(fieldColumn);
                	cell.setCellValue(dataFormatter.formatCellValue(dataRow.getCell(field.getValue())));
            	} else if (fieldColumn > DIAGNOSE_COLUMN) {
            		Cell cell = row.createCell(fieldColumn + diagnoseList.size()-1);
                	cell.setCellValue(dataFormatter.formatCellValue(dataRow.getCell(field.getValue())));
            	} else {
            		List<String> rawDiagnoseList = findDiagnoseFromCell(dataFormatter, dataRow.getCell(DIAGNOSE_COLUMN));
        			List<String> rowDiagnoseList = new ArrayList<String>();
        			for (String rawDiagnose : rawDiagnoseList) {
        				String diagnose = normalizeDiagnose(rawDiagnose);
        				if (!rowDiagnoseList.contains(diagnose)) {
        					rowDiagnoseList.add(diagnose);
        				}
        			}
            		for (int cn = 0; cn < diagnoseList.size();cn++) {
            			Cell cell = row.createCell(DIAGNOSE_COLUMN + cn);
            			if (rowDiagnoseList.contains(diagnoseList.get(cn))) {
            				cell.setCellValue("yes");
            			} else {
            				cell.setCellValue("no");
            			}
            		}
            	}
            }
		}
		
		FileOutputStream fileOut = new FileOutputStream(resultPath+"\\"+file.getName());
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
		
	}
	private void removeFieldWithLessData (File file, List<Pair<String,Integer>> fieldList, String resultPath) throws EncryptedDocumentException, InvalidFormatException, IOException {
		
		Workbook dataFile = WorkbookFactory.create(file);
		Sheet dataSheet = dataFile.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		Iterator<Row> rowIterator = dataSheet.rowIterator();
		rowIterator.next(); // skip header
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("SplitAndFilteredData");
		//create header
		Row headerRow = sheet.createRow(0);
		for(int cellNum = 0; cellNum < fieldList.size();cellNum++) {
			Cell cell = headerRow.createCell(cellNum);
			cell.setCellValue(fieldList.get(cellNum).getKey());
		}
		
		int rowNum = 1;
		while (rowIterator.hasNext()) {
            Row dataRow = rowIterator.next();
            Row row = sheet.createRow(rowNum++);
            for (Pair<String,Integer> field : fieldList) {
            	Cell cell = row.createCell(fieldList.indexOf(field));
            	cell.setCellValue(dataFormatter.formatCellValue(dataRow.getCell(field.getValue())));
            }
		}
		
		FileOutputStream fileOut = new FileOutputStream(resultPath+"\\"+file.getName());
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
	}
	
	private List<Pair<String,Integer>> getFieldMap (String fileName, int minValueToKeep) throws IOException {
		List<Pair<String,Integer>> result = new ArrayList<Pair<String, Integer>>();
		Path filePath = Paths.get(fileName);
		List<String> contents = Files.readAllLines(filePath);
		String fieldMap = contents.get(2);
		fieldMap = fieldMap.substring(0, fieldMap.length()-1);
		List<String> fieldList = Arrays.asList(fieldMap.split(";"));
		for (String field : fieldList) {
			List<String> fieldNameAndValue = Arrays.asList(field.split(": "));
			String fieldName = fieldNameAndValue.get(0);
			int fieldValue = Integer.parseInt(fieldNameAndValue.get(1));
			if (fieldValue >= minValueToKeep) {
				result.add(new Pair<String,Integer>(fieldName,fieldList.indexOf(field)));
			}
		}
		return result;

	}
	private void copySheet(Sheet dataSheet, Sheet newSheet) {
		Iterator<Row> rowIterator = dataSheet.rowIterator();
		DataFormatter dataFormatter = new DataFormatter();
		int rowNum = 0;
		while (rowIterator.hasNext()) {
            Row dataRow = rowIterator.next();
            Row newRow = newSheet.createRow(rowNum++);
            Iterator<Cell> cellIterator = dataRow.cellIterator();
            int cellNum = 0;
            while (cellIterator.hasNext()) {
                Cell dataCell = cellIterator.next();
                newRow.createCell(cellNum++)
                .setCellValue(dataFormatter.formatCellValue(dataCell));
            }
		}
	}
}
