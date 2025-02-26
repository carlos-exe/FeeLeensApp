package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class TextAnalyzer {
	protected String text;
	
	public TextAnalyzer(String text) {
		this.text = text;
	}
	
	protected String runPythonScript(String scriptPath, String input) {
		try {	
	        ProcessBuilder pb = new ProcessBuilder("python", scriptPath, input);
	        Process process = pb.start();
	
	        // Lee la salida estándar (lo que imprime el script con print)
	        BufferedReader reader = new BufferedReader(
	            new InputStreamReader(process.getInputStream())
	        );
	        
	        
	        String line;
	        while ((line = reader.readLine()) != null) {
	            return line;
	        }
	
	        // Lee la salida de error (lo que imprime el script si falla algo)
	        BufferedReader errorReader = new BufferedReader(
	            new InputStreamReader(process.getErrorStream())
	        );
	        
	        
	        while ((line = errorReader.readLine()) != null) {
	           return "Error de Python: ";
	        }
	
	        int exitCode = process.waitFor();
	        if (exitCode != 0) {
	            return "Error al ejecutar el script de Python. Código de salida: ";
	        }
	        
	        return "Error";
	    } catch (IOException | InterruptedException e) {
	        e.printStackTrace();
	        return "Error";
	    }
	}
	
	public abstract ArrayList<String> analyze();
}
