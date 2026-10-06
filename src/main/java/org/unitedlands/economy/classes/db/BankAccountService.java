package org.unitedlands.economy.classes.db;

import java.util.UUID;

import org.unitedlands.economy.classes.BankAccount;
import org.unitedlands.libs.ormlite.dao.Dao;

public class BankAccountService extends BaseDbService<BankAccount> {

    public BankAccountService(Dao<BankAccount, UUID> dao) {
        super(dao);
    }

}
