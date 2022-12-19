package br.com.stency.minas.listener;

import br.com.stency.common.util.world.Cuboid;
import br.com.stency.minas.Minas;
import br.com.stency.minas.data.Mine;
import br.com.stency.minas.event.MineBlockBreakEvent;
import br.com.stency.minas.event.MineResetEvent;
import br.com.stency.minas.service.MineService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MineListener implements Listener {

    @EventHandler
    public void blockBreak(BlockBreakEvent e){
        Player player = e.getPlayer();
        if (!e.isCancelled()){
            for (Mine m : Minas.getInstance().getService(MineService.class).all()){
                Cuboid cuboid = new Cuboid(m.getPos1(),m.getPos2());
                if (cuboid.contains(e.getBlock())){
                    if (m.isReseting()){
                        e.getPlayer().sendMessage("§cAguarde até a mina resetar.");
                        e.setCancelled(true);
                    }else {
                        Bukkit.getServer().getPluginManager().callEvent(new MineBlockBreakEvent(m, e.getBlock(), player));
                    }
                    break;
                }
            }
        }
    }

    @EventHandler
    public void reset(MineResetEvent e){
        e.getMine().reset();
        Bukkit.broadcastMessage("§e§lMINAS §aA mina §f" + e.getMine().getName() + "§a foi resetada!");
    }

}