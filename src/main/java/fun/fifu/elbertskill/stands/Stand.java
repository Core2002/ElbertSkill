package fun.fifu.elbertskill.stands;

import com.alkaidmc.alkaid.bukkit.event.AlkaidEvent;
import fun.fifu.elbertskill.NekoUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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

    public String summonStandTag;

    /**
     * 在此处应有初始化，插件加载时调用
     */
    public void initialize() {
        // 召唤替身
        new AlkaidEvent(plugin).simple()
                .event(PlayerInteractEvent.class)
                .listener(event -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
                        return;
                    Player player = event.getPlayer();
                    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                    if (itemInMainHand.getType().isAir())
                        return;
                    if (!NekoUtil.hasTagItem(itemInMainHand, summonStandTag))
                        return;
                    summon(event.getPlayer());
                })
                .priority(EventPriority.HIGHEST)
                .ignore(false)
                .register();

        // 替身血量和召唤者对等
        new AlkaidEvent(plugin).simple()
                .event(EntityDamageEvent.class)
                .listener(event -> {
                    LivingEntity entity = (LivingEntity) event.getEntity();
                    if (spawnMap.containsKey(entity)) {             // 召唤者受伤
                        LivingEntity stand = spawnMap.get(entity);
                        stand.setMaxHealth(entity.getMaxHealth());
                        stand.setHealth(entity.getHealth() - event.getFinalDamage());
                    } else if (spawnMap.containsValue(entity)) {    //  替身受伤
                        LivingEntity player = getPlayerFromStand(entity);
                        player.setMaxHealth(entity.getMaxHealth());
                        player.setHealth(entity.getHealth() - event.getFinalDamage());
                    }
                })
                .priority(EventPriority.HIGHEST)
                .ignore(false)
                .register();

        // 玩家死亡
        new AlkaidEvent(plugin).simple()
                .event(PlayerDeathEvent.class)
                .listener(event -> {
                    Player player = event.getEntity();
                    if (spawnMap.containsKey(player)) {             // 召唤者死亡
                        LivingEntity stand = spawnMap.get(player);
                        stand.damage(999999999);
                    }
                })
                .priority(EventPriority.HIGHEST)
                .ignore(false)
                .register();

        // 替身死亡
        new AlkaidEvent(plugin).simple()
                .event(EntityDeathEvent.class)
                .listener(event -> {
                    LivingEntity stand = event.getEntity();
                    if (spawnMap.containsValue(stand)) {             // 召唤者死亡
                        getPlayerFromStand(stand).damage(999999999);
                    }
                })
                .priority(EventPriority.HIGHEST)
                .ignore(false)
                .register();
    }

    ;

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
     * @param player    召唤技能的玩家
     * @param tick      多少tick过后归还AI
     */
    public void timeStop(Player player,Integer tick) {
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
        }.runTaskLater(plugin, tick);
    }
}
