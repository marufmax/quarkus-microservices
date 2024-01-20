package com.marufalom.accounts;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

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

        return response.orElseThrow(() -> new WebApplicationException("Account with number %s not found", 404));
    }
}
