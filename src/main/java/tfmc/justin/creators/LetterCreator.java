package tfmc.justin.creators;

import me.Plugins.TLibs.Objects.API.ItemAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import tfmc.justin.config.LetterConfig;

// ==============================================
// Creator class for creating letter items with proper metadata.
// ==============================================
public class LetterCreator {
    
    private final JavaPlugin plugin;
    private final ItemAPI api;
    private final LetterConfig config;
    private final NamespacedKey sealedLetterKey;
    
    public LetterCreator(JavaPlugin plugin, ItemAPI api, LetterConfig config, NamespacedKey sealedLetterKey) {
        this.plugin = plugin;
        this.api = api;
        this.config = config;
        this.sealedLetterKey = sealedLetterKey;
    }
    
    // ==============================================
    // Create a sealed written letter from a signed book.
    // Copies pages and title, optionally sets custom display name.
    // ==============================================
    public ItemStack createSealedLetter(BookMeta signedBookMeta) {
        try {
            ItemStack writtenLetter = api.getCreator()
                    .getItemFromPath(config.getWrittenLetterPath())
                    .clone();
            
            BookMeta writtenMeta = (BookMeta) writtenLetter.getItemMeta();
            if (writtenMeta != null) {
                copyBookContent(signedBookMeta, writtenMeta);
                
                // Mark this as a sealed letter using PersistentDataContainer
                writtenMeta.getPersistentDataContainer()
                        .set(sealedLetterKey, PersistentDataType.BYTE, (byte) 1);
                
                writtenLetter.setItemMeta(writtenMeta);
            }
            
            return writtenLetter;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to create sealed letter: " + e.getMessage());
            return null;
        }
    }
    
    // ==============================================
    // Create an opened letter from a sealed letter.
    // Preserves all book content from the sealed version.
    // ==============================================
    public ItemStack createOpenedLetter(BookMeta sealedBookMeta) {
        try {
            ItemStack openLetter = api.getCreator()
                    .getItemFromPath(config.getWrittenLetterOpenPath())
                    .clone();
            
            BookMeta openMeta = (BookMeta) openLetter.getItemMeta();
            if (openMeta != null) {
                copyBookContent(sealedBookMeta, openMeta);
                openLetter.setItemMeta(openMeta);
            }
            
            return openLetter;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to create opened letter: " + e.getMessage());
            return null;
        }
    }
    
    // ==============================================
    // Copy book content (pages and title) from source to target.
    // Applies custom display name if configured.
    // ==============================================
    private void copyBookContent(BookMeta source, BookMeta target) {
        // Copy pages
        target.setPages(source.getPages());
        
        // Copy title if present and optionally set as display name
        if (source.hasTitle()) {
            String title = source.getTitle();
            target.setTitle(title);
            
            // Override MMOItem display name with the book title (if enabled)
            if (config.shouldUseTitleAsName()) {
                target.setDisplayName(title);
            }
        }
    }
}
