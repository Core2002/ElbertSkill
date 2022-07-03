package fun.fifu.elbertskill.stands;

import com.alkaidmc.alkaid.bukkit.event.AlkaidEvent;
import fun.fifu.elbertskill.ElbertSkill;
import fun.fifu.elbertskill.NekoUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class TheWorld implements Stand {
    Plugin plugin;

    public TheWorld(Plugin plugin) {
        this.plugin = plugin;
    }

    // 玩家 -> 玩家的替身
    public Map<Player, LivingEntity> spawnMap = new HashMap<>();

    // 删除AI的实体
    List<LivingEntity> aiList = new ArrayList<>();

    private final String summonStandTag = "召唤替身（猪人）";

    @Override
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

        // 处理技能物品
        ElbertSkill.skillItemTag.add(summonStandTag);
        ElbertSkill.skillItemTag.add(mudaTag);
    }

    @Override
    public void summon(Player player) {
        // 处理替身
        if (spawnMap.get(player) == null) {
            summonStand(player, PigZombie.class);

            // 1技能：时停
            timeStop(player);

            // 2技能：木大木大
            mudaMuda(player);
        } else {
            removeStand(player);
        }
    }

    private final String mudaTag = "木大木大";

    /**
     * 技能：木大木大
     *
     * @param player 召唤技能的玩家
     */
    private void mudaMuda(Player player) {
        // 发放木大
        ItemStack itemStack = new ItemStack(Material.STICK);
        NekoUtil.makeTagItem(itemStack, mudaTag);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier("value", 18, AttributeModifier.Operation.ADD_NUMBER));
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
                new AttributeModifier("value", 20, AttributeModifier.Operation.ADD_NUMBER));
        itemStack.setItemMeta(itemMeta);
        player.getInventory().addItem(itemStack);

        // 收回木大
        new BukkitRunnable() {
            @Override
            public void run() {
                NekoUtil.spendTagItem(player.getInventory(), mudaTag);
                player.sendMessage("已收回 " + mudaTag);
            }
        }.runTaskLater(plugin, 20 * 20);
    }

    /**
     * 技能：时停
     *
     * @param player 召唤技能的玩家
     */
    private void timeStop(Player player) {
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

    /**
     * 用替身查找召唤者
     *
     * @param stand 替身
     * @return 替身的召唤者
     */
    private Player getPlayerFromStand(LivingEntity stand) {
        for (Player player : spawnMap.keySet()) {
            if (spawnMap.get(player).equals(stand))
                return player;
        }
        return null;
    }

}
