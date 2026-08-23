package org.unitedlands.economy.economy;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.unitedlands.economy.Settings;
import org.unitedlands.economy.managers.BankAccountManager;
import org.unitedlands.utils.Logger;

import net.milkbowl.vault2.economy.EconomyResponse.ResponseType;
import net.milkbowl.vault2.economy.AccountPermission;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;

public class ULEconomy implements Economy {

    @Override
    public @NotNull Collection<String> currencies() {
        return Settings.instance().getCurrencyKeys();
    }

    @Override
    public @NotNull String defaultCurrencyNamePlural(@NotNull String arg0) {
        return Settings.instance().getDefaultCurrency().getNamePlural();
    }

    @Override
    public @NotNull String defaultCurrencyNameSingular(@NotNull String arg0) {
        return Settings.instance().getDefaultCurrency().getNameSingular();
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final String currency) {
        // All accounts support all currencies as long as the currency exist
        return Settings.instance().getCurrencyKeys().contains(currency);
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final String currency, @NotNull final String world) {
        // All accounts support all currencies as long as the currency exist
        return Settings.instance().getCurrencyKeys().contains(currency);
    }

    @Override
    public boolean createAccount(@NotNull final UUID accountID, @NotNull final String name) {
        BankAccountManager.instance().createBankAccountHolder(accountID, name);
        return true;
    }

    @Override
    public boolean createAccount(@NotNull final UUID accountID, @NotNull final String name, final boolean player) {
        BankAccountManager.instance().createBankAccountHolder(accountID, name);
        return true;
    }

    @Override
    public boolean createAccount(@NotNull final UUID accountID, @NotNull final String name,
            @NotNull final String worldName) {
        BankAccountManager.instance().createBankAccountHolder(accountID, name);
        return true;
    }

    @Override
    public boolean createAccount(@NotNull final UUID accountID, @NotNull final String name,
            @NotNull final String worldName, final boolean player) {
        BankAccountManager.instance().createBankAccountHolder(accountID, name);
        return true;
    }

    @Override
    public boolean deleteAccount(@NotNull final String pluginName, @NotNull final UUID accountID) {
        return BankAccountManager.instance().remove(accountID);
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final BigDecimal amount) {
        var newBalance = BankAccountManager.instance().deposit(accountID, null, amount, null, null);
        return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final String worldName, @NotNull final BigDecimal amount) {
        var newBalance = BankAccountManager.instance().deposit(accountID, null, amount, worldName, null);
        return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final String worldName, @NotNull final String currency, @NotNull final BigDecimal amount) {
        var newBalance = BankAccountManager.instance().deposit(accountID, null, amount, worldName, currency);
        return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount) {
        var currency = Settings.instance().getDefaultCurrency();
        return String.format(currency.getFormat(), amount);
    }

    @Override
    public @NotNull String format(@NotNull final String pluginName, @NotNull final BigDecimal amount) {
        var currency = Settings.instance().getDefaultCurrency();
        return String.format(currency.getFormat(), amount);
    }

    @Override
    public @NotNull String format(@NotNull final BigDecimal amount, @NotNull final String currencyKey) {
        var currency = Settings.instance().getDefaultCurrency();
        if (Settings.instance().getCurrencyKeys().contains(currencyKey))
            currency = Settings.instance().getCurrency(currencyKey);
        return String.format(currency.getFormat(), amount);
    }

    @Override
    public @NotNull String format(@NotNull final String pluginName, @NotNull final BigDecimal amount,
            @NotNull final String currencyKey) {
        var currency = Settings.instance().getDefaultCurrency();
        if (Settings.instance().getCurrencyKeys().contains(currencyKey))
            currency = Settings.instance().getCurrency(currencyKey);
        return String.format(currency.getFormat(), amount);
    }

    @Override
    public int fractionalDigits(@NotNull final String pluginName) {
        return Settings.instance().getRoundingDigits();
    }

    @Override
    public Optional<String> getAccountName(@NotNull UUID uuid) {
        return Optional.ofNullable(BankAccountManager.instance().getAccountHolderName(uuid));
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull final String pluginName, @NotNull final UUID accountID) {
        return BankAccountManager.instance().getBalance(accountID, null, null, null);
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final String world) {
        return BankAccountManager.instance().getBalance(accountID, null, world, null);
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final String world, @NotNull final String currency) {
        return BankAccountManager.instance().getBalance(accountID, null, world, currency);
    }

    @Override
    public @NotNull String getDefaultCurrency(@NotNull String pluginName) {
        return Settings.instance().getDefaultCurrency().getKey();
    }

    @Override
    public @NotNull String getName() {
        return "UnitedEconomy";
    }

    @Override
    public @NotNull Map<UUID, String> getUUIDNameMap() {
        return BankAccountManager.instance().getUuidNameMap();
    }

    @Override
    public boolean has(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final BigDecimal amount) {
        return BankAccountManager.instance().has(accountID, null, amount, null, null);
    }

    @Override
    public boolean has(@NotNull final String pluginName, @NotNull final UUID accountID, @NotNull final String worldName,
            @NotNull final BigDecimal amount) {
        return BankAccountManager.instance().has(accountID, null, amount, worldName, null);
    }

