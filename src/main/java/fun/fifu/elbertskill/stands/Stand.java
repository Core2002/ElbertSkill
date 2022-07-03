package fun.fifu.elbertskill.stands;

import org.bukkit.entity.Player;

/**
 * 替身：玩家会随机抽取一个替身，然后获得对应替身的技能
 */
public interface Stand {
    /**
     * 在此处应有初始化，插件加载时调用
     */
    void initialize();

    /**
     * 替身被召唤时调用
     * @param player 召唤替身的玩家
     */
    void summon(Player player);
}
