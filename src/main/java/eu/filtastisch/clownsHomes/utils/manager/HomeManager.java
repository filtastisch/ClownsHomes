package eu.filtastisch.clownsHomes.utils.manager;

import eu.filtastisch.clownsHomes.ClownsHomes;
import eu.filtastisch.clownsHomes.utils.CLogger;
import eu.filtastisch.clownsHomes.utils.types.Home;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

public class HomeManager {

    @Getter
    public static Map<UUID, Home[]> homes = new HashMap<>();

    private static void addHome(Home home) {
        Home[] homeArray = HomeManager.homes.computeIfAbsent(home.getOwner(), k -> new Home[]{home});
        List<Home> homesList = new ArrayList<>(List.of(homeArray));
        homesList.add(home);
        Home[] newHomes = homesList.toArray(new Home[0]);

        homes.put(home.getOwner(), newHomes);
    }

    public static void addHome(Home home, boolean config) {
        addHome(home);
        if (!config) return;
        ClownsHomes.getInstance().getHomesConfig().addHome(home);
    }

    private static void removeHome(Home home) {
        Home[] homeArray = HomeManager.homes.get(home.getOwner());
        if (homeArray != null) {
            Home[] newHomes = Arrays.stream(homeArray)
                    .filter(h -> !h.equals(home))
                    .toArray(Home[]::new);

            if (newHomes.length > 0) {
                HomeManager.homes.put(home.getOwner(), newHomes);
            } else {
                HomeManager.homes.remove(home.getOwner());
            }
        }
    }

    public static void removeHome(Home home, boolean config) {
        removeHome(home);
        if (!config) return;
        ClownsHomes.getInstance().getHomesConfig().removeHome(home);
    }

    public static Home[] getHomes(UUID uuid) {
        return HomeManager.homes.get(uuid);
    }

    public static Home getHome(UUID uuid, String name) {
        return Arrays.stream(getHomes(uuid)).filter(home -> home.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static void startHomeUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                CLogger.info(homes.toString());
                CLogger.info("Running home updater");
                Map<UUID, Home[]> homesMap = ClownsHomes.getInstance().getHomesConfig().getAllHomes();
                if (homesMap.isEmpty()) {
                    CLogger.info("No homes found in config, continuing...");
                }
                homes.forEach((uuid, homes) -> {
                    CLogger.info("Checking homes for " + Bukkit.getOfflinePlayer(uuid).getName() + "...");
                    CLogger.info("Found " + homes.length + " homes");
                    CLogger.info(Arrays.toString(homes));
                    Arrays.stream(homes).forEach(home -> {
                        if (home == null) {
                            CLogger.warn("Home is null for UUID: " + uuid);
                            return;
                        }
                        CLogger.info("Found home: " + home.getName());
                        Home[] homeArray = homesMap.getOrDefault(uuid, new Home[]{});
                        if (Arrays.stream(homeArray).noneMatch(h -> h == home)) {
                            ClownsHomes.getInstance().getHomesConfig().addHome(home);
                            CLogger.info(home.getName() + " not found in config, added");
                            return;
                        }
                        ClownsHomes.getInstance().getHomesConfig().removeHome(home);
                        CLogger.info(home.getName() + " found in config, removed");
                        return;
                    });
                });
            }
        }.runTaskTimerAsynchronously(ClownsHomes.getInstance(), 20 * 10, 20 * 60 * 5);
    }

}
