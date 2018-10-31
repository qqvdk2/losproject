package datavisian.lospredict.util;

public enum Sex {
	UNKNOWN(0),
	MALE(1),
    FEMALE(2);

	 private int label;

	    Sex(int label) {
	        this.label = label;
	    }

	    public int label() {
	        return label;
	    }
	  
	    public Sex getEnum(int code) {
	    	for (Sex value: values()) {
	    		if (value.label == code) {
	    			return value;
	    		} 
	    	}
			return UNKNOWN;
	    }
}
