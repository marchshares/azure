package com.bars.orders.mongo;


import com.bars.orders.json.Order;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import java.util.List;
import java.util.logging.Logger;

import static com.bars.orders.PropertiesHelper.getSystemProp;
import static com.mongodb.client.model.Projections.include;

public class MyMongoClient {
    public static final String ORDER_ID_KEY = "orderId";
    public static final String ORDERS_COLLECTION_NAME = "orders";
    public static final String DATABASE_NAME = "db";
    private final Logger logger;

    MongoCollection<Document> ordersCol;

    private String uri;

    public MyMongoClient(Logger logger) {
        this.logger = logger;
        uri = getSystemProp("MongoURI");
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void init() {

        logger.info("Connecting to Mongo..., uri: " + uri);
        MongoClient mongoClient = new MongoClient(new MongoClientURI(uri));

        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);

        ordersCol = database.getCollection(ORDERS_COLLECTION_NAME);
        //ordersCol.createIndex(Indexes.ascending(ORDER_ID_KEY), new IndexOptions().unique(true));

        logger.info("Successfully connected to Mongo");
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

        logger.info("Order " + order.getOrderId()+ " stored to Mongo");
    }
}
