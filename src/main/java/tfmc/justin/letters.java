package tfmc.justin;

import me.Plugins.TLibs.Enums.APIType;
import me.Plugins.TLibs.Objects.API.ItemAPI;
import me.Plugins.TLibs.TLibs;
import org.bukkit.plugin.java.JavaPlugin;
import tfmc.justin.config.LetterConfig;
import tfmc.justin.creators.LetterCreator;
import tfmc.justin.listeners.BookSignListener;
import tfmc.justin.listeners.LetterOpenListener;
import tfmc.justin.validators.LetterValidator;

public class letters extends JavaPlugin {
    
    private static letters instance;
    private ItemAPI api;
    
    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        api = (ItemAPI) TLibs.getApiInstance(APIType.ITEM_API);
        
        LetterConfig config = new LetterConfig(this);
        LetterValidator validator = new LetterValidator(this, api, config);
        LetterCreator creator = new LetterCreator(this, api, config, validator.getSealedLetterKey());
        
        getServer().getPluginManager().registerEvents(new BookSignListener(this, config, validator, creator), this);
        getServer().getPluginManager().registerEvents(new LetterOpenListener(this, config, validator, creator), this);
        
        getLogger().info("Letters plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Letters plugin has been disabled!");
    }
    
    public static letters getInstance() {
        return instance;
    }
    
    public ItemAPI getApi() {
        return api;
    }
}