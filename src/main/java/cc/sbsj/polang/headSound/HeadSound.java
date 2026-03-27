package cc.sbsj.polang.headSound;

import org.bukkit.plugin.java.JavaPlugin;

public final class HeadSound extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(), this);
        getLogger().info("§e头颅音符盒音效功能加载成功！");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
