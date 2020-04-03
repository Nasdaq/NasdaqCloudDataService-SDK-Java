package com.nasdaq.ncdsclient.news;

import org.apache.avro.Schema;


import java.io.IOException;

public class NewsUtil {


    public static Schema getNewsSchema() throws IOException {
        final Schema newsMessageSchema;
        try {
            Schema.Parser parser = new Schema.Parser();
            newsMessageSchema = parser.parse(ClassLoader.getSystemResourceAsStream("NewsSchema.avsc"));
            return newsMessageSchema;
        } catch (Exception e) {
            throw e;
        }
    }
}
