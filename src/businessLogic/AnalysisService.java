package businessLogic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.PositiveNegativeClassifier;
import data.EmotionsClassifier;
import data.Classification;


// This file manages the classifications so we can show the results.
public class AnalysisService {
    private String input;
    private String mode;
    
    //Map is like a zip in python
    private Map<String, Integer> positiveNegativeResults;
    private Map<String, Integer> emotionResults;
    private Map<String, Double> aggregatedEmotionResults;
    
    //Constructor, we need the texto/filePath (input) and the mode to classify the text/file.
    public AnalysisService(String input, String mode) {
        this.input = input;
        this.mode = mode;
        
        //Intialize the maps.
        this.positiveNegativeResults = new HashMap<>();
        this.emotionResults = new HashMap<>();
        this.aggregatedEmotionResults = new HashMap<>();
    }
    
    // analyze function is what we can see in the terminal
    public void analyze() throws IOException {
        if (mode.equals("text")) {
            analyzeText(input);
        } else if (mode.equals("file")) {
            analyzeFile(input);
        }
    }

    private void analyzeText(String text) throws IOException {
    	//This function is executed if the choice was text
    	
        // Pos/Neg classification
        PositiveNegativeClassifier pnClassifier = new PositiveNegativeClassifier(text); //let's create the classifier
        pnClassifier.classify("single"); //It's with single mode because is just text
        pnClassifier.analyze(); //The analyze function of PosNegClassifier, we'll see it soon
        extractPositiveNegativeResults(pnClassifier.getValues()); // And we apply the extract... function to the values of the pos and negs.

        // Same for emotions classifier
        EmotionsClassifier emotionsClassifier = new EmotionsClassifier(text);
        emotionsClassifier.classify("single");
        emotionsClassifier.analyze();
        extractEmotionResults(emotionsClassifier.getValues());
    }

    private void analyzeFile(String filePath) throws IOException {
    	//Now this is a little tricky, because is the file analyzer, it seems like the other but is really different
        // Lets create the maps for the average of emotions
        Map<String, Integer> emotionSum = new HashMap<>();
        Map<String, Integer> emotionCount = new HashMap<>();
        
        // Then, because the posnegclassifier writes in the PNResume.txt the number of pos and negs, we dont chance anything
        PositiveNegativeClassifier pnClassifier = new PositiveNegativeClassifier(filePath);
        pnClassifier.classify("file"); //We only need to tell them "it's file mode"
        pnClassifier.analyze();
        extractPositiveNegativeResults(pnClassifier.getValues());

        // Now for the emotions we need to take every classification of each line and average the values for each emotion
        EmotionsClassifier emotionsClassifier = new EmotionsClassifier(filePath);
        emotionsClassifier.classify("file");
        emotionsClassifier.analyze(); //It could be a good idea to see a little this function in the EmotionClassifier class.
        
        // Once we get the values for each line, we need to take all of those classification and take the values back
        // in order to sum  them and make an average
        for (Classification entry : emotionsClassifier.getValues()) {
            String emotion = entry.getName();
            int value = entry.getValue();
            
            //Here we full the emotionSum and Coun maps to make the emotion value average
            emotionSum.put(emotion, emotionSum.getOrDefault(emotion, 0) + value);
            emotionCount.put(emotion, emotionCount.getOrDefault(emotion, 0) + 1);
        }

        // Finally, we use this map of every sum and with that we calculate the average
        for (Map.Entry<String, Integer> entry : emotionSum.entrySet()) {
            String emotion = entry.getKey();
            double average = entry.getValue() / (double) emotionCount.get(emotion);
            aggregatedEmotionResults.put(emotion, average); //And this is our result
        }
    }

    private void extractPositiveNegativeResults(List<Classification> values) {
    	//As we were talking, this function takes the values of the pos/neg classification and maps each value with its respective name
        for (Classification classification : values) { //Take all classifications and map it.
            positiveNegativeResults.put(classification.getName(), classification.getValue()); //Put function to put the labels and values on the map
        }
    }

    private void extractEmotionResults(List<Classification> values) {
    	//Same as the previous
        for (Classification classification : values) {
            emotionResults.put(classification.getName(), classification.getValue());
        }
    }

    public Map<String, Integer> getPositiveNegativeResults() {
        return positiveNegativeResults;
    }

    public Map<String, Integer> getEmotionResults() {
        return emotionResults;
    }

    public Map<String, Double> getAggregatedEmotionResults() {
        return aggregatedEmotionResults;
    }

	public String getMode() {
		return this.mode;
	}
}
