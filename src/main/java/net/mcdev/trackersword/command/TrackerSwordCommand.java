package net.mcdev.trackersword.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mcdev.trackersword.TrackerSwordItem;
import net.mcdev.trackersword.TrackerSwordPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TrackerSwordCommand implements CommandExecutor, TabCompleter {

    private final TrackerSwordPlugin plugin;

    public TrackerSwordCommand(TrackerSwordPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                              @NotNull String label, String[] args) {
        Player target;
        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Player '" + args[0] + "' is not online.", NamedTextColor.RED));
                return true;
            }
        } else if (sender instanceof Player selfPlayer) {
            target = selfPlayer;
        } else {
            sender.sendMessage(Component.text("Console must specify a player: /trackersword <player>", NamedTextColor.RED));
            return true;
        }

        target.getInventory().addItem(TrackerSwordItem.create(plugin));
        target.sendMessage(Component.text("You received the Tracker Blade!", NamedTextColor.AQUA));
        if (sender != target) {
            sender.sendMessage(Component.text("Gave a Tracker Blade to " + target.getName() + ".", NamedTextColor.GREEN));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                  @NotNull String alias, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
