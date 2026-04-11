package cc.sbsj.polang.headSound;

import cc.sbsj.polang.headSound.command.Commands;
import cc.sbsj.polang.headSound.config.ConfigManager;
import cc.sbsj.polang.headSound.util.SoundPlay;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * HeadSound 主类 - 头颅音效插件
 */
public final class HeadSound extends JavaPlugin {

    public static HeadSound instance;
    private ConfigManager configManager;
    private SoundPlay soundPlay;

    @Override
    public void onEnable() {
        instance = this;
        
        // 初始化各个模块
        configManager = new ConfigManager();
        soundPlay = new SoundPlay(false);
        
        // 保存并加载配置
        saveDefaultConfig();
        configManager.loadConfig();
        
        // 注册事件和命令
        getServer().getPluginManager().registerEvents(new Events(this), this);
        getCommand("headSound").setExecutor(new Commands(this));
        
        getLogger().info("§e头颅音符盒音效功能加载成功！");
        getLogger().info("§7使用 /headSound 查看帮助");
    }

    @Override
    public void onDisable() {
        getLogger().info("§c头颅音符盒音效功能已卸载");
    }


    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SoundPlay getSoundPlayer() {
        return soundPlay;
    }
}
