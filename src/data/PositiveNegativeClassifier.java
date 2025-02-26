package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import businessLogic.TextAnalyzer;


public class PositiveNegativeClassifier extends TextAnalyzer{
	private ArrayList<Classification> values; 
	private String resumeFile = "resources/PNResume.txt";
	private String scriptPath = "resources/PositivesAndNegatives.py";
	
	public PositiveNegativeClassifier(String text) {
		super(text);
		this.values = new ArrayList<>();
	}
	
	public void classify(String mode) {
		runPythonScript(scriptPath,text,mode);		
	}
	
	public void analyze() throws IOException{		
		File file = new File(resumeFile);

        if (!file.exists()) {
            throw new FileNotFoundException("No se encontró el archivo: " + resumeFile);
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(", ");
            String pos = parts[0];
            String neg = parts[2];
            Integer value_pos = Integer.parseInt(parts[1]);
            Integer value_neg = Integer.parseInt(parts[3]);
            
            values.add(new Classification(pos,value_pos));
            values.add(new Classification(neg,value_neg));
        }
        br.close();
	}
	
	public ArrayList<Classification> getValues() {
		return values;
	}

}
