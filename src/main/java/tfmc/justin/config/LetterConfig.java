package tfmc.justin.config;

import org.bukkit.plugin.java.JavaPlugin;

// ==============================================
// Configuration access for the Letters plugin.
// ==============================================
public class LetterConfig {
    
    private final JavaPlugin plugin;
    
    // Item paths
    private final String letterPath;
    private final String writtenLetterPath;
    private final String writtenLetterOpenPath;
    
    // Settings
    private final boolean hideAuthor;
    private final boolean useTitleAsName;
    
    public LetterConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        
        // Load item paths from config
        this.letterPath = plugin.getConfig().getString("items.letter", "m.books.letter");
        this.writtenLetterPath = plugin.getConfig().getString("items.written-letter", "m.books.written_letter");
        this.writtenLetterOpenPath = plugin.getConfig().getString("items.written-letter-open", "m.books.written_letter_open");
        
        // Load settings from config
        this.hideAuthor = plugin.getConfig().getBoolean("settings.hide-author", true);
        this.useTitleAsName = plugin.getConfig().getBoolean("settings.use-title-as-name", true);
    }
    
    public String getLetterPath() { return letterPath; }
    public String getWrittenLetterPath() { return writtenLetterPath; }
    public String getWrittenLetterOpenPath() { return writtenLetterOpenPath; }
    
    public boolean shouldHideAuthor() { return hideAuthor;}
    public boolean shouldUseTitleAsName() { return useTitleAsName; }
    
    public String getMessage(String path, String defaultMessage) {
        String message = plugin.getConfig().getString(path, defaultMessage);
        return message.replace('&', '§');
    }
}
