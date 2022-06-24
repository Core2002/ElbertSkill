package fun.fifu.elbertskill.functions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.alkaidmc.alkaid.bukkit.event.AlkaidEvent;

import fun.fifu.elbertskill.NekoUtil;

public class 替身 implements Initializable {
    Plugin plugin;

    public 替身(Plugin plugin) {
        this.plugin = plugin;
    }

    private 替身() {
        throw new RuntimeException();
    }

    public Map<Player, Entity> spawnMap = new HashMap<>();

    @Override
    public void initialize() {
        new AlkaidEvent(plugin).simple()
                .event(PlayerInteractEvent.class)
                .listener(event -> {
                    var player = event.getPlayer();
                    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                    if (itemInMainHand.getType().isAir())
                        return;
                    if (NekoUtil.hasTagItem(itemInMainHand, "召唤替身（猪人）")) {
                        var location = player.getLocation();
                        if (spawnMap.get(player) == null) {
                            spawnMap.put(player, location.getWorld().spawn(location, PigZombie.class));
                            
                            player.sendMessage("已召唤替身");
                        } else {
                            spawnMap.get(player).remove();
                            spawnMap.remove(player);
                            player.sendMessage("已收回替身");
                        }
                    }
                })
                .priority(EventPriority.HIGHEST)
                .ignore(false)
                .register();

    }

}
