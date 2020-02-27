package com.bars.orders.mongo;


import com.bars.orders.order.Order;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import java.util.List;
import java.util.Map;

import static com.bars.orders.GlobalLogger.glogger;
import static com.bars.orders.PropertiesHelper.getSystemProp;
import static com.mongodb.client.model.Projections.include;

public class MyMongoClient {
    public static final String ORDER_ID_KEY = "orderId";

    MongoCollection<Document> ordersCol;

    private String uri;
    private String ordersDatabase = "db-8bars";
    private String ordersCollection = "orders";

    public MyMongoClient() {
        uri = getSystemProp("MongoURI");
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setOrdersDatabase(String ordersDatabase) {
        this.ordersDatabase = ordersDatabase;
    }

    public void setOrdersCollection(String ordersCollection) {
        this.ordersCollection = ordersCollection;
    }

    public void init() {

        glogger.info("Connecting to Mongo..., uri: " + uri);
        MongoClient mongoClient = new MongoClient(new MongoClientURI(uri));

        MongoDatabase database = mongoClient.getDatabase(ordersDatabase);

        ordersCol = database.getCollection(ordersCollection);
        ordersCol.createIndex(Indexes.ascending(ORDER_ID_KEY), new IndexOptions().unique(true));

        glogger.info("Successfully connected to Mongo");
    }

    public List<String> getOrderIds() {
        List<String> result = Lists.newArrayList();

        ordersCol.find()
            .projection(include(ORDER_ID_KEY))
            .map(doc -> doc.getString(ORDER_ID_KEY))
            .into(result);

        return result;
    }

    public void storeOrder(Order order) {
        ordersCol.insertOne(new Document(order.toMap()));

        glogger.info("Order " + order.getOrderId()+ " stored to Mongo");
    }

    public void removeOrder(String orderId) {
        Map<String, Object> bson = ImmutableMap.of(ORDER_ID_KEY, orderId);
        DeleteResult deleteResult = ordersCol.deleteOne(new Document(bson));

        if (deleteResult.getDeletedCount() >= 1) {
            glogger.info("Order " + orderId+ " removed from Mongo");
        } else {
            glogger.warning("Order " + orderId+ " NOT removed from Mongo");
        }
    }
}
