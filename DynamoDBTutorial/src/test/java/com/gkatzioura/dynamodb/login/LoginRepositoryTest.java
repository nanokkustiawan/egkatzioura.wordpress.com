package com.gkatzioura.dynamodb.login;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.gkatzioura.dynamodb.TableCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by gkatzioura on 7/2/16.
 */
public class LoginRepositoryTest {

    private AmazonDynamoDB amazonDynamoDB;
    private LoginRepository loginRepository;

    @Before
    public void setUp() {
        AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient();
        amazonDynamoDB.setEndpoint("http://localhost:8000");
        this.amazonDynamoDB = amazonDynamoDB;
        this.loginRepository = new LoginRepository(amazonDynamoDB);
        TableCreator tableCreator = new TableCreator(amazonDynamoDB);
        tableCreator.deleteLoginsTable();
        tableCreator.createLoginsTable();
    }

    @Test
    public void testGetUser() {

        for(int i=0;i<100 ;i++) {

            ZonedDateTime zonedDateTime = ZonedDateTime.now().minusDays(i);
            loginRepository.insertLogin("me@test.com",Date.from(zonedDateTime.toInstant()));
        }

        Date from = Date.from(ZonedDateTime.now().minusDays(50).toInstant());
        Date to = Date.from(ZonedDateTime.now().toInstant());

        List<Map<String,AttributeValue>> items = loginRepository.queryLoginsBetween("me@test.com",from,to);

        Assert.assertEquals(50,items.size());
    }

    @Test
    public void testGetLogins() {

        for(int i=0;i<100 ;i++) {

            ZonedDateTime zonedDateTime = ZonedDateTime.now().minusDays(i);
            loginRepository.insertLogin("me@test.com",Date.from(zonedDateTime.toInstant()));
        }

        List<Map<String,AttributeValue>> items = loginRepository.fetchLoginsDesc("me@test.com");

        Assert.assertEquals(100,items.size());
    }

    @Test
    public void testCountLogins() {
        for(int i=0;i<100 ;i++) {

            ZonedDateTime zonedDateTime = ZonedDateTime.now().minusDays(i);
            loginRepository.insertLogin("me@test.com",Date.from(zonedDateTime.toInstant()));
        }

        Integer totalItems = loginRepository.countLogins("me@test.com");

        Assert.assertEquals(100L,totalItems.longValue());
    }

    @Test
    public void testScanLogins() {

        ZonedDateTime middleDate = null;

        for(int i=0;i<100 ;i++) {

            ZonedDateTime zonedDateTime = ZonedDateTime.now().minusDays(i);
            loginRepository.insertLogin(i+"me@test.com",Date.from(zonedDateTime.toInstant()));
            if(i==50) {
                middleDate = zonedDateTime;
            }
        }

        List<String> emails = loginRepository.scanLogins(Date.from(middleDate.toInstant()));

        Assert.assertEquals(49,emails.size());
   }

}
