package br.com.stency.minas.event;

import br.com.stency.minas.data.Mine;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MineBlockBreakEvent extends Event {

    @Getter private Mine mine;
    @Getter private Block block;
    @Getter private Player player;

    public MineBlockBreakEvent(Mine mine,Block block,Player player){
        this.mine = mine;
        this.player = player;
        this.block = block;
    }


    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
