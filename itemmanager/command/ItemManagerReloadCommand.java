package me.starzorrow.itemmanager.command;

import me.starzorrow.itemmanager.ItemManagerMain;
import me.starzorrow.itemmanager.runnable.ItemRunnable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;

public class ItemManagerReloadCommand implements CommandExecutor {

    private final String noPermission = ChatColor.RED + "You do not have permission to use this.";
    private final String configReloaded = ChatColor.GREEN + "The config has been reloaded.";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String permission = "itemmanager.reload";
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(noPermission);
        }
        if (!(sender instanceof ConsoleCommandSender)) {
            reloadMethod(sender);
            return true;
        }
        reloadMethod(Bukkit.getConsoleSender());
        return true;
    }

    private void reloadMethod(CommandSender sender) {
        ItemManagerMain.call().reloadConfig();
        ItemManagerMain.call().getItemManager().clear();
        ItemManagerMain.call().setupStats();
        ItemRunnable.possibleStats = new ArrayList<>(ItemManagerMain.call().getPossibleStats());
        ItemManagerMain.call().setupItems();
        ItemManagerMain.call().setUpdateTime();
        ItemManagerMain.call().getItemRunnable().cancel();
        ItemManagerMain.call().setItemRunnable();

        sender.sendMessage(configReloaded);
    }
}
