package fun.fifu.elbertskill;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import fun.fifu.elbertskill.stands.TheWorld;

public class ElbertSkill extends JavaPlugin implements Listener {
    public static final Map<Player, Player> damageMap = new HashMap<>();

    public static ElbertSkill slbertSkill;

    @Override
    public void onLoad() {
        slbertSkill = this;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        // 伪命令：写物品标签
        NekoUtil.makePlayerConmmand("fun.fifu.elberskill.writeTag", "#el-writeTag", res -> {
            ItemStack itemInMainHand = res.player().getInventory().getItemInMainHand();
            if (itemInMainHand.getType().isAir())
                return;
            NekoUtil.makeTagItem(itemInMainHand, res.args()[1]);
            res.player().sendMessage("已标记 " + res.args()[1] + " 标签");
        });

        new TheWorld(this).initialize();
    }

    @Override
    public void onDisable() {
        damageMap.clear();
    }

    @EventHandler
    public void tagDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player damage) {
            damageMap.put(player, damage);
        }
    }

}
