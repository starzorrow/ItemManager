package me.starzorrow.itemmanager.manager;

import me.starzorrow.itemmanager.manager.item.Item;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemManager {

    private final List<Item> itemStackList;
    public static Pattern loreRanges;
    public static Pattern loreRanges1;
    public static Random r;

    public ItemManager() {
        this.itemStackList = new ArrayList<>();
        loreRanges = Pattern.compile("\\{([0-9]*)-([0-9]*)\\}");
        loreRanges1 = Pattern.compile("([0-9]*)-([0-9]*)");

        r = new Random();
    }

    public void addItem(Item item) {
        itemStackList.add(item);
    }

    public void removeItem(Item item) {
        itemStackList.remove(item);
    }

    public void clear() {
        itemStackList.clear();
    }

    public Item getItem(ItemStack item) {
        Item returnedItem = new Item();
        if (item == null || item.getType() == Material.AIR) {
            return returnedItem;
        }

        for (Item compare : itemStackList) {
            if (!compare.getName().equals(item.getItemMeta().getDisplayName())) {
                continue;
            }
            returnedItem = compare;
            break;
        }
        return returnedItem;
    }

    public ItemStack giveItem(Item item) {
        ItemStack itemStack = item.getItem().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(itemMeta.getLore());

        if (item.getRGB() != null) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            leatherArmorMeta.setColor(Color.fromBGR(item.getRGB()[0], item.getRGB()[1], item.getRGB()[2]));
            leatherArmorMeta.setLore(replaceLore(lore));
            itemStack.setItemMeta(leatherArmorMeta);

            if (item.getKbRes() != 0) {
                return applyAttributes(itemStack, "generic.knockbackResistance", item.getKbRes(), 0, 894654, 2872);
            }

            return itemStack;
        }

        itemMeta.setLore(replaceLore(lore));
        itemStack.setItemMeta(itemMeta);

        if (item.getKbRes() != 0) {
            return applyAttributes(itemStack, "generic.knockbackResistance", item.getKbRes(), 0, 894654, 2872);
        }

        return itemStack;
    }

    public List<Item> getItemStackList() {
        return itemStackList;
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> color(List<String> lore) {
        List<String> cLore = new ArrayList<>();
        for(String s : lore){
            cLore.add(color(s));
        }
        return cLore;
    }

    public static List<String> replaceLore(List<String> lore) {
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            if (line.contains("{")) {
                Matcher pMatcher = loreRanges.matcher(line);
                while (pMatcher.find()) {
                    int min = Integer.parseInt(pMatcher.group(1));
                    int max = Integer.parseInt(pMatcher.group(2)) + 1;
                    int num = r.nextInt(max - min) + min;
                    line = line.replace(pMatcher.group(0), "" + num);
                }
            }
            newLore.add(line);
        }
        return newLore;
    }

    public static boolean canReplace(String line) {
        if (line.contains("{")) {
            Matcher pMatcher = loreRanges.matcher(line);
            return pMatcher.find();
        }
        return false;
    }

    public static String replaceLore(String line) {
        if (line.contains("{")) {
            Matcher pMatcher = loreRanges.matcher(line);
            while (pMatcher.find()) {
                int min = Integer.parseInt(pMatcher.group(1));
                int max = Integer.parseInt(pMatcher.group(2)) + 1;
                int num = r.nextInt(max - min) + min;
                line = line.replace(pMatcher.group(0), "" + num);
            }
        }
        return line;
    }

    /**
     * Apply attributes based on user input.
     *
     * Uses NMS, and i want to die...
     *
     * @param item returns item to be used with nms
     * @param attributeName returns attribute name
     * @param attribute returns attribute amount
     * @param operation returns math operation used used (addition, multiply, etc)
     * @param least returns least effective UUID
     * @param most returns most effective UUID
     * @return item with nms applied
     */
    public ItemStack applyAttributes(ItemStack item, String attributeName, double attribute, int operation, int least, int most) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
        NBTTagList modifiers = new NBTTagList();
        NBTTagCompound kbRes = new NBTTagCompound();

        kbRes.set("AttributeName", new NBTTagString(attributeName));
        kbRes.set("Name", new NBTTagString(attributeName));
        kbRes.set("Amount", new NBTTagDouble(attribute));
        kbRes.set("Operation", new NBTTagInt(operation));
        kbRes.set("UUIDLeast", new NBTTagInt(least));
        kbRes.set("UUIDMost", new NBTTagInt(most));

        modifiers.add(kbRes);
        compound.set("AttributeModifiers", modifiers);
        nmsStack.setTag(compound);

        item = CraftItemStack.asBukkitCopy(nmsStack);
        return item;
    }
}