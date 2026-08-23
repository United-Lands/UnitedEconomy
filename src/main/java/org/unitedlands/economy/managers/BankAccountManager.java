package org.unitedlands.economy.managers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.unitedlands.economy.Settings;
import org.unitedlands.economy.UnitedEconomy;
import org.unitedlands.economy.classes.BankAccount;
import org.unitedlands.economy.classes.BankAccountHolder;
import org.unitedlands.economy.classes.Currency;
import org.unitedlands.economy.utils.EconomyActivityLogger;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

public class BankAccountManager {

    private static BankAccountManager instance;

    public static BankAccountManager instance() {
        return instance;
    }

    @SuppressWarnings("unused")
    private final UnitedEconomy plugin;
    private final IMessageProvider messageProvider;

    private Map<UUID, BankAccountHolder> bankAccounts = new HashMap<>();

    public BankAccountManager(UnitedEconomy plugin, IMessageProvider messageProvider) {
        this.plugin = plugin;
        this.messageProvider = messageProvider;
        instance = this;
    }

    public void loadDataFromDatabase() {
        CompletableFuture<List<BankAccountHolder>> accountHolderFuture = DatabaseManager.instance()
                .getBankAccountHolderService().getAllAsync();
        CompletableFuture<List<BankAccount>> accountFuture = DatabaseManager.instance().getBankAccountService()
                .getAllAsync();

        try {
            CompletableFuture
                    .allOf(accountHolderFuture, accountFuture)
                    .thenRun(() -> {
                        buildAccounts(accountHolderFuture.join(), accountFuture.join());
                    }).get();
        } catch (Exception ex) {
            Logger.logError("Initialization failed: " + ex.getMessage(), "UnitedEconomy");
            throw new RuntimeException("App init failed", ex);
        }

    }

    private void buildAccounts(List<BankAccountHolder> loadedAccountHolders, List<BankAccount> loadedAccounts) {
        for (var accountHolder : loadedAccountHolders) {
            bankAccounts.put(accountHolder.getUuid(), accountHolder);
        }

        for (var account : loadedAccounts) {
            var holder = bankAccounts.get(account.getAccountHolderId());
            if (holder == null) {
                EconomyActivityLogger.log("Removing abandoned account for " + account.getAccountHolderId().toString());
                DatabaseManager.instance().getBankAccountService().deleteAsync(account);
                continue;
            }
            holder.addAccount(account);
        }
        Logger.log("Loaded " + loadedAccounts.size() + " accounts for " + loadedAccountHolders.size() + " account holders.");
    }

