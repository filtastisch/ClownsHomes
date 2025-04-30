package eu.filtastisch.clownsHomes.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import eu.filtastisch.clownsHomes.ClownsHomes;
import eu.filtastisch.clownsHomes.utils.manager.HomeManager;
import eu.filtastisch.clownsHomes.utils.types.Home;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class HomeCommand {

    public HomeCommand() {
        new CommandAPICommand("home")
                .withAliases("h", "clownshome")
                .withArguments(new GreedyStringArgument("Home Name")
                        .replaceSuggestions(
                                ArgumentSuggestions.stringsAsync((info -> {
                                    if (info.sender() instanceof Player player) {
                                        return CompletableFuture.supplyAsync(() -> getHomeNames(player));
                                    }
                                    return CompletableFuture.supplyAsync(this::getHomeNames);
                                }))
                        )
                )
                .executesPlayer((player, args) -> {
                    String homeNameRaw = (String) args.getOrDefault("Home Name", "bed");
                    if (homeNameRaw.equalsIgnoreCase("bed")) {
                        return;
                    }

                    player.teleport(HomeManager.getHome(player.getUniqueId(), homeNameRaw).getLocation());
                    player.sendMessage("teleported to " + homeNameRaw);
                    //TODO send message to player
                })
                .register("clownshomes");
    }

    private String[] getHomeNames(Player player) {
        List<String> homes = Arrays
                .stream(HomeManager.getHomes(player.getUniqueId()))
                .map(Home::getName).collect(Collectors.toList());
        homes.add("bed");
        return homes.toArray(new String[0]);
    }

    private String[] getHomeNames() {
        List<String> homesNames = new ArrayList<>();
        HomeManager.homes.forEach((uuid, homes) -> {
            for (Home home : homes) {
                homesNames.add(Bukkit.getOfflinePlayer(home.getOwner()).getName() + ":" + home.getName());
            }
        });
        return homesNames.toArray(new String[0]);
    }

}
