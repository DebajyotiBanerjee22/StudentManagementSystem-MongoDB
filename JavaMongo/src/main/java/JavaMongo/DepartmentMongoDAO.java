package JavaMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
public class DepartmentMongoDAO {
    private final MongoCollection<Document> collection;
    public DepartmentMongoDAO() {
        this.collection = MongoConn.getDatabase().getCollection("departments");
    }

    public boolean addDepartment(Department department) {
        Document doc = new Document("deptId", department.getDeptId())
                .append("deptName", department.getDeptName());
        try {
            InsertOneResult result = collection.insertOne(doc);
            if (result.getInsertedId() != null) {
                System.out.println(" Department added: " + department.getDeptName() + " (ID: " + department.getDeptId() + ")");
                return true;
            }
            return false; 
        } catch (Exception e) {
            System.err.println(" Error adding department " + department.getDeptName() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Department getDepartmentById(int deptId) {
        Document doc = collection.find(Filters.eq("deptId", deptId)).first();
        if (doc != null) {
            System.out.println(" Department found for ID: " + deptId);
            return new Department(doc.getInteger("deptId"), doc.getString("deptName"));
        }
        System.out.println(" No department found for ID: " + deptId);
        return null;
    }

    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        for (Document doc : collection.find()) {
            departments.add(new Department(doc.getInteger("deptId"), doc.getString("deptName")));
        }
        System.out.println(" Retrieved " + departments.size() + " departments.");
        return departments;
    }

    public boolean updateDepartment(Department department) {
        Document updateFields = new Document("deptName", department.getDeptName());
        Document updateOperation = new Document("$set", updateFields);

        long modifiedCount = collection.updateOne(Filters.eq("deptId", department.getDeptId()), updateOperation).getModifiedCount();
        if (modifiedCount > 0) {
            System.out.println(" Department with ID " + department.getDeptId() + " updated.");
            return true;
        } else {
            System.out.println(" No department found with ID " + department.getDeptId() + " to update.");
            return false;
        }
    }

    public boolean deleteDepartment(int deptId) {
        long deletedCount = collection.deleteOne(Filters.eq("deptId", deptId)).getDeletedCount();
        if (deletedCount > 0) {
            System.out.println(" Department with ID " + deptId + " deleted.");
            return true;
        } else {
            System.out.println(" No department found with ID " + deptId + " to delete.");
            return false;
        }
    }

    public boolean isDepartmentExists(int deptId) {
        boolean exists = collection.countDocuments(Filters.eq("deptId", deptId)) > 0;
        if (exists) {
            System.out.println(" Department exists for ID: " + deptId);
        } else {
            System.out.println(" Department does NOT exist for ID: " + deptId);
        }
        return exists;
    }
}