package fun.fifu.elbertskill.stands;

import fun.fifu.elbertskill.ElbertSkill;
import fun.fifu.elbertskill.NekoUtil;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//    四、黄金体验[Gold Experience]（小僵尸猪灵）
//    右键物品形式的替身召唤对应替身生物，再次收回
//
//    一技能 木大木大
//
//    给玩家一个拿在主手加5攻击伤害，20攻击速度的物品（最好是棍子这类不能摆放的）
//    20秒后移除
//    冷却5秒
//
//    二技能 生命能量
//    给半径3格内实体（包括自己）虚弱5 瞬间治疗5 持续3秒
//    同时解除所有DEBUFF
//    冷却10秒
//
//    三技能 制造生命
//    在半径十五格内的玩家身边生成一只敌对生物（最好是从姜丝，凋零骷髅，僵尸猪人，和卫道士这样的近战里随机抽取）
//    冷却10秒
public class GoldExperience extends AbstractStand {
    public GoldExperience(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        summonStandTag = "Gold_Experience";
        // 处理技能物品
        ElbertSkill.skillItemTag.add(summonStandTag);
        super.initialize();
    }

    @Override
    void summon(Player player) {
        // 处理替身
        if (spawnMap.get(player) == null) {
            summonStand(player, Sheep.class);
            // 一技能 木大木大
            lala(player, 5, 20);
            // 二技能 生命能量
            lifeEnergy(player);
            // 三技能 制造生命
            makeLife(player);
        } else {
            removeStand(player);
        }
    }

    /**
     * 技能: 生命能量
     *
     * @param player 召唤技能的玩家
     */
    void lifeEnergy(Player player) {
        player.getWorld().getNearbyEntities(player.getLocation(), 3, 3, 3).forEach(entity -> {
            if (!(entity instanceof LivingEntity livingEntity))
                return;
            livingEntity.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(20 * 3, 3));
            livingEntity.addPotionEffect(PotionEffectType.HEAL.createEffect(20 * 3, 3));
        });
    }

    Class[] lifes = {Pig.class, Wolf.class, Creeper.class, Spider.class};

    Random random = new Random();

    /**
     * 技能: 制造生命
     *
     * @param player
     */
    void makeLife(Player player) {
        var location = player.getLocation();
        var vector = location.getDirection().multiply(-1);
        vector.setY(1);
        location.add(vector);
        var life = (LivingEntity) location.getWorld().spawn(location, lifes[random.nextInt(lifes.length)]);
        lifeMap.put(player, life);
        new BukkitRunnable() {
            @Override
            public void run() {
                NekoUtil.spendTagItem(player.getInventory(), oulaTag);
            }
        }.runTaskLater(plugin, 20 * 10);
    }

    // 玩家 -> 召唤物
    public Map<Player, LivingEntity> lifeMap = new HashMap<>();


    /**
     * 收回替身
     *
     * @param player 要收回替身的玩家
     */
    public void removeLife(Player player) {
        lifeMap.get(player).remove();
        lifeMap.remove(player);
    }
}
