package com.skillbox.mongodemo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestMongoDB {

    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> shops;
    MongoCollection<Document> goods;

    public TestMongoDB() {
        mongoClient = new MongoClient("127.0.0.1", 27017);
        database = mongoClient.getDatabase("local");
        shops = database.getCollection("Shops");
        goods = database.getCollection("Goods");
        shops.drop();
        goods.drop();
    }


    public void addShop(String text) {
        Pattern p1 = Pattern.compile("[^ДОБАВИТЬ_МАГАЗИН][^\\s][[a-zA-Z]\\s[А-я][а-я\\s]]*");
        Matcher m1 = p1.matcher(text);
        String string1;
        m1.find();
        string1 = text.substring(m1.start(), m1.end());
        String nameShop = string1.trim();
        List<Document> objects = new ArrayList<>();
        Document firstDocument = new Document()
                .append("name", nameShop)
                .append("goods", objects);
        shops.insertOne(firstDocument);
        System.out.println("магазин " + nameShop + " добавлен в коллекцию Shops");

    }

    public void addGood(String text) {
        Pattern p2 = Pattern.compile("[^ДОБАВИТЬ_ТОВАР][^\\s][[a-zA-Z]\\s[А-я][а-я\\s]]*\\s\\w*");
        Matcher m2 = p2.matcher(text);
        String string2;
        m2.find();
        string2 = text.substring(m2.start(), m2.end());
        String[] text1 = string2.split(" ");
        if (text1.length != 3) {
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println("Wrong format. Correct format: \n" +
                        "ДОБАВИТЬ_ТОВАР МУКА 90");

            }
        } else {
            String name = text1[1];
            Integer price = Integer.valueOf(text1[2]);
            Document secondDocument = new Document()
                    .append("name", name)
                    .append("price", price);
            goods.insertOne(secondDocument);
            System.out.println("товар " + name + " добавлен в коллекцию Goods");
        }
    }

    public void putGoodInShop(String text) {
        Pattern p3 = Pattern.compile("[^ВЫСТАВИТЬ_ТОВАР][^\\s][[a-zA-Z]\\s[А-я][а-я\\s]]*[[a-zA-Z\\s][А-я][а-я\\s]]*");
        Matcher m3 = p3.matcher(text);
        String string3;
        m3.find();
        string3 = text.substring(m3.start(), m3.end());
        String[] text1 = string3.split(" ");
        if (text1.length != 3) {
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println("Wrong format. Correct format: \n" +
                        "ВЫСТАВИТЬ_ТОВАР МУКА ЛЕНТА");
            }
        } else {
            String nameFood = text1[1];
            String nameShop1 = text1[2];
            BsonDocument query2 = BsonDocument.parse("{name: {$eq: \"" + nameFood + "\"}}");
            BsonDocument query3 = BsonDocument.parse("{name: {$eq: \"" + nameShop1 + "\"}}");

            Document doc = goods.find(query2).first();
            Document doc1 = shops.find(query3).first();
            if (doc == null || doc1 == null) {
                {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        System.out.println("такого товара или магазина нет");
                    }
                }
            } else {

                goods.find(query2).forEach((Consumer<Document>) document ->
                        shops.findOneAndUpdate(Document.parse("{name: \"" + nameShop1 + "\"}"), Document.parse("{$push: {goods: " + document.toJson() + "}}")));
                System.out.println("товар " + nameFood + " добавлен в магазин " + nameShop1);
            }
        }
    }

    public void avgPrice() {
        Consumer<Document> processBlock = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                document.toJson();
            }
        };

        List<? extends Bson> pipeline = Arrays.asList(
                new Document()
                        .append("$lookup", new Document()
                                .append("from", "Goods")
                                .append("localField", "goods.name")
                                .append("foreignField", "name")
                                .append("as", "list")
                        ),
                new Document()
                        .append("$unwind", new Document()
                                .append("path", "$list")
                        ),
                new Document()
                        .append("$project", new Document()
                                .append("_id", new Document()
                                        .append("name", "$name")
                                )
                                .append("avgprice", new Document()
                                        .append("$avg", "$goods.price")
                                )
                        )
        );

        shops.aggregate(pipeline)
                .allowDiskUse(false)
                .forEach(processBlock);

    }


    public void minMaxSum() {
        Consumer<Document> processBlock1 = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson());
            }
        };

        List<? extends Bson> pipeline1 = Arrays.asList(
                new Document()
                        .append("$lookup", new Document()
                                .append("from", "Goods")
                                .append("localField", "goods.name")
                                .append("foreignField", "name")
                                .append("as", "list")
                        ),
                new Document()
                        .append("$unwind", new Document()
                                .append("path", "$list")
                        ),
                new Document()
                        .append("$group", new Document()
                                .append("_id", new Document()
                                        .append("name", "$name")
                                )
                                .append("minprice", new Document()
                                        .append("$min", "$list.price")
                                )
                                .append("maxprice", new Document()
                                        .append("$max", "$list.price")
                                )
                                .append("countGoods", new Document()
                                        .append("$sum", 1.0)
                                )
                        )
        );

        shops.aggregate(pipeline1)
                .allowDiskUse(false)
                .forEach(processBlock1);

    }

    public void priceLess() {
        Consumer<Document> processBlock = new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                System.out.println(document.toJson());
            }
        };

        List<? extends Bson> pipeline = Arrays.asList(
                new Document()
                        .append("$lookup", new Document()
                                .append("from", "Goods")
                                .append("localField", "goods.name")
                                .append("foreignField", "name")
                                .append("as", "list")
                        ),
                new Document()
                        .append("$unwind", new Document()
                                .append("path", "$list")
                        ),
                new Document()
                        .append("$project", new Document()
                                .append("_id", new Document()
                                        .append("name", "$name")
                                )
                                .append("goodPrice<100", new Document()
                                        .append("$cond", new Document()
                                                .append("if", new Document()
                                                        .append("$lt", Arrays.asList(
                                                                "$list.price",
                                                                100.0
                                                                )
                                                        )
                                                )
                                                .append("then", "$list.price")
                                                .append("else", "")
                                        )
                                )
                        ),
                new Document()
                        .append("$match", new Document()
                                .append("goodPrice<100", new Document()
                                        .append("$lt", 100.0)
                                )
                        ),
                new Document()
                        .append("$group", new Document()
                                .append("_id", 0.0)
                                .append("SUM", new Document()
                                        .append("$sum", 1.0)
                                )
                        )
        );

        shops.aggregate(pipeline)
                .allowDiskUse(false)
                .forEach(processBlock);
    }

}
