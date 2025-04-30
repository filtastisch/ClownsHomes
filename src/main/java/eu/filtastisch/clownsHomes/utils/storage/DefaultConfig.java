package eu.filtastisch.clownsHomes.utils.storage;

import de.thesourcecoders.capi.config.GenericConfig;
import eu.filtastisch.clownsHomes.ClownsHomes;
import lombok.Getter;

public class DefaultConfig {

    private final GenericConfig genericConfig;

    @Getter
    private boolean debug = false;

    public DefaultConfig(){
        this.genericConfig = GenericConfig.loadFromResourceConfig("config.yml", ClownsHomes.getInstance());
        this.loadValues();
    }

    private void loadValues(){
        this.debug = this.genericConfig.getBoolean("settings.debug");
    }

}
