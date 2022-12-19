package br.com.stency.minas.event;

import br.com.stency.minas.data.Mine;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MineResetEvent extends Event {

    @Getter private Mine mine;

    public MineResetEvent(Mine mine){
        this.mine = mine;
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
