package fun.fifu.elbertskill.stands;

import fun.fifu.elbertskill.ElbertSkill;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

//    三、星尘回音[Stardust Echo]（小僵尸，记得带个钻石帽子）
//    右键物品形式的替身召唤对应替身生物，再次收回
//
//    一技能 矢量喷射
//    给本体玩家2秒的漂浮127级
//    冷却2秒
//
//    二技能 矢量强化
//    给玩家本体速度5 力量5 跳跃提升5 持续三秒
//    冷却五秒
//
//    三技能 矢量崩坏
//    玩家本体为中心，破坏3X3X3范围内所有方块
//
//    四技能 时停
public class StardustEcho extends AbstractStand {
    public StardustEcho(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        summonStandTag = "Stardust Echo";
        // 处理技能物品
        ElbertSkill.skillItemTag.add(summonStandTag);
//        ElbertSkill.skillItemTag.add(oulaTag);
        super.initialize();
    }

    @Override
    void summon(Player player) {
        // 处理替身
        if (spawnMap.get(player) == null) {
            summonStand(player, PigZombie.class);
            // 一技能 矢量喷射
            vectorJet(player);
            // 二技能 矢量强化
            vectorReinforcement(player);
            // 三技能 矢量崩坏
            vectorCollapse(player);
            // 四技能  时停
            timeStop(player, 100);
        } else {
            removeStand(player);
        }
    }

    /**
     * 技能: 矢量喷射
     *
     * @param player 召唤技能的玩家
     */
    void vectorJet(Player player) {
        // TODO 矢量喷射
    }

    /**
     * 技能: 矢量强化
     *
     * @param player 召唤技能的玩家
     */
    void vectorReinforcement(Player player) {
        // TODO 矢量强化
    }

    /**
     * 技能: 矢量崩坏
     *
     * @param player 召唤技能的玩家
     */
    void vectorCollapse(Player player) {
        // TODO 矢量崩坏
    }
}
