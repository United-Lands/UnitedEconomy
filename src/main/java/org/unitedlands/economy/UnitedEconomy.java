package org.unitedlands.economy;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.economy.economy.ULEconomy;
import org.unitedlands.economy.economy.ULEconomyLegacy;
import org.unitedlands.economy.listeners.ServerEventListener;
import org.unitedlands.economy.managers.BankAccountManager;
import org.unitedlands.economy.managers.DatabaseManager;
import org.unitedlands.economy.utils.EconomyActivityLogger;
import org.unitedlands.libs.ormlite.logger.LoggerFactory;
import org.unitedlands.libs.ormlite.logger.NullLogBackend;
import org.unitedlands.utils.United;

import net.milkbowl.vault2.economy.Economy;

@SuppressWarnings({ "deprecation" })
public class UnitedEconomy extends JavaPlugin {

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

        United.logger().info("UnitedLandsEconomy registered with VaultUnlocked");

        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        bankAccountManager = new BankAccountManager();

        EconomyActivityLogger.init(getDataFolder().toPath().resolve("logs"));
        EconomyActivityLogger.log("Plugin enabled.");

        getServer().getPluginManager().registerEvents(new ServerEventListener(this), this);

        United.logger().info("UnitedEconomy initialized.");
    }

    public void onDisable() {
        getServer().getServicesManager().unregisterAll(this);
        DatabaseManager.instance().close();
    }

}
