package org.unitedlands.economy.commands.handlers;

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

@UnitedSubCommand (
    parent = CmdMoney.class,
    name = "balance",
    usage = "/ulm balance <account_holder>",
    aliases = { "bal" }
)
public class CmdMoneyBalance implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            case 2:
                return new ArrayList<String>(UnitedEconomyConfig.get().currencies().keys());
            default:
                break;
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var player = (Player) sender;
        var targetPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (targetPlayer.getLastLogin() == 0) {
            United.messenger().send(player, "errors.player-not-found", args[0]);
            return;
        }

        var balances = BankAccountManager.instance().getBalances(targetPlayer.getUniqueId(), targetPlayer.getName(), null);

        United.messenger().send(player, "balance", args[0]);

        for (var currencyBalance : balances.entrySet()) {
            United.messenger().sendRaw(player, currencyBalance.getKey().plural() + ": " + String.format(currencyBalance.getKey().plural(), currencyBalance.getValue()));
        }
    }

}
