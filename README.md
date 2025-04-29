# 🌍 Countries & Cities Client

A simple Java desktop application (Swing) that connects to a public REST API to retrieve and display information about countries and their cities. The application features an intuitive GUI that allows users to search for data, visualize population growth, and fetch various details such as flags, currencies, and international dialing codes.

## 🔧 Features

- Fetch a list of countries from a REST API
- Retrieve cities based on the selected country
- Display the Czech Republic flag using Apache Batik
- Show international dialing codes
- Display currency information
- Compare population growth of the Czech Republic and its neighboring countries
- Compare population growth among Czech cities
- Visualize results in a clear and user-friendly GUI

## 🧱 Built With

- **Java 8**
- **Maven** – for project build and dependency management
- **OkHttp** – for HTTP communication
- **Gson** – for parsing JSON responses
- **Apache Batik** – for rendering SVG flags
- **JUnit** – for unit testing

## 🚀 How to Run

### 1. Clone the Repository

```bash
git clone https://github.com/Lucky01dot/CountriesCitiesApi.git
cd countries-cities-client

mvn clean package
java -jar target/countries-cities-client.jar