    public Map<UUID, String> getUuidNameMap() {
        return bankAccounts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getName()));
    }

    public boolean createBankAccountHolder(UUID uuid, String name) {
        getOrCreateBankAccountHolder(uuid, name);
        return true;
    }

    public boolean renameBankAccountHolder(UUID uuid, String name) {
        // Name refresh is done autmatically during bank account holder retrieval
        getOrCreateBankAccountHolder(uuid, name);
        return true;
    }

    public BankAccount getOrCreateBankAccount(UUID uuid, String name, String worldName, String currencyKey) {
        var bankAccountHolder = getOrCreateBankAccountHolder(uuid, name);

        var bankAccount = bankAccountHolder.getAccount(worldName, currencyKey);
        if (bankAccount == null) {
            bankAccount = new BankAccount(uuid, worldName, currencyKey);
            bankAccountHolder.addAccount(bankAccount);

            EconomyActivityLogger.log("Bank account created for UUID " + uuid.toString() + "(name: " + name + ")");

            DatabaseManager.instance().getBankAccountService().createAsync(bankAccount);
        }

        return bankAccount;
    }

    public String getAccountHolderName(UUID uuid) {
        if (bankAccounts.containsKey(uuid))
            return bankAccounts.get(uuid).getName();
        return null;
    }

    // Balance

    public Map<Currency, BigDecimal> getBalances(UUID uuid, String name, String worldName) {
        var balances = new HashMap<Currency, BigDecimal>();
        var accountHolder = getOrCreateBankAccountHolder(uuid, name);
        for (var account : accountHolder.getAccounts(worldName)) {
            balances.put(Settings.instance().getCurrency(account.getCurrencyKey()), account.getBalance());
        }
        return balances;
    }

    public BigDecimal getBalance(UUID uuid, String name, String worldName, String currencyKey) {
        var bankAccount = getOrCreateBankAccount(uuid, name, worldName, currencyKey);
        return bankAccount.getBalance();
    }

    // Has

    public boolean has(UUID uuid, String name, BigDecimal amount, String worldName, String currencyKey) {
        var bankAccount = getOrCreateBankAccount(uuid, name, worldName, currencyKey);
        return bankAccount.hasAmount(amount);
    }

    // Deposit

    public BigDecimal deposit(UUID uuid, String name, BigDecimal amount, String worldName, String currencyKey) {

        if (currencyKey == null)
            currencyKey = Settings.instance().getDefaultCurrency().getKey();

        var bankAccount = getOrCreateBankAccount(uuid, name, worldName, currencyKey);
        var newBalance = bankAccount.addAmount(amount);

        EconomyActivityLogger.log(uuid.toString() + " (" + name + ") RECEIVED " + amount.toString() + " "
                + currencyKey + " (--> " + newBalance.toString() + " " + currencyKey + ")");
        DatabaseManager.instance().getBankAccountService().updateAsync(bankAccount);

        sendNotification(uuid, "messages.account-deposit", amount, newBalance, currencyKey);

        return newBalance;
    }

    // Withdraw

    public BigDecimal withdraw(UUID uuid, String name, BigDecimal amount, String worldName, String currencyKey) {

        if (currencyKey == null)
            currencyKey = Settings.instance().getDefaultCurrency().getKey();

        var bankAccount = getOrCreateBankAccount(uuid, name, worldName, currencyKey);
        var newBalance = bankAccount.removeAmount(amount);

        EconomyActivityLogger.log(uuid.toString() + " (" + name + ") LOST " + amount.toString()
                + " " + currencyKey + " (--> " + newBalance.toString() + " " + currencyKey + ")");
        DatabaseManager.instance().getBankAccountService().updateAsync(bankAccount);

        sendNotification(uuid, "messages.account-withdraw", amount, newBalance, currencyKey);

        return newBalance;
    }

    // Withdraw

    public BigDecimal set(UUID uuid, String name, BigDecimal amount, String worldName, String currencyKey) {

        if (currencyKey == null)
            currencyKey = Settings.instance().getDefaultCurrency().getKey();

        var bankAccount = getOrCreateBankAccount(uuid, name, worldName, currencyKey);
        var newBalance = bankAccount.setAmount(amount);

        EconomyActivityLogger.log(uuid.toString() + " (" + name + ") SET TO " + amount.toString() + " "
                + currencyKey + " (--> " + newBalance.toString() + " " + currencyKey + ")");
        DatabaseManager.instance().getBankAccountService().updateAsync(bankAccount);

        sendNotification(uuid, "messages.account-set", amount, newBalance, currencyKey);

        return newBalance;
    }

    // Delete

    public boolean remove(UUID uuid) {
        var accountHolder = getOrCreateBankAccountHolder(uuid, null);
        if (accountHolder == null)
            return true;

        for (var account : accountHolder.getAccounts()) {
            DatabaseManager.instance().getBankAccountService().deleteAsync(account);
        }
        DatabaseManager.instance().getBankAccountHolderService().deleteAsync(accountHolder);

        EconomyActivityLogger.log("Bank account holder " + uuid.toString() + " deleted");

        return true;
    }

    // Helpers

    private BankAccountHolder getOrCreateBankAccountHolder(UUID uuid, String name) {

        var bankAccountHolder = bankAccounts.get(uuid);
        if (bankAccountHolder == null) {
            bankAccountHolder = new BankAccountHolder(uuid, name);
            bankAccounts.put(uuid, bankAccountHolder);

            EconomyActivityLogger.log("Account holder created for UUID " + uuid.toString() + "(name: " + name + ")");
            DatabaseManager.instance().getBankAccountHolderService().createAsync(bankAccountHolder);
        }

        // Refresh names to keep them in sync with player data
        if (name != null) {
            if (!name.equals(bankAccountHolder.getName())) {
                EconomyActivityLogger.log("Updating account holder name for UUID " + uuid.toString() + "(old name: "
                        + bankAccountHolder.getName() + ", new name: " + name + ")");
                bankAccountHolder.setName(name);
                DatabaseManager.instance().getBankAccountHolderService().updateAsync(bankAccountHolder);
            }
        }

        return bankAccountHolder;
    }

    private void sendNotification(UUID uuid, String messageId, BigDecimal amount, BigDecimal balance, String currencyKey) {
        var player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            var currency = Settings.instance().getCurrency(currencyKey);
            Messenger.sendMessage(player, messageProvider.get(messageId),
                    Map.of(
                        "amount", String.format(currency.getFormat(), amount),
                        "balance", String.format(currency.getFormat(), balance)
                    ),
                    messageProvider.get("messages.prefix"));
        }
    }

}
