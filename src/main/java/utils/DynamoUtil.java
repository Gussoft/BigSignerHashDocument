package utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import java.util.*;

public final class DynamoUtil {

    public String tableName = "Register";
    private static final Regions REGION = Regions.US_WEST_2;
    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(AwsServices.connectAws()))
            .withRegion(REGION).build();
    static DynamoDB dynamo = new DynamoDB(client);

    public Table getDynamo(String name, String salida, String hash1, String hash2){
        String time = Fecha.FechaDB() + " "+ Fecha.darHora();
        String hash = UUID.randomUUID().toString();
        Table register = dynamo.getTable(tableName);
        Item item = new Item()
                .withPrimaryKey("Id", hash)
                .withString("file", name)
                .withString("hash", hash1)
                .withString("firmado", hash2)
                .withString("date", time)
                .withString("ruta", salida);

        PutItemOutcome outcome = register.putItem(item);
        System.out.println(outcome.toString());
        return register;
    }
}
