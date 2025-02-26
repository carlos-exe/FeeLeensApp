package ui;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import businessLogic.AnalysisService;

public class Project {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String mode = ""; // Initialize this to prevent errors
        String inputText = "";

        // Ask the user if text or file
        System.out.println("Ingrese 'Texto' o 'Archivo':");
        String choice = scanner.nextLine().trim().toLowerCase(); // Text/file option

        if (choice.equals("texto")) {
            mode = "text";
            System.out.println("Ingrese el texto:");
            inputText = scanner.nextLine();
        } else if (choice.equals("archivo")) {
            mode = "file";
            System.out.println("Ingrese la ruta del archivo:");
            inputText = scanner.nextLine();
        } else {
            System.out.println("Opción inválida. Saliendo...");
            scanner.close();
            return;
        }

        // The service and the following lines creates execute the classification and set the results.
        // service will have an inventory of all the classifications
        AnalysisService service = new AnalysisService(inputText, mode);
        try {
            service.analyze(); // You should go to the analyze function in the AnalysisService class.
            // We do this in order to get all the classification results more details in the respective class.
        } catch (IOException e) {
            System.out.println("Error al analizar: " + e.getMessage());
            e.printStackTrace();
            scanner.close();
            return;
        }

        // Ahow Results
        System.out.println("\n========== Resultados ==========");
        System.out.println("Entrada: " + inputText);

        // Print pos/neg results
        System.out.println("\n📊 Sentimiento Positivo/Negativo:");
        service.getPositiveNegativeResults().forEach((label, value) -> 
            System.out.println(label + ": " + value));

        // Shoe the emotions classification results
        if (mode.equals("text")) {
            System.out.println("\n😊 Clasificación de Emociones:");
            service.getEmotionResults().forEach((label, value) -> 
                System.out.println(label + ": " + value));
        } else if (mode.equals("file")) {
            System.out.println("\n📈 Promedio de emociones en el archivo:");
            for (Map.Entry<String, Double> entry : service.getAggregatedEmotionResults().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }

        scanner.close();
    }
}
