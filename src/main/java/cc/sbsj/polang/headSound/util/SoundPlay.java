package cc.sbsj.polang.headSound.util;

import cc.sbsj.polang.headSound.HeadSound;
import org.bukkit.Bukkit;
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

    //随机选一个；如果列表里有 delay@tick，则按顺序组合播放
    public void playSound(World world, Location loc, List<String> sounds) {
        if (sounds == null || sounds.isEmpty()) {
            return;
        }

        if (hasDelayAction(sounds)) {
            playSoundSequence(world, loc, sounds);
            return;
        }

        // 随机选一个音效
        String soundStr = sounds.get((int) (Math.random() * sounds.size()));
        playSingleSound(world, loc, soundStr);
    }

    private boolean hasDelayAction(List<String> sounds) {
        for (String soundStr : sounds) {
            if (isDelayAction(soundStr)) {
                return true;
            }
        }
        return false;
    }

    private void playSoundSequence(World world, Location loc, List<String> sounds) {
        long delayTicks = 0L;
        Location playLoc = loc.clone();

        if (debugMode) {
            HeadSound.instance.getLogger().info("[调试] 准备顺序播放组合音效，共 " + sounds.size() + " 项");
        }

        for (String soundStr : sounds) {
            if (isDelayAction(soundStr)) {
                delayTicks += parseDelayTicks(soundStr);
                continue;
            }

            long currentDelay = delayTicks;
            if (currentDelay <= 0L) {
                playSingleSound(world, playLoc, soundStr);
                continue;
            }

            Bukkit.getScheduler().runTaskLater(HeadSound.instance, () -> playSingleSound(world, playLoc, soundStr), currentDelay);
        }
    }

    private boolean isDelayAction(String soundStr) {
        return soundStr != null && soundStr.trim().toLowerCase().startsWith("delay@");
    }

    private long parseDelayTicks(String soundStr) {
        try {
            long ticks = Long.parseLong(soundStr.trim().substring("delay@".length()));
            if (ticks < 0L) {
                HeadSound.instance.getLogger().warning("延迟配置不能小于0: " + soundStr);
                return 0L;
            }
            return ticks;
        } catch (Exception e) {
            HeadSound.instance.getLogger().warning("延迟配置不对: " + soundStr);
            if (debugMode) {
                e.printStackTrace();
            }
            return 0L;
        }
    }

    private void playSingleSound(World world, Location loc, String soundStr) {
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
