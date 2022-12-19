package br.com.stency.minas.data;

import lombok.Data;

@Data public class MineStatus {

    private Long lastReset;
    private Integer blocksBreak;
    private Integer totalBlocks;

    public MineStatus(){
        this.lastReset = 0l;
        this.blocksBreak = 0;
        this.totalBlocks = 0;
    }
}
