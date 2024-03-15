package main.java.elementalmp4.service;

import main.java.elementalmp4.GlobalConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;

public class MerchantService {

    private static final List<MerchantRecipe> BILLY_RECIPES = new ArrayList<>();

    static {
        //Elytra
        MerchantRecipe elytra = new MerchantRecipe(new ItemStack(Material.ELYTRA), 100);
        elytra.addIngredient(new ItemStack(Material.DIAMOND, 32));
        elytra.addIngredient(new ItemStack(Material.NETHERITE_INGOT, 1));
        BILLY_RECIPES.add(elytra);

        //Firework Rockets
        MerchantRecipe rockets = new MerchantRecipe(new ItemStack(Material.FIREWORK_ROCKET, 32), 100);
        rockets.addIngredient(new ItemStack(Material.IRON_INGOT, 20));
        BILLY_RECIPES.add(rockets);

        //Netherite
        MerchantRecipe netherite = new MerchantRecipe(new ItemStack(Material.NETHERITE_INGOT), 100);
        netherite.addIngredient(new ItemStack(Material.EMERALD, 16));
        BILLY_RECIPES.add(netherite);

        //Unbreaking 3
        ItemStack unbreakingBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta unbreakingMeta = (EnchantmentStorageMeta) unbreakingBook.getItemMeta();
        unbreakingMeta.addStoredEnchant(Enchantment.DURABILITY, 3, true);
        unbreakingBook.setItemMeta(unbreakingMeta);

        MerchantRecipe unbreaking = new MerchantRecipe(unbreakingBook, 100);
        unbreaking.addIngredient(new ItemStack(Material.EMERALD, 10));
        BILLY_RECIPES.add(unbreaking);

        //Mending
        ItemStack mendingBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta mendingMeta = (EnchantmentStorageMeta) mendingBook.getItemMeta();
        mendingMeta.addStoredEnchant(Enchantment.MENDING, 1, true);
        mendingBook.setItemMeta(mendingMeta);

        MerchantRecipe mending = new MerchantRecipe(mendingBook, 100);
        mending.addIngredient(new ItemStack(Material.EMERALD, 10));
        BILLY_RECIPES.add(mending);
    }

    public static void showSuperSecretShop(Player player) {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.BILLY_ENABLED)) {
            Merchant merchant = Bukkit.createMerchant("Billy");
            merchant.setRecipes(BILLY_RECIPES);
            player.openMerchant(merchant, true);
        }
    }
}