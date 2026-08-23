package org.unitedlands.economy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.unitedlands.economy.classes.Currency;
import org.unitedlands.utils.Logger;

public class Settings {

    private static Settings instance;

    public static Settings instance() {
        return instance;
    }

    private final UnitedEconomy plugin;

    private Map<String, Currency> currencies = new HashMap<>();
    private String defaultCurrency;
    private String defaultWorldName;
    private int roundingDigits;

    public Settings(UnitedEconomy plugin) {
        this.plugin = plugin;
        instance = this;

        reloadConfig();
    }

    public void reloadConfig() {

        var config = plugin.getConfig();

        currencies = new HashMap<>();

        var currencySection = config.getConfigurationSection("currencies");
        for (var key : currencySection.getKeys(false)) {
            var currencyEntry = currencySection.getConfigurationSection(key);
            var currency = new Currency(
                    key,
                    currencyEntry.getString("symbol", "X"),
                    currencyEntry.getString("format", "%sX"),
                    currencyEntry.getString("singular", "X"),
                    currencyEntry.getString("plural", "X"));
            currencies.put(key, currency);
        }

        defaultCurrency = config.getString("default-currency");
        defaultWorldName = config.getString("default-world-name");
        Logger.log(defaultWorldName);
        roundingDigits = config.getInt("rounding-digits", 2);
    }

    public Collection<Currency> getCurrencies() {
        return currencies.values();
    }

    public Collection<String> getCurrencyKeys() {
        return currencies.keySet();
    }

    
    public Currency getCurrency(String key) {
        return currencies.get(key);
    }

    public Currency getDefaultCurrency() {
        return currencies.get(defaultCurrency);
    }

    public String getDefaultWorldName() {
        return defaultWorldName;
    }

    public int getRoundingDigits() {
        return roundingDigits;
    }

}
