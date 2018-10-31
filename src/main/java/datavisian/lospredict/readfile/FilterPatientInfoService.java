package datavisian.lospredict.readfile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import datavisian.lospredict.util.Sex;


public class FilterPatientInfoService implements IFilterPatientInfoSercvice {
	
	private static final int MINIMUM_COLUMN_COUNT = 67;
	private static final int ID_COLUMN = 0;
	private static final int LOS_COLUMN = 22;
	private static final int DATE_IN_COLUMN = 11;
	private static final int DATE_OUT_COLUMN = 19;
	private static final int DATE_APPOINT_COLUMN = 56;
	private static final int AGE_COLUMN = 3;
	private static final int NAME_COLUMN = 2;
	private static final int SEX_COLUMN = 62;
	private static final int RESULT_COLUMN = 17;
	private static final int STATUS_COLUMN = 18;
	private static final int DIAGNOSE_COLUMN = 14;
	private static final int BLOODTEST_COLUMN = 57;
	
	private final static String[] DIABETES = {"ĐTĐ", "ĐÁI THÁO ĐƯỜNG"};

	 public PatientInfoDto readRawData(File file) {
		 
		 
		 PatientInfoDto rawData = new PatientInfoDto();
		
		     try {
				rawData = readAndExtract(file);
			} catch (EncryptedDocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		return rawData;
	 }

	 
	private PatientInfoDto readAndExtract(File file) throws EncryptedDocumentException, InvalidFormatException, IOException, ParseException {
		
		Workbook dataFile = WorkbookFactory.create(file);
		PatientInfoDto result = new PatientInfoDto();
		List<PatientInfoEntryDto> entries = new ArrayList<PatientInfoEntryDto>();
		
		Sheet dataSheet = dataFile.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		Iterator<Row> rowIterator = dataSheet.rowIterator();
		rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            
            PatientInfoEntryDto patientInfoEntry = new PatientInfoEntryDto();
            int lastColumn = Math.max(row.getLastCellNum(), MINIMUM_COLUMN_COUNT);
            for (int cn = 0; cn < lastColumn; cn++) {
                Cell cell = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (cell == null) {
                   // The spreadsheet is empty in this cell
                } else {
                	
                   if(cn == LOS_COLUMN) {
                	   patientInfoEntry.setLos(standardizeLos(dataFormatter.formatCellValue(cell)));
                   }
                   if(cn == ID_COLUMN) {
                	   patientInfoEntry.setId(standardizeId(dataFormatter.formatCellValue(cell)));
                   }
                   if(cn == AGE_COLUMN) {
                	   patientInfoEntry.setAge(standardizeAge(dataFormatter.formatCellValue(cell)));
                   }
                   if(cn == DATE_IN_COLUMN) {
                	   patientInfoEntry.setDayIn(stadardizeDate(cell));
                   }
                   if(cn == DATE_OUT_COLUMN) {
                	   patientInfoEntry.setDayOut(stadardizeDate(cell));
                   }
                   if(cn == DATE_APPOINT_COLUMN) {
                	   patientInfoEntry.setDayAppoint(stadardizeDate(cell));
                   }
                   if(cn == DIAGNOSE_COLUMN) {
                	   patientInfoEntry.setDiagnose(standardizeDiagnose(dataFormatter.formatCellValue(cell)));
                   }
                   if(cn == NAME_COLUMN) {
                	   patientInfoEntry.setName(dataFormatter.formatCellValue(cell));
                   }
                   if(cn == RESULT_COLUMN) {
                	   patientInfoEntry.setResult(dataFormatter.formatCellValue(cell));
                   }
                   if(cn == STATUS_COLUMN) {
                	   patientInfoEntry.setStatus(dataFormatter.formatCellValue(cell));
                   }
                   if (cn == BLOODTEST_COLUMN) {
                	   patientInfoEntry.setBloodTest(dataFormatter.formatCellValue(cell));
                   }
                   if (cn == SEX_COLUMN) {
                	   patientInfoEntry.setSex(standardizeSex(dataFormatter.formatCellValue(cell)));
                	   break;
                   }
                }
             }
			if(patientInfoEntry.getName() == null) {
				continue;
			}
			for (String diabete : DIABETES) {
				if (patientInfoEntry.getDiagnose().contains(diabete)) {
					patientInfoEntry.setHasValue(true);
				}
			}
			patientInfoEntry.setRealLos(calRealLos(patientInfoEntry.getDayIn(),patientInfoEntry.getDayOut(),patientInfoEntry.getLos()));
			patientInfoEntry.setActualLos(calActualLos(patientInfoEntry.getDayAppoint(),patientInfoEntry.getDayOut(),patientInfoEntry.getRealLos()));
			if(patientInfoEntry.getBloodTest() == null || patientInfoEntry.getBloodTest().isEmpty()){
				patientInfoEntry.setHasValue(false);
			}
			
			if (patientInfoEntry.isHasValue()) {
				entries.add(patientInfoEntry);
			}
        }
        result.setEntries(entries);
		return result;
	}
	
