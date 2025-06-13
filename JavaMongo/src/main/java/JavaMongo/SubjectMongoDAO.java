package JavaMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class SubjectMongoDAO {
    private final MongoCollection<Document> collection;
    public SubjectMongoDAO() {
        this.collection = MongoConn.getDatabase().getCollection("subjects");
    }
    public boolean addSubject(Subject subject) {
        Document doc = new Document("subjectId", subject.getSubjectId())
                .append("subjectName", subject.getSubjectName());
        try {
            InsertOneResult result = collection.insertOne(doc);
            if (result.getInsertedId() != null) {
                System.out.println(" Subject added: " + subject.getSubjectName() + " (ID: " + subject.getSubjectId() + ")");
                return true;
            }
            return false; 
        } catch (Exception e) {
            System.err.println(" Error adding subject " + subject.getSubjectName() + " (ID: " + subject.getSubjectId() + "): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Subject getSubjectById(int subjectId) {
        Document doc = collection.find(Filters.eq("subjectId", subjectId)).first();
        if (doc != null) {
            System.out.println(" Subject found for ID: " + subjectId);
            return new Subject(doc.getInteger("subjectId"), doc.getString("subjectName"));
        }
        System.out.println(" No subject found for ID: " + subjectId);
        return null;
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        for (Document doc : collection.find()) {
            subjects.add(new Subject(doc.getInteger("subjectId"), doc.getString("subjectName")));
        }
        System.out.println(" Retrieved " + subjects.size() + " subjects.");
        return subjects;
    }

    public boolean updateSubject(Subject subject) {
        Document updateFields = new Document("subjectName", subject.getSubjectName());
        Document updateOperation = new Document("$set", updateFields);

        long modifiedCount = collection.updateOne(Filters.eq("subjectId", subject.getSubjectId()), updateOperation).getModifiedCount();
        if (modifiedCount > 0) {
            System.out.println(" Subject with ID " + subject.getSubjectId() + " updated.");
            return true;
        } else {
            System.out.println(" No subject found with ID " + subject.getSubjectId() + " to update.");
            return false;
        }
    }

    public boolean deleteSubject(int subjectId) {
        long deletedCount = collection.deleteOne(Filters.eq("subjectId", subjectId)).getDeletedCount();
        if (deletedCount > 0) {
            System.out.println(" Subject with ID " + subjectId + " deleted.");
            return true;
        } else {
            System.out.println(" No subject found with ID " + subjectId + " to delete.");
            return false;
        }
    }

    public boolean isSubjectExists(int subjectId) {
        boolean exists = collection.countDocuments(Filters.eq("subjectId", subjectId)) > 0;
        if (exists) {
            System.out.println(" Subject exists for ID: " + subjectId);
        } else {
            System.out.println(" Subject does NOT exist for ID: " + subjectId);
        }
        return exists;
    }
}