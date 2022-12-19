package br.com.stency.minas;

import br.com.stency.minas.command.MineCommand;
import br.com.stency.minas.listener.MineListener;
import br.com.stency.minas.service.MineService;
import br.com.stency.minas.service.impl.MineServiceImpl;
import lombok.Getter;

public class Minas extends CommonPlugin {


    @Getter
    private static Minas instance;
    public Minas(){
        instance = this;
        provideService(MineService.class,new MineServiceImpl());
    }


    @Override
    public void enable() {
        register(this,new MineCommand());
        register(this,new MineListener());
        getService(MineService.class).init();
        getService(MineService.class).resetRunnable();
    }

    @Override
    public void disable() {
        getService(MineService.class).disable();
    }

    @Override
    public void load() {

    }
}
