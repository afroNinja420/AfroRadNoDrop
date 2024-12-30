package me.afroninja.afroRadNoDrop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AfroRadNoDrop extends JavaPlugin implements Listener {

    private static final String RELOAD_PERMISSION = "afroradnodrop.reload";
    private List<String> permittedWorlds;
    private Boolean deleteOnDrop;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfiguration();

        Plugin slimefunPlugin = Bukkit.getPluginManager().getPlugin("Slimefun");
        if (slimefunPlugin == null || !slimefunPlugin.isEnabled()) {
            getLogger().severe("Slimefun is not loaded! Disabling AfroRadNoDrop.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Slimefun detected! AfroRadNoDrop is integrating with Slimefun.");

        Bukkit.getPluginManager().registerEvents(this, this);

        getLogger().info(ChatColor.AQUA + "AfroRadNoDrop " + ChatColor.GREEN + "Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.AQUA + "AfroRadNoDrop " + ChatColor.RED + "Disabled!");
    }

    private void loadConfiguration() {
        FileConfiguration config = getConfig();
        permittedWorlds = config.getStringList("permitted-worlds");
        if (permittedWorlds == null) permittedWorlds = List.of();
        deleteOnDrop = config.getBoolean("delete-on-drop", false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("afroradnodrop")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission(RELOAD_PERMISSION)) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
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

        assert lore != null;
        for (String s : lore) {
            getLogger().info(s);
            if (s.contains("Radiation level:")){
                if (!permittedWorlds.contains(playerWorld.getName())) {
                    if (deleteOnDrop) {
                        event.getItemDrop().remove();
                        event.getPlayer().sendMessage(ChatColor.RED + "The radioactive item has been destroyed!");
                        getLogger().info("Radioactive item destroyed for player " + event.getPlayer().getName() + " in world " + playerWorld.getName());
                    } else {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "You cannot drop radioactive items in this world!");
                        getLogger().info("Radioactive item drop canceled for player " + event.getPlayer().getName() + " in world " + playerWorld.getName());
                    }
                }
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
