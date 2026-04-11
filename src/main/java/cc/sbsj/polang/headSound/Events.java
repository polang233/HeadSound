package cc.sbsj.polang.headSound;

import cc.sbsj.polang.headSound.util.HeadUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Events implements Listener {

    private final HeadSound plugin;

    public Events(HeadSound plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRedstone(NotePlayEvent event) {
        Location loc = event.getBlock().getLocation();
        World world = event.getBlock().getWorld();

        // 检查前后左右四个方位
        Block[] blocks = {
            event.getBlock().getRelative(-1, 0, 0),
            event.getBlock().getRelative(1, 0, 0),
            event.getBlock().getRelative(0, 0, 1),
            event.getBlock().getRelative(0, 0, -1)
        };

        for (Block block : blocks) {
            if (!block.isEmpty() && HeadUtils.isHeadBlock(block)) {
                // 先检查配置文件中的自定义头颅或玩家头颅
                if (HeadUtils.checkAndPlaySound(block, loc, world)) {
                    event.setCancelled(true);
                    continue;
                }

                // 如果没有匹配的配置，则使用默认音效
                org.bukkit.Sound defaultSound = HeadUtils.getDefaultHeadSound(block.getType());
                if (defaultSound != null) {
                    plugin.getSoundPlayer().playDefaultSound(world, loc, defaultSound);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInventory(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.NOTE_BLOCK) {
            return;
        }

        Location loc = event.getClickedBlock().getRelative(0, 1, 0).getLocation();
        World world = event.getClickedBlock().getWorld();
        Block headBlock = world.getBlockAt(loc);
        Material type = headBlock.getType();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || type == Material.AIR) return;

        event.setCancelled(true);

        // 首先检查是否为头颅方块
        if (HeadUtils.isHeadBlock(headBlock)) {
            // 先检查配置文件中的自定义头颅或玩家头颅
            if (HeadUtils.checkAndPlaySound(headBlock, loc, world)) {
                return;
            }

            // 如果没有匹配的配置，则使用默认音效
            org.bukkit.Sound defaultSound = HeadUtils.getDefaultHeadSound(type);
            if (defaultSound != null) {
                plugin.getSoundPlayer().playDefaultSound(world, loc, defaultSound);
            }
        }
    }
}
