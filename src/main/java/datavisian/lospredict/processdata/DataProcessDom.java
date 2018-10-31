package datavisian.lospredict.processdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.*;

public class DataProcessDom {

	private static int total = 0;
	private static int hasSex = 0;
	private static List<Pair<String,Integer>> fieldMap = new ArrayList<Pair<String, Integer>>();
	
	public static int getTotal() {
		return total;
	}
	public static void setTotal(int total) {
		DataProcessDom.total = total;
	}
	public static int getHasSex() {
		return hasSex;
	}
	public static void setHasSex(int hasSex) {
		DataProcessDom.hasSex = hasSex;
	}
	public static List<Pair<String, Integer>> getFieldMap() {
		return fieldMap;
	}
	public static void setFieldMap(List<Pair<String, Integer>> fieldMap) {
		DataProcessDom.fieldMap = fieldMap;
	}
	

}
