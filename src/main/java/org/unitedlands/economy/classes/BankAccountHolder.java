package org.unitedlands.economy.classes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.unitedlands.economy.Settings;
import org.unitedlands.economy.classes.db.Identifiable;

import com.j256.ormlite.field.DatabaseField;

public class BankAccountHolder implements Identifiable {

    @DatabaseField(id = true, width = 36, canBeNull = false)
    private UUID uuid;

    @DatabaseField(canBeNull = true, columnName = "owner_name")
    private String name;

    public BankAccountHolder() {
    }

    public BankAccountHolder(UUID uuid) {
        this.uuid = uuid;
    }

    public BankAccountHolder(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String ownerName) {
        this.name = ownerName;
    }

    private transient Set<BankAccount> accounts = new HashSet<>();

    public void addAccount(BankAccount account) {
        accounts.add(account);
    }

    public void removeAccount(BankAccount account) {
        accounts.add(account);
    }

    public Set<BankAccount> getAccounts() {
        return accounts;
    }

    public Set<BankAccount> getAccounts(String worldName) {
        if (worldName == null)
            worldName = Settings.instance().getDefaultWorldName();
        final String finalWorldName = worldName;
        return accounts.stream().filter(a -> a.getWorldName().equals(finalWorldName)).collect(Collectors.toSet());
    }

    public BankAccount getAccount() {

        return getAccount(null, null);
    }

    public BankAccount getAccount(String worldName, String currencyKey) {
        if (worldName == null)
            worldName = Settings.instance().getDefaultWorldName();
        if (currencyKey == null)
            currencyKey = Settings.instance().getDefaultCurrency().getKey();

        final String finalWorldName = worldName;
        final String finalCurrencyKey = currencyKey;
        return accounts.stream()
                .filter(a -> a.getWorldName().equals(finalWorldName) && a.getCurrencyKey().equals(finalCurrencyKey))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BankAccountHolder other = (BankAccountHolder) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

}
