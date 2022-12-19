package br.com.stency.minas.service;

import br.com.stency.common.util.file.CommonConfig;
import br.com.stency.minas.data.Mine;

import java.util.Collection;

public interface MineService {

    Mine get(String name);
    Mine create(String name);
    Mine add(String name);
    void remove(String name);
    void resetRunnable();

    Collection<Mine> all();
    CommonConfig config();
    void clearLocations();

    void init();
    void disable();
}
