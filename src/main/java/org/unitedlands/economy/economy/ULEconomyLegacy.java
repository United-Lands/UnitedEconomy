package org.unitedlands.economy.economy;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.unitedlands.economy.Settings;
import org.unitedlands.economy.managers.BankAccountManager;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

@SuppressWarnings({"deprecation"})
public class ULEconomyLegacy implements Economy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "UnitedEconomy (legacy)";
    }

    @Override
    public String currencyNamePlural() {
        return Settings.instance().getDefaultCurrency().getNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return Settings.instance().getDefaultCurrency().getNameSingular();
    }

    @Override
    public int fractionalDigits() {
        return Settings.instance().getRoundingDigits();
    }

    @Override
    public String format(double amount) {
        String currencyFormat = Settings.instance().getDefaultCurrency().getFormat();
        return String.format(currencyFormat, amount);
    }

    @Override
    public boolean hasAccount(String playerName) {
        // Accounts are always generated on any transaction, so always return true
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        // Accounts are always generated on any transaction, so always return true
        return true;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        // Accounts are always generated on any transaction, so always return true
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        // Accounts are always generated on any transaction, so always return true
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        var player = Bukkit.getOfflinePlayer(playerName);
        return createPlayerAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (player.getLastLogin() != 0) {
            BankAccountManager.instance().getOrCreateBankAccount(player.getUniqueId(), player.getName(), null, null);
            return true;
        }
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        var player = Bukkit.getOfflinePlayer(playerName);
        return createPlayerAccount(player, worldName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        if (player.getLastLogin() != 0) {
            BankAccountManager.instance().getOrCreateBankAccount(player.getUniqueId(), player.getName(), worldName, null);
            return true;
        }
        return false;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        var player = Bukkit.getOfflinePlayer(playerName);
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (player.getLastLogin() == 0) {
            return new EconomyResponse(0d, 0d, ResponseType.FAILURE, "Player " + player + " not found.");
        }
        var newBalance = BankAccountManager.instance().deposit(player.getUniqueId(), player.getName(),
                new BigDecimal(amount), null, null);
        return new EconomyResponse(amount, newBalance.doubleValue(), ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        var player = Bukkit.getOfflinePlayer(playerName);
        return depositPlayer(player, worldName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        if (player.getLastLogin() == 0) {
            return new EconomyResponse(0d, 0d, ResponseType.FAILURE, "Player " + player + " not found.");
        }
        var newBalance = BankAccountManager.instance().deposit(player.getUniqueId(), player.getName(),
                new BigDecimal(amount), worldName, null);
        return new EconomyResponse(amount, newBalance.doubleValue(), ResponseType.SUCCESS, null);
    }

    @Override
    public double getBalance(String playerName) {
        var player = Bukkit.getOfflinePlayer(playerName);
        return getBalance(player);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        if (player.getLastLogin() == 0) {
            return 0d;
        }
        return BankAccountManager.instance().getBalance(player.getUniqueId(), player.getName(), null, null)
                .doubleValue();
    }

    @Override
    public double getBalance(String playerName, String worldName) {
        var player = Bukkit.getOfflinePlayer(playerName);
        return getBalance(player, worldName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String worldName) {
        if (player.getLastLogin() == 0) {
            return 0d;
        }
        return BankAccountManager.instance().getBalance(player.getUniqueId(), player.getName(), worldName, null)
                .doubleValue();
    }

    @Override
    public boolean has(String playerName, double amount) {
        var player = Bukkit.getOfflinePlayer(playerName);
        return has(player, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        if (player.getLastLogin() == 0) {
            return false;
        }
        var balance = BankAccountManager.instance().getBalance(player.getUniqueId(), player.getName(), null, null)
                .doubleValue();
        return balance >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        var player = Bukkit.getOfflinePlayer(playerName);
        return has(player, worldName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        if (player.getLastLogin() == 0) {
            return false;
        }
        var balance = BankAccountManager.instance().getBalance(player.getUniqueId(), player.getName(), worldName, null)
                .doubleValue();
        return balance >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        var player = Bukkit.getOfflinePlayer(playerName);
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (player.getLastLogin() == 0) {
            return new EconomyResponse(0d, 0d, ResponseType.FAILURE, "Player " + player + " not found.");
        }
        var newBalance = BankAccountManager.instance().withdraw(player.getUniqueId(), player.getName(),
                new BigDecimal(amount), null, null);
        return new EconomyResponse(amount, newBalance.doubleValue(), ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        var player = Bukkit.getOfflinePlayer(playerName);
        return withdrawPlayer(player, worldName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        if (player.getLastLogin() == 0) {
            return new EconomyResponse(0d, 0d, ResponseType.FAILURE, "Player " + player + " not found.");
        }
        var newBalance = BankAccountManager.instance().withdraw(player.getUniqueId(), player.getName(),
                new BigDecimal(amount), worldName, null);
        return new EconomyResponse(amount, newBalance.doubleValue(), ResponseType.SUCCESS, null);
    }

    // #region Legacy Banks - Disabled

    @Override
    public boolean hasBankSupport() {
        // We don't support the legacy bank system;
        return false;
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0d, 0d, ResponseType.NOT_IMPLEMENTED, "Legacy banks are not supported.");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0d, 0d, ResponseType.NOT_IMPLEMENTED, "Legacy banks are not supported.");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0d, 0d, ResponseType.NOT_IMPLEMENTED, "Legacy banks are not supported.");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0d, 0d, ResponseType.NOT_IMPLEMENTED, "Legacy banks are not supported.");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0d, 0d, ResponseType.NOT_IMPLEMENTED, "Legacy banks are not supported.");
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0d, 0d, ResponseType.NOT_IMPLEMENTED, "Legacy banks are not supported.");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0d, 0d, ResponseType.NOT_IMPLEMENTED, "Legacy banks are not supported.");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0d, 0d, ResponseType.NOT_IMPLEMENTED, "Legacy banks are not supported.");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0d, 0d, ResponseType.NOT_IMPLEMENTED, "Legacy banks are not supported.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0d, 0d, ResponseType.NOT_IMPLEMENTED, "Legacy banks are not supported.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0d, 0d, ResponseType.NOT_IMPLEMENTED, "Legacy banks are not supported.");
    }

    // #endregion

}
