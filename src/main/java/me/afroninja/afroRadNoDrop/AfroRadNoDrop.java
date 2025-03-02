package me.afroninja.afroRadNoDrop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AfroRadNoDrop extends JavaPlugin implements Listener {

    private static final String RELOAD_PERMISSION = "afroradnodrop.reload";
    private static final String RADIATION_CHECK = "Radiation level:";
    private Set<String> permittedWorlds;
    private Boolean deleteOnDrop;
    private Boolean deleteOnDeath;
    private HashMap<String, String> messages = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            loadConfiguration();
        } catch (Exception e) {
            getLogger().severe("Failed to load configuration: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Plugin slimefunPlugin = Bukkit.getPluginManager().getPlugin("Slimefun");
        if (slimefunPlugin == null || !slimefunPlugin.isEnabled()) {
            getLogger().severe("Slimefun is not loaded! Disabling AfroRadNoDrop.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Slimefun detected! AfroRadNoDrop is integrating with Slimefun.");

        if (permittedWorlds.isEmpty()) {
            getLogger().warning("No permitted worlds defined in config!");
        }

        Bukkit.getPluginManager().registerEvents(this, this);

        getLogger().info(ChatColor.AQUA + "AfroRadNoDrop " + ChatColor.GREEN + "Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.AQUA + "AfroRadNoDrop " + ChatColor.RED + "Disabled!");
    }

    private void loadConfiguration() {
        FileConfiguration config = getConfig();
        permittedWorlds = new HashSet<>(config.getStringList("permitted-worlds"));
        messages.put("noDrop", config.getString("messages.noDrop", "&cYou cannot drop radioactive items here!"));
        messages.put("itemDestroyed", config.getString("messages.itemDestroyed", "&cRadioactive item destroyed!"));
        messages.put("noPermission", config.getString("messages.noPermission", "&cNo permission!"));
        deleteOnDrop = config.getBoolean("delete-on-drop", false);
        deleteOnDeath = config.getBoolean("delete-on-death", true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("afroradnodrop")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission(RELOAD_PERMISSION)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.get("noPermission")));
                    return true;
                }
                reloadConfig();
                loadConfiguration();
                sender.sendMessage(ChatColor.AQUA + "AfroRadNoDrop config has been reloaded!");
                getLogger().info("Configuration reloaded by " + sender.getName());
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        World playerWorld = event.getPlayer().getWorld();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        ItemMeta droppedItemMeta = droppedItem.getItemMeta();

        List<String> lore = droppedItemMeta.hasLore() ? droppedItemMeta.getLore() : new ArrayList<>();
        if (lore == null) lore = new ArrayList<>();

        for (String s : lore) {
            getLogger().info(s);
            if (s.contains(RADIATION_CHECK)) {
                if (!permittedWorlds.contains(playerWorld.getName())) {
                    if (deleteOnDrop) {
                        event.getItemDrop().remove();
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.get("itemDestroyed")));
                        getLogger().info("Radioactive item destroyed for player " + event.getPlayer().getName() + " in world " + playerWorld.getName());
                    } else {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.get("noDrop")));
                        getLogger().info("Radioactive item drop canceled for player " + event.getPlayer().getName() + " in world " + playerWorld.getName());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        World playerWorld = event.getEntity().getWorld();
        if (!permittedWorlds.contains(playerWorld.getName()) && deleteOnDeath) {
            List<ItemStack> drops = event.getDrops();
            List<ItemStack> toRemove = new ArrayList<>();

            for (ItemStack item : drops) {
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                if (lore == null) lore = new ArrayList<>();

                for (String s : lore) {
                    if (s.contains(RADIATION_CHECK)) {
                        toRemove.add(item);
                        getLogger().info("Radioactive item removed from death drops for player " + event.getEntity().getName() + " in world " + playerWorld.getName());
                        break;
                    }
                }
            }

            if (!toRemove.isEmpty()) {
                drops.removeAll(toRemove);
                event.getEntity().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.get("itemDestroyed")));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("afroradnodrop")) {
            if (args.length == 1) {
                return List.of("reload");
            }
        }
        return Collections.emptyList();
    }
}