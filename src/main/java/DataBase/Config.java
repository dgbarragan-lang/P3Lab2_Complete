package DataBase;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Properties properties;
    
    static {
        properties = new Properties();
        try {
            InputStream input = Config.class.getClassLoader()
                .getResourceAsStream("config.properties");
            if (input != null) {
                properties.load(input);
            } else {
                // Valores por defecto
                properties.setProperty("mongodb.uri", "mongodb://localhost:27017");
                properties.setProperty("mongodb.database", "hospital_db");
                properties.setProperty("mongodb.collection", "usuarios");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
        }
    }
    
    public static String getMongoURI() {
        return properties.getProperty("mongodb.uri");
    }
    
    public static String getDatabaseName() {
        return properties.getProperty("mongodb.database");
    }
    
    public static String getCollectionName() {
        return properties.getProperty("mongodb.collection");
    }
}