package gestion_usuarios;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class MongoDBTestHelper {
    
    private static MongoServer server;
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    
    @BeforeAll
    static void setupMongoDB() {
        // Iniciar servidor MongoDB en memoria
        server = new MongoServer(new MemoryBackend());
        InetSocketAddress serverAddress = server.bind();
        
        // Conectar cliente
        String connectionString = String.format("mongodb://%s:%d",
            serverAddress.getHostName(), serverAddress.getPort());
        mongoClient = MongoClients.create(connectionString);
        
        // Obtener base de datos
        database = mongoClient.getDatabase("test_hospital_db");
        
        // Insertar datos de prueba
        insertTestData();
        
        System.out.println("✅ MongoDB en memoria iniciado para pruebas");
    }
    
    private static void insertTestData() {
        database.getCollection("usuarios").insertMany(Arrays.asList(
            new Document("nombre", "Dr. Test Pérez")
                .append("email", "test.doctor@hospital.com")
                .append("telefono", "+34 600 111 111")
                .append("rol", "Doctor")
                .append("estado", "🟢 Activio")
                .append("ultimo_acceso", "01/01/2026"),
                
            new Document("nombre", "Paciente Test")
                .append("email", "test.paciente@hospital.com")
                .append("telefono", "+34 600 222 222")
                .append("rol", "Paciente")
                .append("estado", "🟢 Activio")
                .append("ultimo_acceso", "02/01/2026"),
                
            new Document("nombre", "Admin Test")
                .append("email", "test.admin@hospital.com")
                .append("telefono", "+34 600 333 333")
                .append("rol", "Administrador")
                .append("estado", "🔴 Inactivio")
                .append("ultimo_acceso", "03/01/2026")
        ));
    }
    
    public static MongoDatabase getDatabase() {
        return database;
    }
    
    public static MongoClient getMongoClient() {
        return mongoClient;
    }
    
    @AfterAll
    static void tearDown() {
        if (mongoClient != null) {
            mongoClient.close();
        }
        if (server != null) {
            server.shutdown();
        }
        System.out.println("✅ MongoDB en memoria cerrado");
    }
}