    @Override
    public boolean has(@NotNull final String pluginName, @NotNull final UUID accountID, @NotNull final String worldName,
            @NotNull final String currency, @NotNull final BigDecimal amount) {
        return BankAccountManager.instance().has(accountID, null, amount, worldName, currency);
    }

    @Override
    public boolean hasAccount(@NotNull UUID arg0) {
        // Accounts are always generated on any transaction, so always return true
        return true;
    }

    @Override
    public boolean hasAccount(@NotNull UUID arg0, @NotNull String arg1) {
        // Accounts are always generated on any transaction, so always return true
        return true;
    }

    @Override
    public boolean hasCurrency(@NotNull final String currency) {
        return Settings.instance().getCurrencyKeys().contains(currency);
    }

    @Override
    public boolean hasMultiCurrencySupport() {
        return true;
    }

    @Override
    public boolean hasSharedAccountSupport() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean renameAccount(@NotNull final UUID accountID, @NotNull final String name) {
        return BankAccountManager.instance().renameBankAccountHolder(accountID, name);
    }

    @Override
    public boolean renameAccount(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final String name) {
        return BankAccountManager.instance().renameBankAccountHolder(accountID, name);
    }

    @Override
    public @NotNull EconomyResponse canWithdraw(final @NotNull String pluginName, final @NotNull UUID accountID,
            final @NotNull BigDecimal amount) {
        return canWithdraw(pluginName, accountID, null, null, amount);
    }

    @Override
    public @NotNull EconomyResponse canWithdraw(final @NotNull String pluginName, final @NotNull UUID accountID,
            final @NotNull String worldName, final @NotNull BigDecimal amount) {
        return canWithdraw(pluginName, accountID, worldName, null, amount);
    }

    @Override
    public @NotNull EconomyResponse canWithdraw(final @NotNull String pluginName, final @NotNull UUID accountID,
            final @NotNull String worldName, final @NotNull String currency, final @NotNull BigDecimal amount) {
        var balance = BankAccountManager.instance().getBalance(accountID, null, worldName, currency);
        if (balance.compareTo(amount) >= 0)
            return new EconomyResponse(amount, balance, ResponseType.SUCCESS, null);
        else
            return new EconomyResponse(amount, balance, ResponseType.FAILURE, "Insufficient funds.");
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final BigDecimal amount) {
        var newBalance = BankAccountManager.instance().withdraw(accountID, null, amount, null, null);
        return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final String worldName, @NotNull final BigDecimal amount) {
        var newBalance = BankAccountManager.instance().withdraw(accountID, null, amount, worldName, null);
        return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final String worldName, @NotNull final String currency, @NotNull final BigDecimal amount) {
        var newBalance = BankAccountManager.instance().withdraw(accountID, null, amount, worldName, currency);
        return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
    }

    // #region Shared accounts - unsupported

    @Override
    public boolean addAccountMember(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final UUID uuid) {
        throw new UnsupportedOperationException("Unimplemented method 'addAccountMember'");
    }

    @Override
    public boolean addAccountMember(@NotNull final String pluginName, @NotNull final UUID accountID,
            @NotNull final UUID uuid, @NotNull final AccountPermission... initialPermissions) {
        throw new UnsupportedOperationException("Unimplemented method 'addAccountMember'");
    }

    @Override
    public boolean createSharedAccount(@NotNull String arg0, @NotNull UUID arg1, @NotNull String arg2,
            @NotNull UUID arg3) {
        throw new UnsupportedOperationException("Unimplemented method 'createSharedAccount'");
    }

    @Override
    public boolean isAccountMember(@NotNull String arg0, @NotNull UUID arg1, @NotNull UUID arg2) {
        throw new UnsupportedOperationException("Unimplemented method 'isAccountMember'");
    }

    @Override
    public boolean isAccountOwner(@NotNull String arg0, @NotNull UUID arg1, @NotNull UUID arg2) {
        throw new UnsupportedOperationException("Unimplemented method 'isAccountOwner'");
    }

    @Override
    public boolean removeAccountMember(@NotNull String arg0, @NotNull UUID arg1, @NotNull UUID arg2) {
        throw new UnsupportedOperationException("Unimplemented method 'removeAccountMember'");
    }

    @Override
    public boolean setOwner(@NotNull String arg0, @NotNull UUID arg1, @NotNull UUID arg2) {
        throw new UnsupportedOperationException("Unimplemented method 'setOwner'");
    }

    @Override
    public boolean updateAccountPermission(@NotNull String arg0, @NotNull UUID arg1, @NotNull UUID arg2,
            @NotNull AccountPermission arg3, boolean arg4) {
        throw new UnsupportedOperationException("Unimplemented method 'updateAccountPermission'");
    }

    @Override
    public boolean hasAccountPermission(@NotNull String arg0, @NotNull UUID arg1, @NotNull UUID arg2,
            @NotNull AccountPermission arg3) {
        throw new UnsupportedOperationException("Unimplemented method 'hasAccountPermission'");

    }

    // #endregion

}
