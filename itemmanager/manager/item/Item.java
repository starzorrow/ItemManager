package me.starzorrow.itemmanager.manager.item;

import me.starzorrow.itemmanager.manager.ItemManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Item {

    private final Material material;
    private String name;
    private List<String> lore;
    private boolean glow;
    private boolean unbreakable;
    private ItemStack item;
    private ItemMeta itemMeta;
    private int amount;
    private String internalName;
    private int[] rgb;
    private double kbRes;

    public Item() {
        this.material = Material.AIR;
    }

    public Item(Material material, String name, List<String> lore, boolean glow, boolean unbreakable, int amount, String internalName, double kbRes) {
        this.material = material;
        this.name = ItemManager.color(name);
        this.lore = ItemManager.color(lore);
        this.glow = glow;
        this.unbreakable = unbreakable;
        this.amount = amount;
        this.internalName = internalName;
        this.item = new ItemStack(material, amount);
        this.itemMeta = this.item.getItemMeta();
        this.itemMeta.setDisplayName(ItemManager.color(name));
        this.itemMeta.setLore(ItemManager.color(lore));
        this.kbRes = kbRes;
        if (glow) {
            this.itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        if (unbreakable) {
            this.itemMeta.spigot().setUnbreakable(true);
        }
        this.item.setItemMeta(this.getItemMeta());
    }
    public Item(Material material, String name, List<String> lore, boolean glow, boolean unbreakable, int amount, String internalName, int[] rgb, double kbRes) {

        this.material = material;
        this.name = ItemManager.color(name);
        this.lore = ItemManager.color(lore);
        this.glow = glow;
        this.unbreakable = unbreakable;
        this.amount = amount;
        this.internalName = internalName;
        this.item = new ItemStack(material, amount);
        this.itemMeta = this.item.getItemMeta();
        this.itemMeta.setDisplayName(ItemManager.color(name));
        this.itemMeta.setLore(ItemManager.color(lore));
        this.rgb = rgb;
        this.kbRes = kbRes;
        if (glow) {
            this.itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        if (unbreakable) {
            this.itemMeta.spigot().setUnbreakable(true);
        }
        this.item.setItemMeta(this.getItemMeta());
    }

    public Material getMaterial() {
        return this.material;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public boolean getGlow() {
        return this.glow;
    }

    public boolean getUnbreakable() {
        return this.unbreakable;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public ItemMeta getItemMeta() {
        return this.itemMeta;
    }

    public int getAmount() {
        return this.amount;
    }

    public String getInternalName() {
        return this.internalName;
    }

    public int[] getRGB() { return this.rgb; }

    public double getKbRes() { return this.kbRes; }
}
