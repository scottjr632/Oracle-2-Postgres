package resources;

import java.util.HashMap;

public class Map {
	/*
	 * HashMap that contains the data type conversions for Oracle to Postgres
	 */

    public HashMap<String, String> map = new HashMap<>();
    public Map(){
    	// There are more but these are the main data types that are needed
    	// to be converted.
        map.put("varchar2", "varchar");
        map.put("enable", "");
        map.put("number", "numeric");
        map.put("*", "1000");
        map.put("date", "timestamp(0)");
        map.put("blob", "bytea");
    }

}
