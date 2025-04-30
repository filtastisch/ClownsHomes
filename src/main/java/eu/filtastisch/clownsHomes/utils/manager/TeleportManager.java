package eu.filtastisch.clownsHomes.utils.manager;

import eu.filtastisch.clownsHomes.ClownsHomes;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class TeleportManager {

    private static final Map<UUID, List<TeleportRequest>> pendingRequests = new HashMap<>();
    @Getter
    private static int currentTimestamp = 0;

    public static void startTeleportSchedule() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID target : pendingRequests.keySet()) {
                    List<TeleportRequest> requests = pendingRequests.get(target);
                    List<TeleportRequest> requestsToRemove = new ArrayList<>(List.copyOf(requests));
                    requests.forEach(request -> {
                        if (request.timeStamp + 120 >= currentTimestamp){
                            requestsToRemove.remove(request);
                        }
                    });
                    pendingRequests.put(target, requestsToRemove);
                }
                currentTimestamp++;
            }
        }.runTaskTimerAsynchronously(ClownsHomes.getInstance(), 0, 20);

    }

    public static void addRequest(UUID target, TeleportRequest request) {
        List<TeleportRequest> targetRequests = pendingRequests.get(target);
        List<TeleportRequest> requestsToAdd;
        if (targetRequests == null) {
            requestsToAdd = new ArrayList<>();
        } else {
            requestsToAdd = new ArrayList<>(List.copyOf(targetRequests));
        }
        requestsToAdd.add(request);
        pendingRequests.put(target, requestsToAdd);
    }

    public static void removeRequest(UUID target, TeleportRequest request) {
        pendingRequests.computeIfAbsent(target, k -> List.of()).remove(request);
    }

    public static boolean requestSent(UUID sender, UUID target){
        List<TeleportRequest> requests = pendingRequests.get(sender);
        if (requests == null || requests.isEmpty()) {
            return false;
        }
        return requests.stream().anyMatch(request -> request.player.equals(target));
    }

    public static UUID acceptRandomRequest(Player sender){
        List<TeleportRequest> requests = pendingRequests.get(sender.getUniqueId());
        if (requests == null || requests.isEmpty()) {
            sender.sendMessage("No pending requests.");
            return null;
        }
        TeleportRequest request = requests.getLast();
        acceptRequestOf(sender, request.player);
        return request.player;
    }

    /**
     * @param sender should be the player, that executes the command
     * @param target should be the player, that is given in the command
     */
    public static void acceptRequestOf(Player sender, UUID target){
        //TODO something's buggy in here, I can feel it
        List<TeleportRequest> requests = pendingRequests.get(target);
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
        if (requests == null || requests.isEmpty()) {
            sender.sendMessage("No pending requests for " + targetPlayer.getName());
            return;
        }
        if (requests.stream().anyMatch(request -> request.player.equals(target))){
            if (!targetPlayer.isOnline()){
                sender.sendMessage(targetPlayer.getName() + " is not online!");
                return;
            }
            ((Player) targetPlayer).setVelocity(new Vector(0, 0, 0));
            ((Player) targetPlayer).teleport(sender.getLocation());
            sender.sendMessage("Teleported to " + targetPlayer.getName());
        }
    }

    public record TeleportRequest(UUID player, int timeStamp) {}

}
