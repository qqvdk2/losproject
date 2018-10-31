package datavisian.lospredict.readfile;

import java.util.Date;

import datavisian.lospredict.util.Sex;

public class PatientInfoEntryDto {
	
	private long id;
	private int los;
	private Date dayIn;
	private Date dayOut;
	private Date dayAppoint;
	private double realLos;
	private double actualLos;
	private int age;
	private Sex sex;
	private String name;
	private String diagnose;
	private String result;
	private String status;
	private String bloodTest;
	private boolean hasValue;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDayAppoint() {
		return dayAppoint;
	}
	public void setDayAppoint(Date dayAppoint) {
		this.dayAppoint = dayAppoint;
	}
	public double getRealLos() {
		return realLos;
	}
	public void setRealLos(double realLos) {
		this.realLos = realLos;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isHasValue() {
		return hasValue;
	}
	public PatientInfoEntryDto() {
		this.sex = Sex.UNKNOWN;
		this.hasValue = false;
	}
	
	public void setHasValue(boolean hasValue) {
		this.hasValue = hasValue;
	}
	public int getLos() {
		return los;
	}
	public Date getDayIn() {
		return dayIn;
	}
	public void setDayIn(Date dayIn) {
		this.dayIn = dayIn;
	}
	public Date getDayOut() {
		return dayOut;
	}
	public void setDayOut(Date dayOut) {
		this.dayOut = dayOut;
	}
	public void setLos(int los) {
		this.los = los;
	}
	public double getActualLos() {
		return actualLos;
	}
	public void setActualLos(double actualLos) {
		this.actualLos = actualLos;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getDiagnose() {
		return diagnose;
	}
	public void setDiagnose(String diagnose) {
		this.diagnose = diagnose;
	}
	public String getBloodTest() {
		return bloodTest;
	}
	public Sex getSex() {
		return sex;
	}
	public void setSex(Sex sex) {
		this.sex = sex;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setBloodTest(String bloodTest) {
		this.bloodTest = bloodTest;
	}
	
}
