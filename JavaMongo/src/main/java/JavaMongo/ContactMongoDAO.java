package JavaMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult; // Import for UpdateResult
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class ContactMongoDAO {
    private final MongoCollection<Document> collection;

    public ContactMongoDAO() {
        this.collection = MongoConn.getDatabase().getCollection("contacts");
    }

    public boolean addContact(Contact contact) {
        Document doc = new Document("rollNo", contact.getRollNo())
                .append("regNo", contact.getRegNo())
                .append("phone", contact.getPhone())
                .append("email", contact.getEmail());
        try {
            InsertOneResult result = collection.insertOne(doc);
            if (result.getInsertedId() != null) {
                System.out.println(" Contact added for Roll No: " + contact.getRollNo());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println(" Error adding contact for Roll No " + contact.getRollNo() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateContact(String rollNo, String newRegNo, String newPhone, String newEmail) {
        Document updateFields = new Document();
        if (newRegNo != null && !newRegNo.trim().isEmpty()) {
            updateFields.append("regNo", newRegNo.trim());
        }
        if (newPhone != null && !newPhone.trim().isEmpty()) {
            updateFields.append("phone", newPhone.trim());
        }
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            updateFields.append("email", newEmail.trim());
        }
        if (updateFields.isEmpty()) {
            System.out.println("No contact fields provided for update for Roll No: " + rollNo);
            return false;
        }
        Document updateOperation = new Document("$set", updateFields);
        UpdateResult result = collection.updateOne(Filters.eq("rollNo", rollNo), updateOperation);

        if (result.getModifiedCount() > 0) {
            System.out.println("Contact for Roll No " + rollNo + " updated.");
            return true;
        } else if (result.getMatchedCount() > 0) {
            System.out.println("Contact for Roll No " + rollNo + " found, but no changes were necessary (values already matched).");
            return true;
        } else {
            System.out.println("No contact found for Roll No: " + rollNo + " to update.");
            return false;
        }
    }
    public Contact getContactByRollNo(String rollNo) {
        Document doc = collection.find(Filters.eq("rollNo", rollNo)).first();
        if (doc != null) {
            System.out.println("Contact found for Roll No: " + rollNo);
            return new Contact(
                    doc.getString("rollNo"),
                    doc.getString("regNo"),
                    doc.getString("phone"),
                    doc.getString("email")
            );
        }
        System.out.println("No contact found for Roll No: " + rollNo);
        return null;
    }
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        for (Document doc : collection.find()) {
            contacts.add(new Contact(
                    doc.getString("rollNo"),
                    doc.getString("regNo"),
                    doc.getString("phone"),
                    doc.getString("email")
            ));
        }
        System.out.println(" Retrieved " + contacts.size() + " contacts.");
        return contacts;
    }
    public boolean deleteContact(String rollNo) {
        long deletedCount = collection.deleteOne(Filters.eq("rollNo", rollNo)).getDeletedCount();
        if (deletedCount > 0) {
            System.out.println("Contact for Roll No " + rollNo + " deleted.");
            return true;
        } else {
            System.out.println("No contact found for Roll No " + rollNo + " to delete.");
            return false;
        }
    }
    public boolean isContactExists(String rollNo) {
        boolean exists = collection.countDocuments(Filters.eq("rollNo", rollNo)) > 0;
        if (exists) {
            System.out.println("Contact exists for Roll No: " + rollNo);
        } else {
            System.out.println("Contact does NOT exist for Roll No: " + rollNo);
        }
        return exists;
    }
}