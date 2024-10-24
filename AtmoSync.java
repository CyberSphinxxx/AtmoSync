import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;                
import javax.swing.*;               

public class AtmoSync {

    private static final String API_KEY = "4525a9ec3053fd7b173e9195f33778f1"; 
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s"; 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("AtmoSync");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            JTextField cityInput     = new JTextField(15);
            JButton getWeatherButton = new JButton("Get Weather");
            JLabel weatherLabel      = new JLabel("Weather: ");
            JLabel tempLabel         = new JLabel("Temperature: ");
            JLabel humidityLabel     = new JLabel("Humidity: ");

            JPanel panel = new JPanel();
            panel.add(new JLabel("Enter city:"));
            panel.add(cityInput);
            panel.add(getWeatherButton);
            panel.add(weatherLabel);
            panel.add(tempLabel);
            panel.add(humidityLabel);

            getWeatherButton.addActionListener(e -> {
                String city = cityInput.getText().trim();  
                if (!city.isEmpty()) {                      
                    try {
                        String weatherData = getWeather(city);
                        weatherLabel.setText("Weather: " + extractData(weatherData, "\"description\":\"", "\""));
                        double tempCelsius = Double.parseDouble(extractData(weatherData, "\"temp\":", ",")) - 273.15; 
                        tempLabel.setText(String.format("Temperature: %.2f °C", tempCelsius));
                        humidityLabel.setText("Humidity: " + extractData(weatherData, "\"humidity\":", ",") + "%");
                    } 
                    catch (Exception ex) { 
                        JOptionPane.showMessageDialog(frame, "Error fetching weather data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } 
                else { 
                    JOptionPane.showMessageDialog(frame, "Please enter a city name.", "Input Error", JOptionPane.WARNING_MESSAGE);
                }
            });

            frame.add(panel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }

    private static String getWeather(String city) throws Exception {
        String urlString = String.format(API_URL, city, API_KEY); 
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection(); 
        conn.setRequestMethod("GET"); 

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { 
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {   
                    response.append(inputLine);                 
                }
                return response.toString();                     
            }
        } 
        else { 
            throw new Exception("Response code: " + conn.getResponseCode());
        }
    }

    private static String extractData(String json, String keyStart, String keyEnd) {
        int startIndex = json.indexOf(keyStart) + keyStart.length();    
        int endIndex   = json.indexOf(keyEnd, startIndex);                
        return json.substring(startIndex, endIndex);                    
    }
}
