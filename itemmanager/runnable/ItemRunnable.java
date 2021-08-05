package me.starzorrow.itemmanager.runnable;

import me.starzorrow.itemmanager.ItemManagerMain;
import me.starzorrow.itemmanager.manager.ItemManager;
import me.starzorrow.itemmanager.manager.item.Item;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ItemRunnable extends BukkitRunnable {

    private final ItemManager itemManager;
    public static List<String> possibleStats;

    public ItemRunnable() {
        this.itemManager = ItemManagerMain.call().getItemManager();
        possibleStats = ItemManagerMain.call().getPossibleStats();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            checkContents(player.getInventory().getContents());
            checkContents(player.getInventory().getArmorContents());
            checkContents(player.getOpenInventory().getBottomInventory().getContents());
            checkContents(player.getOpenInventory().getTopInventory().getContents());
        }
    }

    private void checkContents(ItemStack[] contents) {
        for (ItemStack itemStack : contents) {
            Item item = itemManager.getItem(itemStack);
            if (item.getMaterial() == Material.AIR) {
                continue;
            }
            if (item.getItem() == itemStack) {
                continue;
            }

            boolean rgb = item.getRGB() != null;

            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> stats = new ArrayList<>();
            List<String> lore = new ArrayList<>(item.getLore());
            String durability = "";

            if (item.getLore() != itemMeta.getLore()) {
                for (String line : itemMeta.getLore()) {
                    if (!ItemManager.canReplace(line)) {
                        if (!line.contains("Durability: "))
                            stats.add(line);
                    }
                    if (line.contains("Durability: ")) {
                        durability = line;
                    }
                }

                for (int i = 0; i < lore.size(); i++) {
                    if (lore.get(i).contains("Durability: ")) {
                        if (!durability.equals(""))
                            lore.set(i, durability);
                    }

                    for (String stat : stats) {
                        if (linesSameStat(stat.replace(ChatColor.COLOR_CHAR, '&'),
                                lore.get(i).replace(ChatColor.COLOR_CHAR, '&'))) {
                            lore.set(i, stat);
                            continue;
                        }
                        if (ItemManager.canReplace(lore.get(i))) {
                            lore.set(i, ItemManager.replaceLore(lore.get(i)));
                        }
                    }
                }

                leatherArmorMeta.setLore(lore);
                itemMeta.setLore(lore);
            }

            if (!item.getGlow()) {
                itemMeta.removeEnchant(Enchantment.DURABILITY);
                leatherArmorMeta.removeEnchant(Enchantment.DURABILITY);
            } else {
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                leatherArmorMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            }

            if (!item.getUnbreakable()) {
                itemMeta.spigot().setUnbreakable(false);
                leatherArmorMeta.spigot().setUnbreakable(false);
            } else {
                itemStack.setDurability((short) 0);
                itemMeta.spigot().setUnbreakable(true);
                leatherArmorMeta.spigot().setUnbreakable(true);
            }

            if (item.getMaterial() != itemStack.getType()) {
                itemStack.setType(item.getMaterial());
            }

            itemMeta.setDisplayName(item.getName());
            leatherArmorMeta.setDisplayName(item.getName());

            if (rgb) {
                leatherArmorMeta.setColor(Color.fromBGR(item.getRGB()[0], item.getRGB()[1], item.getRGB()[2]));

                itemStack.setItemMeta(leatherArmorMeta);
                break;
            }

            itemStack.setItemMeta(itemMeta);
        }
    }

    private boolean linesSameStat(String line, String line1) {
        for (String stat : possibleStats) {
            if (line.endsWith(stat)) {
                if (line1.endsWith(stat)) {
                    return true;
                }
            }
        }
        return false;
    }
}
