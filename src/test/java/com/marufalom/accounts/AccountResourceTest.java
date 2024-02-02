package com.marufalom.accounts;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.List;

@QuarkusTest
public class AccountResourceTest {
    @Test
    void testRetrieveAll() {
       Response result = given()
                .when()
                .get("/accounts")
                .then()
                .statusCode(200)
                .body(
                        containsString("Horgia Dumpkin"),
                        containsString("Dorgia Kumpkin"),
                        containsString("Horgia Dumpkin")
                )
                .extract()
                .response();

       List<Account> accounts = result.jsonPath().getList("$");

        assertThat(accounts, not(empty()));
        assertThat(accounts, hasSize(4));
    }

    @Test
    @Order(2)
    void testGetAccount() {
        Account account =
                given()
                        .when().get("/accounts/{accountNumber}", 133434223)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Account.class);

        assertThat(account.getAccountNumber(), equalTo(133434223L));
        assertThat(account.getCustomerName(), equalTo("Dorgia Kumpkin"));
        assertThat(account.getBalance(), equalTo(new BigDecimal("0")));
        assertThat(account.getAccountStatus(), equalTo(AccountStatus.OPEN));
    }

    @Test
    @Order(3)
    void testCreateAccount() {
        Account newAccount = new Account(324324L, 112244L, "Sandy Holmes", new BigDecimal("154.55"));

        Account returnedAccount =
                given()
                        .contentType(ContentType.JSON)
                        .body(newAccount)
                        .when().post("/accounts")
                        .then()
                        .statusCode(201)
                        .extract()
                        .as(Account.class);

        assertThat(returnedAccount, notNullValue());
        assertThat(returnedAccount, equalTo(newAccount));

        Response result =
                given()
                        .when().get("/accounts")
                        .then()
                        .statusCode(200)
                        .body(
                                containsString("Horgia Dumpkin"),
                                containsString("Dorgia Kumpkin"),
                                containsString("Horgia Dumpkin")
                        )
                        .extract()
                        .response();

        List<Account> accounts = result.jsonPath().getList("$");
        assertThat(accounts, not(empty()));
        assertThat(accounts, hasSize(4));
    }
}
