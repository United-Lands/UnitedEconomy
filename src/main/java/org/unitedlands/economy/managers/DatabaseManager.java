package org.unitedlands.economy.managers;

import java.sql.SQLException;

import org.unitedlands.economy.UnitedEconomy;
import org.unitedlands.economy.classes.BankAccount;
import org.unitedlands.economy.classes.BankAccountHolder;
import org.unitedlands.economy.classes.db.BankAccountHolderService;
import org.unitedlands.economy.classes.db.BankAccountService;
import org.unitedlands.economy.classes.db.SchemaVersion;
import org.unitedlands.libs.ormlite.dao.Dao;
import org.unitedlands.libs.ormlite.dao.DaoManager;
import org.unitedlands.libs.ormlite.jdbc.DataSourceConnectionSource;
import org.unitedlands.libs.ormlite.support.ConnectionSource;
import org.unitedlands.libs.ormlite.table.TableUtils;
import org.unitedlands.libs.zaxxer.hikari.HikariConfig;
import org.unitedlands.libs.zaxxer.hikari.HikariDataSource;
import org.unitedlands.utils.United;

public class DatabaseManager {

    private static DatabaseManager instance;

    public static DatabaseManager instance() {
        return instance;
    }

    private final UnitedEconomy plugin;

    private HikariDataSource hikariDataSource;
    private ConnectionSource connectionSource;

    private BankAccountService bankAccountService;
    private BankAccountHolderService bankAccountHolderService;

    public DatabaseManager(UnitedEconomy plugin) {
        this.plugin = plugin;
    }

    public void initialize() {

        instance = this;

        var fileConfig = plugin.getConfig();

        String host = fileConfig.getString("mysql.host");
        int port = fileConfig.getInt("mysql.port");
        String database = fileConfig.getString("mysql.database");
        String username = fileConfig.getString("mysql.username");
        String password = fileConfig.getString("mysql.password");

        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=%s&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                host,
                port,
                database,
                plugin.getConfig().getBoolean("developer-mode") ? "false" : "true");

        try {

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);

            // Connection pool settings
            config.setMaximumPoolSize(24);
            config.setMinimumIdle(2);
            config.setIdleTimeout(600000); // 10 minutes
            config.setMaxLifetime(1800000); // 30 minutes
            config.setConnectionTimeout(5000); // 5 seconds
            config.setValidationTimeout(3000); // 3 seconds

            // Validation query
            config.setConnectionTestQuery("SELECT 1");
            config.setLeakDetectionThreshold(15000); // Warn if connection held 15+ seconds
            hikariDataSource = new HikariDataSource(config);
            connectionSource = new DataSourceConnectionSource(hikariDataSource, jdbcUrl);

            United.logger().info("Connected to MySQL database with HikariCP.");

            verifySchemaVersion();
            registerServices();

            United.logger().info("DatabaseManager initialized successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void registerServices() throws SQLException {
        bankAccountService = new BankAccountService(getDao(BankAccount.class));
        bankAccountHolderService = new BankAccountHolderService(getDao(BankAccountHolder.class));
    }

    private void verifySchemaVersion() throws SQLException {
        Dao<SchemaVersion, Integer> versionDao = getDao(SchemaVersion.class);
        SchemaVersion version = versionDao.queryForId(1);

        if (version == null) {
            version = new SchemaVersion(1);
            versionDao.create(version);
        }

        applyMigrations(versionDao, version);
    }

    private void applyMigrations(Dao<SchemaVersion, Integer> versionDao, SchemaVersion version) throws SQLException {
        // Example for future migrations on production server

        // if (version.getVersion() < 2) {
        // // Migration 1 → 2: Add new field to `PlayerData`

        // versionDao.executeRaw("ALTER TABLE test_data ADD COLUMN new_field
        // VARCHAR(255) DEFAULT NULL;");

        // version.setVersion(2);
        // versionDao.update(version);
        // }
    }

    public <T, ID> Dao<T, ID> getDao(Class<T> clazz) throws SQLException {

        // In developer mode, drop the table if it exists
        if (plugin.getConfig().getBoolean("developer-mode"))
            TableUtils.dropTable(connectionSource, clazz, true);

        TableUtils.createTableIfNotExists(connectionSource, clazz);
        return DaoManager.createDao(connectionSource, clazz);
    }

    public void close() {
        try {
            if (connectionSource != null) {
                connectionSource.close();
                United.logger().info("Disconnected from MySQL database.");
            }
            if (hikariDataSource != null) {
                hikariDataSource.close();
                United.logger().info("HikariCP connection closed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BankAccountService getBankAccountService() {
        return bankAccountService;
    }

    public BankAccountHolderService getBankAccountHolderService() {
        return bankAccountHolderService;
    }
    
}
