package fun.fifu.elbertskill.functions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.alkaidmc.alkaid.bukkit.event.AlkaidEvent;

import fun.fifu.elbertskill.NekoUtil;

// 一、世界[The World]
// 右键物品形式的替身召唤对应替身生物，再次收回（僵尸猪灵）

// 一技能 时停
// 事件：
// 1.移除全体实体AI
// 2.所有实体循环原地tp（这里把末影珍珠和箭矢一类的弹射物kill一下）
// 3.给所有实体1秒的指定三种DEBUFF（我建议是时长1秒然后循环九秒，防止玩家喝牛奶）
// 虚弱VIII（八级）失明八级 缓慢八级
// 4.经过九秒(180tick)
// 5.归还实体AI，解除实体TP和DEBUFF（kill的弹射物不用管）
// 冷却3秒

// 二技能 木大木大
// 给玩家一个拿在主手加18攻击伤害，20攻击速度的物品（最好是棍子这类不能摆放的）
// 20秒后移除
// 冷却5秒
public class TheWorld implements Initializable {
    Plugin plugin;

    public TheWorld(Plugin plugin) {
        this.plugin = plugin;
    }

    public Map<Player, Entity> spawnMap = new HashMap<>();

    @Override
    public void initialize() {
        // 召唤替身
        new AlkaidEvent(plugin).simple()
                .event(PlayerInteractEvent.class)
                .listener(event -> {
                    var player = event.getPlayer();
                    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                    if (itemInMainHand.getType().isAir())
                        return;
                    if (!NekoUtil.hasTagItem(itemInMainHand, "召唤替身（猪人）"))
                        return;

                    if (spawnMap.get(player) == null) {
                        summonStand(player, PigZombie.class);
                    } else {
                        removeStand(player);
                    }

                })
                .priority(EventPriority.HIGHEST)
                .ignore(false)
                .register();

    }

    /**
     * 让玩家召唤一个替身
     * 
     * @param player 要召唤替身的玩家
     * @param clazz  替身实体种类
     */
    private <T extends LivingEntity> void summonStand(Player player, Class<T> clazz) {
        var location = player.getLocation();
        var vector = location.getDirection().multiply(-1);
        vector.setY(1);
        location.add(vector);
        var Stand = location.getWorld().spawn(location, clazz);
        Stand.setAI(false);
        spawnMap.put(player, Stand);
        player.sendMessage("已召唤替身");
    }

    /**
     * 收回替身
     * 
     * @param player 要收回替身的玩家
     */
    private void removeStand(Player player) {
        spawnMap.get(player).remove();
        spawnMap.remove(player);
        player.sendMessage("已收回替身");
    }

}
