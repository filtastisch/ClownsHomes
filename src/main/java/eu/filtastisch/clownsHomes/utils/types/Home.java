package eu.filtastisch.clownsHomes.utils.types;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
public class Home {

    private final UUID uuid;
    private final String name;
    private final Location location;
    private final UUID owner;


    public Home(UUID uuid, String name, Location location, UUID owner) {
        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.owner = owner;
    }
}
