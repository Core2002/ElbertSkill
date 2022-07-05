package fun.fifu.elbertskill;

import java.util.*;

import com.alkaidmc.alkaid.bukkit.event.AlkaidEvent;
import fun.fifu.elbertskill.stands.StarPlatinum;
import fun.fifu.elbertskill.stands.StardustEcho;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

        // 漏洞处理
        antiBug();

        // 初始化替身
        new TheWorld(this).initialize();
        new StarPlatinum(this).initialize();
        new StardustEcho(this).initialize();
    }

    public static Set<String> skillItemTag = new HashSet<>();

    private void antiBug() {
        // 丢弃技能物品
        new AlkaidEvent(this).simple()
                .event(PlayerDropItemEvent.class)
                .listener(event -> {
                    for (String tag : skillItemTag) {
                        if (NekoUtil.hasTagItem(event.getItemDrop().getItemStack(), tag)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                })
                .priority(EventPriority.HIGHEST)
                .ignore(false)
                .register();
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
