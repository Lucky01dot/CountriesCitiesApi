package cz.client;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.google.gson.JsonObject;

public class MainGUI {

    private static CountriesCitiesApiClient apiClient;

    public MainGUI() {
        apiClient = new CountriesCitiesApiClient();
        createAndShowGUI();
    }

    @SuppressWarnings("Convert2Lambda")
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Countries & Cities API Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton btnPopulation = new JButton("CZ population");
        JButton btnCities = new JButton("CZ cities");
        JButton btn3Cities = new JButton("Ascending 3 CZ cities");
        JButton btnflag = new JButton("CZ flag");
        JButton btnCur = new JButton("Countries and currency");
        JButton btnCodes = new JButton("Countries, currency and dial codes");
        
        JTextArea outputArea = new JTextArea(15, 50);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        
        panel.add(btnPopulation);
        panel.add(btnCities);
        panel.add(btn3Cities);
        panel.add(btnflag);
        panel.add(btnCur);
        panel.add(btnCodes);
        
        panel.add(scrollPane);
        
        frame.add(panel);
        
        // Akce pro tlačítka
        btnPopulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JsonObject population = apiClient.getPopulationOfCzechRepublic();
                    outputArea.setText("Population of Czech Republic:\n" + population.toString());
                } catch (IOException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        btnCities.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JsonObject cities = apiClient.getAllCitiesOfCzechRepublic();
                    outputArea.setText("Cities of Czech Republic:\n" + cities.toString());
                } catch (IOException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });
        btn3Cities.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    JsonObject cities = apiClient.get3CitiesofCzechRepublic();
                    outputArea.setText("3 cities ascending of Czech Republic:\n" + cities.toString());
                } catch (IOException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        btnflag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    JsonObject flag = apiClient.getFlagOfCzechRepublic();
                    outputArea.setText("Flag of Czech Republic:\n" + flag.toString());
                } catch (IOException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });
        btnCur.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    JsonObject curr = apiClient.getAllCountriesAndCurrencies();
                    outputArea.setText("Countries and their currency:\n" + curr.toString());
                } catch (IOException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        btnCodes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    JsonObject codes = apiClient.getAllCountriesCurrenciesAndDialCodes();
                    outputArea.setText("Countries and their currency:\n" + codes.toString());
                } catch (IOException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });
        

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        MainGUI mainGUI = new MainGUI();
    }
}
