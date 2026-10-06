package org.unitedlands.economy.classes.config;

import org.unitedlands.annotations.UnitedConfig;
import org.unitedlands.annotations.UnitedSection;
import org.unitedlands.annotations.UnitedSetting;
import org.unitedlands.registrars.config.UnitedConfigHandler;
import org.unitedlands.registrars.config.UnitedConfigs;
import org.unitedlands.registrars.config.UnitedDynamicSection;

@UnitedConfig(file = "config.yml")
public interface UnitedEconomyConfig extends UnitedConfigHandler {
    static UnitedEconomyConfig get() { return UnitedConfigs.get(UnitedEconomyConfig.class); }

    // Einfacher "developer-mode: true/false" in yaml
    @UnitedSetting(key = "developer-mode", def = "false") 
    boolean developerMode();

    @UnitedSection(key = "mysql")
    MysqlSettings mysql();

    record MysqlSettings(
            @UnitedSetting(key = "host",     def = "localhost")     String host,
            @UnitedSetting(key = "port",     def = "3306")          int    port,
            @UnitedSetting(key = "username", def = "unitedlands")   String username,
            @UnitedSetting(key = "password", def = "unitedlands")   String password,
            @UnitedSetting(key = "database", def = "unitedlands")   String database
    ) {}

    @UnitedSetting (key = "use-per-world-accounts", def = "false") 
    boolean userPerWorldAccounts();

    @UnitedSetting (key = "default-world-name", def = "default") 
    String defaultWorldName();
    @UnitedSetting (key = "default-currency", def = "Gold") 
    String defaultCurrency();

    @UnitedSection (key = "currencies")
    UnitedDynamicSection<CurrencyRecord> currencies();

    public record CurrencyRecord(
        @UnitedSetting(key = "symbol",     def = "G")           String symbol,
        @UnitedSetting(key = "format",     def = "%,.2fG")      String format,
        @UnitedSetting(key = "singular",   def = "Gold")        String singular,
        @UnitedSetting(key = "plural",     def = "Gold")        String plural
    ) { }

}