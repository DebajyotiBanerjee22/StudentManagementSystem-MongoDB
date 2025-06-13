package JavaMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.*; 

public class StudentSubjectMongoDAO {
    private final MongoCollection<Document> collection;

    public StudentSubjectMongoDAO() {
        this.collection = MongoConn.getDatabase().getCollection("student_subjects");
    }

    public boolean addStudentSubject(StudentSubject ss) {
        // Optional: Check if the association already exists to prevent duplicates.
        // if (isStudentSubjectExists(ss.getRollNo(), ss.getSubjectId())) {
        //     System.out.println(" Association for Roll No " + ss.getRollNo() + " and Subject ID " + ss.getSubjectId() + " already exists.");
        //     return false;
        // }

        Document doc = new Document("rollNo", ss.getRollNo())
                .append("subjectId", ss.getSubjectId());
        try {
            InsertOneResult result = collection.insertOne(doc);
            if (result.getInsertedId() != null) {
                System.out.println(" Assigned Subject ID " + ss.getSubjectId() + " to Student Roll No: " + ss.getRollNo());
                return true;
            }
            return false; 
        } catch (Exception e) {
            System.err.println(" Error assigning Subject ID " + ss.getSubjectId() + " to Student Roll No " + ss.getRollNo() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<StudentSubject> getSubjectsByRollNo(String rollNo) {
        List<StudentSubject> list = new ArrayList<>();
        for (Document doc : collection.find(eq("rollNo", rollNo))) {
            list.add(new StudentSubject(doc.getString("rollNo"), doc.getInteger("subjectId")));
        }
        System.out.println(" Retrieved " + list.size() + " subjects for student " + rollNo);
        return list;
    }

    public List<StudentSubject> getAllStudentSubjects() {
        List<StudentSubject> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(new StudentSubject(doc.getString("rollNo"), doc.getInteger("subjectId")));
        }
        System.out.println(" Retrieved " + list.size() + " student-subject associations.");
        return list;
    }

    public boolean deleteStudentSubject(String rollNo, int subjectId) {
        long deletedCount = collection.deleteOne(and(eq("rollNo", rollNo), eq("subjectId", subjectId))).getDeletedCount();
        if (deletedCount > 0) {
            System.out.println(" Deleted association for student " + rollNo + " and subject " + subjectId + ".");
            return true;
        } else {
            System.out.println(" No association found for student " + rollNo + " and subject " + subjectId + " to delete.");
            return false;
        }
    }

    public boolean deleteSubjectsByRollNo(String rollNo) {
        long deletedCount = collection.deleteMany(eq("rollNo", rollNo)).getDeletedCount();
        if (deletedCount > 0) {
            System.out.println(" Deleted " + deletedCount + " subjects for student " + rollNo + ".");
            return true;
        } else {
            System.out.println(" No subjects found for student " + rollNo + " to delete.");
            return false;
        }
    }

    public boolean isStudentSubjectExists(String rollNo, int subjectId) {
        boolean exists = collection.countDocuments(and(eq("rollNo", rollNo), eq("subjectId", subjectId))) > 0;
        if (exists) {
            System.out.println(" Association exists for student " + rollNo + " and subject " + subjectId + ".");
        } else {
            System.out.println(" Association does NOT exist for student " + rollNo + " and subject " + subjectId + ".");
        }
        return exists;
    }
}