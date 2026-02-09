package tfmc.justin.validators;

import me.Plugins.TLibs.Objects.API.ItemAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import tfmc.justin.config.LetterConfig;

// ==============================================
// Validates letter items using TLibs
// ==============================================
public class LetterValidator {
    
    private final JavaPlugin plugin;
    private final ItemAPI api;
    private final LetterConfig config;
    private final NamespacedKey sealedLetterKey;
    
    public LetterValidator(JavaPlugin plugin, ItemAPI api, LetterConfig config) {
        this.plugin = plugin;
        this.api = api;
        this.config = config;
        this.sealedLetterKey = new NamespacedKey(plugin, "sealed_letter");
    }
    
    // ==============================================
    // Check if an item is an unsigned letter.
    // ==============================================
    public boolean isLetter(ItemStack item) {
        if (item == null || item.getType() != Material.WRITABLE_BOOK) {
            return false;
        }
        
        try {
            ItemStack letterTemplate = api.getCreator().getItemFromPath(config.getLetterPath()).clone();
            return item.isSimilar(letterTemplate);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to validate letter: " + e.getMessage());
            return false;
        }
    }
    
    // ==============================================
    // Check if an item is a sealed written letter.
    // Uses persistent data container to identify sealed letters.
    // ==============================================
    public boolean isSealedLetter(ItemStack item) {
        if (item == null || item.getType() != Material.WRITTEN_BOOK) { return false; } 

        if (!item.hasItemMeta()) { return false; }
        
        // Check if the item has our sealed letter marker
        return item.getItemMeta().getPersistentDataContainer().has(sealedLetterKey, PersistentDataType.BYTE);
    }
    
    // ==============================================
    // Get the namespaced key used to mark sealed letters.
    // ==============================================
    public NamespacedKey getSealedLetterKey() {
        return sealedLetterKey;
    }
}
