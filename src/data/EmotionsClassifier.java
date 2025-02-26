package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import businessLogic.TextAnalyzer;

public class EmotionsClassifier extends TextAnalyzer{
	private ArrayList<Classification> values; //To save the classifications
	private String resumeFile = "resources/EmotionsResume.txt"; //File to save the classification of the python file
	private String scriptPath = "resources/Emotions.py";//Python file
	
    public EmotionsClassifier(String text) {
        super(text); //it goes to TextAnalyzer class
		this.values = new ArrayList<>(); //Initialize the list
	}
	
    // Reads the file to classify and writes in the resumeFile the classification 
	public void classify(String mode) {
		runPythonScript(scriptPath,text,mode);		
	}
	
	// Extract all the info of the resumeFile
	public void analyze() throws IOException{		
		File file = new File(resumeFile);

        if (!file.exists()) {
            throw new FileNotFoundException("No se encontró el archivo: " + resumeFile);
        }

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(", ");
            String anger = parts[0];
            String love = parts[2];
            String happiness = parts[4];
            String fear = parts[6];
            String sadness = parts[8];
            
            Integer anger_val = Integer.parseInt(parts[1]);
            Integer love_val = Integer.parseInt(parts[3]);
            Integer happiness_val = Integer.parseInt(parts[5]);
            Integer fear_val = Integer.parseInt(parts[7]);
            Integer sadness_val = Integer.parseInt(parts[9]);
            
            values.add(new Classification(anger,anger_val));
            values.add(new Classification(love,love_val));
            values.add(new Classification(happiness,happiness_val));
            values.add(new Classification(fear,fear_val));
            values.add(new Classification(sadness,sadness_val));
        }
        br.close();
	}
	
	public ArrayList<Classification> getValues() {
		return values;
	}
}
