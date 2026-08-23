package org.unitedlands.economy.classes;

import java.math.BigDecimal;
import java.util.UUID;

import org.unitedlands.economy.Settings;
import org.unitedlands.economy.classes.db.Identifiable;

import com.j256.ormlite.field.DatabaseField;

public class BankAccount implements Identifiable {

    @DatabaseField(id = true, width = 36, canBeNull = false)
    private UUID uuid;

    @DatabaseField(width = 36, canBeNull = false, columnName = "account_holder_id")
    private UUID accountHolderId;

    @DatabaseField(canBeNull = false, columnName = "world_name")
    private String worldName;

    @DatabaseField(canBeNull = false, columnName = "currency_key")
    private String currencyKey;

    @DatabaseField(canBeNull = false, columnName = "balance")
    private BigDecimal balance = new BigDecimal(0);

    @DatabaseField(canBeNull = false)
    private Long created;

    public BankAccount() {
    }

    public BankAccount(UUID accountHolderId) {
        this.uuid = UUID.randomUUID();
        this.accountHolderId = accountHolderId;
        this.worldName = Settings.instance().getDefaultWorldName();
        this.currencyKey = Settings.instance().getDefaultCurrency().getKey();
        this.balance = new BigDecimal(0);
        this.created = System.currentTimeMillis();
    }

    public BankAccount(UUID accountHolderId, String worldName) {
        this.uuid = UUID.randomUUID();
        this.accountHolderId = accountHolderId;
        this.worldName = worldName == null ? Settings.instance().getDefaultWorldName() : worldName;
        this.currencyKey = Settings.instance().getDefaultCurrency().getKey();
        this.balance = new BigDecimal(0);
        this.created = System.currentTimeMillis();
    }

    public BankAccount(UUID accountHolderId, String worldName, String currencyKey) {
        this.uuid = UUID.randomUUID();
        this.accountHolderId = accountHolderId;
        this.worldName = worldName == null ? Settings.instance().getDefaultWorldName() : worldName;
        this.currencyKey = currencyKey == null ? Settings.instance().getDefaultCurrency().getKey() : currencyKey;
        this.balance = new BigDecimal(0);
        this.created = System.currentTimeMillis();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getAccountHolderId() {
        return accountHolderId;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getCurrencyKey() {
        return currencyKey;
    }

    public void setCurrencyKey(String currencyKey) {
        this.currencyKey = currencyKey;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public boolean hasAmount(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    public BigDecimal setAmount(BigDecimal amount) {
        this.balance = amount;
        return this.balance;
    }

    public BigDecimal addAmount(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        return this.balance;
    }

    public BigDecimal removeAmount(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
        return this.balance;
    }

}
