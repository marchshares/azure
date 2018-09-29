package com.bars.orders.mongo;


import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.List;
import java.util.logging.Logger;

public class MyMongoClient {
    private final Logger logger;

    private MongoCollection<Document> ordersIdsCol;

    public MyMongoClient(Logger logger) {
        this.logger = logger;
    }

    public void init() {
        String uri = System.getenv("MongoURI");
//        logger.info("Connectiong to Mongo..., uri: " + uri);
        MongoClient mongoClient = new MongoClient(new MongoClientURI(uri));

        MongoDatabase database = mongoClient.getDatabase("db");

        ordersIdsCol = database.getCollection("coll");

        logger.info("Successfully connected to Mongo");
    }

    public void addOrder(String orderId) {
        Document doc = new Document("orderId", orderId);
        ordersIdsCol.insertOne(doc);
    }

    public List<String> getOrderIds() {
        List<String> result = Lists.newArrayList();

        ordersIdsCol.find()
            .map(doc -> doc.getString("orderId"))
            .into(result);

        return result;
    }

}
