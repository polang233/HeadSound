package cc.sbsj.polang.headSound;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Events implements Listener {
    @EventHandler
    public void onRedstone(NotePlayEvent event) {
        Location loc = event.getBlock().getLocation();
        World world = event.getBlock().getWorld();
        //判断前后左右四个方位是否为头颅
        Block block1 = event.getBlock().getRelative(-1, 0, 0);
        Block block2 = event.getBlock().getRelative(1, 0, 0);
        Block block3 = event.getBlock().getRelative(0, 0, 1);
        Block block4 = event.getBlock().getRelative(0, 0, -1);
        Block[] blocks = {block1, block2, block3, block4};
        for (Block block : blocks) {
            if (!block.isEmpty()) {
                Material type = block.getType();
                switch (type) {
                    case SKELETON_SKULL:
                    case SKELETON_WALL_SKULL:
                        world.playSound(loc, Sound.ENTITY_SKELETON_AMBIENT, 1F, 1F);
                        event.setCancelled(true);
                        break;
                    case WITHER_SKELETON_SKULL:
                    case WITHER_SKELETON_WALL_SKULL:
                        world.playSound(loc, Sound.ENTITY_WITHER_SKELETON_AMBIENT, 1F, 1F);
                        event.setCancelled(true);
                        break;
                    case ZOMBIE_HEAD:
                    case ZOMBIE_WALL_HEAD:
                        world.playSound(loc, Sound.ENTITY_ZOMBIE_AMBIENT, 1F, 1F);
                        event.setCancelled(true);
                        break;
                    case DRAGON_HEAD:
                    case DRAGON_WALL_HEAD:
                        world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1F, 1F);
                        event.setCancelled(true);
                        break;
                    case CREEPER_HEAD:
                    case CREEPER_WALL_HEAD:
                        world.playSound(loc, Sound.ENTITY_CREEPER_PRIMED, 1F, 1F);
                        event.setCancelled(true);
                        break;
                    default:
                        continue;
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
        Material type = world.getBlockAt(loc).getType();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || type == Material.AIR) return;

        event.setCancelled(true);
        switch (type) {
            case SKELETON_SKULL:
                world.playSound(loc, Sound.ENTITY_SKELETON_AMBIENT, 1F, 1F);
                break;
            case WITHER_SKELETON_SKULL:
                world.playSound(loc, Sound.ENTITY_WITHER_SKELETON_AMBIENT, 1F, 1F);
                break;
            case ZOMBIE_HEAD:
                world.playSound(loc, Sound.ENTITY_ZOMBIE_AMBIENT, 1F, 1F);
                break;
            case DRAGON_HEAD:
                world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1F, 1F);
                break;
            case CREEPER_HEAD:
                world.playSound(loc, Sound.ENTITY_CREEPER_PRIMED, 1F, 1F);
                break;
            default:
                break;
        }
    }
}
