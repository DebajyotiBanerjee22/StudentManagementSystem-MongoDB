package JavaMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class StudentMongoDAO {
    private final MongoCollection<Document> collection;
    private final ContactMongoDAO contactDAO;
    private final StudentSubjectMongoDAO studentSubjectDAO;

 StudentMongoDAO() {
        this.collection = MongoConn.getDatabase().getCollection("students");
        this.contactDAO = new ContactMongoDAO();
        this.studentSubjectDAO = new StudentSubjectMongoDAO();
    }

    public boolean addStudent(Student student) {
        Document doc = new Document("rollNo", student.getRollNo())
                .append("name", student.getName())
                .append("semester", student.getSemester())
                .append("yearOfAdmission", student.getYearOfAdmission())
                .append("expectedPassingYear", student.getExpectedPassingYear())
                .append("streamId", student.getStreamId());
        try {
            if (isStudentExists(student.getRollNo())) {
                System.out.println(" Student with Roll No: " + student.getRollNo() + " already exists. Not added.");
                return false;
            }

            InsertOneResult result = collection.insertOne(doc);
            if (result.getInsertedId() != null) {
                System.out.println(" Student added: " + student.getName() + " (Roll No: " + student.getRollNo() + ")");
                return true;
            } else {
                System.out.println(" Failed to add student: " + student.getName() + " (Roll No: " + student.getRollNo() + "). Insert ID was null.");
                return false;
            }
        } catch (Exception e) {
            System.err.println(" Error adding student " + student.getName() + " (Roll No: " + student.getRollNo() + "): " + e.getMessage());
            // e.printStackTrace();
            return false;
        }
    }

    public Student getStudentByRollNo(String rollNo) {
        Document doc = collection.find(Filters.eq("rollNo", rollNo)).first();
        if (doc != null) {
            System.out.println(" Student found for Roll No: " + rollNo);
            return new Student(
                    doc.getString("rollNo"),
                    doc.getString("name"),
                    doc.getInteger("semester"),
                    doc.getInteger("yearOfAdmission"),
                    doc.getInteger("expectedPassingYear"),
                    doc.getInteger("streamId")
            );
        }
        System.out.println(" No student found for Roll No: " + rollNo);
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        for (Document doc : collection.find()) {
            students.add(new Student(
                    doc.getString("rollNo"),
                    doc.getString("name"),
                    doc.getInteger("semester"),
                    doc.getInteger("yearOfAdmission"),
                    doc.getInteger("expectedPassingYear"),
                    doc.getInteger("streamId")
            ));
        }
        System.out.println(" Retrieved " + students.size() + " students.");
        return students;
    }

    public boolean updateStudent(Student student) {
        Document updateFields = new Document("name", student.getName())
                .append("semester", student.getSemester())
                .append("yearOfAdmission", student.getYearOfAdmission())
                .append("expectedPassingYear", student.getExpectedPassingYear())
                .append("streamId", student.getStreamId());
        Document updateOperation = new Document("$set", updateFields);

        long modifiedCount = collection.updateOne(Filters.eq("rollNo", student.getRollNo()), updateOperation).getModifiedCount();
        if (modifiedCount > 0) {
            System.out.println(" Student with Roll No " + student.getRollNo() + " updated.");
            return true;
        } else {
            System.out.println(" No student found with Roll No " + student.getRollNo() + " to update.");
            return false;
        }
    }

    public boolean deleteStudent(String rollNo) {

        if (!isStudentExists(rollNo)) {
            System.out.println(" No student found with Roll No " + rollNo + " to delete.");
            return false;
        }
        System.out.println("Attempting to delete student and associated records for Roll No: " + rollNo);
        boolean contactDeleted = contactDAO.deleteContact(rollNo);
        if (contactDeleted) {
            System.out.println(" Associated contact for " + rollNo + " deleted.");
        } else {
            System.out.println(" No associated contact found or failed to delete contact for " + rollNo + ".");
        }
        boolean subjectsDeleted = studentSubjectDAO.deleteSubjectsByRollNo(rollNo); // Assuming this method exists and works similarly
        if (subjectsDeleted) {
            System.out.println(" Associated subject assignments for " + rollNo + " deleted.");
        } else {
            System.out.println(" No associated subject assignments found or failed to delete for " + rollNo + ".");
        }
        long deletedCount = collection.deleteOne(Filters.eq("rollNo", rollNo)).getDeletedCount();
        if (deletedCount > 0) {
            System.out.println(" Student with Roll No " + rollNo + " deleted successfully!");
            return true;
        } else {
            System.out.println(" Failed to delete student with Roll No " + rollNo + " (Student may not have existed or an error occurred).");
            return false;
        }
    }

    public boolean isStudentExists(String rollNo) {
        return collection.countDocuments(Filters.eq("rollNo", rollNo)) > 0;
    }
}