	private Sex standardizeSex(String rawSex) {
		if ("Nam".equals(rawSex)) {
			return Sex.MALE;
		}
		if ("Nữ".equals(rawSex)) {
			return Sex.FEMALE;
		}
		return Sex.UNKNOWN;
	}


	private int standardizeLos(String rawLos) throws NumberFormatException {
			return Integer.parseInt(rawLos);
	}
	private long standardizeId(String rawId) throws NumberFormatException {
		return Long.parseLong(rawId);
}
	private double calRealLos(Date in, Date out, double los) {
		Date inOne = in;
		Date inTwo = swapDateMonth(in);
		Date outOne = out;
		Date outTwo = swapDateMonth(out);
		double result = los;
		double realLosOne = subDate(inOne,outOne);
		double realLosTwo = subDate(inOne,outTwo);
		double realLosThree = subDate(inTwo,outOne);
		double realLosFour = subDate(inTwo,outTwo);
		double[] realLosList = {realLosOne, realLosTwo, realLosThree, realLosFour};
		for (double realLos : realLosList) {
			if (realLos < result && realLos > 0) {
				result = realLos;
			}
		}
		return result;
	}
	private double calActualLos(Date in, Date out, double realLos) {
		double result = calRealLos(in,out,realLos)+0.5;
		if (result > realLos) {
			return realLos;
		} else {
			return result;
		}
	}
	private Date swapDateMonth(Date date) {
		Calendar temp = new GregorianCalendar();
		Calendar result = new GregorianCalendar();
		temp.setTime(date);
		result.setTime(date);
		result.set(Calendar.DAY_OF_MONTH, temp.get(Calendar.MONTH)+1);
		result.set(Calendar.MONTH, temp.get(Calendar.DAY_OF_MONTH)-1);
		return result.getTime();
	}
	private double subDate (Date in, Date out) {
		return ChronoUnit.HOURS.between(in.toInstant(),out.toInstant())/24.0;
	}
	private Date stadardizeDate(Cell cell) throws ParseException {
		
		if (CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
			return cell.getDateCellValue();
		}
		DataFormatter dataFormatter = new DataFormatter();
		DateFormat dateFormatterOne = new SimpleDateFormat("dd/MM/yyyy h:mm");
		try {
			return dateFormatterOne.parse(dataFormatter.formatCellValue(cell));
		} catch (ParseException e) {
			DateFormat dateFormatterTwo = new SimpleDateFormat("dd/MM/yyyy");
			return dateFormatterTwo.parse(dataFormatter.formatCellValue(cell)); 
		}
		
	}
	private int standardizeAge(String rawAge) {
		return Integer.parseInt(rawAge.replaceAll("TU", ""));
	}
	
	private String standardizeDiagnose(String rawDiagnose) {
		return rawDiagnose.toUpperCase();
	}

}
