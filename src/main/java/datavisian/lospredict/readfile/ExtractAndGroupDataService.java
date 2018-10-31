package datavisian.lospredict.readfile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExtractAndGroupDataService implements IExtractAndGroupDataService {
	
	private static final List<String> DKT_LIST = Arrays.asList( " đã kiểm tra ", "đã kiểm tra", "DA KTRA", "ĐA KTRA", "đktra","đã ktra", "Đktra", "ĐÃ KTRA","ĐÃ ktra", "Đã ktra", "đã ktra", "Đã ktra", "da ktra", "đa ktra", "da kta", "dktra", "đ ktra", "ĐÃ KT", "Đã KT", "đã KT", " đã kt", " đã kt", "Đã kt", "đã kt","đã kt", "da kt", "đãkt", "Đã kt", "Đkt", "ĐKT", "DKT", "ĐKt", " đkt ", "đkt", "dkt");
	
	private static final int TOTAL_IMPORTANT_FIELDS = 9;
	public void extractAndSeparateBloodTestFields(PatientInfoDto rawData, String resultDirectory, String resultName) {
		
		List<String> fields = new ArrayList<String>();
		standardizeBloodTestAndFindField(rawData,fields);
	
//			for(PatientInfoEntryDto entry: rawData.getEntries()) {
//				System.out.println(entry.getId());
//				System.out.println(entry.getSex());
//				System.out.println(entry.getBloodTest());
//			}

		
		try {
			createExtractData(rawData,fields,resultDirectory,resultName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createExtractData(PatientInfoDto rawData, List<String> fields, String resultDirectory, String resultName) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		CreationHelper createHelper = workbook.getCreationHelper();
		Sheet sheet = workbook.createSheet("SeparatedData");
		
		//create header
		int headerRowNum = 0;
		Row headerRow = sheet.createRow(0);
		headerRow.createCell(headerRowNum++).setCellValue("id");
		//headerRow.createCell(headerRowNum++).setCellValue("name");
		headerRow.createCell(headerRowNum++).setCellValue("los");
		headerRow.createCell(headerRowNum++).setCellValue("reallos");
		headerRow.createCell(headerRowNum++).setCellValue("actuallos");
		headerRow.createCell(headerRowNum++).setCellValue("result");
		headerRow.createCell(headerRowNum++).setCellValue("status");
		headerRow.createCell(headerRowNum++).setCellValue("diagnose");
		headerRow.createCell(headerRowNum++).setCellValue("sex");
		headerRow.createCell(headerRowNum++).setCellValue("age");
		for(int index = 0; index < fields.size(); index++) {
			Cell cell = headerRow.createCell(index+TOTAL_IMPORTANT_FIELDS);
			cell.setCellValue(fields.get(index));
		}
		// Create rows and cells with basic patients data
		int rowNum = 1;
		for(PatientInfoEntryDto entry : rawData.getEntries()) {
            Row row = sheet.createRow(rowNum++);
            int cellNum = 0;
            row.createCell(cellNum++)
            		.setCellValue(entry.getId());
//            row.createCell(cellNum++)
//                    .setCellValue(entry.getName());
            row.createCell(cellNum++)
                    .setCellValue(entry.getLos());
            row.createCell(cellNum++)
    				.setCellValue(entry.getRealLos());
            row.createCell(cellNum++)
            		.setCellValue(entry.getActualLos());
            row.createCell(cellNum++)
            		.setCellValue(entry.getResult());
            row.createCell(cellNum++)
    				.setCellValue(entry.getStatus());
            row.createCell(cellNum++)
                    .setCellValue(entry.getDiagnose());
            row.createCell(cellNum++)
            		.setCellValue(entry.getSex().label());
            row.createCell(cellNum++)
    				.setCellValue(entry.getAge());
         // Create rows and cells with fields data
            List<String> testDataList = Arrays.asList(entry.getBloodTest().split("\\|"));
            for(int index = 0; index < fields.size(); index++) {
            	String field = fields.get(index);
            	Cell cell = row.createCell(index+TOTAL_IMPORTANT_FIELDS);
            	for(String testData : testDataList) {
            		List<String> testFieldAndResult = Arrays.asList(testData.split(":"));
            		if(field.equals(testFieldAndResult.get(0))) {
						try {
							cell.setCellValue(testFieldAndResult.get(1));
						} catch (ArrayIndexOutOfBoundsException e) {
							if(!testData.endsWith(":")) {
								System.out.println(testData + entry.getName());
							}
							
						}
            			break;
            		}
            	}
            }
		}
		
		for(int i = 0; i < fields.size()+TOTAL_IMPORTANT_FIELDS; i++) {
            sheet.autoSizeColumn(i);
        }
		FileOutputStream fileOut = new FileOutputStream(resultDirectory+"\\"+resultName+"x");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
	}

	private void standardizeBloodTestAndFindField(PatientInfoDto rawData, List<String> fields) {

			List<PatientInfoEntryDto> standardizedEntries = new ArrayList<PatientInfoEntryDto>();
			for (PatientInfoEntryDto entry: rawData.getEntries()) {
			String tempBloodTest = entry.getBloodTest();
			for(String DKT : DKT_LIST) {
				tempBloodTest = tempBloodTest
						.replaceAll("\\("+DKT+"\\)", "")
						.replaceAll(DKT, "");
			}
			String stdBloodTest = tempBloodTest
				.replaceAll("Định lượng cấp NH3", "Định lượng cấp NH3x")
				.replaceAll(": ", ":")
				.replaceAll(" \\+ ", "+")
				.replaceAll(" - ", "-")
				.replaceAll("; ", ";")
				.replaceAll("Positive\\(\\+\\)", "+1")
				.replaceAll("Negative\\(-\\)", "-1")
				.replaceAll("(?<=\\d) +", "|");
			
			StringBuilder stdBloodTestBuilder = new StringBuilder();
			List<String> testList = Arrays.asList(stdBloodTest.split(";"));
			for(String test : testList) {
				String testFieldAndDatas = Arrays.asList(test.split(":", 2)).get(1);
				List<String> testFieldAndDataList = Arrays.asList(testFieldAndDatas.split("\\|"));
				for(String testFieldAndData : testFieldAndDataList) {
					stdBloodTestBuilder.append(testFieldAndData+"|");
					List<String> fieldAndData = Arrays.asList(testFieldAndData.split(":"));
					String field = fieldAndData.get(0);
					if(!fields.contains(field)) {
						fields.add(field);
					}
				}	
			}
			if (stdBloodTestBuilder.length() > 0) {
				stdBloodTestBuilder.setLength(stdBloodTestBuilder.length() - 1);
				}
			entry.setBloodTest(stdBloodTestBuilder.toString());
			standardizedEntries.add(entry);
			}
			rawData.setEntries(standardizedEntries);
		
	}

	
}
