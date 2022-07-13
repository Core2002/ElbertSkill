package fun.fifu.elbertskill.items;

import com.alkaidmc.alkaid.bukkit.event.AlkaidEvent;
import fun.fifu.elbertskill.NekoUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractItem {
    JavaPlugin plugin;

    public AbstractItem(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 物品名称
     */
    public String itemName;

    /**
     * 在此处应有初始化，插件加载时调用
     */
    public void initialize() {
        // 使用物品
        new AlkaidEvent(plugin).simple()
                .event(PlayerInteractEvent.class)
                .listener(event -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
                        return;
                    Player player = event.getPlayer();
                    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                    if (itemInMainHand.getType().isAir())
                        return;
                    if (!NekoUtil.hasTagItem(itemInMainHand, itemName))
                        return;
                    onUse(event.getPlayer());
                })
                .priority(EventPriority.HIGHEST)
                .ignore(false)
                .register();
    }

    abstract void onUse(Player player);

}
