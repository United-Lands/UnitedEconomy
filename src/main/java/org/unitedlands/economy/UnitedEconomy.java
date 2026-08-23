package org.unitedlands.economy;

import java.util.Objects;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.economy.commands.MoneyCommand;
import org.unitedlands.economy.economy.ULEconomy;
import org.unitedlands.economy.economy.ULEconomyLegacy;
import org.unitedlands.economy.listeners.ServerEventListener;
import org.unitedlands.economy.managers.BankAccountManager;
import org.unitedlands.economy.managers.DatabaseManager;
import org.unitedlands.economy.utils.EconomyActivityLogger;
import org.unitedlands.economy.utils.MessageProvider;
import org.unitedlands.utils.Logger;

import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.logger.NullLogBackend;

import net.milkbowl.vault2.economy.Economy;

@SuppressWarnings({ "deprecation" })
public class UnitedEconomy extends JavaPlugin {

    MessageProvider messageProvider;
    Settings settings;
    DatabaseManager databaseManager;
    BankAccountManager bankAccountManager;

    @Override
    public void onEnable() {

        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        saveDefaultConfig();

        getServer().getServicesManager().register(
                Economy.class,
                new ULEconomy(),
                this,
                ServicePriority.Normal);

        getServer().getServicesManager().register(
                net.milkbowl.vault.economy.Economy.class,
                new ULEconomyLegacy(),
                this,
                ServicePriority.Normal);

        Logger.log("UnitedLandsEconomy registered with VaultUnlocked", "UnitedEconomy");

        messageProvider = new MessageProvider(getConfig());
        
        settings = new Settings(this);
        settings.reloadConfig();

        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        bankAccountManager = new BankAccountManager(this, messageProvider);

        var moneyCommands = new MoneyCommand(this, messageProvider);
        Objects.requireNonNull(getCommand("ulmoney")).setExecutor(moneyCommands);
        Objects.requireNonNull(getCommand("ulmoney")).setTabCompleter(moneyCommands);

        EconomyActivityLogger.init(getDataFolder().toPath().resolve("logs"));
        EconomyActivityLogger.log("Plugin enabled.");

        getServer().getPluginManager().registerEvents(new ServerEventListener(this), this);

        Logger.log("UnitedEconomy initialized.", "UnitedEconomy");
    }

    public void onDisable() {
        getServer().getServicesManager().unregisterAll(this);
        DatabaseManager.instance().close();
    }

}
