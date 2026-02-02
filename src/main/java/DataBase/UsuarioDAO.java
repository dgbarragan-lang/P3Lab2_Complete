package DataBase;

import Model.Usuario;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UsuarioDAO {
    
	private MongoCollection<Document> collection;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // Constructor para testing (inyección de dependencias)
    public UsuarioDAO(MongoCollection<Document> collection) {
        this.collection = collection;
    }
    
    // Constructor normal
    public UsuarioDAO() {
        this.collection = MongoDBConnection.getCollection();
    }
    
    // Obtener todos los usuarios
    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                usuarios.add(documentToUsuario(cursor.next()));
            }
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return usuarios;
    }
    
    // Buscar usuarios por nombre o email
    public List<Usuario> buscarUsuarios(String criterio) {
        List<Usuario> usuarios = new ArrayList<>();
        
        if (criterio == null || criterio.trim().isEmpty()) {
            return getAllUsuarios();
        }
        
        try {
            Pattern pattern = Pattern.compile(criterio, Pattern.CASE_INSENSITIVE);
            
            Bson filter = Filters.or(
                Filters.regex("nombre", pattern),
                Filters.regex("email", pattern)
            );
            
            try (MongoCursor<Document> cursor = collection.find(filter).iterator()) {
                while (cursor.hasNext()) {
                    usuarios.add(documentToUsuario(cursor.next()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al buscar usuarios: " + e.getMessage());
        }
        return usuarios;
    }
    
    // Obtener usuario por ID
    public Usuario getUsuarioById(ObjectId id) {
        try {
            Document doc = collection.find(Filters.eq("_id", id)).first();
            return doc != null ? documentToUsuario(doc) : null;
        } catch (Exception e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            return null;
        }
    }
    
    // Obtener usuario por email
    public Usuario getUsuarioByEmail(String email) {
        try {
            Document doc = collection.find(Filters.eq("email", email)).first();
            return doc != null ? documentToUsuario(doc) : null;
        } catch (Exception e) {
            System.err.println("Error al obtener usuario por email: " + e.getMessage());
            return null;
        }
    }
    
    // Agregar nuevo usuario
    public boolean agregarUsuario(Usuario usuario) {
        try {
            // Verificar si el email ya existe
            if (getUsuarioByEmail(usuario.getEmail()) != null) {
                System.err.println("❌ El email ya está registrado: " + usuario.getEmail());
                return false;
            }
            
            Document doc = usuarioToDocument(usuario);
            InsertOneResult result = collection.insertOne(doc);
            
            if (result.getInsertedId() != null) {
                usuario.setId(result.getInsertedId().asObjectId().getValue());
                System.out.println("✅ Usuario agregado: " + usuario.getNombre());
                return true;
            }
            return false;
            
        } catch (Exception e) {
            System.err.println("❌ Error al agregar usuario: " + e.getMessage());
            return false;
        }
    }
    
    // Actualizar usuario
    public boolean actualizarUsuario(Usuario usuario) {
        try {
            Bson filter = Filters.eq("_id", usuario.getId());
            
            Document updateDoc = new Document()
                .append("nombre", usuario.getNombre())
                .append("email", usuario.getEmail())
                .append("telefono", usuario.getTelefono())
                .append("rol", usuario.getRol())
                .append("estado", usuario.getEstado())
                .append("ultimo_acceso", LocalDate.now().format(FORMATTER));
            
            UpdateResult result = collection.updateOne(filter, 
                new Document("$set", updateDoc));
            
            boolean actualizado = result.getModifiedCount() > 0;
            if (actualizado) {
                System.out.println("✅ Usuario actualizado: " + usuario.getNombre());
            }
            return actualizado;
            
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
    
    // Eliminar usuario
    public boolean eliminarUsuario(ObjectId id) {
        try {
            DeleteResult result = collection.deleteOne(Filters.eq("_id", id));
            boolean eliminado = result.getDeletedCount() > 0;
            if (eliminado) {
                System.out.println("✅ Usuario eliminado: " + id);
            }
            return eliminado;
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    
    // Estadísticas
    public int getTotalUsuarios() {
        try {
            return (int) collection.countDocuments();
        } catch (Exception e) {
            System.err.println("Error al contar usuarios: " + e.getMessage());
            return 0;
        }
    }
    
    public int getUsuariosActivos() {
        try {
            return (int) collection.countDocuments(Filters.eq("estado", "🟢 Activio"));
        } catch (Exception e) {
            System.err.println("Error al contar usuarios activos: " + e.getMessage());
            return 0;
        }
    }
    
    public int getTotalDoctores() {
        try {
            return (int) collection.countDocuments(Filters.eq("rol", "Doctor"));
        } catch (Exception e) {
            System.err.println("Error al contar doctores: " + e.getMessage());
            return 0;
        }
    }
    
    public int getTotalPacientes() {
        try {
            return (int) collection.countDocuments(Filters.eq("rol", "Paciente"));
        } catch (Exception e) {
            System.err.println("Error al contar pacientes: " + e.getMessage());
            return 0;
        }
    }
    
    // Filtrar por rol
    public List<Usuario> getUsuariosPorRol(String rol) {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            try (MongoCursor<Document> cursor = 
                    collection.find(Filters.eq("rol", rol)).iterator()) {
                while (cursor.hasNext()) {
                    usuarios.add(documentToUsuario(cursor.next()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al filtrar por rol: " + e.getMessage());
        }
        return usuarios;
    }
    
    // Métodos de conversión
    private Document usuarioToDocument(Usuario usuario) {
        return new Document()
            .append("nombre", usuario.getNombre())
            .append("email", usuario.getEmail())
            .append("telefono", usuario.getTelefono())
            .append("rol", usuario.getRol())
            .append("estado", usuario.getEstado())
            .append("ultimo_acceso", usuario.getUltimoAcceso());
    }
    
    private Usuario documentToUsuario(Document doc) {
        Usuario usuario = new Usuario();
        usuario.setId(doc.getObjectId("_id"));
        usuario.setNombre(doc.getString("nombre"));
        usuario.setEmail(doc.getString("email"));
        usuario.setTelefono(doc.getString("telefono"));
        usuario.setRol(doc.getString("rol"));
        usuario.setEstado(doc.getString("estado"));
        usuario.setUltimoAcceso(doc.getString("ultimo_acceso"));
        return usuario;
    }
}