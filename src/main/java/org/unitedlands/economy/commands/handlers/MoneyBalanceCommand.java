package org.unitedlands.economy.commands.handlers;

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

public class MoneyBalanceCommand extends BaseCommandHandler<UnitedEconomy> {

    public MoneyBalanceCommand(UnitedEconomy plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            case 2:
                return new ArrayList<String>(Settings.instance().getCurrencyKeys());
            default:
                break;
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
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

        var balances = BankAccountManager.instance().getBalances(targetPlayer.getUniqueId(), targetPlayer.getName(),
                null);

        Messenger.sendMessage(player, messageProvider.get("messages.balance"),
                Map.of("name", args[0]), messageProvider.get("messages.prefix"));

        for (var set : balances.entrySet()) {
            Messenger.sendMessage(player, set.getKey().getNamePlural() + ": " + String.format(set.getKey().getFormat(), set.getValue()),
                    null, messageProvider.get("messages.prefix"));
        }
    }

}
