package eu.filtastisch.clownsHomes.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import eu.filtastisch.clownsHomes.utils.manager.HomeManager;
import eu.filtastisch.clownsHomes.utils.types.Home;

import java.util.UUID;

public class SetHomeCommand {

    public SetHomeCommand() {
        new CommandAPICommand("sethome")
                .withArguments(new GreedyStringArgument("Home Name"))
                .executesPlayer((player, args) -> {
                    String homeName = (String) args.get("Home Name");
                    if (homeName == null) {
                        player.sendMessage("home name is null");
                        return;
                    }
                    if (homeName.equalsIgnoreCase("bed")) {
                        player.sendMessage("bed is a reserved name");
                        return;
                    }
                    if (HomeManager.getHomes(player.getUniqueId()) != null) {
                        if (HomeManager.getHome(player.getUniqueId(), homeName) != null) {
                            player.sendMessage("home already exists");
                            return;
                        }
                    }
                    HomeManager.addHome(new Home(UUID.randomUUID(), homeName, player.getLocation(), player.getUniqueId()), false);
                    player.sendMessage("home set");
                    //TODO make messages nice
                }).register("clownshomes");
    }

}
