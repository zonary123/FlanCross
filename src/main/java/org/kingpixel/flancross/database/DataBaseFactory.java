package org.kingpixel.flancross.database;

/**
 * @author Carlos Varas Alonso - 29/09/2025 2:07
 */
public class DataBaseFactory {
    public static DataBaseClient INSTANCE;

    public static void init(){
        if (INSTANCE != null) INSTANCE.disconnect();
        INSTANCE = new MongoDBClient();
        INSTANCE.connect();
    }



}
