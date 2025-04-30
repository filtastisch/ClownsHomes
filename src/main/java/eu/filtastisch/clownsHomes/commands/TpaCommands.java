package eu.filtastisch.clownsHomes.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import eu.filtastisch.clownsHomes.ClownsHomes;
import eu.filtastisch.clownsHomes.utils.manager.TeleportManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class TpaCommands {

    public TpaCommands() {
        this.registerTpaCommand();
        this.registerTpahereCommand();
        this.registerTpacceptCommand();
    }

    public void registerTpaCommand() {
        new CommandAPICommand("tpa")
                .withArguments(new PlayerArgument("Target player"))
                .executesPlayer((player, args) -> {
                    Player target = (Player) args.get("Target player");
                    this.sendTeleportRequest(player, target, TpaType.TPA);
                })
                .register("clownshomes");
    }

    public void registerTpahereCommand() {
        new CommandAPICommand("tpahere")
                .withArguments(new PlayerArgument("Target player"))
                .executesPlayer((player, args) -> {
                    Player target = (Player) args.get("Target player");
                    this.sendTeleportRequest(target, player, TpaType.TPA_HERE);
                })
                .register("clownshomes");
    }

    public void registerTpacceptCommand() {
        new CommandAPICommand("tpaccept")
                .withOptionalArguments(new PlayerArgument("Target player"))
                .executesPlayer((player, args) -> {
                    Player target = (Player) args.get("Target player");
                    if (target == null) {
                        UUID targetPlayer = TeleportManager.acceptRandomRequest(player);
                        if (targetPlayer == null) {return;}
                        player.sendMessage("You accepted the teleport request of " + Objects.requireNonNull(ClownsHomes.getInstance().getServer().getPlayer(targetPlayer)).getName());
                    } else {
                        TeleportManager.acceptRequestOf(player, target.getUniqueId());
                        player.sendMessage("You accepted the teleport request of " + target.getName());
                    }
                })
                .register("clownshomes");
    }

    private void sendTeleportRequest(Player player, Player target, TpaType type) {
        if (target == null) {
            player.sendMessage("target player is does not exist");
            return;
        }
        if (target.equals(player)) {
            player.sendMessage("you cannot teleport to yourself");
            return;
        }
        if (!target.isOnline()) {
            player.sendMessage("target player is not online");
            return;
        }
        if (TeleportManager.requestSent(player.getUniqueId(), target.getUniqueId())){
            player.sendMessage("You already have a pending teleport request to " + target.getName());
            return;
        }
        player.sendMessage("teleport request sent to " + target.getName());
        target.sendMessage(getTpaMessage(player, type));
        TeleportManager.addRequest(player.getUniqueId(), new TeleportManager.TeleportRequest(target.getUniqueId(), TeleportManager.getCurrentTimestamp()));
    }

    private Component getTpaMessage(Player player, TpaType type) {
        String tpaType = switch (type){
            case TPA -> "wants to teleport to you";
            case TPA_HERE -> "wants you to teleport to them";
        };

        return Component.text(player.getName())
                .appendSpace()
                .append(Component.text(tpaType))
                .appendSpace()
                .append(Component.text("/tpaccept " + player.getName())
                        .clickEvent(ClickEvent
                                .clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tpaccept " + player.getName())
                        )
                        .hoverEvent(HoverEvent
                                .hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("click to type this command"))
                        )
                )
                .appendSpace()
                .append(Component.text("to accept"));
    }

    private enum TpaType {
        TPA,
        TPA_HERE;
    }

}
