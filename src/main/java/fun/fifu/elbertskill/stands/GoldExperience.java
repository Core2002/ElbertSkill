package fun.fifu.elbertskill.stands;

import fun.fifu.elbertskill.ElbertSkill;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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
public class GoldExperience  extends AbstractStand {
    public GoldExperience(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        summonStandTag = "Gold_Experience";
        // 处理技能物品
        ElbertSkill.skillItemTag.add(summonStandTag);
//        ElbertSkill.skillItemTag.add(oulaTag);
        super.initialize();
    }

    @Override
    void summon(Player player) {

    }
}
