package cc.sbsj.polang.headSound.config;

import cc.sbsj.polang.headSound.HeadSound;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final Map<String, List<String>> customHeads = new HashMap<>();
    private final Map<String, List<String>> playerHeads = new HashMap<>();
    private boolean debugMode = false;

    /**
     * 加载配置文件
     */
    public void loadConfig() {
        customHeads.clear();
        playerHeads.clear();

        HeadSound plugin = HeadSound.instance;
        plugin.reloadConfig();
        plugin.getLogger().info("开始加载配置...");

        // 加载自定义头颅配置
        ConfigurationSection customSection = plugin.getConfig().getConfigurationSection("custom");
        if (customSection != null) {
            for (String key : customSection.getKeys(false)) {
                List<String> values = customSection.getStringList(key + ".value");
                List<String> sounds = customSection.getStringList(key + ".sound");
                
                for (String value : values) {
                    customHeads.put(value, sounds);
                }
                
                if (debugMode) {
                    plugin.getLogger().info("[调试] 加载自定义头颅: " + key + " (有" + values.size() + "个value, " + sounds.size() + "个音效)");
                }
            }
        }

        // 加载玩家头颅配置
        ConfigurationSection playerSection = plugin.getConfig().getConfigurationSection("player");
        if (playerSection != null) {
            for (String key : playerSection.getKeys(false)) {
                List<String> sounds = playerSection.getStringList(key + ".sound");
                playerHeads.put(key.toLowerCase(), sounds);
                
                if (debugMode) {
                    plugin.getLogger().info("[调试] 加载玩家头颅: " + key + " (" + sounds.size() + "个音效)");
                }
            }
        }

        plugin.getLogger().info("配置加载完成\n自定义配置: " + customHeads.size() + "个, 玩家配置: " + playerHeads.size() + "个");
    }

    /**
     * 获取自定义头颅的音效列表
     */
    public List<String> getCustomHeadSounds(String textureValue) {
        return customHeads.get(textureValue);
    }

    /**
     * 获取玩家头颅的音效列表
     */
    public List<String> getPlayerHeadSounds(String playerName) {
        return playerHeads.get(playerName.toLowerCase());
    }

    /**
     * 设置调试模式
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        HeadSound.instance.getLogger().info("调试模式已" + (debugMode ? "开启" : "关闭"));
    }

    /**
     * 获取调试模式状态
     */
    public boolean isDebugMode() {
        return debugMode;
    }
}
