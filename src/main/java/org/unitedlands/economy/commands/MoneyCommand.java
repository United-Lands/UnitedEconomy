package org.unitedlands.economy.commands;

import org.unitedlands.classes.BaseCommandExecutor;
import org.unitedlands.economy.UnitedEconomy;
import org.unitedlands.economy.commands.handlers.MoneyBalanceCommand;
import org.unitedlands.economy.commands.handlers.MoneyGiveCommand;
import org.unitedlands.economy.commands.handlers.MoneySetCommand;
import org.unitedlands.economy.commands.handlers.MoneyTakeCommand;
import org.unitedlands.interfaces.IMessageProvider;

public class MoneyCommand extends BaseCommandExecutor<UnitedEconomy> {

    public MoneyCommand(UnitedEconomy plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    protected void registerHandlers() {
        handlers.put("set", new MoneySetCommand(plugin, messageProvider));
        handlers.put("take", new MoneyTakeCommand(plugin, messageProvider));
        handlers.put("give", new MoneyGiveCommand(plugin, messageProvider));
        handlers.put("balance", new MoneyBalanceCommand(plugin, messageProvider));
    }

}
