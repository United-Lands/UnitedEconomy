package org.unitedlands.economy.commands.handlers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.economy.Settings;
import org.unitedlands.economy.UnitedEconomy;
import org.unitedlands.economy.managers.BankAccountManager;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.utils.Messenger;

public class MoneyGiveCommand extends BaseCommandHandler<UnitedEconomy> {

    public MoneyGiveCommand(UnitedEconomy plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            case 3:
                return new ArrayList<String>(Settings.instance().getCurrencyKeys());
            default:
                break;
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length < 2 || args.length > 3) {
            // TODO: usage
            return;
        }

        var player = (Player) sender;
        var targetPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (targetPlayer.getLastLogin() == 0) {
            Messenger.sendMessage(player, messageProvider.get("messages.errors.player-not-found"),
                    Map.of("name", args[0]), messageProvider.get("messages.prefix"));
            return;
        }

        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(args[1]);
        } catch (Exception ex) {
            Messenger.sendMessage(player, messageProvider.get("messages.errors.wrong-number-format"),
                    Map.of("input", args[1]), messageProvider.get("messages.prefix"));
            return;
        }

        var currency = Settings.instance().getDefaultCurrency();
        if (args.length == 3) {
            if (Settings.instance().getCurrencyKeys().contains(args[2]))
                currency = Settings.instance().getCurrency(args[2]);
        }

        var newBalance = BankAccountManager.instance().deposit(targetPlayer.getUniqueId(), targetPlayer.getName(), amount,
                null, currency.getKey());

        Messenger.sendMessage(player, messageProvider.get("messages.give"),
                Map.of("name", args[0],
                    "amount", String.format(currency.getFormat(), amount),
                    "balance", String.format(currency.getFormat(), newBalance)
                ), messageProvider.get("messages.prefix"));
    }

}
