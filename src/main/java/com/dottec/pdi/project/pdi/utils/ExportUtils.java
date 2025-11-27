package com.dottec.pdi.project.pdi.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ExportUtils {
	public static Path converStringToPath(String string) {
		return Path.of(string);
	}
	
	public static void exportToCSV(Path path, String string) throws IOException {
		if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
		Files.writeString(path, string);
	}

	public static String createCSV(Map<String, Object[]> entry, String[] headers) {
	    StringBuilder sb = new StringBuilder();
	    
	    sb.append("sep=,\n");
	    
	    if (headers != null && headers.length > 0) {
	        for (int i = 0; i < headers.length; i++) {
	            if (i > 0) sb.append(",");
	            sb.append(escapeCSV(headers[i]));
	        }
	        sb.append("\n");
	    }
	    
	    for(Map.Entry<String, Object[]> mapEntry : entry.entrySet()) {
	        String key = mapEntry.getKey();
	        Object[] values = mapEntry.getValue();
	        
	        sb.append(escapeCSV(key));
	        for(Object val : values) {
	            sb.append(",");
	            if(val != null) {
	                sb.append(escapeCSV(val.toString()));
	            }
	        }
	        sb.append("\n");
	    }
	    return sb.toString();
	}
	
    public static String createCSVFromNumberMap(Map<String, ? extends Number> map) {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, ? extends Number> entry : map.entrySet()) {
            sb.append(escapeCSV(entry.getKey()))
              .append(",")
              .append(entry.getValue())
              .append("\n");
        }
        return sb.toString();
    }
	
    public static String createCSVFromObjectMap(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("Chave,Valor\n");
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(escapeCSV(entry.getKey()))
              .append(",")
              .append(escapeCSV(entry.getValue() != null ? entry.getValue().toString() : ""))
              .append("\n");
        }
        return sb.toString();
    }
    
    public static String createCSVFromList(List<Object[]> list, String[] headers) {
        StringBuilder sb = new StringBuilder();
        sb.append("sep=,\n");
        
        if(headers != null && headers.length > 0) {
            for(int i = 0; i < headers.length; i++) {
                if(i > 0) sb.append(",");
                sb.append(escapeCSV(headers[i]));
            }
            sb.append("\n");
        }
        
        for(Object[] row : list) {
            for(int i = 0; i < row.length; i++) {
                if(i > 0) sb.append(",");
                sb.append(escapeCSV(row[i] != null ? row[i].toString() : ""));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public static String escapeCSV(String value) {
    	if(value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}