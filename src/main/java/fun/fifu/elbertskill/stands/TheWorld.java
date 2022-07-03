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

//    一、世界[The World]
//    右键物品形式的替身召唤对应替身生物，再次收回（僵尸猪灵）
//
//    一技能 时停
//    事件：
//    1.移除全体实体AI
//    2.所有实体循环原地tp（这里把末影珍珠和箭矢一类的弹射物kill一下）
//    3.给所有实体1秒的指定三种DEBUFF（我建议是时长1秒然后循环九秒，防止玩家喝牛奶）
//    虚弱VIII（八级）失明八级 缓慢八级
//    4.经过九秒(180tick)
//    5.归还实体AI，解除实体TP和DEBUFF（kill的弹射物不用管）
//    冷却3秒
//
//    二技能 木大木大
//    给玩家一个拿在主手加18攻击伤害，20攻击速度的物品（最好是棍子这类不能摆放的）
//    20秒后移除
//    冷却5秒
public class TheWorld extends Stand {
    public TheWorld(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        summonStandTag = "The World";
        // 处理技能物品
        ElbertSkill.skillItemTag.add(summonStandTag);
        ElbertSkill.skillItemTag.add(mudaTag);
        super.initialize();
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

}
