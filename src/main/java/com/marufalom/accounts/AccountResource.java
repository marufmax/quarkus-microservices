package com.marufalom.accounts;

import jakarta.annotation.PostConstruct;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.math.BigDecimal;
import java.util.*;

@Path("/accounts")
public class AccountResource {
    Set<Account> accounts = new HashSet<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Account> allAccounts() {
        return accounts;
    }

    @PostConstruct
    public void setup() {
        accounts.add(new Account(123456789L, 987654321L, "Georgia Humpkin", new BigDecimal("315.13")));
        accounts.add(new Account(1344332332L, 987623231L, "Horgia Dumpkin", new BigDecimal("2332.13")));
        accounts.add(new Account(133434223L, 987623231L, "Horgia Dumpkin", new BigDecimal("0")));
    }

    @GET
    @Path("/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("accountNumber") Long accountNumber) {
        Optional<Account> response = accounts
                .stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst();

        return response.orElseThrow(() -> new WebApplicationException(String.format("Account with number %s not found", accountNumber), 404));
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception exception) {

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            JsonObjectBuilder entityBuilder = Json.createObjectBuilder()
                    .add("Exception type", exception.getClass().getName())
                    .add("code", code)
                ;

            if (exception.getMessage() != null) {
                entityBuilder.add("Error", exception.getMessage());
            }

            return Response.status(code)
                    .entity(entityBuilder.build())
                    .build()
                ;
        }
    }
}
