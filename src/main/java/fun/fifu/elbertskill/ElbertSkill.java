package fun.fifu.elbertskill;

import com.alkaidmc.alkaid.bukkit.event.AlkaidEvent;
import fun.fifu.elbertskill.stands.AbstractStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        NekoUtil.makePlayerCommand("fun.fifu.elberskill.writeTag", "#el-writeTag", res -> {
            ItemStack itemInMainHand = res.player().getInventory().getItemInMainHand();
            if (itemInMainHand.getType().isAir())
                return;
            NekoUtil.makeTagItem(itemInMainHand, res.args()[1]);
            res.player().sendMessage("已标记 " + res.args()[1] + " 标签");
        });

        // 漏洞处理
        antiBug();

        // 初始化替身
        ClassUtils.instance.getClasses("fun.fifu.elbertskill.stands").forEach(c -> {
            try {
                if (Modifier.isAbstract(c.getModifiers()) || c.isAnonymousClass())
                    return;
                if (c.getConstructor(JavaPlugin.class).newInstance(this) instanceof AbstractStand stand)
                    stand.initialize();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        getLogger().info("插件已启动");
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
