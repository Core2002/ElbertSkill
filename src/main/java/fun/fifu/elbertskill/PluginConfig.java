package fun.fifu.elbertskill;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

@Data
public class PluginConfig {
    public static PluginConfig INSTEN_CONFIG;
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Plugin plugin = ElbertSkill.slbertSkill;

    
    static {
        INSTEN_CONFIG = build();
        plugin.getLogger().info("成功加载配置文件：" + gson.toJson(INSTEN_CONFIG));
    }

    private static PluginConfig build() {
        plugin.saveResource("config.json", false);
        File file = Arrays.stream(Objects.requireNonNull(plugin.getDataFolder().listFiles()))
                .filter(f -> f.getName().equals("config.json"))
                .findFirst().get();
        try (FileInputStream fis = new FileInputStream(file)) {
            return gson.fromJson(new String(fis.readAllBytes(), StandardCharsets.UTF_8), PluginConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PluginConfig() {
    }

}
