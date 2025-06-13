package JavaMongo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection; 
import org.bson.Document;
import java.util.Iterator; 
public class MongoConn {

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "Students_db";
    private MongoConn() {
    }

    private static synchronized void initialize() {
        if (mongoClient == null) {
            try {
                mongoClient = MongoClients.create(CONNECTION_STRING);
                database = mongoClient.getDatabase(DATABASE_NAME);
                System.out.println(" MongoDB connection established to database: " + DATABASE_NAME);
            } catch (Exception e) {
                System.err.println(" Failed to establish MongoDB connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public static MongoDatabase getDatabase() {
        if (database == null) {
            initialize(); 
        }
        if (database == null) {
            throw new IllegalStateException("MongoDB database connection could not be established.");
        }
        return database;
    }
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null; 
            System.out.println(" MongoDB connection closed.");
        }
    }

    public static void main(String[] args) {
        MongoDatabase db = MongoConn.getDatabase();
        if (db != null) {
            System.out.println("Database name from getDatabase(): " + db.getName());
            MongoCollection<Document> studentsCollection = db.getCollection("students");
            System.out.println(" Documents in 'students' collection:");
            Iterator<Document> iterator = studentsCollection.find().iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next().toJson());
            }
        } else {
            System.out.println("Could not retrieve database instance.");
        }
        MongoConn.closeConnection();
    }
}