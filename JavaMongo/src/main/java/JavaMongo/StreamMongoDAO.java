package JavaMongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class StreamMongoDAO {
    private final MongoCollection<Document> collection;
    public StreamMongoDAO() {
        this.collection = MongoConn.getDatabase().getCollection("streams");
    }
    
    public boolean addStream(Stream stream) {
            Document doc = new Document("streamId", stream.getStreamId())
                .append("streamName", stream.getStreamName())
                .append("deptId", stream.getDeptId());
        try {
            InsertOneResult result = collection.insertOne(doc);
            if (result.getInsertedId() != null) {
                System.out.println(" Stream added: " + stream.getStreamName() + " (ID: " + stream.getStreamId() + ")");
                return true;
            }
            return false; 
        } catch (Exception e) {
            System.err.println(" Error adding stream " + stream.getStreamName() + " (ID: " + stream.getStreamId() + "): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Stream getStreamById(int streamId) {
        Document doc = collection.find(Filters.eq("streamId", streamId)).first();
        if (doc != null) {
            System.out.println(" Stream found for ID: " + streamId);
            return new Stream(
                    doc.getInteger("streamId"),
                    doc.getString("streamName"),
                    doc.getInteger("deptId")
            );
        }
        System.out.println(" No stream found for ID: " + streamId);
        return null;
    }

    public List<Stream> getAllStreams() {
        List<Stream> streams = new ArrayList<>();
        for (Document doc : collection.find()) {
            streams.add(new Stream(
                    doc.getInteger("streamId"),
                    doc.getString("streamName"),
                    doc.getInteger("deptId")
            ));
        }
        System.out.println(" Retrieved " + streams.size() + " streams.");
        return streams;
    }

    public boolean updateStream(Stream stream) {
        Document updateFields = new Document("streamName", stream.getStreamName())
                .append("deptId", stream.getDeptId());
        Document updateOperation = new Document("$set", updateFields);

        long modifiedCount = collection.updateOne(Filters.eq("streamId", stream.getStreamId()), updateOperation).getModifiedCount();
        if (modifiedCount > 0) {
            System.out.println(" Stream with ID " + stream.getStreamId() + " updated.");
            return true;
        } else {
            System.out.println(" No stream found with ID " + stream.getStreamId() + " to update.");
            return false;
        }
    }

    public boolean deleteStream(int streamId) {
        long deletedCount = collection.deleteOne(Filters.eq("streamId", streamId)).getDeletedCount();
        if (deletedCount > 0) {
            System.out.println(" Stream with ID " + streamId + " deleted.");
            return true;
        } else {
            System.out.println(" No stream found with ID " + streamId + " to delete.");
            return false;
        }
    }

    public boolean isStreamExists(int streamId) {
        boolean exists = collection.countDocuments(Filters.eq("streamId", streamId)) > 0;
        if (exists) {
            System.out.println(" Stream exists for ID: " + streamId);
        } else {
            System.out.println(" Stream does NOT exist for ID: " + streamId);
        }
        return exists;
    }
}