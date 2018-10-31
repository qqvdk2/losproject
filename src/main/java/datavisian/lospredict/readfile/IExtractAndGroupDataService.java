package datavisian.lospredict.readfile;

public interface IExtractAndGroupDataService {
	public void extractAndSeparateBloodTestFields(PatientInfoDto rawData, String resultDirectory, String resultName);


}
