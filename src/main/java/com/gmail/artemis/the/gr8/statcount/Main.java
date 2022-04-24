package com.gmail.artemis.the.gr8.statcount;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().info("enabled PlayerStats!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("disabled PlayerStats!");
    }


}
