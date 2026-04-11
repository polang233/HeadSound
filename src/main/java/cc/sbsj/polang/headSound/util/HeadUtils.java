package cc.sbsj.polang.headSound.util;

import cc.sbsj.polang.headSound.HeadSound;
import cc.sbsj.polang.headSound.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;

import java.util.List;

/**
 * 头颅工具类 - 帮忙获取头颅的各种信息
 */
public class HeadUtils {

    /**
     * 检查并播放自定义音效
     * @return 如果播放了配置音效返回true，否则返回false
     */
    public static boolean checkAndPlaySound(Block block, Location loc, World world) {
        ConfigManager configMgr = HeadSound.instance.getConfigManager();
        SoundPlay soundPlay = HeadSound.instance.getSoundPlayer();
        boolean debugMode = configMgr.isDebugMode();

        if (debugMode) HeadSound.instance.getLogger().info("[调试] 检查头颅音效 - 位置: " + block.getLocation());

        // 先尝试获取texture value（所有玩家头颅都有texture）
        String textureValue = getTextureValue(block);
        if (textureValue != null) {
            // 有texture，先检查自定义头颅配置
            List<String> sounds = configMgr.getCustomHeadSounds(textureValue);
            if (sounds != null && !sounds.isEmpty()) {
                if (debugMode) HeadSound.instance.getLogger().info("[调试] 匹配到自定义头颅配置");
                soundPlay.playSound(world, loc, sounds);
                return true;
            }

            // 再检查玩家头颅配置（通过玩家名称）
            String ownerName = getPlayerName(block);
            if (ownerName != null) {
                sounds = configMgr.getPlayerHeadSounds(ownerName);
                if (sounds != null && !sounds.isEmpty()) {
                    if (debugMode)  HeadSound.instance.getLogger().info("[调试] 匹配到玩家头颅配置: " + ownerName);

                    soundPlay.playSound(world, loc, sounds);
                    return true;
                }
            }
        } else {
            // 没有texture，可能是生物头颅（如僵尸、骷髅等），直接返回false使用默认音效
            if (debugMode)  HeadSound.instance.getLogger().info("[调试] 没有texture，跳过自定义检查");

        }

        return false;
    }
    /**
     * 获取头颅的texture value
     */
    public static String getTextureValue(Block block) {
        if (!(block.getState() instanceof Skull skull)) {
            return null;
        }

        try {
            // 通过 PlayerProfile 获取 texture
            var profile = skull.getPlayerProfile();
            if (profile != null) {
                var textures = profile.getTextures();
                var skinUrl = textures.getSkin();
                if (skinUrl != null) {
                    // 将URL转成Base64编码的value
                    String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + skinUrl.toString() + "\"}}}";
                    return java.util.Base64.getEncoder().encodeToString(json.getBytes());
                }
            }
        } catch (Exception e) {
            // PlayerProfile 获取失败（可能是网络问题）
            if (HeadSound.instance.getConfigManager().isDebugMode()) {
                HeadSound.instance.getLogger().warning("[调试] 获取texture失败: " + e.getMessage());
            }
        }

        return null;
    }

    /**
     * 获取头颅的玩家名称
     */
    public static String getPlayerName(Block block) {
        if (!(block.getState() instanceof Skull skull)) {
            return null;
        }

        // 优先使用 owningPlayer（数据来自 usercache.json，纯本地）
        if (skull.hasOwner()) {
            var owner = skull.getOwningPlayer();
            if (owner != null && owner.getName() != null) {
                return owner.getName();
            }
        }

        // 从 PlayerProfile 的本地 NBT 数据读取名字
        try {
            var profile = skull.getPlayerProfile();
            if (profile != null) {
                String name = profile.getName();
                if (name != null && !name.isEmpty()) {
                    return name;
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    /**
     * 判断是不是头颅方块
     */
    public static boolean isHeadBlock(Block block) {
        String typeName = block.getType().name();
        return typeName.contains("SKULL") || typeName.contains("HEAD");
    }

    /**
     * 获取头颅的默认音效（生物头颅）
     */
    public static Sound getDefaultHeadSound(Material type) {
        return switch (type) {
            case SKELETON_SKULL, SKELETON_WALL_SKULL -> org.bukkit.Sound.ENTITY_SKELETON_AMBIENT;
            case WITHER_SKELETON_SKULL, WITHER_SKELETON_WALL_SKULL -> org.bukkit.Sound.ENTITY_WITHER_SKELETON_AMBIENT;
            case ZOMBIE_HEAD, ZOMBIE_WALL_HEAD -> org.bukkit.Sound.ENTITY_ZOMBIE_AMBIENT;
            case DRAGON_HEAD, DRAGON_WALL_HEAD -> org.bukkit.Sound.ENTITY_ENDER_DRAGON_AMBIENT;
            case CREEPER_HEAD, CREEPER_WALL_HEAD -> org.bukkit.Sound.ENTITY_CREEPER_PRIMED;
            default -> null;
        };
    }
}
