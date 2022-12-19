package br.com.stency.minas.service.impl;

import br.com.stency.common.util.file.CommonConfig;
import br.com.stency.minas.Minas;
import br.com.stency.minas.data.Mine;
import br.com.stency.minas.event.MineResetEvent;
import br.com.stency.minas.service.MineService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MineServiceImpl implements MineService {

    private Map<String, Mine> mineMap = Maps.newHashMap();
    public CommonConfig config = new CommonConfig(Minas.getInstance(),"mines.yml");

    @Override
    public Mine get(String name) {
        return mineMap.get(name);
    }

    @Override
    public Mine create(String name) {
        if (get(name) == null){
            return add(name);
        }
        return get(name);
    }

    @Override
    public Mine add(String name) {
        Mine mine = new Mine(name);
        mineMap.put(name,mine);
        return mine;
    }

    @Override
    public void remove(String name) {
        mineMap.remove(name);
    }

    @Override
    public Collection<Mine> all() {
        return mineMap.values();
    }

    @Override
    public CommonConfig config() {
        return config;
    }

    public List<Location> location = Lists.newArrayList();
    @Override
    public void resetRunnable() {
            new BukkitRunnable(){
                @Override
                public void run() {
                    for (Mine mine : all()){
                        if (mine.getBlocks().isEmpty())continue;
                        if (mine.getResetTime() == null)continue;
                        long reset= mine.getStatus().getLastReset() + (mine.getResetTime() * 60000);
                        mine.reloadHD();
                        if (!mine.isReseting()) {
                            for (Block b : mine.getCuboid()) {
                                if (b.getType() == Material.AIR) {
                                    if (location.contains(b.getLocation()))continue;
                                    mine.getStatus().setBlocksBreak(mine.getStatus().getBlocksBreak() + 1);
                                    location.add(b.getLocation());
                                }
                            }
                        }
                        if (mine.getStatus().getBlocksBreak()>= mine.getStatus().getTotalBlocks() || System.currentTimeMillis() >= reset){
                            Bukkit.getPluginManager().callEvent(new MineResetEvent(mine));
                            location = Lists.newArrayList();
                        }
                    }
                }
            }.runTaskTimer(Minas.getInstance(),0,5);
    }

    @Override
    public void clearLocations() {
        location.clear();
    }

    @Override
    public void init() {
        ConfigurationSection configurationSection = config.getConfigurationSection("Mines");
        if (configurationSection == null)return;
        for (String s : configurationSection.getKeys(false)){
            Mine mine = add(s);

            mine.setPos1(config.getLocation("Mines." + s + ".pos1"));
            mine.setPos2(config.getLocation("Mines." + s + ".pos2"));
            mine.setStatusHologram(config.getLocation("Mines." + s + ".statusHD").add(0.5,0,0.5));
            mine.setResetTime(config.getLong("Mines." + s + ".resetTime"));
            mine.setupHD();

            for (String ores : config.getStringList("Mines." + s + ".ores")) {
                String[] split = ores.split(";");
                ItemStack item = new ItemStack(Material.valueOf(split[0].toUpperCase()), 1, Short.parseShort(split[1]));
                mine.getBlocks().add(item);
            }

            mine.reset();
        }
    }

    @Override
    public void disable() {
        for (Mine mine : all()){
            config.set("Mines." + mine.getName() + ".pos1",mine.getPos1());
            config.set("Mines." + mine.getName() + ".pos2",mine.getPos2());
            config.set("Mines." + mine.getName() + ".statusHD",mine.getStatusHologram());
            config.set("Mines." + mine.getName() + ".resetTime",mine.getResetTime());

            List<String> ores = Lists.newArrayList();
            for (ItemStack item : mine.getBlocks()){
                if (item == null)continue;
                ores.add(item.getType() + ";" + item.getDurability());
            }
            config.set("Mines." + mine.getName() + ".ores",ores);
        }
        config.save();
    }
}
