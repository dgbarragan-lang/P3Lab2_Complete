package DataBase;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.MongoException;
import org.bson.Document;
import java.util.Arrays;

public class MongoDBConnection {
    
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    
    public static MongoCollection<Document> getCollection() {
        if (collection == null) {
            try {
                String connectionString = Config.getMongoURI();
                System.out.println("Conectando a MongoDB: " + connectionString);
                
                mongoClient = MongoClients.create(connectionString);
                database = mongoClient.getDatabase(Config.getDatabaseName());
                collection = database.getCollection(Config.getCollectionName());
                
                System.out.println("Conexión exitosa a MongoDB");
                
                // Crear índices
                crearIndices();
                
                // Insertar datos iniciales si está vacío
                if (collection.countDocuments() == 0) {
                    insertarDatosIniciales();
                }
                
            } catch (MongoException e) {
                System.err.println("Error al conectar con MongoDB: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
        return collection;
    }
    
    private static void crearIndices() {
        try {
            // Índice único para email
            collection.createIndex(new Document("email", 1));
            
        } catch (Exception e) {
            System.err.println("Error al crear índices: " + e.getMessage());
        }
    }
    
    private static void insertarDatosIniciales() {
        try {
            Document[] usuarios = {
                new Document("nombre", "Dr. Juan Pérez")
                    .append("email", "juan.perez@hospital.com")
                    .append("telefono", "+34 600 123 456")
                    .append("rol", "Doctor")
                    .append("estado", "🟢 Activio")
                    .append("ultimo_acceso", "17/01/2026"),
                    
                new Document("nombre", "Dra. María González")
                    .append("email", "maria.gonzalez@hospital.com")
                    .append("telefono", "+34 600 234 567")
                    .append("rol", "Doctor")
                    .append("estado", "🟢 Activio")
                    .append("ultimo_acceso", "16/01/2026"),
                    
                new Document("nombre", "Carlos Ramírez")
                    .append("email", "carlos.ramirez@paciente.com")
                    .append("telefono", "+34 600 345 678")
                    .append("rol", "Paciente")
                    .append("estado", "🟢 Activio")
                    .append("ultimo_acceso", "15/01/2026"),
                    
                new Document("nombre", "Ana López")
                    .append("email", "ana.lopez@hospital.com")
                    .append("telefono", "+34 600 456 789")
                    .append("rol", "Personal")
                    .append("estado", "🟢 Activio")
                    .append("ultimo_acceso", "17/01/2026"),
                    
                new Document("nombre", "Admin Sistema")
                    .append("email", "admin@hospital.com")
                    .append("telefono", "+34 600 567 890")
                    .append("rol", "Administrador")
                    .append("estado", "🟢 Activio")
                    .append("ultimo_acceso", "17/01/2026"),
                    
                new Document("nombre", "Roberto Fernández")
                    .append("email", "roberto.fernandez@paciente.com")
                    .append("telefono", "+34 600 678 901")
                    .append("rol", "Paciente")
                    .append("estado", "🔴 Inactivio")
                    .append("ultimo_acceso", "19/11/2025")
            };
            
            collection.insertMany(Arrays.asList(usuarios));
            System.out.println("✅ Datos iniciales insertados correctamente.");
            
        } catch (Exception e) {
            System.err.println("Error al insertar datos iniciales: " + e.getMessage());
        }
    }
    
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Conexión a MongoDB cerrada.");
        }
    }
}
