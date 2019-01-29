package resources;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ReaderWriter {

    private static String removeStorage(Scanner sc, String wordToCheck  ){
    	// Removes any storage options in the Oracle DDL
        if (wordToCheck.equals(")")					// Storage options are after last ) 
                || wordToCheck.contains("segment")	// All storage keywords in Oracle
                || wordToCheck.contains("using")
                || wordToCheck.contains("pctfree")){
            wordToCheck = sc.next().toLowerCase();
            // Create keyword is the next keyword after any storage options
            while(!wordToCheck.contains("create") && sc.hasNext()){
                wordToCheck = sc.next().toLowerCase();
            }
            // If it is at the end of the file do not return that word because it will
            // write but instead return a closing parenthesis and semicolon
            if (!sc.hasNext()){
                return ") ;";
            }
            return ") ; \n " + wordToCheck;
        }
        return wordToCheck;
    }

    public static File createTempFile(ResultSet rs) throws IOException, SQLException {
    	// Creates temporary Oracle DDL file
        File tmpFile = File.createTempFile("oracleDDL", ".tmp");
        FileWriter writer = new FileWriter(tmpFile);

        while(rs.next()){
            writer.write(rs.getString(3));
        }
        writer.close();

        return tmpFile;
    }

    public static File createPgFile(File tmpFile) throws IOException {
    	// Creates the Postgres DDL file with appropriate changes
        File pgFile = new File("pgFile.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(pgFile));

        Scanner sc = new Scanner(tmpFile);
        sc.useDelimiter(" ");

        Map mp = new Map();
        while(sc.hasNext()){
            String word = sc.next().toLowerCase();
            word = removeStorage(sc, word);
            word = removeSchemaNames(sc, word);
            word = checkForCreateIndex(sc, word, writer);
            // Checks if the data type needs to be changed
            String toWrite = (mp.map.get(word) != null) ? mp.map.get(word) : word ;
            for (String key : mp.map.keySet()) {
                if (toWrite.startsWith(key)){
                    toWrite = toWrite.replace(key, mp.map.get(key));
                }
                if (toWrite.endsWith(key)){
                    toWrite = toWrite.replace(key, mp.map.get(key));
                }
            }
            writer.write(toWrite + " ");
        }
        writer.close();
        return pgFile;
    }
    
    private final static String removeSchemaNames(Scanner sc, String wordToCheck) {
    	if (wordToCheck.matches(".*[.].*")) {
			// Check if string matches regex. This is used to get rid of schema names 
			// so the schema name can be changed to whatever is needed in postgres
			wordToCheck = wordToCheck.substring(wordToCheck.indexOf(".")+1).trim();
    	}
    	return wordToCheck;
    }
    
    private final static String checkForCreateIndex(Scanner sc, String wordToCheck, 
    		Writer writer) throws IOException {
    	// This checks for a create index statement. These are special as they do not
    	// contain any data types but need to have a semicolon added at the end because
    	// Oracle's generate_ddl does not include them.
    	String word = wordToCheck;
    	if (word.equals("index")) {
    		writer.write(word + " ");
    		word = sc.next().toLowerCase();
    		while(!word.contains(")")) {    			
    			word = removeSchemaNames(sc, word);
    			writer.write(word + " ");
    			word = sc.next().toLowerCase();
    		}
    		writer.write(word + " ; \n");
    		return sc.next().toLowerCase();
    	}
    	return word;
    }
}
