package me.starzorrow.itemmanager.command;

import me.starzorrow.itemmanager.ItemManagerMain;
import me.starzorrow.itemmanager.manager.item.Item;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GiveItemCommand implements CommandExecutor, TabCompleter {

    private final String permission = "itemmanager.use";
    private final String givePermission = "itemmanager.give";
    private final String itemInvalid = ChatColor.RED + "There is no configured item with that name";
    private final String itemGiven = ChatColor.GREEN + "You were given %item%";
    private final String itemGave = ChatColor.GREEN + "You gave %item% to %player%";
    private final String noPermission = ChatColor.RED + "You do not have permission to use this.";
    private final String playerNotFound = ChatColor.RED + "%player% is currently offline.";
    private final String noConsole = ChatColor.RED + "Only /giveitem <itemName> <playerName> can be run from console, not /giveitem <itemName>";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(noPermission);
            return true;
        }

        switch (args.length) {
            case 2: {
                if (!(sender instanceof ConsoleCommandSender)) {
                    if (!sender.hasPermission(givePermission)) {
                        sender.sendMessage(noPermission);
                        break;
                    }
                    commandMethod(sender, args);
                    break;
                }
                commandConsoleMethod(Bukkit.getConsoleSender(), args);
                break;
            }
            case 1: {
                if (!(sender instanceof ConsoleCommandSender)) {
                    if (!sender.hasPermission(givePermission)) {
                        sender.sendMessage(noPermission);
                        break;
                    }
                    commandMethod(sender, args);
                    break;
                }
                sender.sendMessage(noConsole);
                break;
            }
            default: {
                helpMenu(sender);
            }
        }

        return true;
    }

    private void commandConsoleMethod(ConsoleCommandSender sender, String[] args) {
        String itemName = args[0];
        String playerName = args[1];
        Player target;
        boolean found = false;
        if (Bukkit.getPlayer(playerName) == null) {
            sender.sendMessage(playerNotFound.replace("%player%", playerName));
            return;
        }
        target = Bukkit.getPlayer(playerName);

        for (Item item : ItemManagerMain.call().getItemManager().getItemStackList()) {
            if (!item.getInternalName().equalsIgnoreCase(itemName)) {
                continue;
            }
            found = true;

            target.getInventory().addItem(ItemManagerMain.call().getItemManager().giveItem(item));
            break;
        }
        sender.sendMessage(found ? itemGave
                .replace("%item%", itemName)
                .replace("%player%", playerName)
                : itemInvalid);
    }

    private void commandMethod(CommandSender sender, String[] args) {
        String itemName = args[0];
        String playerName = "";
        Player target = (Player) sender;
        boolean found = false;

        if (args.length == 2) {
            playerName = args[1];

            if (Bukkit.getPlayer(playerName) == null) {
                sender.sendMessage(playerNotFound.replace("%player%", playerName));
                return;
            }
            target = Bukkit.getPlayer(playerName);
        }

        for (Item item : ItemManagerMain.call().getItemManager().getItemStackList()) {
            if (!item.getInternalName().equalsIgnoreCase(itemName)) {
                continue;
            }
            found = true;

            target.getInventory().addItem(ItemManagerMain.call().getItemManager().giveItem(item));
            break;
        }
        if (args.length == 2) {
            sender.sendMessage(found ? itemGave
                    .replace("%item%", itemName)
                    .replace("%player%", playerName)
                    : itemInvalid);
        } else {
            sender.sendMessage(found ? itemGiven.replace("%item%", itemName) : itemInvalid);
        }
    }

    private void helpMenu(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender = Bukkit.getConsoleSender();
        }
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(noPermission);
        }
        if (!sender.hasPermission(givePermission)) {
            sender.sendMessage(noPermission);
        }
        sender.sendMessage(ChatColor.RED + "=== GiveItem Usage ===");
        sender.sendMessage(ChatColor.RED + "Usage: /giveitem <itemName> (playerName)");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> items = new ArrayList<>();
        for (Item item : ItemManagerMain.call().getItemManager().getItemStackList()) {
            items.add(item.getInternalName());
        }
        return items;
    }
}
