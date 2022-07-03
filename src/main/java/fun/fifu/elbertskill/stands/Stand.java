package fun.fifu.elbertskill.stands;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 替身：玩家会随机抽取一个替身，然后获得对应替身的技能
 */
public abstract class Stand {
    Plugin plugin;

    public Stand(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 在此处应有初始化，插件加载时调用
     */
    abstract void initialize();

    /**
     * 替身被召唤时调用
     *
     * @param player 召唤替身的玩家
     */
    abstract void summon(Player player);


    // 玩家 -> 玩家的替身
    public Map<Player, LivingEntity> spawnMap = new HashMap<>();

    /**
     * 让玩家召唤一个替身
     *
     * @param player 要召唤替身的玩家
     * @param clazz  替身实体种类
     */
    public <T extends LivingEntity> void summonStand(Player player, Class<T> clazz) {
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
    public void removeStand(Player player) {
        spawnMap.get(player).remove();
        spawnMap.remove(player);
        player.sendMessage("已收回替身");
    }

    /**
     * 用替身查找召唤者
     *
     * @param stand 替身
     * @return 替身的召唤者
     */
    public Player getPlayerFromStand(LivingEntity stand) {
        for (Player player : spawnMap.keySet()) {
            if (spawnMap.get(player).equals(stand))
                return player;
        }
        return null;
    }


    // 删除AI的实体
    List<LivingEntity> aiList = new ArrayList<>();

    /**
     * 技能：时停
     *
     * @param player 召唤技能的玩家
     */
    public void timeStop(Player player) {
        // 移除全体实体AI (半径100)
        player.getWorld().getEntities().forEach(entity -> {
            if (entity.equals(player))
                return;
            if (!(entity instanceof LivingEntity livingEntity))
                return;
            if (entity.getLocation().distance(player.getLocation()) > 100)
                return;
            livingEntity.setAI(false);

            livingEntity.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(20 * 8, 8));
            livingEntity.addPotionEffect(PotionEffectType.SLOW.createEffect(20 * 8, 8));
            livingEntity.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(20 * 8, 8));
            aiList.add(livingEntity);

            player.sendMessage("已删除AI");
        });

        // 放回AI
        new BukkitRunnable() {
            @Override
            public void run() {
                aiList.forEach(livingEntity -> livingEntity.setAI(true));
                player.sendMessage("已放回AI");
            }
        }.runTaskLater(plugin, 180);
    }
}
