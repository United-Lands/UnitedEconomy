package org.unitedlands.economy.classes.db;

import java.util.UUID;

import org.unitedlands.economy.classes.BankAccountHolder;

import com.j256.ormlite.dao.Dao;

public class BankAccountHolderService extends BaseDbService<BankAccountHolder> {

    public BankAccountHolderService(Dao<BankAccountHolder, UUID> dao) {
        super(dao);
    }

}
