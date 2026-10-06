package org.unitedlands.economy.commands;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;

@UnitedCommand (
    name = "ulmoney",
    aliases = { "money", "ulm" },
    permission = "united.economy.admin"
)
public class CmdMoney implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

    }

}
