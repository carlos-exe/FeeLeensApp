package businessLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import data.Classification;

public abstract class TextAnalyzer {
    protected String text;

    public TextAnalyzer(String text) {
        this.text = text;
    }

    protected String runPythonScript(String scriptPath, String input, String mode) {
        try {
            // Construcción del proceso con los parámetros adecuados
            ProcessBuilder pb = new ProcessBuilder("py","-3.10", scriptPath, input, mode);
            Process process = pb.start();

            // Lectura de la salida estándar del script de Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Lectura de posibles errores del script de Python
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            // Espera a que el proceso termine y revisa el código de salida
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "Error al ejecutar el script de Python. Código de salida: " + exitCode + "\n" + errorOutput;
            }

            return output.toString().trim();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error al ejecutar el script de Python.";
        }
    }
    
    public abstract ArrayList<Classification> getValues();

    public abstract void analyze() throws IOException;
}

