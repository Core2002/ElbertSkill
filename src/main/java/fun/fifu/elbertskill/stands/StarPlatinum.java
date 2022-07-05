package fun.fifu.elbertskill.stands;

import fun.fifu.elbertskill.ElbertSkill;
import fun.fifu.elbertskill.NekoUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

//    二、白金之星[Star Platinum]（小白，记得带个钻石帽子）
//    右键物品形式的替身召唤对应替身生物，再次收回
//
//    一技能 时停
//    事件：
//    1.移除全体实体AI
//    2.所有实体循环原地tp（这里把末影珍珠和箭矢一类的弹射物kill一下）
//    3.给所有实体1秒的指定三种DEBUFF（我建议是时长1秒然后循环五秒，防止玩家喝牛奶）
//    虚弱VIII（八级）失明八级 缓慢八级
//    4.经过五秒(100tick)
//    5.归还实体AI，解除实体TP和DEBUFF（kill的弹射物不用管）
//    冷却3秒
//
//    二技能 欧拉欧拉
//
//    给玩家一个拿在主手加20攻击伤害，20攻击速度的物品（最好是棍子这类不能摆放的）
//    20秒后移除
//    冷却5秒
public class StarPlatinum extends AbstractStand {

    public StarPlatinum(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        summonStandTag = "Star_Platinum";
        // 处理技能物品
        ElbertSkill.skillItemTag.add(summonStandTag);
        ElbertSkill.skillItemTag.add(oulaTag);
        super.initialize();
    }

    @Override
    public void summon(Player player) {
        // 处理替身
        if (spawnMap.get(player) == null) {
            summonStand(player, PigZombie.class);

            // 一技能  时停
            timeStop(player, 100);

            // 二技能  欧拉欧拉
            oulaOula(player);
        } else {
            removeStand(player);
        }
    }

    String oulaTag = "欧拉欧拉";

    /**
     * 技能：欧拉欧拉
     * @param player    召唤技能的玩家
     */
    void oulaOula(Player player) {
        // 发放欧拉
        ItemStack itemStack = new ItemStack(Material.STICK);
        NekoUtil.makeTagItem(itemStack, oulaTag);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier("value", 20, AttributeModifier.Operation.ADD_NUMBER));
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
                new AttributeModifier("value", 20, AttributeModifier.Operation.ADD_NUMBER));
        itemStack.setItemMeta(itemMeta);
        player.getInventory().addItem(itemStack);

        // 收回木大
        new BukkitRunnable() {
            @Override
            public void run() {
                NekoUtil.spendTagItem(player.getInventory(), oulaTag);
                player.sendMessage("已收回 " + oulaTag);
            }
        }.runTaskLater(plugin, 20 * 20);
    }
}
