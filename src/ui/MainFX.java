package ui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import businessLogic.AnalysisService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class MainFX extends Application {

    private TextArea inputArea;
    private TextFlow outputFlow;
    private AnalysisService service;
    private Button showBarChartButton;
    private Button showPieChartButton;
    private ProgressIndicator progressIndicator;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("FeeLens🔍");
        

        String path = Paths.get("assets/Icon.png").toUri().toString();
        Image logo = new Image(path);
        
        primaryStage.getIcons().add(logo);

        // --- SECCIÓN LATERAL (TÍTULO, CONTACTO Y BOTÓN DE SALIDA) ---
        VBox sideMenu = new VBox(20);
        
        sideMenu.setAlignment(Pos.CENTER);
        sideMenu.setPadding(new Insets(20));
        sideMenu.setStyle("-fx-background-color: #1E88E5;");
        
        String logoString = Paths.get("assets/IconErased.png").toUri().toString();
        Image logoErased = new Image(logoString);
        ImageView logoView = new ImageView(logoErased);

        // Ajustar tamaño de la imagen
        logoView.setFitWidth(100);
        logoView.setPreserveRatio(true);

        Label title = new Label("FeeLens 🔍");
        title.setFont(Font.font(24));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Emociones detrás de las palabras");
        subtitle.setFont(Font.font(14));
        subtitle.setTextFill(Color.WHITE);
        subtitle.setWrapText(true);
        subtitle.setAlignment(Pos.CENTER);

        Button exitButton = new Button("Salir");
        exitButton.getStyleClass().add("menu-button");
        exitButton.setOnAction(e -> primaryStage.close());

        // Contact information at the bottom of the side menu
        Label contactInfo = new Label("contacto: cordonez@unal.edu.co\nsescarraga@unal.edu.co");
        contactInfo.setFont(Font.font(12));
        contactInfo.setTextFill(Color.WHITE);
        contactInfo.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2); -fx-padding: 10px; -fx-background-radius: 5px;");
        contactInfo.setAlignment(Pos.CENTER);

        VBox sideContent = new VBox(20, logoView, title, subtitle, exitButton);
        sideContent.setAlignment(Pos.TOP_CENTER);

        BorderPane sideLayout = new BorderPane();
        sideLayout.setCenter(sideContent);
        sideLayout.setBottom(contactInfo);
        BorderPane.setAlignment(contactInfo, Pos.BOTTOM_CENTER);
        BorderPane.setMargin(contactInfo, new Insets(20, 0, 0, 0));

        sideMenu.getChildren().add(sideLayout);

        // --- SECCIÓN PRINCIPAL ---
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: linear-gradient(to bottom,#D4EAF7, #A9D6F2, #7EC8E3);");

        Label inputLabel = new Label("Ingrese texto o seleccione un archivo:");
        inputLabel.getStyleClass().add("title-label");

        inputArea = new TextArea();
        inputArea.setPromptText("Escriba el texto aquí o seleccione un archivo...");
        inputArea.getStyleClass().add("text-area-custom");

        // --- BOTONES DE ANÁLISIS ---
        Button classifyTextButton = new Button("Analizar Texto");
        classifyTextButton.getStyleClass().add("custom-button");
        classifyTextButton.setOnAction(e -> classifyText());

        Button classifyFileButton = new Button("Analizar Archivo");
        classifyFileButton.getStyleClass().add("custom-button");
        classifyFileButton.setOnAction(e -> classifyFile(primaryStage));

        HBox buttonBox = new HBox(15, classifyTextButton, classifyFileButton);
        buttonBox.setAlignment(Pos.CENTER);

        // --- BOTONES DE VISUALIZACIÓN ---
        showBarChartButton = new Button("Ver Gráficos de Barras");
        showBarChartButton.getStyleClass().add("custom-button");
        showBarChartButton.setOnAction(e -> showBarCharts());
        showBarChartButton.setDisable(true);

        showPieChartButton = new Button("Ver Gráficos de Torta");
        showPieChartButton.getStyleClass().add("custom-button");
        showPieChartButton.setOnAction(e -> showPieCharts());
        showPieChartButton.setDisable(true);

        HBox chartButtons = new HBox(15, showBarChartButton, showPieChartButton);
        chartButtons.setAlignment(Pos.CENTER);

        // --- ÁREA DE RESULTADOS ---
        outputFlow = new TextFlow();
        outputFlow.setPadding(new Insets(10));
        outputFlow.setStyle("-fx-background-color: white; -fx-border-color: #A7C7E7; -fx-border-radius: 12px; -fx-padding: 12px;");

        // --- LOADING INDICATOR ---
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        Label loadingLabel = new Label("Analizando...");
        loadingLabel.setVisible(false);

        HBox loadingBox = new HBox(10, progressIndicator, loadingLabel);
        loadingBox.setAlignment(Pos.CENTER);

        // --- LAYOUT PRINCIPAL ---
        mainContent.getChildren().addAll(inputLabel, inputArea, buttonBox, outputFlow, loadingBox, chartButtons);

        BorderPane root = new BorderPane();
        root.setLeft(sideMenu);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void classifyText() {
        String input = inputArea.getText();
        if (input.isEmpty()) {
            showErrorAlert("Error", "Campo de texto vacío", "Por favor, ingrese texto para analizar.");
            return;
        }

        progressIndicator.setVisible(true);
        outputFlow.getChildren().clear();
        outputFlow.getChildren().add(new Text("Analizando..."));

        setButtonsDisabled(true);

        new Thread(() -> {
            service = new AnalysisService(input, "text");
            try {
                service.analyze();
                javafx.application.Platform.runLater(() -> {
                    displayResults(input);
                    enableChartButtons();
                });
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    outputFlow.getChildren().clear();
                    outputFlow.getChildren().add(new Text("Error al analizar el texto: " + e.getMessage()));
                    e.printStackTrace();
                });
            } finally {
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    setButtonsDisabled(false);
                });
            }
        }).start();
    }

    private void classifyFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona el archivo de texto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de texto", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            showErrorAlert("Error", "Archivo no seleccionado", "Por favor, seleccione un archivo de texto.");
            return;
        }
        String filePath = selectedFile.getAbsolutePath();

        progressIndicator.setVisible(true);
        outputFlow.getChildren().clear();
        outputFlow.getChildren().add(new Text("Analizando..."));

        setButtonsDisabled(true);

        new Thread(() -> {
            service = new AnalysisService(filePath, "file");
            try {
                service.analyze();
                javafx.application.Platform.runLater(() -> {
                    displayResults(filePath);
                    enableChartButtons();
                });
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    outputFlow.getChildren().clear();
                    outputFlow.getChildren().add(new Text("Error al analizar el archivo: " + e.getMessage()));
                    e.printStackTrace();
                });
            } finally {
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    setButtonsDisabled(false);
                });
            }
        }).start();
    }

    private void displayResults(String input) {
        outputFlow.getChildren().clear();

        Text inputText = new Text("Entrada: " + input + "\n\n");
        inputText.setStyle("-fx-font-weight: bold; -fx-fill: #333;");

        Text sentimentText = new Text("📊 Sentimiento Positivo/Negativo:\n");
        sentimentText.setStyle("-fx-font-weight: bold; -fx-fill: #333;");

        outputFlow.getChildren().addAll(inputText, sentimentText);

        for (Map.Entry<String, Integer> entry : service.getPositiveNegativeResults().entrySet()) {
            Text resultText = new Text(entry.getKey() + ": " + entry.getValue() + "\n");
            resultText.setStyle("-fx-fill: #555;");
            outputFlow.getChildren().add(resultText);
        }

        Text emotionText = new Text("\n😊 Clasificación de Emociones:\n");
        emotionText.setStyle("-fx-font-weight: bold; -fx-fill: #333;");
        outputFlow.getChildren().add(emotionText);

        if (service.getMode().equals("text")) {
            for (Map.Entry<String, Integer> entry : service.getEmotionResults().entrySet()) {
                Text resultText = new Text(entry.getKey() + ": " + entry.getValue() + "\n");
                resultText.setStyle("-fx-fill: #555;");
                outputFlow.getChildren().add(resultText);
            }
        } else {
            for (Map.Entry<String, Double> entry : service.getAggregatedEmotionResults().entrySet()) {
                Text resultText = new Text(entry.getKey() + ": " + entry.getValue() + "\n");
                resultText.setStyle("-fx-fill: #555;");
                outputFlow.getChildren().add(resultText);
            }
        }
    }

    private void enableChartButtons() {
        showBarChartButton.setDisable(false);
        showPieChartButton.setDisable(false);
    }

    private void setButtonsDisabled(boolean disabled) {
        showBarChartButton.setDisable(disabled);
        showPieChartButton.setDisable(disabled);
    }

    private void showBarCharts() {
        if (service == null) {
            showErrorAlert("Error", "Sin datos", "No hay datos de emociones para mostrar.");
            return;
        }

        Stage chartStage = new Stage();
        chartStage.setTitle("Gráficos de Barras - Emociones y Sentimientos");

        VBox chartContainer = new VBox(20);
        chartContainer.setPadding(new Insets(20));
        chartContainer.setStyle("-fx-background-color: linear-gradient(to bottom,#D4EAF7, #A9D6F2, #7EC8E3);");

        // Bar chart for emotions
        CategoryAxis xAxisEmotions = new CategoryAxis();
        NumberAxis yAxisEmotions = new NumberAxis();
        BarChart<String, Number> emotionBarChart = new BarChart<>(xAxisEmotions, yAxisEmotions);
        emotionBarChart.setTitle("Distribución de Emociones");

        XYChart.Series<String, Number> emotionSeries = new XYChart.Series<>();
        emotionSeries.setName("Emociones");
        if (service.getMode().equals("text")) {
            for (Map.Entry<String, Integer> entry : service.getEmotionResults().entrySet()) {
                emotionSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        } else {
            for (Map.Entry<String, Double> entry : service.getAggregatedEmotionResults().entrySet()) {
                emotionSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }
        emotionBarChart.getData().add(emotionSeries);

        // Bar chart for sentiments
        CategoryAxis xAxisSentiments = new CategoryAxis();
        NumberAxis yAxisSentiments = new NumberAxis();
        BarChart<String, Number> sentimentBarChart = new BarChart<>(xAxisSentiments, yAxisSentiments);
        sentimentBarChart.setTitle("Distribución de Sentimientos");

        XYChart.Series<String, Number> sentimentSeries = new XYChart.Series<>();
        sentimentSeries.setName("Sentimientos");
        for (Map.Entry<String, Integer> entry : service.getPositiveNegativeResults().entrySet()) {
            sentimentSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        sentimentBarChart.getData().add(sentimentSeries);

        chartContainer.getChildren().addAll(emotionBarChart, sentimentBarChart);

        Scene chartScene = new Scene(chartContainer, 800, 600);
        chartStage.setScene(chartScene);
        chartStage.show();
    }

    private void showPieCharts() {
        if (service == null) {
            showErrorAlert("Error", "Sin datos", "No hay datos de emociones para mostrar.");
            return;
        }

        Stage chartStage = new Stage();
        chartStage.setTitle("Gráficos de Torta - Emociones y Sentimientos");

        VBox chartContainer = new VBox(20);
        chartContainer.setPadding(new Insets(20));
        chartContainer.setStyle("-fx-background-color: linear-gradient(to bottom,#D4EAF7, #A9D6F2, #7EC8E3);");

        // Pie chart for emotions
        PieChart emotionPieChart = new PieChart();
        emotionPieChart.setTitle("Distribución de Emociones");

        if (service.getMode().equals("text")) {
            for (Map.Entry<String, Integer> entry : service.getEmotionResults().entrySet()) {
                emotionPieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        } else {
            for (Map.Entry<String, Double> entry : service.getAggregatedEmotionResults().entrySet()) {
                emotionPieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
        }

        // Pie chart for sentiments
        PieChart sentimentPieChart = new PieChart();
        sentimentPieChart.setTitle("Distribución de Sentimientos");

        for (Map.Entry<String, Integer> entry : service.getPositiveNegativeResults().entrySet()) {
            sentimentPieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        chartContainer.getChildren().addAll(emotionPieChart, sentimentPieChart);

        Scene chartScene = new Scene(chartContainer, 800, 600);
        chartStage.setScene(chartScene);
        chartStage.show();
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}