package me.starzorrow.itemmanager;

import me.starzorrow.itemmanager.command.GiveItemCommand;
import me.starzorrow.itemmanager.command.ItemManagerReloadCommand;
import me.starzorrow.itemmanager.manager.ItemManager;
import me.starzorrow.itemmanager.manager.item.Item;
import me.starzorrow.itemmanager.runnable.ItemRunnable;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemManagerMain extends JavaPlugin {

    private ItemManager itemManager;
    private static ItemManagerMain itemManagerMain;
    private BukkitTask itemRunnable;
    private long updateTime;
    private List<String> possibleStats;
    
    public void onEnable() {
        this.itemManager = new ItemManager();
        itemManagerMain = this;
        saveDefaultConfig();

        setupStats();
        setupItems();
        setUpdateTime();
        setItemRunnable();

        getCommand("giveitem").setExecutor(new GiveItemCommand());
        getCommand("giveitem").setTabCompleter(new GiveItemCommand());
        getCommand("imreload").setExecutor(new ItemManagerReloadCommand());
    }

    public void setUpdateTime() {
        if (getConfig().get("update-time") != null) {
            this.updateTime = getConfig().getLong("update-time");
        } else {
            this.updateTime = 5;
        }
    }

    public void setItemRunnable() {
        this.itemRunnable = new ItemRunnable().runTaskTimer(this, 0L, getUpdateTime() * 20);
    }

    public void setupStats() {
        if (getConfig().get("stats") != null) {
            possibleStats = getConfig().getStringList("stats");
        } else {
            possibleStats = new ArrayList<String>(){{
                add("Damage");
                add("Armour");
                add("Dodge");
                add("Block");
                add("Crit Chance");
                add("Crit Damage");
                add("Health");
                add("Health Regen");
                add("Life Steal");
                add("Ignition");
                add("Reflect");
                add("Slow");
                add("Poison");
                add("Wither");
                add("Harming");
                add("Blind");
                add("Speed");
                add("XP Bonus");
                add("PvE Dmg Modifier");
                add("PvP Dmg Modifier");
                add("Level");
                add("Class");
                add("Bound to");
                add("Weapon Speed");
            }};
        }
    }

    public void setupItems() {
        Set<String> set = getConfig().getConfigurationSection("items").getKeys(false);
        for (String configName : set) {
            if (getConfig().get("items." + configName) == null) {
                continue;
            }
            if (getConfig().get("items." + configName + ".material") == null) {
                continue;
            }
            if (getConfig().get("items." + configName + ".lore") == null) {
                continue;
            }
            if (getConfig().get("items." + configName + ".glow") == null) {
                continue;
            }
            if (getConfig().get("items." + configName + ".unbreakable") == null) {
                continue;
            }
            if (getConfig().get("items." + configName + ".display-name") == null) {
                continue;
            }
            if (getConfig().get("items." + configName + ".amount") == null) {
                continue;
            }

            Material material = Material.getMaterial(getConfig().getInt("items." + configName + ".material"));
            List<String> lore = getConfig().getStringList("items." + configName + ".lore");
            boolean glow = getConfig().getBoolean("items." + configName + ".glow");
            boolean unbreakable = getConfig().getBoolean("items." + configName + ".unbreakable");
            String name = getConfig().getString("items." + configName + ".display-name");
            int amount = getConfig().getInt("items." + configName + ".amount");
            
            double kbRes = 0;
            if (getConfig().get("items." + configName + ".kb-res") != null) {
                kbRes = getConfig().getDouble("items." + configName + ".kb-res");
            }

            if (getConfig().get("items." + configName + ".rgb") != null) {
                String[] strRGB = getConfig().getString("items." + configName + ".rgb").split(",");
                int[] rgb = new int[3];

                for (int i = 0; i < 3; i++) {
                    rgb[i] = Integer.parseInt(strRGB[i]);
                }

                getItemManager().addItem(new Item(material, name, lore, glow, unbreakable, amount, configName, rgb, kbRes));
                continue;
            }

            getItemManager().addItem(new Item(material, name, lore, glow, unbreakable, amount, configName, kbRes));
        }
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public List<String> getPossibleStats() {
        return new ArrayList<>(possibleStats);
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public BukkitTask getItemRunnable() {
        return this.itemRunnable;
    }

    public static ItemManagerMain call() {
        return itemManagerMain;
    }
}
