package cz.client;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * MainGUI is the graphical user interface (GUI) class for interacting with 
 * the CountriesCitiesApiClient to fetch and display data related to countries, cities, 
 * flags, populations, and currencies.
 */
public class MainGUI {

    private static CountriesCitiesApiClient apiClient;

    /**
     * Constructs the MainGUI and initializes the API client.
     */
    public MainGUI() {
        apiClient = new CountriesCitiesApiClient();
        createAndShowGUI();
    }
    /**
     * Creates and displays the graphical user interface (GUI) with various actions.
     * The user can select different actions from a JComboBox to get information about 
     * countries, cities, populations, flags, and currencies.
     */
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Countries & Cities API Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        String[] actions = {
            "Population of Czech Republic",
            "All Czech Cities",
            "Top 3 Cities Ascending",
            "Czech Flag",
            "Countries and Currency",
            "Countries, Currency and Dial Codes",
            "Compare CZ City Populations",
            "Compare CZ & Neighbor Populations"
        };

        JComboBox<String> comboBox = new JComboBox<>(actions);
        JButton runButton = new JButton("View");
        JTextArea outputArea = new JTextArea(20, 60);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel topPanel = new JPanel();
        topPanel.add(comboBox);
        topPanel.add(runButton);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // ActionListener to handle button click events

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) comboBox.getSelectedItem();
                try {
                    switch (selected) {
                        case "Population of Czech Republic":
                            JsonObject pop = apiClient.getPopulationOfCzechRepublic();
                            displayPopulationData(pop, outputArea);
                            break;
                        case "All Czech Cities":
                            JsonObject allCities = apiClient.getAllCitiesOfCzechRepublic();
                            displayAllCzechCities(allCities, outputArea);
                            break;
                        case "Top 3 Cities Ascending":
                            JsonObject threeCities = apiClient.get3CitiesofCzechRepublic();
                            displayFormattedCityList(threeCities, outputArea);
                            break;                        
                        case "Czech Flag":
                            JsonObject flag = apiClient.getFlagOfCzechRepublic();
                            displayFlag(flag, outputArea);
                            break;
                        case "Countries and Currency":
                            JsonObject curr = apiClient.getAllCountriesAndCurrencies();
                            JsonArray currArray  = curr.getAsJsonArray("currency");
                            displayFormattedCountriesAndCurrencies(currArray, outputArea);
                            break;
                        case "Countries, Currency and Dial Codes":
                            JsonObject codes = apiClient.getAllCountriesCurrenciesAndDialCodes();
                            JsonArray codesArray  = codes.getAsJsonArray("codes");
                            displayFormattedCountryCurrencyDialCodes(codesArray, outputArea);
                            break;
                        case "Compare CZ City Populations":
                            JsonObject cities = apiClient.getCityPopulations();
                            JsonArray cityArray = cities.getAsJsonArray("cities");
                            displayFormattedComparison(cityArray, outputArea);
                            break;
                        case "Compare CZ & Neighbor Populations":
                            JsonObject comp = apiClient.comparePopulationsAndGrowths();
                            JsonArray comparison = comp.getAsJsonArray("country");
                            displayFormattedComparison(comparison, outputArea);
                            break;
                    }
                } catch (IOException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }
    /**
     * Displays the population data of the Czech Republic in the output area.
     * 
     * @param json The JSON object containing population data of the Czech Republic.
     * @param outputArea The JTextArea where the data will be displayed.
     */
    private void displayPopulationData(JsonObject json, JTextArea outputArea) {
        JsonObject data = json.getAsJsonObject("data");
        JsonArray populationCounts = data.getAsJsonArray("populationCounts");
        StringBuilder sb = new StringBuilder();
    
        outputArea.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 13));
        
        sb.append(String.format("Population of %s (%s):\n", 
            data.get("country").getAsString(), 
            data.get("code").getAsString()));
        sb.append("------------------------------------\n");
        sb.append(String.format("%-6s %-15s\n", "Year", "Population"));
        sb.append("------------------------------------\n");
    
        for (int i = 0; i < populationCounts.size(); i++) {
            JsonObject record = populationCounts.get(i).getAsJsonObject();
            int year = record.get("year").getAsInt();
            long population = record.get("value").getAsLong();
            sb.append(String.format("%-6d %-15d\n", year, population));
        }
    
        outputArea.setText(sb.toString());
    }
    
    /**
     * Displays a list of all cities in the Czech Republic in the output area.
     * 
     * @param json The JSON object containing all the Czech cities.
     * @param outputArea The JTextArea where the city list will be displayed.
     */
    private void displayAllCzechCities(JsonObject json, JTextArea outputArea) {
        JsonArray cities = json.getAsJsonArray("data");
        StringBuilder sb = new StringBuilder();
    
        outputArea.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 13));
        
        sb.append(String.format("%-5s %-30s\n", "No.", "City"));
        sb.append("--------------------------------------\n");
    
        for (int i = 0; i < cities.size(); i++) {
            String city = cities.get(i).getAsString();
            sb.append(String.format("%-5d %-30s\n", i + 1, city));
        }
    
        outputArea.setText(sb.toString());
    }

    /**
     * Displays the top 3 cities in ascending order based on population in the output area.
     * 
     * @param json The JSON object containing the top 3 cities.
     * @param outputArea The JTextArea where the list will be displayed.
     */
    private void displayFormattedCityList(JsonObject json, JTextArea outputArea) {
        JsonArray cities = json.getAsJsonArray("cities");
        StringBuilder sb = new StringBuilder();
    
        outputArea.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 14));
        
        sb.append(String.format("%-5s %-30s\n", "No.", "City"));
        sb.append("--------------------------------------\n");
    
        for (int i = 0; i < cities.size(); i++) {
            String city = cities.get(i).getAsString();
            sb.append(String.format("%-5d %-30s\n", i + 1, city));
        }
    
        outputArea.setText(sb.toString());
    }
    
    /**
     * Displays the flag of the Czech Republic in a new window.
     * 
     * @param flag The JSON object containing the flag URL of the Czech Republic.
     * @param outputArea The JTextArea where the message about displaying the flag will be shown.
     */
    private void displayFlag(JsonObject flag, JTextArea outputArea) {
        try {
            String flagUrl = flag.get("flagUri").getAsString();
            InputStream inputStream = new URL(flagUrl).openStream();
            TranscoderInput input = new TranscoderInput(inputStream);
    
            final BufferedImage[] image = new BufferedImage[1];
    
            ImageTranscoder t = new ImageTranscoder() {
                @Override
                public BufferedImage createImage(int w, int h) {
                    return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                }
    
                @Override
                public void writeImage(BufferedImage img, TranscoderOutput out) {
                    image[0] = img;
                }
            };
    
            t.transcode(input, null);
    
            if (image[0] != null) {
                JLabel flagLabel = new JLabel(new ImageIcon(image[0]));
                JFrame flagFrame = new JFrame("Flag of Czech Republic");
                flagFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                flagFrame.add(flagLabel, BorderLayout.CENTER);
                flagFrame.pack();
                flagFrame.setVisible(true);
                outputArea.setText("Flag of Czech Republic shown in the new window.");
            } else {
                outputArea.setText("Error: Could not render the flag image.");
            }
    
        } catch (HeadlessException | IOException | TranscoderException e) {
            outputArea.setText("Error rendering SVG flag: " + e.getMessage());
        }
    }

    /**
     * Displays a formatted comparison of population data for cities or countries.
     * 
     * @param comparison The JSON array containing population comparison data.
     * @param outputArea The JTextArea where the formatted comparison will be displayed.
     */
    private void displayFormattedComparison(JsonArray array, JTextArea outputArea) {
        StringBuilder sb = new StringBuilder();
    
        
        outputArea.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 14));
    
        
        boolean isCountry = array.get(0).getAsJsonObject().has("country");
    

        if (isCountry) {
            sb.append(String.format("%-25s %-8s %-20s %-15s\n", "Country", "Year", "Population", "Growth"));
            sb.append("----------------------------------------------------------------------\n");
        } else {
            sb.append(String.format("%-25s %-8s %-20s %-15s\n", "City", "Year", "Population", "Growth"));
            sb.append("----------------------------------------------------------------------\n");
        }
    
        for (JsonElement elem : array) {
            JsonObject obj = elem.getAsJsonObject();
            String name = obj.has("country") ? obj.get("country").getAsString() : obj.get("city").getAsString();
            String year = obj.get("latest_year").getAsString();
            String population = String.format("%,d", obj.get("population").getAsLong());
            long growthVal = obj.get("growth").getAsLong();
            String growth = (growthVal >= 0 ? "+" : "") + String.format("%,d", growthVal);
    
            sb.append(String.format("%-25s %-8s %-20s %-15s\n", name, year, population, growth));
        }
    
        outputArea.setText(sb.toString());
    }

    /**
     * Displays a formatted list of countries, currencies, and their respective dial codes.
     * 
     * @param codes The JSON array containing country, currency, and dial code data.
     * @param outputArea The JTextArea where the formatted list will be displayed.
     */
    private void displayFormattedCountryCurrencyDialCodes(JsonArray array, JTextArea outputArea) {
        StringBuilder sb = new StringBuilder();
    
        // Nastav monospaced font
        outputArea.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 14));
    
        // Hlavička tabulky
        sb.append(String.format("%-35s %-15s %-15s\n", "Country", "Currency", "Dial Code"));
        sb.append("---------------------------------------------------------------\n");
    
        for (JsonElement elem : array) {
            JsonObject obj = elem.getAsJsonObject();
            String country = obj.get("country").getAsString();
            String currency = obj.get("currency").getAsString();
            String dialCode = obj.get("dial_code").getAsString();
    
            sb.append(String.format("%-35s %-15s %-15s\n", country, currency, dialCode));
        }
    
        outputArea.setText(sb.toString());
    }

    /**
     * Displays a formatted list of countries and their currencies.
     * 
     * @param currencies The JSON array containing country and currency data.
     * @param outputArea The JTextArea where the formatted countries and currencies will be displayed.
     */
    private void displayFormattedCountriesAndCurrencies(JsonArray array, JTextArea outputArea) {
        StringBuilder sb = new StringBuilder();
    
        // Nastav monospaced font pro lepší zarovnání
        outputArea.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 14));
    
        // Hlavička tabulky
        sb.append(String.format("%-40s %-15s\n", "Country", "Currency"));
        sb.append("---------------------------------------------------------------\n");
    
        for (JsonElement elem : array) {
            JsonObject obj = elem.getAsJsonObject();
            String country = obj.get("country").getAsString();
            String currency = obj.get("currency").getAsString();
    
            sb.append(String.format("%-40s %-15s\n", country, currency));
        }
    
        outputArea.setText(sb.toString());
    }
    
    
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI());
    }
}
