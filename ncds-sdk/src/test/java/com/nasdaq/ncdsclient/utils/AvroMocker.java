package com.nasdaq.ncdsclient.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AvroMocker {
    private Schema schema;
    private int noOfRecords = 0;

    public AvroMocker(Schema schema, int numberOfRecords){
        this.schema = schema;
        this.noOfRecords = numberOfRecords;
    }

    private GenericRecord createMessage(){
        GenericRecord record = new GenericData.Record(schema);
        List<Schema.Field> fields = record.getSchema().getFields();
        for( int i=0; i<fields.size(); i++){
            Schema.Type field= fields.get(i).schema().getType();
            if(field.equals(Schema.Type.INT)){
               record.put(fields.get(i).name(), new Random().nextInt());
            }
            else if(field.equals(Schema.Type.LONG)){
                record.put(fields.get(i).name(), new Random().nextLong());
            }
            else if (field.equals(Schema.Type.STRING)){
                String randomStringMessage= RandomStringUtils.randomAlphabetic(1).toUpperCase();
                record.put(fields.get(i).name(), randomStringMessage);
            }
        }
        return record;
    }

    public ArrayList<GenericRecord> generateMockMessages(){
        ArrayList<GenericRecord> records = new ArrayList<GenericRecord>();
        for (int i=0; i<noOfRecords; i++){
            records.add(createMessage());
        }
        return records;
    }
}
