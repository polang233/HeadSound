package cc.sbsj.polang.headSound.util;

import cc.sbsj.polang.headSound.HeadSound;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

import java.util.List;

public class SoundPlay {

    private boolean debugMode;

    public SoundPlay(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    //随机选一个
    public void playSound(World world, Location loc, List<String> sounds) {
        if (sounds == null || sounds.isEmpty()) {
            return;
        }

        // 随机选一个音效
        String soundStr = sounds.get((int) (Math.random() * sounds.size()));
        
        if (debugMode) {
            HeadSound.instance.getLogger().info("[调试] 准备播放音效: " + soundStr);
        }

        String[] parts = soundStr.split("-");

        try {
            Sound sound = Sound.valueOf(parts[0]);
            float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0F;
            float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0F;

            if (debugMode) {
                HeadSound.instance.getLogger().info("[调试] 播放: " + sound + " 音量:" + volume + " 音调:" + pitch);
            }

            world.playSound(loc, sound, volume, pitch);
        } catch (Exception e) {
            HeadSound.instance.getLogger().warning("音效配置不对: " + soundStr);
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    //默认处理
    public void playDefaultSound(World world, Location loc, Sound sound) {
        if (debugMode) {
            HeadSound.instance.getLogger().info("[调试] 播放默认音效: " + sound);
        }
        world.playSound(loc, sound, 1F, 1F);
    }
}
