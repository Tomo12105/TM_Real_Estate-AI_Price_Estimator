package com.autominutes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main extends JFrame {

    private final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private final String MODEL_NAME = "timisoara-ai";

    private JComboBox<String> zoneCombo;
    private JTextField areaField;
    private JTextField roomsField;
    private JTextField yearField;
    private JComboBox<String> heatingCombo;
    private JCheckBox balconyCheck;
    private JCheckBox elevatorCheck;
    private JCheckBox acCheck;

    private JLabel priceBoxLabel;
    private JTextArea resultArea;
    private JButton predictButton;

    public Main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setTitle("AI Real Estate Timisoara");
        setSize(420, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 35, 25, 35));

        Font titleFont = new Font("Segoe UI", Font.BOLD, 22);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel titleLabel = new JLabel("Estimează Prețul");
        titleLabel.setFont(titleFont);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] zoneOptions = {
                "Aradului", "Blascovici", "Braytim", "Bucovina", "Cetate",
                "Circumvalatiunii", "Complex Studentesc", "Dacia", "Dumbravita",
                "Elisabetin", "Fabric", "Freidorf", "Ghiroda", "Giroc", "Girocului",
                "Iosefin", "Lipovei", "Mehala", "Mosnita Noua", "Odobescu",
                "Ronat", "Sag", "Sagului", "Soarelui", "Steaua", "Take Ionescu", "Torontalului"
        };
        zoneCombo = new JComboBox<>(zoneOptions);
        zoneCombo.setFont(inputFont);
        zoneCombo.setBackground(Color.WHITE);
        mainPanel.add(createInputPanel("Zona / Cartier", zoneCombo, labelFont));

        areaField = createPremiumTextField("72", inputFont);
        mainPanel.add(createInputPanel("Suprafață utilă (m²)", areaField, labelFont));

        roomsField = createPremiumTextField("3", inputFont);
        mainPanel.add(createInputPanel("Număr de camere", roomsField, labelFont));

        yearField = createPremiumTextField("2026", inputFont);
        mainPanel.add(createInputPanel("Anul construcției", yearField, labelFont));

        String[] heatingOptions = {"Încălzire Centralizată", "Centrală pe gaz (Proprie)"};
        heatingCombo = new JComboBox<>(heatingOptions);
        heatingCombo.setFont(inputFont);
        heatingCombo.setBackground(Color.WHITE);
        mainPanel.add(createInputPanel("Tip Încălzire", heatingCombo, labelFont));

        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JLabel facLabel = new JLabel("Facilități suplimentare");
        facLabel.setFont(labelFont);
        facLabel.setForeground(new Color(120, 120, 120));
        facLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(facLabel);

        balconyCheck = createPremiumCheckBox("Balcon", inputFont);
        elevatorCheck = createPremiumCheckBox("Lift / Elevator", inputFont);
        acCheck = createPremiumCheckBox("Aer Condiționat (AC)", inputFont);

        mainPanel.add(balconyCheck);
        mainPanel.add(elevatorCheck);
        mainPanel.add(acCheck);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        predictButton = new JButton("CERE ANALIZA AI");
        predictButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        predictButton.setForeground(Color.WHITE);
        predictButton.setBackground(new Color(33, 37, 41));
        predictButton.setOpaque(true);
        predictButton.setBorderPainted(false);
        predictButton.setFocusPainted(false);
        predictButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        predictButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension buttonSize = new Dimension(300, 45);
        predictButton.setPreferredSize(buttonSize);
        predictButton.setMinimumSize(buttonSize);
        predictButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        mainPanel.add(predictButton);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        priceBoxLabel = new JLabel("0 €", SwingConstants.CENTER);
        priceBoxLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        priceBoxLabel.setForeground(new Color(40, 116, 240));
        priceBoxLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(priceBoxLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        resultArea = new JTextArea("Aștept datele pentru a începe analiza...");
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        resultArea.setMargin(new Insets(10, 10, 10, 10));
        resultArea.setBackground(new Color(245, 245, 245));
        resultArea.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        mainPanel.add(scrollPane);

        add(mainPanel);

        predictButton.addActionListener(e -> startPredictionTask());
    }

    private JPanel createInputPanel(String labelText, JComponent inputComp, Font font) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel label = new JLabel(labelText);
        label.setFont(font);
        label.setForeground(new Color(120, 120, 120));
        panel.add(label, BorderLayout.NORTH);
        panel.add(inputComp, BorderLayout.CENTER);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        return panel;
    }

    private JTextField createPremiumTextField(String text, Font font) {
        JTextField tf = new JTextField(text);
        tf.setFont(font);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }

    private JCheckBox createPremiumCheckBox(String text, Font font) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(font);
        cb.setBackground(Color.WHITE);
        cb.setFocusPainted(false);
        return cb;
    }

    private void startPredictionTask() {
        try {
            String zone = (String) zoneCombo.getSelectedItem();
            String area = areaField.getText();
            String rooms = roomsField.getText();
            String year = yearField.getText();
            String heating = (String) heatingCombo.getSelectedItem();
            String hasBalcony = balconyCheck.isSelected() ? "Da" : "Nu";
            String hasElevator = elevatorCheck.isSelected() ? "Da" : "Nu";
            String hasAc = acCheck.isSelected() ? "Da" : "Nu";

            String prompt = String.format("Analizează următorul apartament:\\nZonă: %s\\nSuprafață: %s mp\\nCamere: %s\\nAn: %s\\nÎncălzire: %s\\nLift: %s\\nBalcon: %s\\nAC: %s",
                    zone, area, rooms, year, heating, hasElevator, hasBalcony, hasAc);

            String jsonPayload = String.format("{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}", MODEL_NAME, prompt);

            predictButton.setText("ANALIZĂ ÎN CURS...");
            predictButton.setEnabled(false);
            priceBoxLabel.setText("Se calculează...");
            priceBoxLabel.setForeground(new Color(150, 150, 150));
            resultArea.setText("Modelul AI procesează datele din piață...");

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    return fetchOllamaResponse(jsonPayload);
                }

                @Override
                protected void done() {
                    try {
                        String fullResponse = get();
                        formatAndDisplayResponse(fullResponse);
                    } catch (Exception ex) {
                        resultArea.setText("Eroare de procesare a răspunsului.");
                        priceBoxLabel.setText("Eroare");
                    }
                    predictButton.setText("CERE ANALIZA AI");
                    predictButton.setEnabled(true);
                }
            };
            worker.execute();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Date invalide. Verificați câmpurile.");
        }
    }

    private void formatAndDisplayResponse(String fullResponse) {
        String finalPrice = "Indisponibil";
        String finalAnalysis = fullResponse;

        if (fullResponse.contains("ESTIMARE PREȚ:")) {
            String[] parts = fullResponse.split("ARGUMENTE NEGOCIERE:");

            if (parts.length >= 2) {
                // Extragem doar ce e înainte de prima paranteză '('
                String rawPriceLine = parts[0].replace("ESTIMARE PREȚ:", "").trim();
                if (rawPriceLine.contains("(")) {
                    finalPrice = rawPriceLine.substring(0, rawPriceLine.indexOf("(")).trim();
                } else {
                    finalPrice = rawPriceLine;
                }

                finalAnalysis = "ARGUMENTE NEGOCIERE:\n" + parts[1].trim();
            }
        }

        priceBoxLabel.setText(finalPrice);
        priceBoxLabel.setForeground(new Color(40, 116, 240));
        resultArea.setText(finalAnalysis);
    }

    private String fetchOllamaResponse(String jsonPayload) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return parseJsonResponse(response.body());

        } catch (Exception e) {
            return "Eroare conexiune cu Ollama. Asigură-te că Ollama rulează pe fundal.\nDetalii: " + e.getMessage();
        }
    }

    private String parseJsonResponse(String json) {
        try {
            int startIndex = json.indexOf("\"response\":\"") + 12;
            int endIndex = json.indexOf("\",\"done\"");
            if (startIndex > 11 && endIndex > startIndex) {
                String rawResponse = json.substring(startIndex, endIndex);
                return rawResponse.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
            }
        } catch (Exception ignored) {}
        return "Răspuns neașteptat de la AI.";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}