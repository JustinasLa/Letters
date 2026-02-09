package tfmc.justin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;
import tfmc.justin.config.LetterConfig;
import tfmc.justin.creators.LetterCreator;
import tfmc.justin.validators.LetterValidator;

// ==============================================
// Handles the opening of sealed letters.
// Listens for PlayerInteractEvent and <<<converts sealed letters to opened letters>>>.
// ==============================================
public class LetterOpenListener implements Listener {
    
    private final JavaPlugin plugin;
    private final LetterConfig config;
    private final LetterValidator validator;
    private final LetterCreator creator;
    
    public LetterOpenListener(JavaPlugin plugin, LetterConfig config, LetterValidator validator, LetterCreator creator) {
        this.plugin = plugin;
        this.config = config;
        this.validator = validator;
        this.creator = creator;
    }
    
    @EventHandler
    public void onBookOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && 
            event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        // Don't open letters when placing them in bookshelves or lecterns
        if (event.getClickedBlock() != null) {
            Material blockType = event.getClickedBlock().getType();
            if (blockType == Material.CHISELED_BOOKSHELF || blockType == Material.LECTERN) {
                return;
            }
        }
        
        ItemStack item = event.getItem();
        
        if (!validator.isSealedLetter(item)) { return; }
        
        // Get the current book content
        BookMeta currentMeta = (BookMeta) item.getItemMeta();
        if (currentMeta == null) {
            return;
        }
        
        ItemStack openedLetter = creator.createOpenedLetter(currentMeta);
        
        if (openedLetter == null) {
            handleOpeningFailure(event.getPlayer());
            return;
        }
        
        // Replace the sealed letter with the opened letter
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.getInventory().setItemInMainHand(openedLetter);
            sendOpeningMessage(player);
        });
    }
    
    // ==============================================
    // Handle letter opening failure.
    // ==============================================
    private void handleOpeningFailure(Player player) {
        plugin.getLogger().warning("Failed to create opened letter for " + player.getName());
        String message = config.getMessage("messages.letter-open-failed", 
                "&cFailed to open letter. Please make a ticket.");
        player.sendMessage(message);
    }
    
    // ==============================================
    // Send opening message to the player.
    // ==============================================
    private void sendOpeningMessage(Player player) {
        String message = config.getMessage("messages.letter-opened", 
                "&7The seal breaks as you open the letter...");
        player.sendMessage(message);
    }
}
