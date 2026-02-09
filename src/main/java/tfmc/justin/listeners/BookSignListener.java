package tfmc.justin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;
import tfmc.justin.config.LetterConfig;
import tfmc.justin.creators.LetterCreator;
import tfmc.justin.validators.LetterValidator;

// ==============================================
// Handles the signing of unsigned letters.
// Listens for PlayerEditBookEvent and <<<converts unsigned letters to sealed letters>>>.
// ==============================================
public class BookSignListener implements Listener {
    
    private final JavaPlugin plugin;
    private final LetterConfig config;
    private final LetterValidator validator;
    private final LetterCreator creator;
    
    public BookSignListener(JavaPlugin plugin, LetterConfig config, LetterValidator validator, LetterCreator creator) {
        this.plugin = plugin;
        this.config = config;
        this.validator = validator;
        this.creator = creator;
    }
    
    @EventHandler
    public void onBookSign(PlayerEditBookEvent event) {
        if (!event.isSigning()) { return; }
        
        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        
        // Check if the item is an unsigned letter
        if (!validator.isLetter(handItem)) { return; }
        
        // Cancel the default book signing to prevent vanilla written book
        event.setCancelled(true);
        
        // Get the new book meta from the event
        BookMeta newBookMeta = event.getNewBookMeta();
        
        // Create sealed letter item
        ItemStack sealedLetter = creator.createSealedLetter(newBookMeta);
        
        if (sealedLetter == null) {
            handleCreationFailure(event.getPlayer());
            return;
        }
        
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.getInventory().setItemInMainHand(sealedLetter);
            sendSuccessMessage(player);
        });
    }
    
    // ==============================================
    // Handle letter creation failure.
    // ==============================================
    private void handleCreationFailure(Player player) {
        plugin.getLogger().warning("Failed to create sealed letter for " + player.getName());
        String message = config.getMessage("messages.letter-creation-failed", 
                "&cFailed to create letter. Please make a ticket.");
        player.sendMessage(message);
    }
    
    // ==============================================
    // Send success message to the player.
    // ==============================================
    private void sendSuccessMessage(Player player) {
        String message = config.getMessage("messages.letter-signed", 
                "&aYou have successfully signed your letter!");
        player.sendMessage(message);
    }
}
