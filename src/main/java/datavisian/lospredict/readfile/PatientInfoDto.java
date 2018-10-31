package datavisian.lospredict.readfile;

import java.util.List;

public class PatientInfoDto {
	
	private static long idCount = 0;
	private long id;
	private List<PatientInfoEntryDto> entries;
	
	public PatientInfoDto() {
		this.id = idCount++;
	}
	
	public long getId() {
		return id;
	}

	public List<PatientInfoEntryDto> getEntries() {
		return entries;
	}

	public void setEntries(List<PatientInfoEntryDto> entries) {
		this.entries = entries;
	}

}
