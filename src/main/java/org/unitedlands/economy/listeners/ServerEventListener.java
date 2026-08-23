package org.unitedlands.economy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.unitedlands.economy.UnitedEconomy;
import org.unitedlands.economy.managers.BankAccountManager;


public class ServerEventListener implements Listener {

    @SuppressWarnings("unused")
    private final UnitedEconomy plugin;

    public ServerEventListener(UnitedEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        BankAccountManager.instance().loadDataFromDatabase();
    }

}
