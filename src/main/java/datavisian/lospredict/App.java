package datavisian.lospredict;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DateUtil;

import datavisian.lospredict.processdata.ProcessingDataService;
import datavisian.lospredict.processdata.StatisticalData;
import datavisian.lospredict.readfile.ExtractAndGroupDataService;
import datavisian.lospredict.readfile.FilterPatientInfoService;
import datavisian.lospredict.readfile.PatientInfoDto;
import datavisian.lospredict.readfile.PatientInfoEntryDto;
import datavisian.lospredict.readfile.StandardizeExcelFileService;

public class App 
{
	
	public static final String DIR_PATH = "samplerawdata";
	public static final String RAW_DATA_PATH = "rawdata";
	public static final String FILTERED_DATA_PATH = "filteredData";
	public static final String SEPARATED_DATA_PATH = "separatedData";
	public static final String CLEANED_DATA_PATH = "cleanedData";
	
	public static final String SEPARATED_DIAGNOSE_DATA_PATH = "separatedDiagnoseData";
	public static final String FILTERED_DIAGNOSE_DATA_PATH = "filteredDiagnoseData";
	public static final String FILTERED_FIELDS_PATH = "filteredFieldsData";
	public static final String NORMALIZED_FIELDS_PATH = "normalizedFieldsData";
	private static final String RESULT_PATH = "result";
	private static final String RESULT_SAMPLE_PATH = "resultdatasample";
	
	public static final int MIN_RECORD_TO_KEEP = 400;
	
    public static void main( String[] args )
    
    {
//    	String dirPath = DIR_PATH;
//    	String resultPath = RESULT_SAMPLE_PATH;
//    	FilterPatientInfoService filterService = new FilterPatientInfoService();
//    	ExtractAndGroupDataService extractAndGroupService = new ExtractAndGroupDataService();
//    	File directory = new File(dirPath);
//		File[] files = directory.listFiles();
//		for (File file : files) {
//		 	PatientInfoDto rawData = filterService.readRawData(file);
//	    	extractAndGroupService.extractAndSeparateBloodTestFields(rawData,resultPath,file.getName());
//	     }
    	
    	
//    	StandardizeExcelFileService stdService = new StandardizeExcelFileService();
//    	stdService.standardizeExcelFiles(FILTERED_DATA_PATH,SEPARATED_DATA_PATH);
		
    	StatisticalData processService = new StatisticalData();
//    	processService.statisticData(SEPARATED_DATA_PATH);
    	ProcessingDataService processDataService = new ProcessingDataService();
//    	processDataService.removeFieldWithLessData(SEPARATED_DATA_PATH, CLEANED_DATA_PATH, MIN_RECORD_TO_KEEP);
//    	processService.statisticData(CLEANED_DATA_PATH);
//    	processDataService.separateDiagnoseFields(CLEANED_DATA_PATH, SEPARATED_DIAGNOSE_DATA_PATH);
//    	processService.statisticData(SEPARATED_DIAGNOSE_DATA_PATH);
//    	processDataService.removeFieldWithLessData(SEPARATED_DIAGNOSE_DATA_PATH, FILTERED_DIAGNOSE_DATA_PATH, MIN_RECORD_TO_KEEP);
//    	processService.statisticData(FILTERED_DIAGNOSE_DATA_PATH);
//    	String[] fieldsToRemove = {"Rh","OTHER"};
//    	processDataService.removeFields(FILTERED_DIAGNOSE_DATA_PATH, FILTERED_FIELDS_PATH, Arrays.asList(fieldsToRemove));
    	processDataService.normalizeFields(FILTERED_FIELDS_PATH, NORMALIZED_FIELDS_PATH);
    	
    	
    }
}
