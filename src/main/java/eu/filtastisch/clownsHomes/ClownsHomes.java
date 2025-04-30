package eu.filtastisch.clownsHomes;

import eu.filtastisch.clownsHomes.commands.HomeCommand;
import eu.filtastisch.clownsHomes.commands.SetHomeCommand;
import eu.filtastisch.clownsHomes.commands.TpaCommands;
import eu.filtastisch.clownsHomes.utils.CLogger;
import eu.filtastisch.clownsHomes.utils.manager.HomeManager;
import eu.filtastisch.clownsHomes.utils.manager.TeleportManager;
import eu.filtastisch.clownsHomes.utils.storage.DefaultConfig;
import eu.filtastisch.clownsHomes.utils.storage.HomesConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClownsHomes extends JavaPlugin {

    @Getter
    private static ClownsHomes instance;
    @Getter
    private HomesConfig homesConfig;
    @Getter
    private DefaultConfig defaultConfig;

    @Override
    public void onEnable() {
        instance = this;
        this.loadConfigs();
        this.registerCommands();
        HomeManager.startHomeUpdater();
        TeleportManager.startTeleportSchedule();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadConfigs(){
        this.homesConfig = new HomesConfig();
        this.defaultConfig = new DefaultConfig();
    }

    public void registerCommands(){
        new HomeCommand();
        new SetHomeCommand();
        new TpaCommands();
    }

}
