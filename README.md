# Timișoara Real Estate AI Price Estimator

A Java-based desktop application that leverages local AI (via Ollama) to provide data-driven real estate price estimations and negotiation strategies for properties in Timișoara and the surrounding metropolitan area.

## Features

- Local AI Inference: Uses the Qwen2.5:3b model running locally via Ollama, ensuring data privacy and offline capability.
- Data-Driven Analysis: Trained on a custom dataset of 130+ real-world real estate listings covering Timișoara and metropolitan areas like Dumbrăvița, Giroc, and Moșnița.
- Intuitive GUI: Modern, minimalist interface built with Java Swing.
- Smart Parsing: Automatically separates price estimations from textual negotiation advice using custom regex parsers.

## Requirements

To run this application, you must have the following installed:

1. Java Development Kit (JDK) 11 or higher.
2. Ollama: Download and install from https://ollama.com/.
3. Custom Model Setup:
   - Place the provided Modelfile in your project root.
   - Open your terminal in the project directory and run:
     ```bash
     ollama create timisoara-ai -f Modelfile
     ```

## Setup and Execution

1. Ensure the Ollama service is running in the background.
2. Build the project using Maven or IntelliJ IDEA.
3. Run the `com.autominutes.Main` class.
4. Input the property details in the GUI.
5. Click "CERE ANALIZA AI" to receive the price estimation and negotiation strategies.

## Project Structure

- `src/main/java/com/autominutes/Main.java`: The core application logic, GUI components, and the Ollama HTTP client implementation.
- `Modelfile`: Contains the system instructions, inference rules, and the training dataset (CSV formatted).
