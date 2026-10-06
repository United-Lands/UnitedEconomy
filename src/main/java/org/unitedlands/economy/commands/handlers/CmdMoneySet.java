package org.unitedlands.economy.commands.handlers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.economy.classes.config.UnitedEconomyConfig;
import org.unitedlands.economy.commands.CmdMoney;
import org.unitedlands.economy.managers.BankAccountManager;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.utils.United;

@UnitedSubCommand(parent = CmdMoney.class, name = "set", usage = "/ulm set <account_holder> <amount> [currency]")
public class CmdMoneySet implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            case 3:
                return new ArrayList<String>(UnitedEconomyConfig.get().currencies().keys());
            default:
                break;
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length < 2 || args.length > 3) {
            sendUsage(sender);
            return;
        }

        var player = (Player) sender;
        var targetPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (targetPlayer.getLastLogin() == 0) {
            United.messenger().send(player, "errors.player-not-found", args[0]);
            return;
        }

        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(args[1]);
        } catch (Exception ex) {
            United.messenger().send(player, "errors.wrong-number-format", args[1]);
            return;
        }

        var currency = BankAccountManager.instance().getDefaultCurrency();
        if (args.length == 3) {
            if (UnitedEconomyConfig.get().currencies().has(args[2]))
                currency = UnitedEconomyConfig.get().currencies().get(args[2]);
        }

        var newBalance = BankAccountManager.instance().set(targetPlayer.getUniqueId(), targetPlayer.getName(), amount,
                null, args[2]);

        United.messenger().send(player, "set",
                args[0],
                String.format(currency.format(), amount),
                String.format(currency.format(), newBalance));
    }

}
