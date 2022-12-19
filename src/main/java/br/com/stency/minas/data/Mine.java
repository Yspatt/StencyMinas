package br.com.stency.minas.data;

import br.com.stency.common.util.string.Time;
import br.com.stency.common.util.world.Cuboid;
import br.com.stency.minas.Minas;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.collect.Lists;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


@Data
public class Mine {

    private String name;
    private Location pos1,pos2;
    private Location statusHologram;
    private MineStatus status;
    private boolean reseting;
    private Hologram hologram;
    private Cuboid cuboid;
    private List<TextLine> textLine = Lists.newArrayList();
    private Long resetTime;
    private List<ItemStack> blocks = Lists.newArrayList();

    public Mine(String name){
        this.name = name;
        this.status = new MineStatus();
    }

    public void setupHD(){
        hologram = HologramsAPI.createHologram(Minas.getInstance(), statusHologram);
        textLine.add(hologram.appendTextLine("§7..."));
        textLine.add(hologram.appendTextLine("§aUse /mina edit " + getName()));
        textLine.add(hologram.appendTextLine("§aPara editar sua mina"));
        textLine.add(hologram.appendTextLine("§7..."));
        this.cuboid = new Cuboid(pos1,pos2);
    }

    DecimalFormat decimalFormat = new DecimalFormat("###.##");
    public void reloadHD(){
        long reset= getStatus().getLastReset() + (getResetTime() * 60000);
        String percent = (getStatus().getBlocksBreak() == 0 ? "0%" : (decimalFormat.format(((float)getStatus().getBlocksBreak() / getStatus().getTotalBlocks()) * 100)) + "%");
        getTextLine().get(0).setText("§a§lMina §b" + getName());
      //  getTextLine().get(1).setText("§aExclusiva para: §b" + getName() + "I, " + getName() + "II, " + getName() + "III");
        getTextLine().get(1).setText("§aBlocos quebrados: §f" + getStatus().getBlocksBreak() + "/" + getStatus().getTotalBlocks() + " (" + percent + ")");
        getTextLine().get(2).setText("§aJogadores minerando: §f" + mining());
        getTextLine().get(3).setText("§aReseta em: " + Time.getTime(reset));
    }

    public int mining(){
        int mining = 0;
        for (Player p: Bukkit.getOnlinePlayers()) {
            if (getCuboid().contains(p.getLocation())){
                mining++;
            }
        }
      return mining;
    }

    public void reset(){
        reseting = true;
        hologram.teleport(statusHologram);
        status.setLastReset(System.currentTimeMillis());
        Bukkit.getOnlinePlayers().forEach(p ->{
            if (getCuboid().contains(p.getLocation())){
                p.teleport(hologram.getLocation());
            }
        });

        status.setBlocksBreak(0);
        Iterator<Block> iterator = getCuboid().iterator();
        new BukkitRunnable(){
            int t = 0;
            @Override
            public void run() {
                if (!iterator.hasNext()){
                    reseting = false;
                    cancel();
                }
                for (int i = 0; i < getCuboid().getFace(Cuboid.CuboidDirection.East).getBlocks().size()*5; i++){
                    status.setTotalBlocks(t);
                    if (!iterator.hasNext())break;
                    Block block = iterator.next();
                    int random = new Random().nextInt(blocks.size());
                    block.setType(blocks.get(random).getType());
                    t++;
                }
            }
        }.runTaskTimer(Minas.getInstance(),0,20);



    }
}
