import java.awt.*;                  // Importing AWT classes for GUI components
import java.io.BufferedReader;      // Importing BufferedReader for reading input streams
import java.io.InputStreamReader;   // Importing InputStreamReader for converting byte streams to character streams
import java.net.HttpURLConnection;  // Importing HttpURLConnection for making HTTP requests
import java.net.URL;                // Importing URL for creating URLs for the API
import javax.swing.*;               // Importing Swing classes for creating a GUI

public class AtmoSync {

    private static final String API_KEY = "4525a9ec3053fd7b173e9195f33778f1"; // CyberSphinx's Free API key, Don't worry about my API key leaking
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s"; // Base URL for API

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the main application window (JFrame)
            JFrame frame = new JFrame("AtmoSync");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            // Create input field for city name and labels for weather information
            JTextField cityInput = new JTextField(15);
            JButton getWeatherButton = new JButton("Get Weather");
            JLabel weatherLabel = new JLabel("Weather: ");
            JLabel tempLabel = new JLabel("Temperature: ");
            JLabel humidityLabel = new JLabel("Humidity: ");

            // Panel to hold the components
            JPanel panel = new JPanel();
            panel.add(new JLabel("Enter city:"));
            panel.add(cityInput);
            panel.add(getWeatherButton);
            panel.add(weatherLabel);
            panel.add(tempLabel);
            panel.add(humidityLabel);

            // Action listener for the weather button
            getWeatherButton.addActionListener(e -> {
                String city = cityInput.getText().trim();   // Trim input to remove excess whitespace
                if (!city.isEmpty()) {                      // Check if the input is not empty
                    try {
                        // Fetch weather data for the specified city
                        String weatherData = getWeather(city);
                        // Update labels with extracted weather data
                        weatherLabel.setText("Weather: " + extractData(weatherData, "\"description\":\"", "\""));
                        double tempCelsius = Double.parseDouble(extractData(weatherData, "\"temp\":", ",")) - 273.15; // Convert temperature from Kelvin to Celsius
                        tempLabel.setText(String.format("Temperature: %.2f °C", tempCelsius));
                        humidityLabel.setText("Humidity: " + extractData(weatherData, "\"humidity\":", ",") + "%");
                    } 
                    catch (Exception ex) { // Handle exceptions during data fetching
                        JOptionPane.showMessageDialog(frame, "Error fetching weather data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } 
                else { // Show warning if the city name is empty
                    JOptionPane.showMessageDialog(frame, "Please enter a city name.", "Input Error", JOptionPane.WARNING_MESSAGE);
                }
            });

            // Add the panel to the frame and make it visible
            frame.add(panel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }

    // Method to fetch weather data from the OpenWeatherMap API
    private static String getWeather(String city) throws Exception {
        String urlString = String.format(API_URL, city, API_KEY); // Create the full API URL
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection(); // Open the connection to the API
        conn.setRequestMethod("GET"); // Set the request method to GET

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // Check if the response is OK
            // Read the response using a BufferedReader
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {   // Read each line of the response
                    response.append(inputLine);                 // Append line to the response
                }
                return response.toString();                     // Return the complete response
            }
        } 
        else { // Throw an exception for non-OK responses
            throw new Exception("Response code: " + conn.getResponseCode());
        }
    }

    // Method to extract specific data from the JSON response
    private static String extractData(String json, String keyStart, String keyEnd) {
        int startIndex = json.indexOf(keyStart) + keyStart.length();    // Find the starting index of the data
        int endIndex = json.indexOf(keyEnd, startIndex);                // Find the ending index of the data
        return json.substring(startIndex, endIndex);                    // Return the extracted data
    }
}
