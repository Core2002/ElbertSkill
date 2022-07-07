package fun.fifu.elbertskill.stands;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

//    五、疯狂钻石[Crazy Diamond]
//    右键物品形式的替身召唤对应替身生物（僵尸，记得带个钻石帽子）
//
//    一技能 嘟啦啦啦
//    给玩家一个拿在主手加10攻击伤害，20攻击速度的物品（最好是棍子这类不能摆放的）
//    20秒后移除
//    冷却5秒
//
//    二技能 修复
//    给半径三格内玩家治疗5（不包括自己）持续3秒（办得到的话让这些玩家背包内有耐久的东西全恢复满耐久）
//
//
//
//    此条独立：做一只箭，命名虫箭，功能和前文提到的替身之箭相同，但特殊的替身（会指明）
//    已有替身的情况下使用这只箭可以将替身进化成对应的镇魂曲替身
//    事件：
//    检测组内玩家
//    检测允许发生此事件的替身对应的替身使者使用虫箭
//    将玩家从当前替身组删除
//    将玩家加入对应镇魂曲替身组
public class CrazyDiamond extends AbstractStand {

    public CrazyDiamond(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    void summon(Player player) {
        // 处理替身
        if (spawnMap.get(player) == null) {
            summonStand(player, Zombie.class);

            // 一技能 嘟啦啦啦
            lala(player, 10, 20);

            // 二技能 修复
            fix(player);

        } else {
            removeStand(player);
        }
    }

    void fix(Player player) {
        player.getWorld().getNearbyEntities(player.getLocation(), 3, 3, 3).forEach(entity -> {
            if (!(entity instanceof LivingEntity livingEntity))
                return;
            livingEntity.addPotionEffect(PotionEffectType.HEAL.createEffect(20 * 3, 5));
        });
    }
}
