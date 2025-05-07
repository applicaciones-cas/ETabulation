package ui.etabulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import ui.etabulation.Crypter;


public class Conn {

    private String host;
    private String username;
    private String password;
    private String port;
    private String test;

    public Conn(String iniFilePath) {
        parseIniFile(iniFilePath);
    }
    
    private void parseIniFile(String iniFilePath) {
        Map<String, String> iniData = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(iniFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim().toLowerCase();
                    String value = parts[1].trim();
                    iniData.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading INI file: " + e.getMessage());
        }
        host = iniData.get("id");        
        username = Crypter.decrypt(iniData.get("username"), "rasengan");
        password = Crypter.decrypt(iniData.get("password"), "rasengan");
        port = iniData.get("port");
    }

    public Connection getConnection(String databaseName) throws SQLException {


        String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
        return DriverManager.getConnection(url, username, password);
    }
}
