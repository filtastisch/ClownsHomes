package eu.filtastisch.clownsHomes.utils.storage;

import de.thesourcecoders.capi.config.GenericConfig;
import eu.filtastisch.clownsHomes.ClownsHomes;
import eu.filtastisch.clownsHomes.utils.manager.HomeManager;
import eu.filtastisch.clownsHomes.utils.types.Home;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class HomesConfig {

    private final GenericConfig config;

    public HomesConfig() {
        this.config = GenericConfig.load(ClownsHomes.getInstance(), "homes.yml");
        new BukkitRunnable() {
            @Override
            public void run() {
                loadHomes();
            }
        }.runTaskLaterAsynchronously(ClownsHomes.getInstance(), 20L);
    }

    public void addHome(Home home) {
        if (home == null) {
            throw new IllegalArgumentException("Home cannot be null!");
        }
        if (home.getOwner() == null) {
            throw new IllegalArgumentException("Home owner cannot be null! Home: " + home);
        }
        this.config.set("homes." + home.getOwner() + "." + home.getUuid() + ".name", home.getName());
        this.config.setLocation("homes." + home.getOwner() + "." + home.getUuid() + ".location", home.getLocation());
        this.config.save();
    }

    public void removeHome(Home home) {
        this.config.set("homes." + home.getOwner() + "." + home.getUuid(), HomeManager.homes.get(home.getOwner()));
        this.config.save();
    }

    public Map<UUID, Home[]> getAllHomes() {
        if (this.config.getConfigurationSection("homes") == null) return new HashMap<>();
        Map<UUID, Home[]> homeMap = new HashMap<>();
        this.config.getConfigurationSection("homes").getKeys(false).forEach(uuid -> {
            UUID playerId = UUID.fromString(uuid);
            List<Home> homeList = new ArrayList<>();
            this.config.getConfigurationSection("homes." + uuid).getKeys(false).forEach(homeId -> {
                UUID homeUuid = UUID.fromString(homeId);
                String homeName = this.config.getString("homes." + playerId + "." + homeUuid + ".name");
                Location homeLocation;
                try {
                    homeLocation = this.config.getLocation("homes." + playerId + "." + homeUuid + ".location").get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }

                homeList.add(new Home(homeUuid, homeName, homeLocation, playerId));
            });
            homeMap.put(playerId, homeList.toArray(new Home[0]));
        });
        return homeMap;
    }

    public void loadHomes() {
        if (this.config.getConfigurationSection("homes") == null) return;
        this.getAllHomes().forEach((playerId, homes) -> Arrays.stream(homes).forEach(home -> HomeManager.addHome(home, false)));
    }

}
