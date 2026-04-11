package cc.sbsj.polang.headSound.command;

import cc.sbsj.polang.headSound.HeadSound;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 命令处理器 - 处理各种命令
 */
public class Commands implements CommandExecutor, TabCompleter {

    private final HeadSound plugin;

    public Commands(HeadSound plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "reload":
                handleReload(sender);
                break;
            case "debug":
                handleDebug(sender);
                break;
            case "info":
                handleInfo(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        //补全子命令
        if (args.length == 1) {
            completions = Arrays.asList("reload", "debug", "info");
        }

        return completions;
    }

    /**
     * 显示帮助信息
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§e========== HeadSound 帮助 ==========");
        sender.sendMessage("§6/headSound reload §7- 重载配置");
        sender.sendMessage("§6/headSound debug §7- 切换调试模式");
        sender.sendMessage("§6/headSound info §7- 查看手中物品详细信息");
        sender.sendMessage("§e===================================");
    }

    private void handleReload(CommandSender sender) {
        plugin.getConfigManager().loadConfig();
        sender.sendMessage("§a配置已重载！");
    }

    private void handleDebug(CommandSender sender) {
        boolean currentMode = plugin.getConfigManager().isDebugMode();
        boolean newMode = !currentMode;
        
        plugin.getConfigManager().setDebugMode(newMode);
        plugin.getSoundPlayer().setDebugMode(newMode);
        
        sender.sendMessage("§a调试模式已" + (newMode ? "开启" : "关闭"));
    }

    private void handleInfo(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c这个命令只能玩家使用！");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            sender.sendMessage("§c请手持一个物品！");
            return;
        }

        sender.sendMessage("§e========== 物品信息 ==========");
        
        // 基础信息
        sender.sendMessage("§7类型: §f" + item.getType().name());
        sender.sendMessage("§7数量: §f" + item.getAmount());
        
        if (item.hasItemMeta()) {
            var meta = item.getItemMeta();
            
            // 显示名称
            if (meta.hasDisplayName()) {
                sender.sendMessage("§7Name: §f" + meta.getDisplayName());
            }
            
            // Lore
            if (meta.hasLore()) {
                sender.sendMessage("§7Lore:");
                for (String lore : meta.getLore()) {
                    sender.sendMessage("  §8" + lore);
                }
            }
            
            // 自定义模型数据
            if (meta.hasCustomModelData()) {
                sender.sendMessage("§7自定义模型数据: §f" + meta.getCustomModelData());
            }
        }

        // 头颅显示额外信息
        String typeName = item.getType().name();
        if (typeName.contains("SKULL") || typeName.contains("HEAD")) {
            sender.sendMessage("");
            sender.sendMessage("§6===== head =====");
            
            // 尝试获取 PlayerProfile
            if (item.hasItemMeta() && item.getItemMeta() instanceof SkullMeta skullMeta) {

                // 获取玩家名称
                if (skullMeta.hasOwner()) {
                    String ownerName = skullMeta.getOwningPlayer() != null ? 
                            skullMeta.getOwningPlayer().getName() : "未知";
                    sender.sendMessage("§7§l玩家名称: §f" + ownerName);
                } else {
                    sender.sendMessage("§7玩家名称: §c无");
                }
                
                // 尝试获取 PlayerProfile 和 texture
                try {
                    PlayerProfile profile = skullMeta.getPlayerProfile();
                    if (profile != null) {
                        var textures = profile.getTextures();
                        var skinUrl = textures.getSkin();
                        
                        if (skinUrl != null) {
                            sender.sendMessage("§7§l有 Texture: §a是");
                            sender.sendMessage("§7头颅URL: §f" + skinUrl.toString());
                        } else {
                            sender.sendMessage("§7有 Texture: §c否");
                        }
                    }
                } catch (Exception e) {
                    sender.sendMessage("§7Texture 信息: §c获取失败");
                }
            }
        }

        sender.sendMessage("§e===============================");
    }
}
